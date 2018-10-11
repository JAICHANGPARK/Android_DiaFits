package com.dreamwalker.diabetesfits.activity.fitnessloadtest;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.IActivityBasicSetting;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;

public class BikeScanActivity extends AppCompatActivity implements IActivityBasicSetting {
    private static final String TAG = "BikeScanActivity";

    @BindView(R.id.cv_countdownView)
    CountdownView countdownView;

    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_scan);
        initSetting();

        timer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timer.start();
                countdownView.start(30000);
//                mTextField.setText("done!");
            }
        }.start();
        countdownView.start(30000);

    }

    @Override
    public void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initSetting() {
        bindView();
    }
}
