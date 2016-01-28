package com.karmahostage.portsweeper.scanning.service;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostDiscoveryResponse;
import com.karmahostage.portsweeper.scanning.model.HostStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HostDiscoveryTask extends AsyncTask<Void, Integer, Set<Host>> {

    public static final int MAX_IPS = 254;
    String IPADDRESS_PATTERN =
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";

    Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);

    private Set<Host> discoveredHosts = Sets.newConcurrentHashSet();
    private HostDiscoveryResponse hostDiscoveryResponse;

    private byte[] myIp = null;

    private ExecutorService arpThreadPool;
    private ExecutorService scanningThreadPool;

    public HostDiscoveryTask(HostDiscoveryResponse hostDiscoveryResponse) {
        this.hostDiscoveryResponse = hostDiscoveryResponse;
    }

    private String readProcArp() {
        String output = "";
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            while ((sCurrentLine = br.readLine()) != null) {
                output += (sCurrentLine) + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) br.close();
            } catch (IOException ex) {
                Log.e("PSW", "couldn't read from arpproc");
            }
        }
        return output;
    }

    private Set<Host> extractHostsFromProcArp() {
        Set<Host> arpDiscoveredHosts = Sets.newConcurrentHashSet();
        String[] split = readProcArp().split("\n");
        for (String entry : split) {
            try {
                if (entry.contains("0x02")) {
                    Matcher matcher = pattern.matcher(entry);
                    if (matcher.find()) {
                        String extractedIp = matcher.group();
                        arpDiscoveredHosts.add(
                                new Host()
                                        .setIpAddress(extractedIp)
                                        .setIp(InetAddress.getByName(extractedIp).getAddress())
                                        .setStatus(HostStatus.ONLINE)
                        );
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return arpDiscoveredHosts;
    }

    @Override
    protected Set<Host> doInBackground(Void... params) {
        this.hostDiscoveryResponse.onProgressUpdate("Starting ARP-Scan");
        this.arpThreadPool = Executors.newFixedThreadPool(4);
        findMachinesBasedOnArpScan();
        this.arpThreadPool.shutdown();
        try {
            arpThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.hostDiscoveryResponse.onProgressUpdate("ARP-Scan Finished, Starting full (slower) scan");
        this.scanningThreadPool = Executors.newFixedThreadPool(4);
        findMachinesBasedOnLocalIp();
        this.scanningThreadPool.shutdown();
        try {
            scanningThreadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.hostDiscoveryResponse.onProgressUpdate("Finished scanning for nearby hosts");
        return discoveredHosts;
    }

    private void findMachinesBasedOnArpScan() {
        Set<Host> hosts = extractHostsFromProcArp();
        for (final Host host : hosts) {
            arpThreadPool.submit(
                    new Runnable() {
                        @Override
                        public void run() {
                            searchForIp(host.getIp());
                        }
                    }
            );
        }
    }

    private void findMachinesBasedOnLocalIp() {
        Optional<InetAddress> localhost = getLocalAddress();
        if (localhost.isPresent()) {
            InetAddress inetAddress = localhost.get();
            myIp = inetAddress.getAddress();
            scanningThreadPool.submit(
                    new Runnable() {
                        @Override
                        public void run() {
                            if (!isCancelled()) {
                                searchForIp(myIp);
                            }
                        }
                    }
            );
            for (int i = 1; i <= MAX_IPS; i++) {
                final int finalI = i;
                scanningThreadPool.submit(
                        new Runnable() {
                            @Override
                            public void run() {
                                if (!isCancelled()) {
                                    searchForIp(createSubnetAddress(myIp, (byte) finalI));
                                }
                            }
                        }
                );

            }
        }
    }

    private void searchForIp(final byte[] ipToLookFor) {
        Optional<InetAddress> address = getRemoteAddress(ipToLookFor);
        if (address.isPresent() && !alreadyContainsIp(ipToLookFor)) {
            try {
                Process exec = Runtime.getRuntime().exec(String.format("ping -c 1  -W 1 %s", address.get().getHostName()));
                Log.d("PSW", "pinging " + address.get().getHostName());
                int returnValue = exec.waitFor();
                exec.destroy();
                if (returnValue == 0) {
                    Host discoveredHost = new Host()
                            .setIp(address.get().getAddress())
                            .setIpAddress(address.get().getHostAddress())
                            .setStatus(Arrays.equals(address.get().getAddress(), myIp) ? HostStatus.SELF : HostStatus.ONLINE);
                    discoveredHosts.add(
                            discoveredHost
                    );
                   /* try {
                        NbtAddress nbtAddress = NbtAddress.getAllByAddress(discoveredHost.getIpAddress())[0];
                        discoveredHost.setHostName(nbtAddress.getHostName());
                    } catch (Exception ex) {
                        //silently fail on the hostname
                    }*/
                    hostDiscoveryResponse.onResult(discoveredHost);
                } else if (isFoundInDnsLookup(address.get())) {
                    Host discoveredHost = new Host()
                            .setIp(address.get().getAddress())
                            .setIpAddress(address.get().getHostAddress())
                            .setStatus(HostStatus.OFFLINE);
                    discoveredHosts.add(
                            discoveredHost
                    );
                    Log.d("PSW", String.format("%s was not reachable, but found in dns records", address.get().getHostAddress()));
                    hostDiscoveryResponse.onResult(discoveredHost);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.d("PSW", String.format("%s was not reachable", address.get().getHostAddress()));
            }
        }
    }

    private boolean isFoundInDnsLookup(InetAddress address) {
        return !address.getHostAddress().equals(address.getHostName());
    }

    private byte[] createSubnetAddress(byte[] myIp, byte lastByte) {
        return new byte[]{myIp[0], myIp[1], myIp[2], lastByte};
    }

    private Optional<InetAddress> getRemoteAddress(byte[] addressAsBytes) {
        try {
            return Optional.fromNullable(InetAddress.getByAddress(addressAsBytes));
        } catch (Exception ex) {
            return Optional.absent();
        }
    }


    public static Optional<InetAddress> getLocalAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress() && addr instanceof Inet4Address) {
                        return Optional.fromNullable(addr);
                    }
                }
            }
            return Optional.absent();
        } catch (Exception ex) {
            return Optional.absent();
        }
    }

    public boolean alreadyContainsIp(byte[] myIp) {
        for (Host host : this.discoveredHosts) {
            if (Arrays.equals(myIp, host.getIp())) {
                return true;
            }
        }
        return false;
    }
}
