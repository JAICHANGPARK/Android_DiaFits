package com.dreamwalker.diabetesfits.activity.sync.indoorbike;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.model.fitness.Fitness;

import java.util.ArrayList;

import butterknife.ButterKnife;
import io.paperdb.Paper;

public class SyncIndoorBikeResultActivity extends AppCompatActivity {
    private static final String TAG = "SyncIndoorBikeResultAct";

    ArrayList<Fitness> fitnessArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_indoor_bike_result);

        ButterKnife.bind(this);
        Paper.init(this);

        if (Paper.book("syncIndoorBike").read("data") == null) {
            Snackbar.make(getWindow().getDecorView().getRootView(), "데이터가 없어요", Snackbar.LENGTH_SHORT).show();
        } else {
            // TODO: 2018-02-27 동기화된 데이터를 가져옴.
            fitnessArrayList = Paper.book("syncIndoorBike").read("data");
        }

        for (Fitness f : fitnessArrayList){
            Log.e(TAG, "onCreate: " + f.getFitnessTime());
        }
    }
}
