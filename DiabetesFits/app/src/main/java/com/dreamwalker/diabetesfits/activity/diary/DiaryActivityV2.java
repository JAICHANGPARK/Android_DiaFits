package com.dreamwalker.diabetesfits.activity.diary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.diary.GlobalDiaryAdapter;
import com.dreamwalker.diabetesfits.adapter.diary.ItemClickListener;
import com.dreamwalker.diabetesfits.consts.GlobalTag;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diabetesfits.database.model.Fitness;
import com.dreamwalker.diabetesfits.database.model.Glucose;
import com.dreamwalker.diabetesfits.model.diary.Global;
import com.dreamwalker.diarydatepicker.DatePickerTimeline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DiaryActivityV2 extends AppCompatActivity implements ItemClickListener {
    private static final String TAG = "DiaryActivityV2";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;

    @BindView(R.id.timeline)
    DatePickerTimeline timeline;

    @BindView(R.id.bottomAppBar)
    BottomAppBar bottomAppBar;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;


    Realm realm;
    RealmConfiguration realmConfiguration;

    RealmResults<Glucose> glucoseResults;
    RealmResults<Fitness> fitnessesResults;

    LinearLayoutManager layoutManager;
    ArrayList<Global> globalArrayList = new ArrayList<>();
    GlobalDiaryAdapter adapter;
    SimpleDateFormat simpleDateFormat;

    String todayString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_v2);

        initSetting();


//        fetchFromRealm(todayString);


    }


    private void initSetting() {

        bindView(); //뷰 바인딩
        initRealm(); // Realm 초기화
        initRecyclerView(); // 리사이클러뷰 초기화
        initTimeSet(); //시간 객체 초기화
        setTimeLineView(); // 타임라인 초기화
        setBottomAppBar(); // Bottom App Bar 초기화
        setRecyclerViewScrollListener();
//        setNestedScrollView(); // setNestedScrollView

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

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new GlobalDiaryAdapter(this, globalArrayList);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    private void initTimeSet() {

        // Default Date set to Today.
        final Calendar defaultSelectedDate = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Date todayDate = defaultSelectedDate.getTime();
        todayString = simpleDateFormat.format(todayDate);

    }

    private void setTimeLineView() {
        timeline.setDateLabelAdapter((calendar, index) -> {
            String selectDate = simpleDateFormat.format(calendar.getTime());
            glucoseResults = realm.where(Glucose.class).equalTo("date", selectDate).findAll();
            fitnessesResults = realm.where(Fitness.class).equalTo("date", selectDate).findAll();
            int count = glucoseResults.size() + fitnessesResults.size();
            Log.e(TAG, "getLabel: " + calendar.getTimeInMillis() + " | " + index);
            return Integer.toString(count) + "개";

//                return Integer.toString(calendar.get(Calendar.MONTH) + 1) + "/" + (calendar.get(Calendar.YEAR) % 2000);
        });

        timeline.setOnDateSelectedListener((year, month, day, index) -> {
            Log.e(TAG, "setTimeLineView: setOnDateSelectedListener --> !!!!!!!!1 ");

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            String selectDate = simpleDateFormat.format(calendar.getTime());
            fetchFromRealm(selectDate, true);
            adapter.notifyDataSetChanged();
//                Toast.makeText(DiaryActivityV2.this, "" + year + month + day, Toast.LENGTH_SHORT).show();
        });

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.YEAR, -2);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 2);
        Calendar defaultDate = Calendar.getInstance();

        Log.e(TAG, "setTimeLineView: " + startDate.get(Calendar.YEAR) + startDate.get(Calendar.MONTH) + startDate.get(Calendar.DAY_OF_MONTH));
        Log.e(TAG, "setTimeLineView: " + Calendar.JULY);

//        timeline.setFirstVisibleDate(2016, Calendar.JULY, 19);
//        timeline.setLastVisibleDate(2020, Calendar.JULY, 19);
//
        timeline.setFirstVisibleDate(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
        timeline.setLastVisibleDate(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
        timeline.setSelectedDate(defaultDate.get(Calendar.YEAR), defaultDate.get(Calendar.MONTH), defaultDate.get(Calendar.DAY_OF_MONTH));

    }

    private void setBottomAppBar() {
        bottomAppBar = findViewById(R.id.bottomAppBar);
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
    }

    private void setRecyclerViewScrollListener() {


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                Log.e(TAG, "onScrollStateChanged: -->" + newState );

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    floatingActionButton.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && floatingActionButton.isShown()) {
                    floatingActionButton.hide();
                }


//                Log.e(TAG, "onScrolled: dy -- >" + dy );
//                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    private void fetchFromRealm(String today) {
        glucoseResults = realm.where(Glucose.class).equalTo("date", today).findAll().sort("datetime");
        fitnessesResults = realm.where(Fitness.class).equalTo("date", today).findAll().sort("datetime");
        Log.e(TAG, "onCreate:  glucoseResults --> " + glucoseResults.size());
        Log.e(TAG, "onCreate: fitnessesResults -->  " + fitnessesResults.size());

        if (glucoseResults.size() == 0 && fitnessesResults.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {


            if (glucoseResults.size() != 0) {
                for (Glucose g : glucoseResults) {
                    globalArrayList.add(new Global(GlobalTag.GLUCOSE_TAG_NUMBER, g.getType(), g.getValue(),
                            g.getDate(), g.getTime(), g.getTimestamp(), g.getLongTs(), g.getDatetime()));
                }
            }

            if (fitnessesResults.size() != 0) {
                for (Fitness f : fitnessesResults) {
                    globalArrayList.add(new Global(GlobalTag.FITNESS_TAG_NUMBER, f.getType(), f.getFitnessTime(),
                            f.getDate(), f.getTime(), f.getTimestamp(), f.getLongTs(), f.getDatetime()));
                }
            }

            // TODO: 2018-10-05 오름차순 정렬
            Collections.sort(globalArrayList, (o1, o2) -> {
                if (o1.getLongTs() < o2.getLongTs()) {
                    return -1;
                } else if (o1.getLongTs() > o2.getLongTs()) {
                    return 1;
                }
                return 0;
            });

            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }

    }

    private void fetchFromRealm(String today, boolean flush) {

        if (flush) {
            globalArrayList.clear();
        }
        glucoseResults = realm.where(Glucose.class).equalTo("date", today).findAll().sort("datetime");
        fitnessesResults = realm.where(Fitness.class).equalTo("date", today).findAll().sort("datetime");
        Log.e(TAG, "onCreate:  glucoseResults --> " + glucoseResults.size());
        Log.e(TAG, "onCreate: fitnessesResults -->  " + fitnessesResults.size());

        if (glucoseResults.size() == 0 && fitnessesResults.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {


            if (glucoseResults.size() != 0) {
                for (Glucose g : glucoseResults) {
                    globalArrayList.add(new Global(GlobalTag.GLUCOSE_TAG_NUMBER, g.getType(), g.getValue(),
                            g.getDate(), g.getTime(), g.getTimestamp(), g.getLongTs(), g.getDatetime()));
                }
            }

            if (fitnessesResults.size() != 0) {
                for (Fitness f : fitnessesResults) {
                    globalArrayList.add(new Global(GlobalTag.FITNESS_TAG_NUMBER, f.getType(), f.getFitnessTime(),
                            f.getDate(), f.getTime(), f.getTimestamp(), f.getLongTs(), f.getDatetime()));
                }
            }

            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.diary_glucose:
                startActivity(new Intent(DiaryActivityV2.this, DiaryGlucoseActivity.class));
                return true;
            case R.id.diary_fitness:
                startActivity(new Intent(DiaryActivityV2.this, DiaryFitnessActivity.class));
                return true;
            case R.id.home:
                Calendar defaultDate = Calendar.getInstance();
                timeline.setSelectedDate(defaultDate.get(Calendar.YEAR), defaultDate.get(Calendar.MONTH), defaultDate.get(Calendar.DAY_OF_MONTH));
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

    @Override
    public void onItemClick(View v, int position) {

    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    class AscendingTime implements Comparator<Long> {

        @Override
        public int compare(Long o1, Long o2) {
            return o2.compareTo(o1);
        }
    }
}
