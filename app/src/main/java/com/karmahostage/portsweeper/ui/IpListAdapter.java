package com.karmahostage.portsweeper.ui;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.karmahostage.portsweeper.R;
import com.karmahostage.portsweeper.scanning.model.Host;
import com.karmahostage.portsweeper.scanning.model.HostStatus;

import java.util.List;

public class IpListAdapter extends ArrayAdapter<Host> {


    public IpListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public IpListAdapter(Context context, int resource, List<Host> hosts) {
        super(context, resource, hosts);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.ip_list_view, null);
        }
        Host currentHost = getItem(position);

        if (currentHost != null) {
            TextView ipTextView = (TextView) v.findViewById(R.id.ipText);
            TextView macTextView = (TextView) v.findViewById(R.id.macAddressText);
            TextView hostNameTextView = (TextView) v.findViewById(R.id.hostNameView);

            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);

            if (ipTextView != null) {
                ipTextView.setText(currentHost.getIpAddress());
            }

            if (macTextView != null) {
                macTextView.setText("notyetimplemented");
            }

            if (hostNameTextView != null) {
                hostNameTextView.setText(currentHost.getHostName());
            }

            if (imageView != null) {
                if (currentHost.getStatus() == HostStatus.OFFLINE) {
                    ColorMatrix cm = new ColorMatrix();
                    cm.setSaturation(0);
                    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
                    imageView.setColorFilter(filter);
                    imageView.setImageResource(R.mipmap.connection);
                } else if (currentHost.getStatus() == HostStatus.SELF) {
                    imageView.setImageResource(R.mipmap.android);
                } else if (currentHost.getStatus() == HostStatus.ONLINE) {
                    imageView.setImageResource(R.mipmap.connection);
                }
            }
        }

        return v;
    }
}
