package com.karmahostage.portsweeper.scanning.model;

import com.google.common.collect.Sets;

import java.util.Set;

public class ScanTargetBuilder {

    public ScanTarget full(String hostName) {
        return new ScanTarget()
                .setHost(hostName)
                .setPorts(getAllPorts());
    }

    private Set<Integer> getAllPorts() {
        Set<Integer> ports = Sets.newConcurrentHashSet();
        for (int i = 1; i <= 65535; i++) {
            ports.add(i);
        }
        return ports;
    }
}
