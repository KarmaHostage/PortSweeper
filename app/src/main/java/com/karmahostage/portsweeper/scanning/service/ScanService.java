package com.karmahostage.portsweeper.scanning.service;

import android.os.AsyncTask;

import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostDiscoveryResponse;
import com.karmahostage.portsweeper.scanning.model.ScanTarget;

import java.util.List;
import java.util.Set;

public interface ScanService {

    void doScan(List<ScanTarget> target);

    AsyncTask<Void, Integer, Set<Host>> resolveNetworkHosts(HostDiscoveryResponse hostDiscoveryResponse);

}
