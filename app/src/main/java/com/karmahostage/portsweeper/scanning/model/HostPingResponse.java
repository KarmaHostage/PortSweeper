package com.karmahostage.portsweeper.scanning.model;

public interface HostPingResponse {

    void onHostPinged(long responseTime);

}
