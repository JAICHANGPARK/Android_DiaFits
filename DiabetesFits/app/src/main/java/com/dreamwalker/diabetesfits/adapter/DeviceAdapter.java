/*  Copyright (C) 2015-2018 Andreas Shimokawa, Carsten Pfeiffer, Daniele
    Gobbetti, Lem Dulfo

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package com.dreamwalker.diabetesfits.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.chart.IndoorBikeRealTimeActivity;
import com.dreamwalker.diabetesfits.activity.sync.bsm.SyncBSMDataActivity;
import com.dreamwalker.diabetesfits.activity.sync.indoorbike.SyncIndoorBikeDataActivity;
import com.dreamwalker.diabetesfits.consts.isens.PremierNConst;
import com.dreamwalker.diabetesfits.device.knu.egzero.EGZeroConst;
import com.dreamwalker.diabetesfits.model.Device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.paperdb.Paper;

import static com.dreamwalker.diabetesfits.consts.IntentConst.REAL_TIME_INDOOR_BIKE_DEVICE;
import static com.dreamwalker.diabetesfits.consts.IntentConst.SYNC_BSM_DEVICE;
import static com.dreamwalker.diabetesfits.consts.IntentConst.SYNC_INDOOR_BIKE_DEVICE;


/**
 * Adapter for displaying GBDevice instances.
 */
public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private final Context context;
    private List<Device> deviceList;
    private int expandedDevicePosition = RecyclerView.NO_POSITION;
    private ViewGroup parent;
    private static final String TAG = "DeviceAdapter";

    HashSet<Device> deviceDatabase = new HashSet<>();
    ArrayList<Device> deviceArrayList;

    public DeviceAdapter(Context context, List<Device> deviceList) {
        this.context = context;
        this.deviceList = deviceList;

    }

    @NonNull
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Paper.init(context);
        this.parent = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //final GBDevice device = deviceList.get(position);
        //final DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(device);

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (device.isInitialized() || device.isConnected()) {
//                    showTransientSnackbar(R.string.controlcenter_snackbar_need_longpress);
//                } else {
//                    showTransientSnackbar(R.string.controlcenter_snackbar_connecting);
//                    GBApplication.deviceService().connect(device);
//                }
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if (device.getState() != GBDevice.State.NOT_CONNECTED) {
//                    showTransientSnackbar(R.string.controlcenter_snackbar_disconnecting);
//                    GBApplication.deviceService().disconnect();
//                }
                return true;
            }
        });
        //holder.deviceImageView.setImageResource(R.drawable.level_list_device);
        //level-list does not allow negative values, hence we always add 100 to the key.
        //holder.deviceImageView.setImageLevel(device.getType().getKey() + 100 + (device.isInitialized() ? 100 : 0));

        //holder.deviceNameLabel.setText(getUniqueDeviceName(device));
        String deviceName = deviceList.get(position).getDeviceName();
        String deviceAddress = deviceList.get(position).getDeviceAddress();
        holder.deviceNameLabel.setText(deviceList.get(position).getDeviceName());
        holder.deviceStatusLabel.setText(deviceList.get(position).getDeviceAddress());

//        if (device.isBusy()) {
//            holder.deviceStatusLabel.setText(device.getBusyTask());
//            holder.busyIndicator.setVisibility(View.VISIBLE);
//        } else {
//            holder.deviceStatusLabel.setText(device.getStateString());
//            holder.busyIndicator.setVisibility(View.INVISIBLE);
//        }

//        //begin of action row
//        //battery
//        holder.batteryStatusBox.setVisibility(View.GONE);
//        short batteryLevel = device.getBatteryLevel();
//        if (batteryLevel != GBDevice.BATTERY_UNKNOWN) {
//            holder.batteryStatusBox.setVisibility(View.VISIBLE);
//            holder.batteryStatusLabel.setText(device.getBatteryLevel() + "%");
//            BatteryState batteryState = device.getBatteryState();
//            if (BatteryState.BATTERY_CHARGING.equals(batteryState) ||
//                    BatteryState.BATTERY_CHARGING_FULL.equals(batteryState)) {
//                holder.batteryIcon.setImageLevel(device.getBatteryLevel() + 100);
//            } else {
//                holder.batteryIcon.setImageLevel(device.getBatteryLevel());
//            }
//        }


        if (deviceName.equals(PremierNConst.PREMIER_N_BLE)) {
            holder.fetchActivityData.setVisibility(View.VISIBLE);
            holder.showActivityTracks.setVisibility(View.GONE);
        } else {
            holder.fetchActivityData.setVisibility(View.VISIBLE);
        }
        //fetch activity data
//        holder.fetchActivityDataBox.setVisibility((device.isInitialized() && coordinator.supportsActivityDataFetching()) ? View.VISIBLE : View.GONE);
        holder.fetchActivityData.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            switch (deviceName) {
                                                                case PremierNConst.PREMIER_N_BLE:
                                                                    Log.e("클릭됨", "onClick: 클릭툄" );
                                                                    Intent bsmIntent = new Intent(context, SyncBSMDataActivity.class);
                                                                    bsmIntent.putExtra(SYNC_BSM_DEVICE, deviceAddress);
                                                                    context.startActivity(bsmIntent);
                                                                    break;

                                                                case EGZeroConst.DEVICE_NAME:
                                                                    Log.e("클릭됨", "onClick: 클릭툄" );
                                                                    Intent bikeIntent = new Intent(context, SyncIndoorBikeDataActivity.class);
                                                                    bikeIntent.putExtra(SYNC_INDOOR_BIKE_DEVICE, deviceAddress);
                                                                    context.startActivity(bikeIntent);
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                        }
                                                    }
        );


//        //take screenshot
//        holder.takeScreenshotView.setVisibility((device.isInitialized() && coordinator.supportsScreenshots()) ? View.VISIBLE : View.GONE);
//        holder.takeScreenshotView.setOnClickListener(new View.OnClickListener()
//
//                                                     {
//                                                         @Override
//                                                         public void onClick(View v) {
//                                                             showTransientSnackbar(R.string.controlcenter_snackbar_requested_screenshot);
//                                                             GBApplication.deviceService().onScreenshotReq();
//                                                         }
//                                                     }
//        );
//
//        //manage apps
//        holder.manageAppsView.setVisibility((device.isInitialized() && coordinator.supportsAppsManagement()) ? View.VISIBLE : View.GONE);
//        holder.manageAppsView.setOnClickListener(new View.OnClickListener()
//
//                                                 {
//                                                     @Override
//                                                     public void onClick(View v) {
//                                                         DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(device);
//                                                         Class<? extends Activity> appsManagementActivity = coordinator.getAppsManagementActivity();
//                                                         if (appsManagementActivity != null) {
//                                                             Intent startIntent = new Intent(context, appsManagementActivity);
//                                                             startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
//                                                             context.startActivity(startIntent);
//                                                         }
//                                                     }
//                                                 }
//        );
//
//        //set alarms
//        holder.setAlarmsView.setVisibility(coordinator.supportsAlarmConfiguration() ? View.VISIBLE : View.GONE);
//        holder.setAlarmsView.setOnClickListener(new View.OnClickListener()
//
//                                                {
//                                                    @Override
//                                                    public void onClick(View v) {
//                                                        Intent startIntent;
//                                                        startIntent = new Intent(context, ConfigureAlarms.class);
//                                                        startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
//                                                        context.startActivity(startIntent);
//                                                    }
//                                                }
//        );

//        //show graphs
//        holder.showActivityGraphs.setVisibility(coordinator.supportsActivityTracking() ? View.VISIBLE : View.GONE);
//        holder.showActivityGraphs.setOnClickListener(new View.OnClickListener()
//
//                                                     {
//                                                         @Override
//                                                         public void onClick(View v) {
//                                                             Intent startIntent;
//                                                             startIntent = new Intent(context, ChartsActivity.class);
//                                                             startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
//                                                             context.startActivity(startIntent);
//                                                         }
//                                                     }
//        );

        //show activity tracks
        //holder.showActivityTracks.setVisibility(coordinator.supportsActivityTracks() ? View.VISIBLE : View.GONE);
        holder.showActivityTracks.setOnClickListener(new View.OnClickListener() {
                                                         @Override
                                                         public void onClick(View v) {

                                                             switch (deviceName) {
                                                                 case EGZeroConst.DEVICE_NAME:
                                                                     Log.e("클릭됨", "onClick: 클릭툄" );
                                                                     Intent bsmIntent = new Intent(context, IndoorBikeRealTimeActivity.class);
                                                                     bsmIntent.putExtra(REAL_TIME_INDOOR_BIKE_DEVICE, deviceAddress);
                                                                     context.startActivity(bsmIntent);
                                                                     break;
                                                                 default:
                                                                     break;
                                                             }
//                                                             Intent startIntent;
//                                                             startIntent = new Intent(context, ActivitySummariesActivity.class);
//                                                             startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
//                                                             context.startActivity(startIntent);
                                                         }
                                                     }
        );

        //ItemWithDetailsAdapter infoAdapter = new ItemWithDetailsAdapter(context, device.getDeviceInfos());
        //infoAdapter.setHorizontalAlignment(true);
        // holder.deviceInfoList.setAdapter(infoAdapter);
        //justifyListViewHeightBasedOnChildren(holder.deviceInfoList);
        //holder.deviceInfoList.setFocusable(false);

        //final boolean detailsShown = position == expandedDevicePosition;
        // boolean showInfoIcon = device.hasDeviceInfos() && !device.isBusy();
        //holder.deviceInfoView.setVisibility(showInfoIcon ? View.VISIBLE : View.GONE);
        //holder.deviceInfoBox.setActivated(detailsShown);
        //holder.deviceInfoBox.setVisibility(detailsShown ? View.VISIBLE : View.GONE);
        holder.deviceInfoView.setOnClickListener(new View.OnClickListener() {
                                                     @Override
                                                     public void onClick(View v) {

                                                         android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                                                         switch (deviceName) {

                                                             case PremierNConst.PREMIER_N_BLE:
                                                                 builder.setTitle(PremierNConst.PREMIER_N_BLE);
                                                                 builder.setMessage("자가 혈당 측정기기. 채혈을 통한 혈당 수치 측정을 돕는다.");
                                                                 builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(DialogInterface dialog, int which) {
                                                                         dialog.dismiss();

                                                                     }
                                                                 });
                                                                 builder.show();
                                                                 break;
                                                             case EGZeroConst.DEVICE_NAME:
                                                                 builder.setTitle(EGZeroConst.DEVICE_NAME);
                                                                 builder.setMessage("에르고미터(실내자전거). 실내에서 탈 수 있는 자전거.");
                                                                 builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                                     @Override
                                                                     public void onClick(DialogInterface dialog, int which) {
                                                                         dialog.dismiss();
                                                                     }
                                                                 });
                                                                 builder.show();
                                                                 break;
                                                             default:
                                                                 break;
                                                         }
//                                                         expandedDevicePosition = detailsShown ? -1 : position;
//                                                         TransitionManager.beginDelayedTransition(parent);
//                                                         notifyDataSetChanged();
                                                     }
                                                 }

        );

        holder.deviceRemove.setOnClickListener(v -> {

            Log.e(TAG, "deviceAddress: --> " + deviceAddress);
            Log.e(TAG, "deviceName: --> " + deviceName);

            deviceDatabase = Paper.book("device").read("user_device");
            deviceArrayList = new ArrayList<>(deviceDatabase);

            for (Device d : deviceArrayList){
                Log.e(TAG, "deviceArrayList: --> " + d.getDeviceAddress());
                Log.e(TAG, "deviceArrayList: --> " + d.getDeviceName());
            }
            Log.e(TAG, "onBindViewHolder: " + deviceList.get(position).getDeviceName());
//
//            Set<Device> tmpSet = new HashSet<>();
//            tmpSet.add(new Device(deviceName, deviceAddress));
            Log.e(TAG, "onBindViewHolder: " +   deviceList.indexOf(deviceList.get(position))) ;

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.HomeAlertDialog);

            builder.setTitle("장비 삭제하기");
            builder.setMessage("등록하신 장비를 삭제하시겠어요?");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.e(TAG, "onClick: " + deviceAddress);
//                    deviceDatabase.remove()
//                    boolean removeResult = tmp.remove(new Device(deviceName, deviceAddress));
//                    Log.e(TAG, "removeResult: " + removeResult );
//                    Paper.book("device").delete("user_device");
//                    Paper.book("device").write("user_device", tmp);
                    deviceList.remove(deviceList.get(position));

                    HashSet<Device> tmpSet = new HashSet<>(deviceList);

                    for (Device d : tmpSet){
                        Log.e(TAG, "tmpSet: --> " + d.getDeviceAddress());
                        Log.e(TAG, "tmpSet: --> " + d.getDeviceName());
                    }

                    Paper.book("device").delete("user_device");
                    Paper.book("device").write("user_device", tmpSet);

                    notifyDataSetChanged();

                }
            });
            builder.show();

        });

//        holder.findDevice.setVisibility(device.isInitialized() ? View.VISIBLE : View.GONE);
//        holder.findDevice.setOnClickListener(new View.OnClickListener()
//
//                                             {
//                                                 @Override
//                                                 public void onClick(View v) {
//                                                     if (device.getType() == DeviceType.VIBRATISSIMO) {
//                                                         Intent startIntent;
//                                                         startIntent = new Intent(context, VibrationActivity.class);
//                                                         startIntent.putExtra(GBDevice.EXTRA_DEVICE, device);
//                                                         context.startActivity(startIntent);
//                                                         return;
//                                                     }
//                                                     GBApplication.deviceService().onFindDevice(true);
//                                                     //TODO: extract string resource if we like this solution.
//                                                     Snackbar.make(parent, R.string.control_center_find_lost_device, Snackbar.LENGTH_INDEFINITE).setAction("Found it!", new View.OnClickListener() {
//                                                         @Override
//                                                         public void onClick(View v) {
//                                                             GBApplication.deviceService().onFindDevice(false);
//                                                         }
//                                                     }).setCallback(new Snackbar.Callback() {
//                                                         @Override
//                                                         public void onDismissed(Snackbar snackbar, int event) {
//                                                             GBApplication.deviceService().onFindDevice(false);
//                                                             super.onDismissed(snackbar, event);
//                                                         }
//                                                     }).show();
////                                                     ProgressDialog.show(
////                                                             context,
////                                                             context.getString(R.string.control_center_find_lost_device),
////                                                             context.getString(R.string.control_center_cancel_to_stop_vibration),
////                                                             true, true,
////                                                             new DialogInterface.OnCancelListener() {
////                                                                 @Override
////                                                                 public void onCancel(DialogInterface dialog) {
////                                                                     GBApplication.deviceService().onFindDevice(false);
////                                                                 }
////                                                             });
//                                                 }
//                                             }
//
//        );

//        //remove device, hidden under details
//        holder.removeDevice.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//                new AlertDialog.Builder(context)
//                        .setCancelable(true)
//                        .setTitle(context.getString(R.string.controlcenter_delete_device_name, device.getName()))
//                        .setMessage(R.string.controlcenter_delete_device_dialogmessage)
//                        .setPositiveButton(R.string.Delete, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                try {
//                                    DeviceCoordinator coordinator = DeviceHelper.getInstance().getCoordinator(device);
//                                    if (coordinator != null) {
//                                        coordinator.deleteDevice(device);
//                                    }
//                                    DeviceHelper.getInstance().removeBond(device);
//                                } catch (Exception ex) {
//                                    GB.toast(context, "Error deleting device: " + ex.getMessage(), Toast.LENGTH_LONG, GB.ERROR, ex);
//                                } finally {
//                                    Intent refreshIntent = new Intent(DeviceManager.ACTION_REFRESH_DEVICELIST);
//                                    LocalBroadcastManager.getInstance(context).sendBroadcast(refreshIntent);
//                                }
//                            }
//                        })
//                        .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do nothing
//                            }
//                        })
//                        .show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView container;

        ImageView deviceImageView;
        TextView deviceNameLabel;
        TextView deviceStatusLabel;

        //actions
        LinearLayout batteryStatusBox;
        TextView batteryStatusLabel;
        ImageView batteryIcon;
        LinearLayout fetchActivityDataBox;
        ImageView fetchActivityData;
        ProgressBar busyIndicator;
        ImageView takeScreenshotView;
        ImageView manageAppsView;
        ImageView setAlarmsView;
        ImageView showActivityGraphs;
        ImageView showActivityTracks;

        ImageView deviceInfoView;
        ImageView deviceRemove;

        //overflow
        ListView deviceInfoList;
        ImageView findDevice;
        ImageView removeDevice;

        ViewHolder(View view) {
            super(view);
            container = view.findViewById(R.id.card_view);

            deviceImageView = view.findViewById(R.id.device_image);
            deviceNameLabel = view.findViewById(R.id.device_name);
            deviceStatusLabel = view.findViewById(R.id.device_status);

            //actions
            fetchActivityData = view.findViewById(R.id.device_action_fetch_activity);
            showActivityTracks = view.findViewById(R.id.device_action_show_activity_tracks);
            deviceInfoView = view.findViewById(R.id.device_info_image);

            deviceRemove = view.findViewById(R.id.device_info_trashcan);

        }
    }

}
