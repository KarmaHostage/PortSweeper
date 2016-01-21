package com.karmahostage.portsweeper.scanning.model;

import java.io.Serializable;
import java.util.LinkedList;

public class HostPingResult implements Serializable {
    private Host host;
    private LinkedList<Long> pingResponses;

    public HostPingResult(Host host) {
        this.host = host;
        pingResponses = new LinkedList<>();
    }

    public Host getHost() {
        return host;
    }

    public void setHost(Host host) {
        this.host = host;
    }

    public LinkedList<Long> getPingResponses() {
        return pingResponses;
    }
}
