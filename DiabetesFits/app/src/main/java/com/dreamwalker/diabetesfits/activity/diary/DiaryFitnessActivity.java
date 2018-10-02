package com.dreamwalker.diabetesfits.activity.diary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diabetesfits.database.model.Fitness;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DiaryFitnessActivity extends AppCompatActivity {

    private static final String TAG = "DiaryFitnessActivity";

    Realm realm;
    RealmConfiguration realmConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_fitness);

        initSetting();

        RealmResults<Fitness> result = realm.where(Fitness.class).findAll();
        Log.e(TAG, "onCreate: " + result.size() );
        for (Fitness fitness: result){
            Log.e(TAG, "onCreate: --> " + fitness.getFitnessTime());
            Log.e(TAG, "onCreate: --> " + fitness.getDate());
        }
    }


    private void initSetting(){
        initRealm();
    }
    private void initRealm(){
        Realm.init(this);
        realmConfiguration = RealmManagement.getRealmConfiguration();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getInstance(realmConfiguration);
    }

    @Override
    protected void onDestroy() {
        Realm.getInstance(realmConfiguration).close();
        realm.close();
        super.onDestroy();
    }
}
