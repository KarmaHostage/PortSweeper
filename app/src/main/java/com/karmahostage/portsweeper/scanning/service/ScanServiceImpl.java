package com.karmahostage.portsweeper.scanning.service;

import android.os.AsyncTask;

import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostDiscoveryResponse;
import com.karmahostage.portsweeper.scanning.model.HostIsUpResponse;
import com.karmahostage.portsweeper.scanning.model.HostPingResponse;
import com.karmahostage.portsweeper.scanning.model.HostPingResult;
import com.karmahostage.portsweeper.scanning.model.ScanTarget;

import java.net.InetAddress;
import java.util.List;
import java.util.Set;

public class ScanServiceImpl implements ScanService {

    @Override
    public void doScan(List<ScanTarget> target) {
        new ScanTask()
                .execute(target.toArray(new ScanTarget[target.size()]));
    }

    @Override
    public AsyncTask<Void, Integer, Set<Host>> resolveNetworkHosts(HostDiscoveryResponse hostDiscoveryResponse) {
        return new HostDiscoveryTask(hostDiscoveryResponse)
                .execute();
    }

    @Override
    public AsyncTask<Host, Integer, HostPingResult> pingHost(Host host, HostPingResponse hostPingResponse) {
        return new HostPingTask(hostPingResponse)
                .execute(host);
    }

    @Override
    public void isUp(final Host host, final HostIsUpResponse isUpResponse) {
        new AsyncTask<Void, Integer, Void>() {
            boolean reachable = false;
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InetAddress byAddress = InetAddress.getByAddress(host.getIp());
                    reachable = byAddress.isReachable(5000);
                } catch (Exception ex) {
                    reachable = false;
                } finally {
                    isUpResponse.isUp(reachable);
                    return null;
                }
            }
        }.execute();
    }
}
