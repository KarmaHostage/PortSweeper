package com.karmahostage.portsweeper.scanning.model;

public interface HostDiscoveryResponse {
    void onResult(Host discoveredHosts);

    void onProgressUpdate(Integer value);
}
