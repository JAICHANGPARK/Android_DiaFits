package com.dreamwalker.diabetesfits.activity.fitnessloadtest;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.IActivityBasicSetting;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;

public class LoadTestActivity extends AppCompatActivity  implements IActivityBasicSetting {

    private static final String TAG = "LoadTestActivity";

    final static int SET_2_MINUTE = 120000;


    @BindView(R.id.cv_countdownView)
    CountdownView countdownView;

    CountDownTimer timer;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_test);
        initSetting();

        mediaPlayer = MediaPlayer.create(this,  R.raw.load_test_01);

        timer = new CountDownTimer(SET_2_MINUTE, 1000) {

            public void onTick(long millisUntilFinished) {
//                mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
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
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
