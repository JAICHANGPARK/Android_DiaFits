package com.dreamwalker.diabetesfits.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.chart.ChartActivity;
import com.dreamwalker.diabetesfits.activity.diary.DiaryActivity;
import com.dreamwalker.diabetesfits.activity.diary.WriteBSActivity;
import com.dreamwalker.diabetesfits.activity.education.KADNEHomeActivity;
import com.dreamwalker.diabetesfits.adapter.DeviceAdapter;
import com.dreamwalker.diabetesfits.consts.PageConst;
import com.dreamwalker.diabetesfits.model.Device;
import com.dreamwalker.diabetesfits.model.Validate;
import com.dreamwalker.diabetesfits.remote.IUploadAPI;
import com.yalantis.guillotine.animation.GuillotineAnimation;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import client.yalantis.com.foldingtabbar.FoldingTabBar;
import de.cketti.library.changelog.ChangeLog;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.dreamwalker.diabetesfits.consts.Url.BASE_URL;

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

    @BindView(R.id.folding_tab_bar)
    FoldingTabBar tabBar;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    DeviceAdapter deviceAdapter;

    //HashMap<String, String> deviceMap = new HashMap<>();
    //ArrayList<HashMap<String, String>> deviceArrayList = new ArrayList<>();
    HashSet<Device> deviceDatabase = new HashSet<>();
    ArrayList<Device> deviceArrayList;

    String userID;
    final String pageNum = String.valueOf(PageConst.HOME_PAGE);

    Retrofit retrofit;
    IUploadAPI service;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Paper.init(this);
        Realm.init(this);

        initToasty();


        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        userID = Paper.book("user").read("userID");
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        service = retrofit.create(IUploadAPI.class);

        Call<Validate> accessQueue = service.userAccess(userID, pageNum);

        // wifi 또는 모바일 네트워크 어느 하나라도 연결이 되어있다면,
        if (wifi.isConnected() || mobile.isConnected()) {
            Log.e("연결됨", "연결이 되었습니다.");
            accessQueue.enqueue(new Callback<Validate>() {
                @Override
                public void onResponse(Call<Validate> call, Response<Validate> response) {

                }

                @Override
                public void onFailure(Call<Validate> call, Throwable t) {
                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Snackbar.make(getWindow().getDecorView().getRootView(), "인터넷 연결 없음", Snackbar.LENGTH_SHORT).show();
            Log.e("연결 안 됨", "연결이 다시 한번 확인해주세요");
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        tabBar.setOnFoldingItemClickListener(menuItem -> {
            Log.e(TAG, "onFoldingItemSelected: " + menuItem.getItemId());
            switch (menuItem.getItemId()) {
                case R.id.ftb_menu_nearby:

                    startActivity(new Intent(HomeActivity.this, DeviceChooseActivity.class));
                    break;
                case R.id.ftb_menu_new_chat:
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View writeView = inflater.inflate(R.layout.layout_dialog_home_select, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setView(writeView);
                    MaterialButton fitnessButton = writeView.findViewById(R.id.workout_write_button);
                    fitnessButton.setOnClickListener(v -> {
                        alertDialog.dismiss();
                        Toasty.info(this,  getResources().getString(R.string.under_construction), Toast.LENGTH_SHORT, true).show();

                    });

                    MaterialButton glucoseButton = writeView.findViewById(R.id.glucose_write_button);
                    glucoseButton.setOnClickListener(v -> {
                        alertDialog.dismiss();
                        startActivity(new Intent(HomeActivity.this, WriteBSActivity.class));
                    });

                    alertDialog = builder.create();

                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.getWindow().setGravity(Gravity.BOTTOM);

                    WindowManager.LayoutParams layoutParams = alertDialog.getWindow().getAttributes();
                    layoutParams.y = 30; // bottom margin
                    alertDialog.getWindow().setAttributes(layoutParams);

                    alertDialog.show();

//                    startActivity(new Intent(HomeActivity.this, WriteBSActivity.class));
                    break;
            }
            return false;
        });

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
        deviceDatabase = Paper.book("device").read("user_device");
        if (deviceDatabase != null) {
            if (deviceDatabase.size() != 0) {

                deviceLayout.setVisibility(View.VISIBLE);
                emptyLayout.setVisibility(View.GONE);
                deviceArrayList = new ArrayList<>(deviceDatabase);
                deviceAdapter = new DeviceAdapter(this, deviceArrayList);
                recyclerView.setAdapter(deviceAdapter);

                for (int i = 0; i < deviceArrayList.size(); i++) {
                    Device device = deviceArrayList.get(i);
                    Log.e(TAG, "onCreate: " + device.getDeviceName() + ", " + device.getDeviceAddress());
                }
            }
        } else {
            Log.e(TAG, "onCreate: " + "등록된 장비 없음");
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
        LinearLayout activityLayout = guillotineMenu.findViewById(R.id.activity_group);
        LinearLayout feedLayout = guillotineMenu.findViewById(R.id.feed_group);
        LinearLayout profileLayout = guillotineMenu.findViewById(R.id.profile_group);
        LinearLayout diaryLayout = guillotineMenu.findViewById(R.id.activity_diary);
        LinearLayout educationLayout = guillotineMenu.findViewById(R.id.activity_education);

        educationLayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, KADNEHomeActivity.class);
//            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        diaryLayout.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, DiaryActivity.class);
//            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        profileLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
//            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        settingLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SettingActivityV2.class);
//            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        activityLayout.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ChartActivity.class));
        });

        feedLayout.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, GlucoseFeedActivity.class));
        });


    }

    private void initToasty() {
        Toasty.Config.getInstance().apply(); // required
    }
}
