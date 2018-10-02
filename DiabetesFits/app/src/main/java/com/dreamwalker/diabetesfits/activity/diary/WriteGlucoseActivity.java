package com.dreamwalker.diabetesfits.activity.diary;

import android.os.Build;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.database.RealmManagement;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WriteGlucoseActivity extends AppCompatActivity {
    private static final String TAG = "WriteGlucoseActivity";

    @BindView(R.id.nice_spinner)
    NiceSpinner niceSpinner;
    @BindView(R.id.nice_spinner_2)
    NiceSpinner niceSpinner2;
    @BindView(R.id.bottomAppBar)
    BottomAppBar bottomAppBar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    Realm realm;
    RealmConfiguration realmConfiguration;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_glucose);
        initSetting();

    }

    private void initSetting() {
        initRealm();
        bindView();
        setStatusBar();
        initToolbar();
        setNiceSpinner();
    }

    private void initToolbar() {
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
//        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
    }

    private void bindView() {
        ButterKnife.bind(this);
    }

    private void initRealm() {
        Realm.init(this);
        realmConfiguration = RealmManagement.getRealmConfiguration();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getInstance(realmConfiguration);

    }

    private void setStatusBar() {
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.default_background));
        }
    }


    private void setNiceSpinner() {

        List<String> dataset = new LinkedList<>(Arrays.asList("공복", "취침 전", "운동", "아침 식사", "점심 식사", "저녁 식사"));
        List<String> treadmillSet = new LinkedList<>(Arrays.asList("가볍게걷기", "일반 걷기", "달리기"));
        List<String> indoorBikeSet = new LinkedList<>(Arrays.asList("보통으로", "빠르게", "가볍게"));
        List<String> rpeSet = new LinkedList<>(Arrays.asList("전혀 힘들지 않다", "힘들지 않다", "보통이다", "약간 힘들다", "힘들다", "매우 힘들다", "매우 매우 힘들다"));

        niceSpinner.attachDataSource(dataset);


        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                }
                Log.e(TAG, "onItemSelected: " + position + "," + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @OnClick(R.id.fab)
    public void onClickFlotingActionButton(){

    }

    @Override
    protected void onDestroy() {
        Realm.getInstance(realmConfiguration).close();
        super.onDestroy();
    }

}
