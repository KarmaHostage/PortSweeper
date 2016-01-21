package com.karmahostage.portsweeper.scanning.service;

import android.os.AsyncTask;

import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostDiscoveryResponse;
import com.karmahostage.portsweeper.scanning.model.ScanTarget;

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
}
