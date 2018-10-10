package com.dreamwalker.diabetesfits.activity;

import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.chip.ChipGroup;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.consts.IntentConst;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RealtimeSettingActivity extends AppCompatActivity  implements IActivityBasicSetting{

    private static final String TAG = "RealtimeSettingActivity";

    String deviceAddress;

    @BindView(R.id.start_fitness_button)
    MaterialButton startFitnessButton;
    @BindView(R.id.chip_group)
    ChipGroup chipGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_setting);
        initSetting();

        deviceAddress = getIntent().getStringExtra(IntentConst.REAL_TIME_INDOOR_BIKE_DEVICE);

        chipGroup.setOnCheckedChangeListener((chipGroup, i) -> Log.e(TAG, "onCheckedChanged: " + i ));



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
