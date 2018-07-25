package com.dreamwalker.diabetesfits.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.database.model.Glucose;

import io.realm.Realm;
import io.realm.RealmResults;

public class GlucoseFeedActivity extends AppCompatActivity {
    private static final String TAG = "GlucoseFeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose_feed);

        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Glucose> glucose = realm.where(Glucose.class).findAll();

        for (int i = 0;  i < glucose.size(); i++){
            Log.e(TAG, "onCreate: " + glucose.get(i).getValue());
        }

    }
}
