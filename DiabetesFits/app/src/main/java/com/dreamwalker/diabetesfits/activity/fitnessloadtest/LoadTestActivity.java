package com.dreamwalker.diabetesfits.activity.fitnessloadtest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.IActivityBasicSetting;
import com.dreamwalker.diabetesfits.consts.IntentConst;
import com.dreamwalker.gaugeview.GaugeView;

import java.util.Random;

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

    CountDownTimer timer;
    MediaPlayer mediaPlayer;

    String deviceAddress, deviceName;

    float aimSpeed = 20.0f;
    int testStage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_test);
        deviceAddress = getIntent().getStringExtra(IntentConst.FITNESS_LOAD_TEST_DEVICE_ADDRESS);
        Log.e(TAG, "onCreate: " + deviceAddress);
        initSetting();

        aimSpeedTextView.setText(String.valueOf(aimSpeed) + " km/h");
        stageTextView.setText("Stage : " + String.valueOf(testStage));

        mediaPlayer = MediaPlayer.create(this, R.raw.load_test_01);

        timer = new CountDownTimer(SET_2_MINUTE, 1000) {

            public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
                gaugeView.setTargetValue(new Random().nextInt(101));
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
        }.start();

        countdownView.start(SET_2_MINUTE);
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
        super.onDestroy();
    }
}
