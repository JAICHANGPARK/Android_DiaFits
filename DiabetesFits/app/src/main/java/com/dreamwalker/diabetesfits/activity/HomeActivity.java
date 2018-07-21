package com.dreamwalker.diabetesfits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dreamwalker.diabetesfits.R;

import de.cketti.library.changelog.ChangeLog;

import static com.dreamwalker.diabetesfits.consts.IntentConst.EXTRAS_DEVICE_ADDRESS;
import static com.dreamwalker.diabetesfits.consts.IntentConst.EXTRAS_DEVICE_NAME;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    String mDeviceName;
    String mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }


        Log.e(TAG, "onCreate: " + mDeviceName + " | "  + mDeviceAddress);

    }
}
