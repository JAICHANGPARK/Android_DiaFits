package com.dreamwalker.diabetesfits.fragment.isens;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.isens.DeviceListAdapter;
import com.dreamwalker.diabetesfits.consts.isens.PremierNConst;
import com.dreamwalker.diabetesfits.device.isens.ScannerServiceParser;
import com.dreamwalker.diabetesfits.model.isens.ExtendedDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by isens on 2015. 10. 13..
 */
public class ScannerFragment extends DialogFragment{

    private BluetoothAdapter mBluetoothAdapter;
    private DeviceListAdapter mAdapter;
    private Handler mHandler = new Handler();
    private TextView mTxtCancel;
    private ImageView mImgCancel;
    private ProgressBar mLoading;

    private boolean mIsScanning = false;

    private ScanCallback scanCallback;

    private static final boolean DEVICE_IS_BONDED = true;
    private static final boolean DEVICE_NOT_BONDED = false;
    /* package */public static final int NO_RSSI = -1000;
    public final static int BLE_SCAN_DURATION = 5000;


    public static ScannerFragment getInstance(Context context) {
        final ScannerFragment fragment = new ScannerFragment();
        return fragment;
    }

    public interface OnDismissDialog {
        public void onDismiss(DialogInterface dialog);
    }

    /**
     * Interface required to be implemented by activity
     */
    public static interface OnDeviceSelectedListener {
        /**
         * Fired when user selected the device
         *
         * @param device
         *            the device to connect to
         */
        public void onDeviceSelected(BluetoothDevice device);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final BluetoothManager manager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = manager.getAdapter();
    }

    @Override
    public void onDestroyView() {
        stopScan();
        super.onDestroyView();
    }

    /**
     * When dialog is created then set AlertDialog with list and button views
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        dialog.setContentView(R.layout.fragment_device_selection);

        dialog.setCancelable(false);
        final ListView listview = (ListView) dialog.findViewById(android.R.id.list);

        listview.setEmptyView(dialog.findViewById(android.R.id.empty));
        listview.setAdapter(mAdapter = new DeviceListAdapter(getActivity()));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                stopScan();
                dialog.cancel();
//				mListener.onDeviceSelected(((ExtendedBluetoothDevice) mAdapter.getItem(position)).device);
                final Intent i = new Intent(PremierNConst.INTENT_BLE_CONNECTED_DEVICE);
                i.putExtra(PremierNConst.INTENT_BLE_EXTRA_DATA, ((ExtendedDevice) mAdapter.getItem(position)).device.toString());
                getActivity().sendBroadcast(i);
            }
        });

        dialog.findViewById(R.id.action_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsScanning) {
                    stopScan();
                } else {
                    startScan();
                }
            }
        });

        dialog.findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dismiss();
            }

        });

        mImgCancel = (ImageView) dialog.findViewById(R.id.img_cancel);
        mTxtCancel = (TextView) dialog.findViewById(R.id.txt_cancel);
        mLoading = (ProgressBar) dialog.findViewById(R.id.loading);

        addBondedDevices();
        if (savedInstanceState == null)
            startScan();

        return dialog;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initCallbackLollipop(){
        if(scanCallback != null) return;
        this.scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                //Do whatever you want

                if (result!= null) {
                    updateScannedDevice(result.getDevice(), result.getRssi());
                    try {
                        if (ScannerServiceParser.decodeDeviceAdvData(result.getScanRecord().getBytes())) {
                            // On some devices device.getName() is always null. We have to parse the name manually :(
                            // This bug has been found on Sony Xperia Z1 (C6903) with Android 4.3.
                            // https://devzone.nordicsemi.com/index.php/cannot-see-device-name-in-sony-z1
                            addScannedDevice(result.getDevice(), result.getRssi(), DEVICE_NOT_BONDED);
                        }
                    } catch (Exception e) {
//						DebugLogger.e(TAG, "Invalid data in Advertisement packet " + e.toString());
                    }
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);

                for (ScanResult result: results){

                    //Do whatever you want
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);

            }
        };
    }

    /**
     * Scan for 5 seconds and then stop scanning when a BluetoothLE device is found then mLEScanCallback is activated This will perform regular scan for custom BLE Service UUID and then filter out
     * using class ScannerServiceParser
     */
    private void startScan() {
        mLoading.setVisibility(View.VISIBLE);

        mAdapter.clearDevices();
        mImgCancel.setImageResource(R.drawable.btn_stop);
        mTxtCancel.setText(R.string.STOP);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(scanCallback == null)
                initCallbackLollipop();

            List<ScanFilter> filters = new ArrayList<ScanFilter>();

            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // or BALANCED previously
                    .setReportDelay(0)
                    .build();

            //[2016.06.10][leenain] clear scan callback
            mBluetoothAdapter.getBluetoothLeScanner().flushPendingScanResults(scanCallback);
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, settings, scanCallback);
        }
        else {
            mBluetoothAdapter.startLeScan(mLEScanCallback);
        }
//        mIsScanning = true;
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mIsScanning) {
//                    stopScan();
//                }
//            }
//        }, Const.BLE_SCAN_DURATION);
    }

    /**
     * Stop scan if user tap Cancel button
     */
    private void stopScan() {
        mLoading.setVisibility(View.GONE);
        if (mIsScanning) {
            mImgCancel.setImageResource(R.drawable.icon_ble);
            mTxtCancel.setText(R.string.SEARCH);
//			mBluetoothAdapter.stopLeScan(mLEScanCallback);
            try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mBluetoothAdapter != null) {
                    // Stop scan and flush pending scan
                    mBluetoothAdapter.getBluetoothLeScanner().flushPendingScanResults(scanCallback);
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                }else{
                    mBluetoothAdapter.stopLeScan(mLEScanCallback);
                }
            }catch (NullPointerException e) {
//                Util.log("addScannedDevice " + e.toString());
            }
            catch (Exception e) {
//                Util.log("addScannedDevice " + e.toString());
            }
            mIsScanning = false;
        }

    }

    private void addBondedDevices() {
        final Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            mAdapter.addBondedDevice(new ExtendedDevice(device, NO_RSSI, DEVICE_IS_BONDED));
        }
    }

    /**
     * if scanned device already in the list then update it otherwise add as a new device
     */
    private void addScannedDevice(final BluetoothDevice device, final int rssi, final boolean isBonded) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addOrUpdateDevice(new ExtendedDevice(device, rssi, isBonded));
                }
            });
        }catch (NullPointerException e) {
//            Util.log("addScannedDevice " + e.toString());
        }
        catch (Exception e) {
//            Util.log("addScannedDevice " + e.toString());
        }
    }

    /**
     * if scanned device already in the list then update it otherwise add as a new device
     */
    private void updateScannedDevice(final BluetoothDevice device, final int rssi) {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateRssiOfBondedDevice(device.getAddress(), rssi);
                }
            });
        }catch (NullPointerException e) {
//            Util.log("addScannedDevice " + e.toString());
        }
        catch (Exception e) {
//            Util.log("addScannedDevice " + e.toString());
        }
    }


    /**
     * Callback for scanned devices class {@link ScannerServiceParser} will be used to filter devices with custom BLE service UUID then the device will be added in a list
     */
    private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null) {
                updateScannedDevice(device, rssi);
                try {
                    if (ScannerServiceParser.decodeDeviceAdvData(scanRecord)) {
                        // On some devices device.getName() is always null. We have to parse the name manually :(
                        // This bug has been found on Sony Xperia Z1 (C6903) with Android 4.3.
                        // https://devzone.nordicsemi.com/index.php/cannot-see-device-name-in-sony-z1
                        addScannedDevice(device, rssi, DEVICE_NOT_BONDED);
                    }
                } catch (Exception e) {
//					DebugLogger.e(TAG, "Invalid data in Advertisement packet " + e.toString());
                }
            }
        }
    };

}
