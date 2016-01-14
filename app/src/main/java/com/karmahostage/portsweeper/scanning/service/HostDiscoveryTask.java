package com.karmahostage.portsweeper.scanning.service;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostDiscoveryResponse;
import com.karmahostage.portsweeper.scanning.model.HostStatus;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class HostDiscoveryTask extends AsyncTask<Void, Integer, Set<Host>> {

    private Set<Host> discoveredHosts = Sets.newConcurrentHashSet();
    private HostDiscoveryResponse hostDiscoveryResponse;

    public HostDiscoveryTask(HostDiscoveryResponse hostDiscoveryResponse) {
        this.hostDiscoveryResponse = hostDiscoveryResponse;
    }

    @Override
    protected Set<Host> doInBackground(Void... params) {
        Optional<InetAddress> localhost = getLocalAddress();
        if (localhost.isPresent()) {
            InetAddress inetAddress = localhost.get();
            byte[] myIp = inetAddress.getAddress();

            for (int i = 1; i <= 254; i++) {
                Optional<InetAddress> address = getRemoteAddress(createSubnetAddress(myIp, (byte) i));
                if (address.isPresent()) {
                    try {
                        if (address.get().isReachable(1000)) {
                            discoveredHosts.add(
                                    new Host()
                                            .setIp(address.get().getAddress())
                                            .setHostName(address.get().getHostName())
                                            .setIpAddress(address.get().getHostAddress())
                                            .setStatus(HostStatus.ONLINE)
                            );

                            Log.d("PSW", String.format("%s was reachable", address.get().getHostAddress()));

                        } else if (isFoundInDnsLookup(address.get())) {
                            discoveredHosts.add(
                                    new Host()
                                            .setIp(address.get().getAddress())
                                            .setHostName(address.get().getHostName())
                                            .setIpAddress(address.get().getHostAddress())
                                            .setStatus(HostStatus.OFFLINE)
                            );
                            Log.d("PSW", String.format("%s was not reachable, but found in dns records", address.get().getHostAddress()));
                        }
                    } catch (Exception ex) {
                        Log.d("PSW", String.format("%s was not reachable", address.get().getHostAddress()));
                    }
                }
            }

        }
        return discoveredHosts;
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

    @Override
    protected void onPostExecute(Set<Host> hosts) {
        super.onPostExecute(hosts);
        hostDiscoveryResponse.onResult(hosts);
    }
}
