package com.karmahostage.portsweeper.scanning.model;

public class Host {

    private byte[] ip;
    private String ipAddress;
    private String hostName;
    private HostStatus status;

    public byte[] getIp() {
        return ip;
    }

    public Host setIp(byte[] ip) {
        this.ip = ip;
        return this;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Host setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public String getHostName() {
        return hostName;
    }

    public Host setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public HostStatus getStatus() {
        return status;
    }

    public Host setStatus(HostStatus status) {
        this.status = status;
        return this;
    }
}
