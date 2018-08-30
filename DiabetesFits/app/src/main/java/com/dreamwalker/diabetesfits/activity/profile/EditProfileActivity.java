package com.dreamwalker.diabetesfits.activity.profile;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;

import com.dreamwalker.diabetesfits.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.glucose_min_edt)
    TextInputEditText minGlucoseEditText;

    @BindView(R.id.glucose_max_edt)
    TextInputEditText maxGlucoseEditText;

    @BindView(R.id.weight_edt)
    TextInputEditText weightEditText;

    @BindView(R.id.height_edt)
    TextInputEditText heightEditText;

    String minGlucose;
    String maxGlucose;
    String userHeight;
    String userWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initSetting();
        minGlucose = Paper.book("user").read("userGlucoseMin");
        maxGlucose = Paper.book("user").read("userGlucoseMax");
        userHeight = Paper.book("user").read("userHeight");
        userWeight = Paper.book("user").read("userWeight");

        if (minGlucose != null) {
            minGlucoseEditText.setText(minGlucose);
        } else {
            minGlucose = "None";
        }

        if (maxGlucose != null) {
            maxGlucoseEditText.setText(maxGlucose);
        } else {
            maxGlucose = "None";
        }

        if (userHeight == null) {
            userHeight = "None";
        } else {
            heightEditText.setText(userHeight);
        }

        if (userWeight == null) {
            userWeight = "None";
        }else {
            weightEditText.setText(userWeight);
        }


    }

    private void bindView() {
        ButterKnife.bind(this);
    }

    private void getPaper() {
        Paper.init(this);
    }

    private void initSetting() {
        bindView();
        getPaper();

    }
}
