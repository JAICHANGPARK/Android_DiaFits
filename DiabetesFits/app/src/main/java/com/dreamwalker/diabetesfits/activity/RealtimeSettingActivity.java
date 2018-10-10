package com.dreamwalker.diabetesfits.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.chart.IndoorBikeRealTimeActivity;
import com.dreamwalker.diabetesfits.consts.IntentConst;
import com.dreamwalker.diabetesfits.widget.CircleTimerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RealtimeSettingActivity extends AppCompatActivity implements IActivityBasicSetting, CircleTimerView.CircleTimerListener {

    private static final String TAG = "RealtimeSettingActivity";

    String deviceAddress;

    @BindView(R.id.start_fitness_button)
    MaterialButton startFitnessButton;

    @BindView(R.id.chip_group)
    ChipGroup chipGroup;

    @BindView(R.id.ctv)
    CircleTimerView circleTimerView;

    @BindView(R.id.info)
    ImageView infoButton;

    @BindView(R.id.chip_01)
    Chip chip01;

    @BindView(R.id.chip_02)
    Chip chip02;

    @BindView(R.id.chip_03)
    Chip chip03;

    // TODO: 2018-10-10 0 : 저강도, 1: 중강도, 2: 고강도
    // TODO: 2018-10-10 defualt value :  시간: 30분, 중강도
    int workoutTime = 1800;
    int workoutIntense = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime_setting);
        initSetting();

        deviceAddress = getIntent().getStringExtra(IntentConst.REAL_TIME_INDOOR_BIKE_DEVICE);

    }

    private void initChipGroup() {

        chip01.setChipBackgroundColorResource(R.color.white);
        chip02.setChipBackgroundColorResource(R.color.shopAccent);
        chip03.setChipBackgroundColorResource(R.color.white);


        chipGroup.setOnCheckedChangeListener((chipGroup, i) -> {
            Log.e(TAG, "onCheckedChanged: " + i);

            Chip chip = chipGroup.findViewById(i);
            Log.e(TAG, "onCheckedChanged: " + chipGroup.getCheckedChipId());
            if (chip != null) {
                Log.e(TAG, "onCheckedChanged: " + chip.getText());

                String value = chip.getText().toString();

                switch (value) {
                    case "저강도 운동":
                        workoutIntense = 0;
                        chip01.setChipBackgroundColorResource(R.color.shopAccent);
                        chip02.setChipBackgroundColorResource(R.color.white);
                        chip03.setChipBackgroundColorResource(R.color.white);
                        break;
                    case "중강도 운동":
                        workoutIntense = 1;
                        chip01.setChipBackgroundColorResource(R.color.white);
                        chip02.setChipBackgroundColorResource(R.color.shopAccent);
                        chip03.setChipBackgroundColorResource(R.color.white);
                        break;
                    case "고강도 운동":
                        workoutIntense = 2;
                        chip01.setChipBackgroundColorResource(R.color.white);
                        chip02.setChipBackgroundColorResource(R.color.white);
                        chip03.setChipBackgroundColorResource(R.color.shopAccent);
                        break;
                }
            }
        });

    }

    private void initTimerView() {


        circleTimerView.setCurrentTime(1800);
        circleTimerView.setCircleTimerListener(this);

    }

    @Override
    public void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initSetting() {
        bindView();
        initChipGroup();
        initTimerView();
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
        workoutTime = time;
    }

    @Override
    public void onTimerSetValueChange(int time) {
        Log.e(TAG, "onTimerSetValueChange: " + time);
        workoutTime = time;
    }

    @OnClick(R.id.start_fitness_button)
    public void onClickedStartFitnessButton() {
        Bundle bundle = new Bundle();

        Intent intent = new Intent(RealtimeSettingActivity.this, IndoorBikeRealTimeActivity.class);
        bundle.putString(IntentConst.REAL_TIME_DEVICE_ADDEDSS, deviceAddress);
        bundle.putInt(IntentConst.REAL_TIME_WORKOUT_TOTAL_TIME, workoutTime);
        bundle.putInt(IntentConst.REAL_TIME_WORKOUT_INTENSE, workoutIntense);
        intent.putExtra(IntentConst.REAL_TIME_SETTING_FITNESS_INFO, bundle);
        startActivity(intent);
        finish();

    }

    @OnClick(R.id.info)
    public void onClickedInfoButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("정보");
        builder.setMessage("운동 시간은 30-60분이 적당합니다. 혈당 감소에는 중강도 운동, 고강도 운동이 효과가 있다는 보고가 있습니다. 시간을 설정한 후 운동 강도를 설정해주세요");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }
}
