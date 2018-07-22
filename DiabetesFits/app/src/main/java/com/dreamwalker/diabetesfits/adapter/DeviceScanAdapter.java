package com.dreamwalker.diabetesfits.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
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

class DeviceScanViewHolder extends RecyclerView.ViewHolder{

    TextView deviceName;
    TextView deviceAddress;
    LinearLayout container;

    public DeviceScanViewHolder(View itemView) {
        super(itemView);
        this.deviceName = itemView.findViewById(R.id.device_name);
        this.deviceAddress = itemView.findViewById(R.id.device_address);
        this.container = itemView.findViewById(R.id.container);
    }

}

public class DeviceScanAdapter extends RecyclerView.Adapter<DeviceScanViewHolder>{

    ArrayList<BluetoothDevice> deviceArrayList;
    Context context;
    SharedPreferences preferences;

//    HashMap<String,String> deviceMap = new HashMap<>();
    //ArrayList<HashMap<String,String>> deviceDatabase = new ArrayList<>();
    HashSet<Device> deviceDatabase = new HashSet<>();

    public DeviceScanAdapter(ArrayList<BluetoothDevice> deviceArrayList, Context context) {
        this.deviceArrayList = deviceArrayList;
        this.context = context;

        preferences = this.context.getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        Paper.init(this.context);
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
        final String deviceAddress = deviceArrayList.get(position).getAddress();
        if (deviceName != null && deviceName.length() > 0)
            holder.deviceName.setText(deviceName);
        else
            holder.deviceName.setText(R.string.unknown_device);
//        holder.deviceName.setText(deviceArrayList.get(position).getName());
        holder.deviceAddress.setText(deviceArrayList.get(position).getAddress());

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
}
