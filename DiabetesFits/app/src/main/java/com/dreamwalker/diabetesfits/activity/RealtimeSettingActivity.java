package com.dreamwalker.diabetesfits.activity;

import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.consts.IntentConst;
import com.dreamwalker.diabetesfits.widget.CircleTimerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RealtimeSettingActivity extends AppCompatActivity implements IActivityBasicSetting, CircleTimerView.CircleTimerListener {

    private static final String TAG = "RealtimeSettingActivity";

    String deviceAddress;

    @BindView(R.id.start_fitness_button)
    MaterialButton startFitnessButton;

    @BindView(R.id.chip_group)
    ChipGroup chipGroup;

    @BindView(R.id.ctv)
    CircleTimerView circleTimerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_setting);
        initSetting();

        deviceAddress = getIntent().getStringExtra(IntentConst.REAL_TIME_INDOOR_BIKE_DEVICE);


        chipGroup.setOnCheckedChangeListener((chipGroup, i) -> {
            Log.e(TAG, "onCheckedChanged: " + i);
            Chip chip = chipGroup.findViewById(i);
            Log.e(TAG, "onCheckedChanged: " + chipGroup.getCheckedChipId());
            Log.e(TAG, "onCheckedChanged: " + chip.getText());
        });

        circleTimerView.setCurrentTime(1800);


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
    public void onTimerStop() {

    }

    @Override
    public void onTimerStart(int time) {
        Log.e(TAG, "onTimerStart: " + time);
    }

    @Override
    public void onTimerPause(int time) {
        Log.e(TAG, "onTimerPause: " + time);
    }

    @Override
    public void onTimerTimingValueChanged(int time) {
        Log.e(TAG, "onTimerTimingValueChanged: " + time);
    }

    @Override
    public void onTimerSetValueChanged(int time) {
        Log.e(TAG, "onTimerSetValueChanged: " + time);
    }

    @Override
    public void onTimerSetValueChange(int time) {
        Log.e(TAG, "onTimerSetValueChange: " + time);
    }
}
