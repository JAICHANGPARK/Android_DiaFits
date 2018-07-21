package com.dreamwalker.diabetesfits.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class DeviceScanViewHolder extends RecyclerView.ViewHolder{

    TextView deviceName;
    TextView deviceAddress;

    public DeviceScanViewHolder(View itemView, TextView deviceName, TextView deviceAddress) {
        super(itemView);
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
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

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceScanViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }
}
