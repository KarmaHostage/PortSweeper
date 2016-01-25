package com.karmahostage.portsweeper.scanning.service;

import android.os.AsyncTask;

import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostPingResponse;
import com.karmahostage.portsweeper.scanning.model.HostPingResult;

import java.net.InetAddress;


public class HostPingTask extends AsyncTask<Host, Integer, HostPingResult> {

    private HostPingResult result;
    private HostPingResponse hostPingResponse;

    public HostPingTask(HostPingResponse hostPingResponse) {
        this.hostPingResponse = hostPingResponse;
    }

    @Override
    protected HostPingResult doInBackground(Host... params) {
        if (params != null && params[0] != null) {
            Host hostToScan = params[0];
            this.result = new HostPingResult(hostToScan);
            for (int i = 0; i < 10; i++) {
                scanHost(hostToScan);
            }
            return result;
        } else {
            throw new IllegalArgumentException("You didn't provide any hosts to scan");
        }
    }

    private void scanHost(Host hostToScan) {
        long responseTime = 0;
        try {
            InetAddress address = InetAddress.getByAddress(hostToScan.getIp());
            long startTime = System.currentTimeMillis();
            if (address.isReachable(10000)) {
                long endTime = System.currentTimeMillis();
                responseTime = endTime - startTime;
            }
        } catch (Exception ex) {

        } finally {
            this.result.getPingResponses().add(responseTime);
            hostPingResponse.onHostPinged(responseTime);
        }
    }
}
