package com.dreamwalker.diabetesfits.adapter.isens;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.HomeActivity;
import com.dreamwalker.diabetesfits.model.Device;

import java.util.ArrayList;
import java.util.HashSet;

import io.paperdb.Paper;

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
    HashSet<Device> deviceDatabase = new HashSet<>();

    public DeviceSearchAdapter(Context context, ArrayList<Device> deviceArrayList) {
        this.context = context;
        this.deviceArrayList = deviceArrayList;
        Paper.init(this.context);
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

        holder.container.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Check");
            builder.setMessage(deviceName + " 장비를 등록합니다.");
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                // TODO: 2018-07-22 기존 장비 케시 데이터베이스를 가져온다.
                if (Paper.book("device").read("user_device") != null) {
                    deviceDatabase = Paper.book("device").read("user_device");

                    if (deviceDatabase == null) {
                        // TODO: 2018-07-22 없으면 추가해서 저장하고
                        deviceDatabase.add(new Device(deviceName, deviceAddress));
                        Paper.book("device").write("user_device", deviceDatabase);

                    } else {
                        for (Device s : deviceDatabase) {
                            if (s.getDeviceAddress().equals(deviceAddress)) {
                                Log.e("혈당계 추가  ", "onBindViewHolder: " + "이미 장비 추가되어 있음");
                            } else {
                                // TODO: 2018-07-22 있으면 가져온 데이터베이스에 추가해서 저장한다.
                                deviceDatabase.add(new Device(deviceName, deviceAddress));
                                Paper.book("device").write("user_device", deviceDatabase);
                            }
                        }
                    }

                } else {
                    deviceDatabase.add(new Device(deviceName, deviceAddress));
                    Paper.book("device").write("user_device", deviceDatabase);
                }

                Intent intent = new Intent(context, HomeActivity.class);
//                intent.putExtra(IntentConst.EXTRAS_DEVICE_NAME, deviceName);
//                intent.putExtra(IntentConst.EXTRAS_DEVICE_ADDRESS, deviceAddress);
                context.startActivity(intent);
                ((Activity) context).finish();

            });
            builder.show();
        });

    }

    @Override
    public int getItemCount() {
        return deviceArrayList.size();
    }
}
