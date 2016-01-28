package com.karmahostage.portsweeper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karmahostage.portsweeper.R;
import com.karmahostage.portsweeper.infrastructure.ForApplication;
import com.karmahostage.portsweeper.infrastructure.ui.PortSweeperBaseActivity;
import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostIsUpResponse;
import com.karmahostage.portsweeper.scanning.service.ScanService;
import com.michaldrabik.tapbarmenulib.TapBarMenu;

import java.io.Serializable;

import javax.inject.Inject;

public class IpDetailActivity extends PortSweeperBaseActivity {

    public static final String RESOLVED_IP = "com.karmahostage.portsweeper.PortSweeperActivity.resolvedIp";

    @Inject
    @ForApplication
    ScanService scanService;

    private TapBarMenu tapBarMenu;
    private Host resolvedIp;
    private TextView title;

    private TextView upStatus;
    private Button btnDevicePing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_detail);

        this.upStatus = (TextView) findViewById(R.id.upStatus);

        fetchResolvedIp();

        this.title = (TextView) findViewById(R.id.title);
        if (this.title != null) {
            title.setText(resolvedIp.getIpAddress());
        }

        this.tapBarMenu = (TapBarMenu) findViewById(R.id.tapBarMenu);
        tapBarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapBarMenu.toggle();
            }
        });

        this.btnDevicePing = (Button)findViewById(R.id.btnDevicePing);
        this.btnDevicePing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ipDetailActivity = new Intent(IpDetailActivity.this, PingActivity.class);
                ipDetailActivity.putExtra(RESOLVED_IP, resolvedIp);
                startActivity(ipDetailActivity);
            }
        });
    }

    private void fetchResolvedIp() {
        Serializable serializableExtra = getIntent().getSerializableExtra(RESOLVED_IP);
        if (serializableExtra != null) {
            resolvedIp = (Host) serializableExtra;
            scanService.isUp(resolvedIp, new HostIsUpResponse() {
                @Override
                public void isUp(final boolean isUp) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            upStatus.setText(isUp ? "Device is Up" : "Device is Down");
                        }
                    });
                }
            });
        }
    }
}
