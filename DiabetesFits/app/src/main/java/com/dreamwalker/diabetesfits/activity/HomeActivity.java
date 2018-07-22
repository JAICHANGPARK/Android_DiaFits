package com.dreamwalker.diabetesfits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.DeviceAdapter;
import com.dreamwalker.diabetesfits.model.Device;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.util.ArrayList;

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

    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.device_layout)
    LinearLayout deviceLayout;


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    DeviceAdapter deviceAdapter;

    //HashMap<String, String> deviceMap = new HashMap<>();
    //ArrayList<HashMap<String, String>> deviceArrayList = new ArrayList<>();
    ArrayList<Device> deviceArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Paper.init(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        if (deviceArrayList != null){
            if (deviceArrayList.size() != 0) {

                deviceLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);

                deviceAdapter = new DeviceAdapter(this, deviceArrayList);
                recyclerView.setAdapter(deviceAdapter);

                for (int i = 0; i < deviceArrayList.size(); i++) {
                    Device device = deviceArrayList.get(i);
                    Log.e(TAG, "onCreate: " + device.getDeviceName() + ", " + device.getDeviceAddress());
                }
            }
        }else {
            Log.e(TAG, "onCreate: " + "등록된 장비 없음" );
            emptyLayout.setVisibility(View.VISIBLE);
            deviceLayout.setVisibility(View.GONE);
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

        LinearLayout settingLayout = guillotineMenu.findViewById(R.id.settings_group);
        settingLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

    }
}
