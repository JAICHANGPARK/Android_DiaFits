package com.dreamwalker.diabetesfits.adapter.fitnessloadtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.LoginActivity;
import com.dreamwalker.diabetesfits.model.Device;

import java.util.ArrayList;
import java.util.HashSet;

import io.paperdb.Paper;



public class DeviceScanAdapterV2 extends RecyclerView.Adapter<DeviceScanAdapterV2.DeviceScanViewHolder>{

    ArrayList<BluetoothDevice> deviceArrayList;
    ArrayList<ScanResult> scanResultArrayList;
    Context context;
    SharedPreferences preferences;

    HashSet<Device> deviceDatabase = new HashSet<>();

    DeviceItemClickListener deviceItemClickListener;


    public void setDeviceItemClickListener(DeviceItemClickListener deviceItemClickListener) {
        this.deviceItemClickListener = deviceItemClickListener;
    }

    public DeviceScanAdapterV2(ArrayList<BluetoothDevice> deviceArrayList, ArrayList<ScanResult> scanResults ,Context context) {
        this.deviceArrayList = deviceArrayList;
        this.scanResultArrayList = scanResults;
        this.context = context;

        preferences = this.context.getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        Paper.init(this.context);
    }

    @NonNull
    @Override
    public DeviceScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.list_item_device_v2, parent, false);
        return new DeviceScanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceScanViewHolder holder, int position) {

        final String deviceName = deviceArrayList.get(position).getName();
        final String deviceAddress = deviceArrayList.get(position).getAddress();
        int deviceRssi = scanResultArrayList.get(position).getRssi();

        if (deviceName != null && deviceName.length() > 0)
            holder.deviceName.setText(deviceName);
        else
            holder.deviceName.setText(R.string.unknown_device);
//        holder.deviceName.setText(deviceArrayList.get(position).getName());
        holder.deviceAddress.setText(deviceArrayList.get(position).getAddress());
        holder.deviceRssi.setText(String.valueOf(deviceRssi));

        holder.container.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Check");
            builder.setMessage(deviceName + " 장비를 등록합니다.");
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("activity_executed",true);
                editor.apply();
                // TODO: 2018-07-22 장비 등록 내부 케시에 저장한다. - 박제창 
//                deviceMap.put("deviceName", deviceName);
//                deviceMap.put("deviceAddress", deviceAddress);
                deviceDatabase.add(new Device(deviceName, deviceAddress));
                Paper.book("device").write("user_device", deviceDatabase);

                Intent intent = new Intent(context, LoginActivity.class);
//                intent.putExtra(IntentConst.EXTRAS_DEVICE_NAME, deviceName);
//                intent.putExtra(IntentConst.EXTRAS_DEVICE_ADDRESS, deviceAddress);
                context.startActivity(intent);
                ((Activity)context).finish();

            });
            builder.show();
        });
    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }


    class DeviceScanViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
        LinearLayout container;

        public DeviceScanViewHolder(View itemView) {
            super(itemView);
            this.deviceName = itemView.findViewById(R.id.device_name);
            this.deviceAddress = itemView.findViewById(R.id.device_address);
            this.deviceRssi = itemView.findViewById(R.id.device_rssi);
            this.container = itemView.findViewById(R.id.container);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (deviceItemClickListener != null){
                deviceItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
