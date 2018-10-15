package com.dreamwalker.diabetesfits.activity.fitnessloadtest;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.IActivityBasicSetting;
import com.dreamwalker.diabetesfits.consts.IntentConst;
import com.dreamwalker.diabetesfits.service.knu.egzero.EZBLEService;
import com.dreamwalker.gaugeview.GaugeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;

public class LoadTestActivity extends AppCompatActivity implements IActivityBasicSetting {

    private static final String TAG = "LoadTestActivity";

    final static int SET_2_MINUTE = 120000;

    @BindView(R.id.cv_countdownView)
    CountdownView countdownView;


    @BindView(R.id.gauge_view)
    GaugeView gaugeView;

    @BindView(R.id.aim_speed_text_view)
    TextView aimSpeedTextView;

    @BindView(R.id.stage_text_view)
    TextView stageTextView;

    @BindView(R.id.guide_text_view)
    TextView guideTextView;

    @BindView(R.id.heart_rate_text_view)
    TextView heartRateTextView;
    @BindView(R.id.speed_text_view)
    TextView speedTextView;

    CountDownTimer timer;
    MediaPlayer mediaPlayer;

    String deviceAddress, deviceName;

    float aimSpeed = 20.0f;
    int testStage = 1;

    BluetoothGattCharacteristic mNotifyCharacteristic;
    EZBLEService mBluetoothLeService;
    private boolean mConnected = false;

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

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (EZBLEService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                //updateConnectionState(R.string.connected);
//                invalidateOptionsMenu();
//                emptyLayout.setVisibility(View.GONE);
//                infoLayout.setVisibility(View.VISIBLE);
//                countdownView.setVisibility(View.VISIBLE);

            } else if (EZBLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
//                chronometer.stop();
//                countdownView.pause();
//                //updateConnectionState(R.string.disconnected);
//                invalidateOptionsMenu();
//                emptyLayout.setVisibility(View.VISIBLE);
//                infoLayout.setVisibility(View.GONE);
//                countdownView.setVisibility(View.GONE);
//
//                firstStartFlag = true;
                //clearUI();
            } else if (EZBLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                timer.start();
                countdownView.start(SET_2_MINUTE);

                // Show all the supported services and characteristics on the user interface.
                // TODO: 2018-07-24 서비스와 연결되엉ㅅ을때 방송되어 받아지는 리시버 - 박제창
//                countdownView.start(workoutTime * 1000);

//                startIndicator = true;
//                chronometer.start();
//
//                // TODO: 2018-10-10 처음 시작한다. --> 장비와 연결이 종료되었다. --> 타이머를 잠시 정지한다. --> 장비가 연결되면 다시 시작한다.
//                if (firstStartFlag){
//                    countdownView.restart();
//                    firstStartFlag = false;
//                }
                //chronometer.start();
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (EZBLEService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.e(TAG, "onReceive: " + intent.getStringExtra(EZBLEService.EXTRA_DATA));
//                displayData(intent.getStringExtra(EZBLEService.EXTRA_DATA));
            } else if (EZBLEService.ACTION_HEART_RATE_AVAILABLE.equals(action)) {
                Log.e(TAG, "onReceive:  실시간 화면에서 심박수 받앗어요 ");
                String hr = intent.getStringExtra(EZBLEService.EXTRA_DATA);
                heartRateTextView.setText(hr);
//                setLineChartData(hr);

//                float userHR = Float.parseFloat(hr);
//                if (userHR > 0.0f && userHR < userMinHeartRate) {
//                    String msg = "운동강도를 올릴 필요가 있습니다.";
//                    userStateMsgTextView.setText(msg);
////                    customWaveView.setmBlowWaveColor(ContextCompat.getColor(IndoorBikeRealTimeActivity.this, R.color.low_stat));
//                    customWaveView.setmAboveWaveColor(ContextCompat.getColor(IndoorBikeRealTimeActivity.this, R.color.low_stat));
//                    customWaveView.setBackgroundColor(ContextCompat.getColor(IndoorBikeRealTimeActivity.this, R.color.low_stat));
//                } else if (userHR >= userMinHeartRate && userHR < userMaxHeartRate) {
//                    String msg = "적절한 운동강도로 운동중입니다.";
//                    userStateMsgTextView.setText(msg);
////                    customWaveView.setmBlowWaveColor(ContextCompat.getColor(IndoorBikeRealTimeActivity.this, R.color.shopAccent));
//                    customWaveView.setmAboveWaveColor(ContextCompat.getColor(IndoorBikeRealTimeActivity.this, R.color.shopAccent));
//                    customWaveView.setBackgroundColor(ContextCompat.getColor(IndoorBikeRealTimeActivity.this, R.color.shopAccent));
//                } else if (userHR >= userMaxHeartRate) {
//                    String msg = "운동강도가 초과됬습니다. 속도를 낮추고나 W를 낮춰주세요";
//                    userStateMsgTextView.setText(msg);
////                    customWaveView.setmBlowWaveColor(ContextCompat.getColor(IndoorBikeRealTimeActivity.this, R.color.bsp_red));
//                    customWaveView.setmAboveWaveColor(ContextCompat.getColor(IndoorBikeRealTimeActivity.this, R.color.bsp_red));
//                    customWaveView.setBackgroundColor(ContextCompat.getColor(IndoorBikeRealTimeActivity.this, R.color.bsp_red));
//                } else if (userHR == 0.0f) {
//                    String msg = "심박센서 착용 및 위치 확인해주세요.";
//                    userStateMsgTextView.setText(msg);
//                }

            } else if (EZBLEService.ACTION_INDOOR_BIKE_AVAILABLE.equals(action)) {

                String nowSpeed = intent.getStringExtra(EZBLEService.EXTRA_DATA);
                speedTextView.setText(nowSpeed);
                gaugeView.setTargetValue(Float.parseFloat(nowSpeed));
//                if (!nowSpeed.equals("0.00")) {
//                    globalKCal += countSpeed(nowSpeed);
//                    String tmp = String.format("%3.2f", globalKCal);
//                    String msg = tmp + "kcal";
//                    kcalTextview.setText(msg);
//                    sumSpeed += Float.parseFloat(nowSpeed);
//                    float meanSpeed = sumSpeed / speedCount;
//                    String meanSpeedMsg = String.format("%2.1f", meanSpeed);
//                    meanSpeedTextView.setText(meanSpeedMsg);
//                    speedCount++;
//                }
            } else if (EZBLEService.ACTION_TREADMILL_AVAILABLE.equals(action)) {
                String totalDistance = intent.getStringExtra(EZBLEService.EXTRA_DATA);
//                totalDistanceTextView.setText(totalDistance);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_test);
        deviceAddress = getIntent().getStringExtra(IntentConst.FITNESS_LOAD_TEST_DEVICE_ADDRESS);
        Log.e(TAG, "onCreate: " + deviceAddress);
        initSetting();

        mediaPlayer = MediaPlayer.create(this, R.raw.load_test_01);
        aimSpeedTextView.setText(String.valueOf(aimSpeed) + " km/h");
        stageTextView.setText("Stage : " + String.valueOf(testStage));


        timer = new CountDownTimer(SET_2_MINUTE, 1000) {

            public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
//                gaugeView.setTargetValue(new Random().nextInt(101));
            }

            public void onFinish() {
                aimSpeed += 2.0;
                testStage += 1;
                aimSpeedTextView.setText(String.valueOf(aimSpeed) + " km/h");
                stageTextView.setText("Stage : " + String.valueOf(testStage));

                timer.start();
                countdownView.start(SET_2_MINUTE);
                mediaPlayer.start();
//                mTextField.setText("done!");
            }
        };

        Intent gattServiceIntent = new Intent(this, EZBLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

//        countdownView.start(SET_2_MINUTE);
    }

    @Override
    public void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initSetting() {
        bindView();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unbindService(mServiceConnection);
        mBluetoothLeService = null;

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(deviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
//        chronometer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
        countdownView.stop();
        unregisterReceiver(mGattUpdateReceiver);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("검사를 종료하시겠어요?");
        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        builder.show();
//        super.onBackPressed();
    }
}
