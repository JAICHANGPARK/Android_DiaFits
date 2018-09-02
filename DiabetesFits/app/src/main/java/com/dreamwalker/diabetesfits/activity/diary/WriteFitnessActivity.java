package com.dreamwalker.diabetesfits.activity.diary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.dreamwalker.diabetesfits.R;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WriteFitnessActivity extends AppCompatActivity {
    private static final String TAG = "WriteFitnessActivity";

    @BindView(R.id.nice_spinner)
    NiceSpinner niceSpinner;

    String selectType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_fitness);
        initSetting();

        selectType = "트레드밀";


    }

    private void bindView(){
        ButterKnife.bind(this);
    }

    private void initSetting(){
        bindView();
        setNiceSpinner();
    }

    private void setNiceSpinner(){
        List<String> dataset = new LinkedList<>(Arrays.asList("트레드밀", "실내자전거"));
        niceSpinner.attachDataSource(dataset);
        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.e(TAG, "onItemSelected: " + position + "," + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
