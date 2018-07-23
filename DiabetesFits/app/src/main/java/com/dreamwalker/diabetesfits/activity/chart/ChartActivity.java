package com.dreamwalker.diabetesfits.activity.chart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.model.isens.BloodSugar;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class ChartActivity extends AppCompatActivity {
    private static final String TAG = "ChartActivity";

    @BindView(R.id.analysis_line_chart)
    LineChart lineChart;

    ArrayList<BloodSugar> mBSList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initSetting();
    }

    private void initSetting(){
        viewBinding();
        initPaper();
    }
    private void viewBinding(){
        ButterKnife.bind(this);
    }

    private void initPaper(){
        Paper.init(this);
    }
}
