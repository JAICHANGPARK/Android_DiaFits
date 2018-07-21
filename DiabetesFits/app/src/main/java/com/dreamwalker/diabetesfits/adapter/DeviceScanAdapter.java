package com.dreamwalker.diabetesfits.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;

import java.util.ArrayList;

class DeviceScanViewHolder extends RecyclerView.ViewHolder{

    TextView deviceName;
    TextView deviceAddress;

    public DeviceScanViewHolder(View itemView) {
        super(itemView);
        this.deviceName = itemView.findViewById(R.id.device_name);
        this.deviceAddress = itemView.findViewById(R.id.device_address);
    }

}

public class DeviceScanAdapter extends RecyclerView.Adapter<DeviceScanViewHolder>{

    ArrayList<BluetoothDevice> deviceArrayList;
    Context context;

    public DeviceScanAdapter(ArrayList<BluetoothDevice> deviceArrayList, Context context) {
        this.deviceArrayList = deviceArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public DeviceScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.listitem_device, parent, false);
        return new DeviceScanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceScanViewHolder holder, int position) {

        final String deviceName = deviceArrayList.get(position).getName();
        if (deviceName != null && deviceName.length() > 0)
            holder.deviceName.setText(deviceName);
        else
            holder.deviceName.setText(R.string.unknown_device);
//        holder.deviceName.setText(deviceArrayList.get(position).getName());
        holder.deviceAddress.setText(deviceArrayList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }
}
