package com.dreamwalker.diabetesfits.activity.diary;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.diary.DiaryGlucoseAdapter;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diabetesfits.database.model.Glucose;
import com.dreamwalker.diabetesfits.model.diary.Gluco;
import com.dreamwalker.horizontalcalendar.HorizontalCalendar;
import com.dreamwalker.horizontalcalendar.HorizontalCalendarView;
import com.dreamwalker.horizontalcalendar.model.CalendarEvent;
import com.dreamwalker.horizontalcalendar.utils.CalendarEventsPredicate;
import com.dreamwalker.horizontalcalendar.utils.HorizontalCalendarListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DiaryGlucoseActivity extends AppCompatActivity {
    private static final String TAG = "DiaryGlucoseActivity";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;
    @BindView(R.id.bottomAppBar)
    BottomAppBar bottomAppBar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    ArrayList<Gluco> glucoArrayList = new ArrayList<>();
    DiaryGlucoseAdapter adapter;
    LinearLayoutManager layoutManager;


    HorizontalCalendar horizontalCalendar;
    Realm realm;
    RealmConfiguration realmConfiguration;

    RealmResults<Glucose> todayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_glucose);
        initSetting();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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

        initHorizontalCalendar(startDate, endDate, defaultSelectedDate, simpleDateFormat);

        sortAndProcessGlucose(todayString, true);

        RealmResults<Glucose> preResult = realm.where(Glucose.class)
                .greaterThanOrEqualTo("datetime", startDate.getTime())
                .lessThanOrEqualTo("datetime", endDate.getTime())
                .findAll();


        adapter = new DiaryGlucoseAdapter(this, glucoArrayList);
        recyclerView.setAdapter(adapter);


//        RealmResults<Glucose> result = realm.where(Glucose.class).findAll();
//        Log.e(TAG, "onCreate: " + result.size() );
//        for (Glucose glucose: result){
//            Log.e(TAG, "onCreate: getValue --> " + glucose.getValue());
//            Log.e(TAG, "onCreate: getLongTs --> " + glucose.getDate());
//        }

        horizontalCalendar.goToday(true);

    }

    private void sortAndProcessGlucose(String ts, boolean clearFlag) {
        if(clearFlag){
            glucoArrayList.clear();
        }

        todayList = realm.where(Glucose.class).equalTo("date", ts).findAll().sort("datetime");
//        todayList = realm.where(Glucose.class).equalTo("date", todayString).findAll().sort("datetime");
//        for (Glucose glucose : todayList) {
//            Log.e(TAG, "onCreate: todayList  getValue --> " + glucose.getValue());
//            Log.e(TAG, "onCreate: todayList  getLongTs --> " + glucose.getDate());
//
//        }
        if (todayList.size() != 0){
            ArrayList<Integer> changeList = new ArrayList<>();
            for (int i = (todayList.size() - 1); i > 0; i--) {
                float change = Float.parseFloat(todayList.get(i).getValue()) - Float.parseFloat(todayList.get(i - 1).getValue());
                Log.e(TAG, "onCreate: " + change);
                changeList.add((int) change);
            }

            glucoArrayList.add(new Gluco(todayList.get(0).getValue(),
                    todayList.get(0).getType(),
                    todayList.get(0).getDate(),
                    todayList.get(0).getTime(),
                    todayList.get(0).getTimestamp(),
                    todayList.get(0).getLongTs(),
                    todayList.get(0).getDatetime(), 0));

            for (int i = 1; i < todayList.size(); i++) {
                glucoArrayList.add(new Gluco(todayList.get(i).getValue(),
                        todayList.get(i).getType(),
                        todayList.get(i).getDate(),
                        todayList.get(i).getTime(),
                        todayList.get(i).getTimestamp(),
                        todayList.get(i).getLongTs(),
                        todayList.get(i).getDatetime(),
                        changeList.get(changeList.size() - i)));
            }
        }else {
            // TODO: 2018-10-02 값이 없을때 뷰처리
        }


    }

    private void initHorizontalCalendar(Calendar start, Calendar end, Calendar defaultDate, SimpleDateFormat sdf) {

        horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(start, end)
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
                .defaultSelectedDate(defaultDate)
                .addEvents(new CalendarEventsPredicate() {
                    @Override
                    public List<CalendarEvent> events(Calendar date) {
                        List<CalendarEvent> events = new ArrayList<>();
                        String eventDate = sdf.format(date.getTime());
                        RealmResults<Glucose> tmp = realm.where(Glucose.class).equalTo("date", eventDate).findAll();

                        for (int i = 0; i < tmp.size(); i++) {
                            Log.e(TAG, "events: " + date.getTimeInMillis());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                events.add(new CalendarEvent(getColor(R.color.shopAccent), "count"));
                            } else {
                                events.add(new CalendarEvent(R.color.shopAccent, "count"));
                            }
                        }
                        return events;
                    }
                })
                .build();

        Log.e("Default Date", DateFormat.format("EEE, MMM d, yyyy", defaultDate).toString());

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                String selectedDateStr = DateFormat.format("EEE, MMM d, yyyy", date).toString();
                String selectedDate = DateFormat.format("yyyy-MM-dd", date).toString();
                Toast.makeText(DiaryGlucoseActivity.this, selectedDate + " selected!", Toast.LENGTH_SHORT).show();
//                Log.e("onDateSelected", selectedDateStr + " - Position = " + position);
                Log.e("onDateSelected", selectedDate + " - Position = " + position);
                sortAndProcessGlucose(selectedDate, true);
                adapter.notifyDataSetChanged();
            }
        });

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
    protected void onPause() {
        Realm.getInstance(realmConfiguration).close();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Realm.getInstance(realmConfiguration).close();
        super.onDestroy();
    }

    @OnClick(R.id.fab)
    public void onClickedFab() {
        Intent intent = new Intent(DiaryGlucoseActivity.this, WriteGlucoseActivity.class);
        // Configurations cannot be different if used to open the same file.
        // The most likely cause is that equals() and hashCode() are not overridden in the migration class: com.dreamwalker.diabetesfits.database.MyMigration
        startActivity(intent);

//        horizontalCalendar.goToday(false);

    }
}
