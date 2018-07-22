package com.dreamwalker.diabetesfits.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.isens.DeviceSearchAdapter;
import com.dreamwalker.diabetesfits.device.isens.ScannerServiceParser;
import com.dreamwalker.diabetesfits.model.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BSMScanActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 2;
    private static final int SCAN_PERIOD = 60000;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private ScanCallback scanCallback;

    private boolean _isScanning = false;
    private Handler mHandler;



    ArrayList<Device> bonedList = new ArrayList<>();
    ArrayList<Device> scanList = new ArrayList<>();

    @BindView(R.id.bonded_recycler_view)
    RecyclerView bondRecyclerView;
    @BindView(R.id.scan_recycler_view)
    RecyclerView scanRecyclerView;

    DeviceSearchAdapter bondAdapter;
    DeviceSearchAdapter scanAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsmscan);
        ButterKnife.bind(this);

        bondRecyclerView.setHasFixedSize(true);
        bondRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bondRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        scanRecyclerView.setHasFixedSize(true);
        scanRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        scanRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        scanAdapter = new DeviceSearchAdapter(this, scanList);
        scanRecyclerView.setAdapter(scanAdapter);

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.ValidationWarningPopup_31, Toast.LENGTH_SHORT).show();
        }


//        final ScannerFragment dialog = ScannerFragment.getInstance(getApplicationContext());
//        dialog.setCancelable(false);
//        dialog.show(getFragmentManager(), "scan_fragment");
        mHandler = new Handler();
        startScan();
        mHandler.postDelayed(() -> stopScan(), SCAN_PERIOD);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initCallbackLollipop(){
        if(scanCallback != null){
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            this.scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    //Do whatever you want
                    if (result != null) {
                        try {
                            if (ScannerServiceParser.decodeDeviceAdvData(result.getScanRecord().getBytes())) {
                                if (result.getDevice().getBondState() == BluetoothDevice.BOND_BONDED) {

                                    //connect(result.getDevice().toString());
                                } else {

                                    if (scanList.size() < 1){
                                        scanList.add(new Device(result.getDevice().getName(), result.getDevice().getAddress()));
                                        scanAdapter.notifyDataSetChanged();
                                    }
                                    else {
                                        boolean flag = true;
                                        for (int i = 0; i< scanList.size(); i++){
                                            if (result.getDevice().getAddress().equals(scanList.get(i).getDeviceAddress())){
                                                flag = false;
                                            }
                                        }
                                        if (flag){
                                            scanList.add(new Device(result.getDevice().getName(), result.getDevice().getAddress()));
                                            scanAdapter.notifyDataSetChanged();
                                        }
                                    }

                                }
                            }
                        } catch (Exception e) {
//						DebugLogger.e(TAG, "Invalid data in Advertisement packet " + e.toString());
                        }
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }
            };
        }
    }

    private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null) {
                try {
                    if (ScannerServiceParser.decodeDeviceAdvData(scanRecord)) {
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            //connect( device.toString());
                        }
                    }
                } catch (Exception e) {
                }
            }
        }
    };

    public void startScan() {
        if(mBluetoothAdapter.getState() == BluetoothAdapter.STATE_ON) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // TODO: 2018-07-22 롤리팝 이상이면  
                if (scanCallback == null){
                    initCallbackLollipop();
                }
                
                List<ScanFilter> filters = new ArrayList<>();
                
                ScanSettings settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // or BALANCED previously
                        .setReportDelay(0)
                        .build();

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mBluetoothAdapter.getBluetoothLeScanner().flushPendingScanResults(scanCallback);
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                    mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, settings, scanCallback);
                }

            } else {
                // TODO: 2018-07-22 롤리팝 이하 버전이면
                // Samsung Note II with Android 4.3 build JSS15J.N7100XXUEMK9 is not filtering by UUID at all. We have to disable it
                mBluetoothAdapter.startLeScan(mLEScanCallback);
            }
        }
        _isScanning = true;
    }


    @Override
    public void onResume() {
        super.onResume();

        boolean isBleAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if(isBleAvailable && runningOnKitkatOrHigher() && mBluetoothAdapter.isEnabled()) {
            final Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : devices) {
                bonedList.add(new Device(device.getName(), device.getAddress()));
//                    Util.log("####Resume()- Device name:" + device.getName() +", bond status: " + device.getBondState());
                Log.e("####Resume()", "Device name:" + device.getName() +", bond status: " + device.getBondState());
                if(device.getBondState() == BluetoothDevice.BOND_BONDED) {
//                    startScan();
                }
            }
            bondAdapter = new DeviceSearchAdapter(this, bonedList);
            bondRecyclerView.setAdapter(bondAdapter);

        }
    }

    private static boolean runningOnKitkatOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public void stopScan() {
        if (_isScanning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Stop scan and flush pending scan
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
            } else {
                mBluetoothAdapter.stopLeScan(mLEScanCallback);
            }
            _isScanning = false;
        }

    }

    @Override
    protected void onDestroy() {
        stopScan();
        super.onDestroy();
    }
}
