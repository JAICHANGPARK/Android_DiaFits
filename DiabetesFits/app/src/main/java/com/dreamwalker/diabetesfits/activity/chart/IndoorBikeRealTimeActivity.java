package com.dreamwalker.diabetesfits.activity.chart;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.service.knu.egzero.EZBLEService;
import com.dreamwalker.waveviewlib.WaveView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dreamwalker.diabetesfits.consts.IntentConst.REAL_TIME_INDOOR_BIKE_DEVICE;

public class IndoorBikeRealTimeActivity extends AppCompatActivity {
    private static final String TAG = "IndoorBikeRealTimeActiv";

    BluetoothGattCharacteristic mNotifyCharacteristic;
    EZBLEService mBluetoothLeService;

    String mDeviceAddress;
    private boolean mConnected = false;

    @BindView(R.id.text_view)
    TextView textView;

    @BindView(R.id.wave_view)
    WaveView customWaveView;

    @BindView(R.id.textView3)
    TextView nowSpeedTextView;

    @BindView(R.id.textView9)
    TextView totalDistanceTextView;

    @BindView(R.id.textView13)
    TextView heartRateTextView;

    @BindView(R.id.chronometer)
    Chronometer chronometer;

    private boolean startIndicator = false;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((EZBLEService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
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

                chronometer.stop();
                //updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                //clearUI();
            } else if (EZBLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                // TODO: 2018-07-24 서비스와 연결되엉ㅅ을때 방송되어 받아지는 리시버 - 박제창
                startIndicator = true;
                //chronometer.start();
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (EZBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e(TAG, "onReceive: " + intent.getStringExtra(EZBLEService.EXTRA_DATA));
                displayData(intent.getStringExtra(EZBLEService.EXTRA_DATA));
            } else if (EZBLEService.ACTION_HEART_RATE_AVAILABLE.equals(action)) {
                Log.e(TAG, "onReceive:  실시간 화면에서 심박수 받앗어요 " );
                String hr = intent.getStringExtra(EZBLEService.EXTRA_DATA);
                heartRateTextView.setText(hr);
            } else if (EZBLEService.ACTION_INDOOR_BIKE_AVAILABLE.equals(action)) {

                String nowSpeed = intent.getStringExtra(EZBLEService.EXTRA_DATA);
                nowSpeedTextView.setText(nowSpeed);
            } else if (EZBLEService.ACTION_TREADMILL_AVAILABLE.equals(action)) {

                String totalDistance = intent.getStringExtra(EZBLEService.EXTRA_DATA);
                totalDistanceTextView.setText(totalDistance);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indoor_bike_real_time);

        ButterKnife.bind(this);
        // TODO: 2018-07-24 폰트 설정
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/grobold.ttf");
        chronometer.setTypeface(font, Typeface.NORMAL);
        chronometer.setTextSize(80);

        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2, -2);
        lp.gravity = Gravity.BOTTOM | Gravity.CENTER;

        mDeviceAddress = getIntent().getStringExtra(REAL_TIME_INDOOR_BIKE_DEVICE);

        Intent gattServiceIntent = new Intent(this, EZBLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        chronometer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

        chronometer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        chronometer.stop();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void displayData(String data) {
        if (data != null) {
            textView.append(data);
        }
    }

}
