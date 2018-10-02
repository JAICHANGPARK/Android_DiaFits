package com.dreamwalker.diabetesfits.activity.diary;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diabetesfits.database.model.Glucose;
import com.dreamwalker.horizontalcalendar.HorizontalCalendar;
import com.dreamwalker.horizontalcalendar.HorizontalCalendarView;
import com.dreamwalker.horizontalcalendar.utils.HorizontalCalendarListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DiaryGlucoseActivity extends AppCompatActivity {
    private static final String TAG = "DiaryGlucoseActivity";

    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;
    @BindView(R.id.bottomAppBar)
    BottomAppBar bottomAppBar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    HorizontalCalendar horizontalCalendar;
    Realm realm;
    RealmConfiguration realmConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_glucose);
        initSetting();


        /* start 2 months ago from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -2);

        /* end after 2 months from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 2);

        // Default Date set to Today.
        final Calendar defaultSelectedDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Date todayDate = defaultSelectedDate.getTime();
        String todayString = simpleDateFormat.format(todayDate);
        Log.e(TAG, "onCreate: todayString --> " + todayString);

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .configure()
                .formatTopText("MMM")
                .formatMiddleText("dd")
                .formatBottomText("EE")
                .showTopText(true)
                .showBottomText(true)
                .textColor(Color.LTGRAY, Color.WHITE)
                .colorTextMiddle(Color.LTGRAY, Color.parseColor("#ffd54f"))
                .end()
                .defaultSelectedDate(defaultSelectedDate)
                .build();

        Log.e("Default Date", DateFormat.format("EEE, MMM d, yyyy", defaultSelectedDate).toString());

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                String selectedDateStr = DateFormat.format("EEE, MMM d, yyyy", date).toString();
                String selectedDate = DateFormat.format("yyyy-MM-dd", date).toString();
                Toast.makeText(DiaryGlucoseActivity.this, selectedDate + " selected!", Toast.LENGTH_SHORT).show();
//                Log.e("onDateSelected", selectedDateStr + " - Position = " + position);
                Log.e("onDateSelected", selectedDate + " - Position = " + position);
            }
        });

//        RealmResults<Glucose> result = realm.where(Glucose.class).findAll();
//        Log.e(TAG, "onCreate: " + result.size() );
//        for (Glucose glucose: result){
//            Log.e(TAG, "onCreate: getValue --> " + glucose.getValue());
//            Log.e(TAG, "onCreate: getLongTs --> " + glucose.getDate());
//        }

        RealmResults<Glucose> todayList = realm.where(Glucose.class).equalTo("date", todayString).findAll();
        for (Glucose glucose : todayList) {
            Log.e(TAG, "onCreate: todayList  getValue --> " + glucose.getValue());
            Log.e(TAG, "onCreate: todayList  getLongTs --> " + glucose.getDate());
        }
    }

    private void initSetting() {
        initRealm();
        bindView();
        setStatusBar();
        initToolbar();
    }

    private void initToolbar() {
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
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
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.diary_bottom_appbar_color2));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_diary_glucose, menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.home:
                horizontalCalendar.goToday(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        Realm.getInstance(realmConfiguration).close();
        realm.close();
        super.onDestroy();
    }

    @OnClick(R.id.fab)
    public void onClickedFab() {

//        horizontalCalendar.goToday(false);

    }
}
