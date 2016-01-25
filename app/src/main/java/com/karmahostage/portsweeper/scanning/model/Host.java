package com.karmahostage.portsweeper.scanning.model;

import java.io.Serializable;

public class Host implements Serializable {

    private byte[] ip;
    private String ipAddress;
    private String hostName = "";
    private HostStatus status;
    private String macAddress;

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

    public String getMacAddress() {
        return macAddress;
    }

    public Host setMacAddress(String macAddress) {
        this.macAddress = macAddress;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host host = (Host) o;

        return !(ipAddress != null ? !ipAddress.equals(host.ipAddress) : host.ipAddress != null);

    }

    @Override
    public int hashCode() {
        return ipAddress != null ? ipAddress.hashCode() : 0;
    }
}
