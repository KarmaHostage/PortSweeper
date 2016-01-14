package com.karmahostage.portsweeper.scanning.model;

import java.util.Set;

public interface HostDiscoveryResponse {
    void onResult(Set<Host> discoveredHosts);
}
