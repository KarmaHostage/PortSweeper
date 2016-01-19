package com.karmahostage.portsweeper.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.karmahostage.portsweeper.R;
import com.karmahostage.portsweeper.infrastructure.ForApplication;
import com.karmahostage.portsweeper.infrastructure.ui.PortSweeperBaseActivity;
import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostDiscoveryResponse;
import com.karmahostage.portsweeper.scanning.model.ScanTargetBuilder;
import com.karmahostage.portsweeper.scanning.service.ScanService;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PortSweeperActivity extends PortSweeperBaseActivity {


    @Inject
    @ForApplication
    ScanService scanService;

    @Inject
    @ForApplication
    ScanTargetBuilder scanTargetBuilder;

    private ArrayAdapter<Host> ipListAdapter;

    private List<Host> ipAddresses = new ArrayList<>();

    private ProgressBar progressBar;

    private Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_port_sweeper);

        this.ipListAdapter = new IpListAdapter(this, android.R.layout.simple_list_item_1, ipAddresses);

        final ListView ipListView = (ListView) findViewById(R.id.listView);
        ipListView.setAdapter(ipListAdapter);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        scanButton = (Button) findViewById(R.id.btnScan);

        final Context c = this;


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProgress(0);
                clearIps();
                CharSequence scanning = getText(R.string.btn_scanning);
                CharSequence scan = getText(R.string.btn_scan);
                if (scanButton.getText().equals(scanning)) {
                    stopScanning();
                } else if (scanButton.getText().equals(scan)) {
                    startScanning(c);
                }
            }
        });
    }

    private void stopScanning() {
        //TODO: stop the active scan
        scanButton.setText(R.string.btn_scan);
    }

    private void startScanning(final Context c) {
        scanService.resolveNetworkHosts(new HostDiscoveryResponse() {
            @Override
            public void onResult(final Host discoveredHosts) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addIpToList(discoveredHosts);
                        Toast.makeText(c, R.string.found_host, Toast.LENGTH_SHORT);
                    }
                });
            }

            @Override
            public void onProgressUpdate(final Integer value) {
                setProgress(value);
            }
        });
        scanButton.setText(R.string.btn_scanning);
    }

    private void setProgress(final Integer value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (value == 100) {
                    if (scanButton != null) {
                        PortSweeperActivity.this.scanButton.setText(getText(R.string.btn_scan));
                    }
                } else {
                    progressBar.setProgress(value);
                }
            }
        });
    }


    private void clearIps() {
        this.ipAddresses.clear();
        ipListAdapter.notifyDataSetChanged();
    }

    private void addIpToList(Host ip) {
        this.ipAddresses.add(ip);
        ipListAdapter.notifyDataSetChanged();
    }

}
