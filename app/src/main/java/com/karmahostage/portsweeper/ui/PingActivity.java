package com.karmahostage.portsweeper.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.karmahostage.portsweeper.R;
import com.karmahostage.portsweeper.infrastructure.ForApplication;
import com.karmahostage.portsweeper.infrastructure.ui.PortSweeperBaseActivity;
import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostPingResponse;
import com.karmahostage.portsweeper.scanning.service.ScanService;
import com.michaldrabik.tapbarmenulib.TapBarMenu;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.io.Serializable;

import javax.inject.Inject;

public class PingActivity extends PortSweeperBaseActivity {

    public static final String RESOLVED_IP = "com.karmahostage.portsweeper.PortSweeperActivity.resolvedIp";

    @Inject
    @ForApplication
    ScanService scanService;

    private Host resolvedIp;
    private TextView title;
    private Button startPingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping_device);

        fetchResolvedIp();

        this.title = (TextView) findViewById(R.id.pingTitle);
        if (this.title != null) {
            title.setText(resolvedIp.getIpAddress());
        }

        this.startPingButton = (Button)findViewById(R.id.startPingButton);

        final ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

        final ValueLineSeries series = new ValueLineSeries();
        series.setColor(0xFF56B7F1);


        this.startPingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanService.pingHost(resolvedIp, new HostPingResponse() {
                    @Override
                    public void onHostPinged(final long responseTime) {
                        Log.i("PSW", "Pinged Host after " + responseTime + "ms");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCubicValueLineChart.addSeries(series);
                                series.addPoint(new ValueLinePoint("ms", responseTime));
                            }
                        });

                    }
                });
            }
        });

    }

    private void fetchResolvedIp() {
        Serializable serializableExtra = getIntent().getSerializableExtra(RESOLVED_IP);
        if (serializableExtra != null) {
            resolvedIp = (Host) serializableExtra;
        }
    }
}
