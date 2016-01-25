package com.karmahostage.portsweeper.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import java.util.Set;

import javax.inject.Inject;

public class PortSweeperActivity extends PortSweeperBaseActivity {


    public static final String RESOLVED_IP = "com.karmahostage.portsweeper.PortSweeperActivity.resolvedIp";
    @Inject
    @ForApplication
    ScanService scanService;

    @Inject
    @ForApplication
    ScanTargetBuilder scanTargetBuilder;

    private ArrayAdapter<Host> ipListAdapter;

    private List<Host> ipAddresses = new ArrayList<>();

    private Button scanButton;
    private AsyncTask<Void, Integer, Set<Host>> networkHostResolver;
    private TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_port_sweeper);

        this.txtStatus = (TextView) findViewById(R.id.txtStatus);
        this.ipListAdapter = new IpListAdapter(this, android.R.layout.simple_list_item_1, ipAddresses);

        final ListView ipListView = (ListView) findViewById(R.id.listView);
        ipListView.setAdapter(ipListAdapter);
        ipListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (ipAddresses.get(position) != null) {
                    Intent ipDetailActivity = new Intent(PortSweeperActivity.this, IpDetailActivity.class);
                    ipDetailActivity.putExtra(RESOLVED_IP, ipAddresses.get(position));
                    startActivity(ipDetailActivity);
                }
            }
        });




        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        scanButton = (Button) findViewById(R.id.btnScan);

        final Context c = this;


        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        if (this.networkHostResolver != null) {
            this.networkHostResolver.cancel(true);
            this.networkHostResolver = null;
            scanButton.setText(R.string.btn_scan);
        }
    }

    private void startScanning(final Context c) {
        clearIps();
        this.networkHostResolver = scanService.resolveNetworkHosts(new HostDiscoveryResponse() {
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
            public void onProgressUpdate(final String value) {
                setMyProgress(value);
            }
        });
        scanButton.setText(R.string.btn_scanning);
    }

    private void setMyProgress(final String value) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtStatus.setText(value);
            }
        });
    }


    private void clearIps() {
        this.ipAddresses.clear();
        ipListAdapter.notifyDataSetChanged();
    }

    private void addIpToList(Host ip) {
        if (!this.ipAddresses.contains(ip)) {
            this.ipAddresses.add(ip);
            ipListAdapter.notifyDataSetChanged();
        }
    }

}
