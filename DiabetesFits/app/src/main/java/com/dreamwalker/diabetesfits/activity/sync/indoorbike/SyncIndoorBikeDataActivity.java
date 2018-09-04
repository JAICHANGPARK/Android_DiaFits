package com.dreamwalker.diabetesfits.activity.sync.indoorbike;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.service.knu.egzero.EZBLEService;
import com.dreamwalker.diabetesfits.service.knu.egzero.EZSyncService;

import butterknife.ButterKnife;
import io.paperdb.Paper;

import static com.dreamwalker.diabetesfits.consts.IntentConst.SYNC_INDOOR_BIKE_DEVICE;

public class SyncIndoorBikeDataActivity extends AppCompatActivity {

    private static final String TAG = "SyncIndoorBikeDataActiv";


    BluetoothGattCharacteristic mNotifyCharacteristic;
    EZSyncService mBluetoothLeService;
    boolean mConnected = false;

    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;

    private BluetoothGattCharacteristic mDateTimeSyncCharacteristic;  //시간 동기화 특성
    private BluetoothGattCharacteristic mDateTimeCharacteristic;   // 아직 정해진것 없음
    private BluetoothGattCharacteristic mAuthCharacteristic;  // 인증 특성
    private BluetoothGattCharacteristic mDataContextCharacteristic;   // 데이터 컨텍스트
    private BluetoothGattCharacteristic mDataSyncCharacteristic;   // 데이터 전송

    private void initCharacteristics() {
        mDateTimeSyncCharacteristic = null;
        mDateTimeCharacteristic = null;
        mAuthCharacteristic = null;
        mDataContextCharacteristic = null;
        mDataSyncCharacteristic = null;
    }
//    private BluetoothGattCharacteristic mDeviceSoftwareRevisionCharacteristic;
//    private BluetoothGattCharacteristic mCustomTimeCharacteristic;

    private Handler mHandler;
    private boolean _isScanning = false;
    private boolean bolBroacastRegistred;
    private boolean bondingCheckFlag = false;

    String deviceAddress;


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((EZSyncService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EZBLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(EZBLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(EZBLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(EZBLEService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(EZBLEService.ACTION_HEART_RATE_AVAILABLE);
        intentFilter.addAction(EZBLEService.ACTION_INDOOR_BIKE_AVAILABLE);
        intentFilter.addAction(EZBLEService.ACTION_TREADMILL_AVAILABLE);
        return intentFilter;
    }

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (EZBLEService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                //updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (EZBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                //updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                //clearUI();
            } else if (EZBLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                // TODO: 2018-07-24 서비스와 연결되엉ㅅ을때 방송되어 받아지는 리시버 - 박제창
//                startIndicator = true;
                //chronometer.start();
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (EZBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e(TAG, "onReceive: " + intent.getStringExtra(EZBLEService.EXTRA_DATA));
//                displayData(intent.getStringExtra(EZBLEService.EXTRA_DATA));
            } else if (EZBLEService.ACTION_HEART_RATE_AVAILABLE.equals(action)) {
                Log.e(TAG, "onReceive:  실시간 화면에서 심박수 받앗어요 ");
                String hr = intent.getStringExtra(EZBLEService.EXTRA_DATA);
//                heartRateTextView.setText(hr);
            } else if (EZBLEService.ACTION_INDOOR_BIKE_AVAILABLE.equals(action)) {

                String nowSpeed = intent.getStringExtra(EZBLEService.EXTRA_DATA);
//                nowSpeedTextView.setText(nowSpeed);
            } else if (EZBLEService.ACTION_TREADMILL_AVAILABLE.equals(action)) {

                String totalDistance = intent.getStringExtra(EZBLEService.EXTRA_DATA);
//                totalDistanceTextView.setText(totalDistance);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_indoor_bike_data);

        Paper.init(this);
        ButterKnife.bind(this);

        deviceAddress = getIntent().getStringExtra(SYNC_INDOOR_BIKE_DEVICE);
        Log.e(TAG, "onCreate: " + deviceAddress);


        Intent gattServiceIntent = new Intent(this, EZSyncService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(deviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
}
