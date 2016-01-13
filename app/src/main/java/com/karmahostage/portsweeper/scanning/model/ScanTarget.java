package com.karmahostage.portsweeper.scanning.model;

import java.util.Set;

public class ScanTarget {
    private Set<Integer> ports;
    private String host;

    public Set<Integer> getPorts() {
        return ports;
    }

    public ScanTarget setPorts(Set<Integer> ports) {
        this.ports = ports;
        return this;
    }

    public String getHost() {
        return host;
    }

    public ScanTarget setHost(String host) {
        this.host = host;
        return this;
    }
}
