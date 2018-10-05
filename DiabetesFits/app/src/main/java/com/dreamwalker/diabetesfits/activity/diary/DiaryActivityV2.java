package com.dreamwalker.diabetesfits.activity.diary;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diarydatepicker.DatePickerTimeline;
import com.dreamwalker.diarydatepicker.MonthView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class DiaryActivityV2 extends AppCompatActivity {
    private static final String TAG = "DiaryActivityV2";

    @BindView(R.id.timeline)
    DatePickerTimeline timeline;
    @BindView(R.id.bottomAppBar)
    BottomAppBar bottomAppBar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;


    Realm realm;
    RealmConfiguration realmConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_v2);
        initSetting();


    }

    private void initSetting(){
        bindView();
        setTimeLineView();
        setBottomAppBar();
        initRealm();

    }

    private void bindView(){
        ButterKnife.bind(this);
    }

    private void initRealm() {
        Realm.init(this);
        realmConfiguration = RealmManagement.getRealmConfiguration();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getInstance(realmConfiguration);

    }

    private void setTimeLineView(){
        timeline.setDateLabelAdapter(new MonthView.DateLabelAdapter() {
            @Override
            public CharSequence getLabel(Calendar calendar, int index) {
                Log.e(TAG, "getLabel: " + calendar.getTimeInMillis() + " | " + index );
                return Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/" + (calendar.get(Calendar.YEAR) % 2000);
            }
        });

        timeline.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                Toast.makeText(DiaryActivityV2.this, "" + year + month + day, Toast.LENGTH_SHORT).show();
            }
        });

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -2);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 2);
        Calendar defaultDate = Calendar.getInstance();

        Log.e(TAG, "setTimeLineView: " + startDate.get(Calendar.YEAR) + startDate.get(Calendar.MONTH) +  startDate.get(Calendar.DAY_OF_MONTH) );
        Log.e(TAG, "setTimeLineView: " +   Calendar.JULY );

//        timeline.setFirstVisibleDate(2016, Calendar.JULY, 19);
//        timeline.setLastVisibleDate(2020, Calendar.JULY, 19);
//
        timeline.setFirstVisibleDate(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
        timeline.setLastVisibleDate(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH) , endDate.get(Calendar.DAY_OF_MONTH));
        timeline.setSelectedDate(defaultDate.get(Calendar.YEAR), defaultDate.get(Calendar.MONTH), defaultDate.get(Calendar.DAY_OF_MONTH));

    }

    private void setBottomAppBar(){
        bottomAppBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.diary_glucose:
                startActivity(new Intent(DiaryActivityV2.this, DiaryGlucoseActivity.class));
                return true;
            case R.id.diary_fitness:
                startActivity(new Intent(DiaryActivityV2.this, DiaryFitnessActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diary, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

}
