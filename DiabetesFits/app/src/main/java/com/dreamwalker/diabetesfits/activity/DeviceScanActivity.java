package com.dreamwalker.diabetesfits.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.DeviceScanAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceScanActivity extends AppCompatActivity {

    private static final String TAG = "DeviceScanActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1000;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000; // Stops scanning after 10 seconds.

    @BindView(R.id.animation_view)
    LottieAnimationView animationView;

    @BindView(R.id.textView)
    TextView skipTextView;
    @BindView(R.id.button)
    Button StateButton;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    DeviceScanAdapter adapter;
    ArrayList<BluetoothDevice> bleDeviceList;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;

    Handler handler;

    boolean mScanning;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_scan);
//
//        SharedPreferences preferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
//        if (preferences.getBoolean("activity_init_scan", false)){
//            Intent intent = new Intent(this, HomeActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
        preferences = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        bleDeviceList = new ArrayList<>();
        handler = new Handler();

        setStatusBar();
        viewBinding();
        checkScanPermission();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        checkBleSupport();
        getBluetoothAdapter();
        checkBluetoothSupport();


    }

    private void viewBinding() {
        ButterKnife.bind(this);
    }


    private void checkBleSupport() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void getBluetoothAdapter() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    private void checkBluetoothSupport() {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkScanPermission() {
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.permission_alert_title));
            builder.setMessage(getString(R.string.permission_alert_message));
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION));
            builder.show();
        }
    }


    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.device_scan_background));
    }


    private void scanLeDevice(final boolean enable) {
        if (!bluetoothAdapter.isEnabled()) { // TODO: 2018-10-06 블루투스 오류 처리 하기

            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

        } else {

            if (enable) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        bluetoothLeScanner.stopScan(leScanCallback);
                        StateButton.setText("SCAN");
                        animationView.cancelAnimation();
                        animationView.setFrame(0);
                    }
                }, SCAN_PERIOD);
                mScanning = true;
                startNEWBTLEDiscovery();
                //bluetoothLeScanner.startScan(leScanCallback);
            } else {
                mScanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);
            }

        }


    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();

            // TODO: 2018-07-21 장비가 중복되어 리스트에 추가되는 현상을 막아줍니다. - 박제창
            if (bleDeviceList.size() < 1) {
                bleDeviceList.add(device);
                adapter.notifyDataSetChanged();
            } else {
                boolean flag = true;
                for (int i = 0; i < bleDeviceList.size(); i++) {
                    if (device.getAddress().equals(bleDeviceList.get(i).getAddress())) {
                        flag = false;
                    }
                }
                if (flag) {
                    bleDeviceList.add(device);
                    adapter.notifyDataSetChanged();
                }
            }

            //String address = device.getAddress();
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

    @OnClick(R.id.textView)
    public void onClickedSkipButton() {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("activity_executed", true);
        editor.apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @OnClick(R.id.button)
    public void onClickScanButton() {

        if (!mScanning) {
            StateButton.setText("STOP");
            bleDeviceList.clear();
            adapter.notifyDataSetChanged();
            animationView.playAnimation();
            scanLeDevice(true);

        } else {
            animationView.cancelAnimation();
            animationView.setProgress(0);
            StateButton.setText("SCAN");
            scanLeDevice(false);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO: 2018-07-21 스캔 동작하기 -- 박제창 (Dreamwalker)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, " +
                            "this app will not be able to discover BLE Device when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> finish());
                    builder.show();
                }

                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            adapter = new DeviceScanAdapter(bleDeviceList, this);
            recyclerView.setAdapter(adapter);
            scanLeDevice(true);
            StateButton.setText("STOP");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        bleDeviceList.clear();
    }

    private void stopBLEDiscovery() {
        if (adapter != null)
            bluetoothLeScanner.stopScan(leScanCallback);
    }

    // New BTLE Discovery use startScan (List<ScanFilter> filters,
    //                                  ScanSettings settings,
    //                                  ScanCallback callback)
    // It's added on API21
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startNEWBTLEDiscovery() {
        // Only use new API when user uses Lollipop+ device
        if (bluetoothLeScanner == null) {
            getBluetoothAdapter();
        } else {
            bluetoothLeScanner.startScan(getScanFilters(), getScanSettings(), leScanCallback);
        }

    }

    private List<ScanFilter> getScanFilters() {
        List<ScanFilter> allFilters = new ArrayList<>();
        ScanFilter scanFilter0 = new ScanFilter.Builder().setDeviceName("KNU EG0").build();
        allFilters.add(scanFilter0);
//        ScanFilter scanFilter1 = new ScanFilter.Builder().setDeviceName("").build();
//        for (DeviceCoordinator coordinator : DeviceHelper.getInstance().getAllCoordinators()) {
//            allFilters.addAll(coordinator.createBLEScanFilters());
//        }
        return allFilters;
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanSettings getScanSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new ScanSettings.Builder()
                    .setScanMode(android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setMatchMode(android.bluetooth.le.ScanSettings.MATCH_MODE_STICKY)
                    .build();
        } else {
            return new ScanSettings.Builder()
                    .setScanMode(android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
        }
    }



}
