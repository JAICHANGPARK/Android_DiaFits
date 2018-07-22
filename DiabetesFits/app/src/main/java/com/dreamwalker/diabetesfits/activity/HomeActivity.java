package com.dreamwalker.diabetesfits.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.dreamwalker.diabetesfits.R;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.cketti.library.changelog.ChangeLog;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final long RIPPLE_DURATION = 250;

    String mDeviceName;
    String mDeviceAddress;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.content_hamburger)
    View contentHamburger;

    HashMap<String, String> deviceMap = new HashMap<>();
    ArrayList<HashMap<String, String>> deviceArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Paper.init(this);

//        final Intent intent = getIntent();
//        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
//        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
//        Log.e(TAG, "onCreate: " + mDeviceName + " | "  + mDeviceAddress);
//        if (mDeviceName != null && mDeviceAddress != null){
//            deviceMap.put("deviceName", mDeviceName);
//            deviceMap.put("deviceAddress", mDeviceAddress);
//            deviceArrayList.add(deviceMap);
//            Paper.book("device").write("user_device", deviceArrayList);
//        }
        deviceArrayList = Paper.book("device").read("user_device");

        for (int i = 0; i < deviceArrayList.size(); i++) {
            HashMap<String, String> map = deviceArrayList.get(i);
            Log.e(TAG, "onCreate: " + map.get("deviceName") );
            Log.e(TAG, "onCreate: " + map.get("deviceAddress") );
        }


        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }


        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null);
        root.addView(guillotineMenu);

        new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .build();

    }
}
