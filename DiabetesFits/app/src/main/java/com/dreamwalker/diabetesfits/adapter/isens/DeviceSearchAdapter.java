package com.dreamwalker.diabetesfits.adapter.isens;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.model.Device;

import java.util.ArrayList;

class DeviceSearchViewHolder extends RecyclerView.ViewHolder {

    TextView deviceName;
    TextView deviceAddress;
    LinearLayout container;

    public DeviceSearchViewHolder(View itemView) {
        super(itemView);
        this.deviceName = itemView.findViewById(R.id.device_name);
        this.deviceAddress = itemView.findViewById(R.id.device_address);
        this.container = itemView.findViewById(R.id.container);
    }
}

public class DeviceSearchAdapter extends RecyclerView.Adapter<DeviceSearchViewHolder> {
    Context context;
    ArrayList<Device> deviceArrayList;

    public DeviceSearchAdapter(Context context, ArrayList<Device> deviceArrayList) {
        this.context = context;
        this.deviceArrayList = deviceArrayList;
    }


    @NonNull
    @Override
    public DeviceSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.listitem_device, parent, false);
        return new DeviceSearchViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceSearchViewHolder holder, int position) {
        holder.deviceName.setTextSize(18.0f);
        final String deviceName = deviceArrayList.get(position).getDeviceName();
        final String deviceAddress = deviceArrayList.get(position).getDeviceAddress();
        if (deviceName != null && deviceName.length() > 0) {
            holder.deviceName.setText(deviceName);
        } else {
            holder.deviceName.setText(R.string.unknown_device);
        }
        holder.deviceAddress.setText(deviceAddress);

    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }
}
