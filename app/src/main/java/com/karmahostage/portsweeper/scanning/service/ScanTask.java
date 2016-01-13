package com.karmahostage.portsweeper.scanning.service;

import android.os.AsyncTask;
import android.util.Log;

import com.karmahostage.portsweeper.scanning.model.ScanResult;
import com.karmahostage.portsweeper.scanning.model.ScanTarget;

import java.net.InetSocketAddress;
import java.net.Socket;

public class ScanTask extends AsyncTask<ScanTarget, Integer, ScanResult> {
    @Override
    protected ScanResult doInBackground(ScanTarget... params) {
        for (ScanTarget scanTarget : params) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(scanTarget.getHost(), 80), 3000);
                socket.close();
                Log.i("PSW", String.format("%s was OPEN", scanTarget.getHost()));
            } catch (Exception ex) {
                Log.e("PSW", String.format("%s was OPEN", scanTarget.getHost()));
            }
        }
        return new ScanResult();
    }
}
