package com.karmahostage.portsweeper.scanning.service;

import android.os.AsyncTask;

import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostDiscoveryResponse;
import com.karmahostage.portsweeper.scanning.model.HostIsUpResponse;
import com.karmahostage.portsweeper.scanning.model.HostPingResponse;
import com.karmahostage.portsweeper.scanning.model.HostPingResult;
import com.karmahostage.portsweeper.scanning.model.ScanTarget;

import java.util.List;
import java.util.Set;

public interface ScanService {

    void doScan(List<ScanTarget> target);
    AsyncTask<Void, Integer, Set<Host>> resolveNetworkHosts(HostDiscoveryResponse hostDiscoveryResponse);
    AsyncTask<Host, Integer, HostPingResult> pingHost(Host host, HostPingResponse hostPingResponse);

    void isUp(Host host, HostIsUpResponse isUpResponse);
}
