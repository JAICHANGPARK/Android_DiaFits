package com.dreamwalker.diabetesfits.activity.diary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diabetesfits.database.model.Glucose;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DiaryGlucoseActivity extends AppCompatActivity {
    private static final String TAG = "DiaryGlucoseActivity";

    Realm realm;
    RealmConfiguration realmConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_glucose);
        initSetting();


        RealmResults<Glucose> result = realm.where(Glucose.class).findAll();
        Log.e(TAG, "onCreate: " + result.size() );
        for (Glucose glucose: result){
            Log.e(TAG, "onCreate: getValue --> " + glucose.getValue());
            Log.e(TAG, "onCreate: getLongTs --> " + glucose.getLongTs());
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
