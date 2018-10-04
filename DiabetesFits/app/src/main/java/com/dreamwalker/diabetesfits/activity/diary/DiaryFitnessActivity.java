package com.dreamwalker.diabetesfits.activity.diary;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.diary.DiaryFitnessAdapter;
import com.dreamwalker.diabetesfits.adapter.diary.ItemClickListener;
import com.dreamwalker.diabetesfits.consts.IntentConst;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diabetesfits.database.model.Fitness;
import com.dreamwalker.diabetesfits.model.diary.Fitnes;
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
import io.realm.Sort;

public class DiaryFitnessActivity extends AppCompatActivity implements ItemClickListener {

    private static final String TAG = "DiaryFitnessActivity";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;
    @BindView(R.id.bottomAppBar)
    BottomAppBar bottomAppBar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;


    Realm realm;
    RealmConfiguration realmConfiguration;
    RealmResults<Fitness> todayList;

    HorizontalCalendar horizontalCalendar;
    Bundle bundle = new Bundle();  //데이터 수정 액티비티에 전달할 번들 객체 생성
    String userSelectedGlobalDate;


    ArrayList<Fitnes> fitnesArrayList = new ArrayList<>();
    DiaryFitnessAdapter adapter;
    LinearLayoutManager layoutManager;


    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_fitness);

        initSetting();

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        RealmResults<Fitness> result = realm.where(Fitness.class).findAll();
        Log.e(TAG, "onCreate: " + result.size());
        for (Fitness fitness : result) {
            Log.e(TAG, "onCreate: --> " + fitness.getFitnessTime());
            Log.e(TAG, "onCreate: --> " + fitness.getDate());
        }

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
        adapter = new DiaryFitnessAdapter(this, fitnesArrayList);
        adapter.setItemClickListener(this);

        recyclerView.setAdapter(adapter);

        horizontalCalendar.goToday(true);

    }

    private void sortAndProcessGlucose(String ts, boolean clearFlag) {

        if (clearFlag) {
            fitnesArrayList.clear();
        }

        todayList = realm.where(Fitness.class).equalTo("date", ts).findAll().sort("datetime");
//        todayList = realm.where(Glucose.class).equalTo("date", todayString).findAll().sort("datetime");
        for (Fitness f : todayList) {
            Log.e(TAG, "onCreate: todayList  getValue --> " + f.getUserValue());
            Log.e(TAG, "onCreate: todayList  getLongTs --> " + f.getDate());
            Log.e(TAG, "onCreate: todayList  getLongTs --> " + f.getTimestamp());
        }

        if (todayList.size() != 0) {

            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);


            /**
             *         fitness.setType(userInputMap.get("selectType"));
             *                 fitness.setSelectTypeDetail(userInputMap.get("selectTypeDetail"));
             *                 fitness.setSelectRpeExpression(userInputMap.get("selectRpeExpression"));
             *                 fitness.setFitnessTime(userInputMap.get("fitnessTime"));
             *                 fitness.setDistance(userInputMap.get("fitnessDistance"));
             *                 fitness.setSpeed(userInputMap.get("fitnessSpeed"));
             *                 fitness.setRpeScore(userInputMap.get("rpeScore"));
             *                 fitness.setKcal(userKcal);
             *                 fitness.setDate(dateTime[0]);
             *                 fitness.setTime(dateTime[1]);
             *                 fitness.setTimestamp(userInputMap.get("timestamp"));
             *                 fitness.setLongTs(userTs);
             *                 fitness.setDatetime(userDateTimes);
             */

            for (int i = 0; i < todayList.size(); i++) {
                fitnesArrayList.add(new Fitnes(todayList.get(i).getType(),
                        todayList.get(i).getSelectTypeDetail(),
                        todayList.get(i).getSelectRpeExpression(),
                        todayList.get(i).getFitnessTime(),
                        todayList.get(i).getDistance(),
                        todayList.get(i).getSpeed(),
                        todayList.get(i).getRpeScore(),
                        todayList.get(i).getKcal(),
                        todayList.get(i).getDate(),
                        todayList.get(i).getTime(),
                        todayList.get(i).getTimestamp(),
                        todayList.get(i).getLongTs(),
                        todayList.get(i).getDatetime()));
            }

        } else {
            // TODO: 2018-10-02 값이 없을때 뷰처리
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 필터 적용 메소드
     * 오름차순 , 내림차순
     *
     * @param ts
     * @param clearFlag
     * @param sort
     */
    private void sortAndProcessGlucose(String ts, boolean clearFlag, Sort sort) {
        if (clearFlag) {
            fitnesArrayList.clear();
        }

        todayList = realm.where(Fitness.class).equalTo("date", ts).findAll().sort("datetime", sort);
//        todayList = realm.where(Glucose.class).equalTo("date", todayString).findAll().sort("datetime");
        for (Fitness f : todayList) {
            Log.e(TAG, "onCreate: todayList  getValue --> " + f.getUserValue());
            Log.e(TAG, "onCreate: todayList  getLongTs --> " + f.getDate());
            Log.e(TAG, "onCreate: todayList  getLongTs --> " + f.getTimestamp());
        }

        if (todayList.size() != 0) {

            recyclerView.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);

//            ArrayList<Integer> changeList = new ArrayList<>();
//            for (int i = (todayList.size() - 1); i > 0; i--) {
//                float change = Float.parseFloat(todayList.get(i).getUserValue()) - Float.parseFloat(todayList.get(i - 1).getUserValue());
//                Log.e(TAG, "onCreate: " + change);
//                changeList.add((int) change);
//            }

//            glucoArrayList.add(new Gluco(todayList.get(0).getValue(),
//                    todayList.get(0).getType(),
//                    todayList.get(0).getDate(),
//                    todayList.get(0).getTime(),
//                    todayList.get(0).getTimestamp(),
//                    todayList.get(0).getLongTs(),
//                    todayList.get(0).getDatetime(), 0));

            /**
             *         fitness.setType(userInputMap.get("selectType"));
             *                 fitness.setSelectTypeDetail(userInputMap.get("selectTypeDetail"));
             *                 fitness.setSelectRpeExpression(userInputMap.get("selectRpeExpression"));
             *                 fitness.setFitnessTime(userInputMap.get("fitnessTime"));
             *                 fitness.setDistance(userInputMap.get("fitnessDistance"));
             *                 fitness.setSpeed(userInputMap.get("fitnessSpeed"));
             *                 fitness.setRpeScore(userInputMap.get("rpeScore"));
             *                 fitness.setKcal(userKcal);
             *                 fitness.setDate(dateTime[0]);
             *                 fitness.setTime(dateTime[1]);
             *                 fitness.setTimestamp(userInputMap.get("timestamp"));
             *                 fitness.setLongTs(userTs);
             *                 fitness.setDatetime(userDateTimes);
             */

            for (int i = 0; i < todayList.size(); i++) {
                fitnesArrayList.add(new Fitnes(todayList.get(i).getType(),
                        todayList.get(i).getSelectTypeDetail(),
                        todayList.get(i).getSelectRpeExpression(),
                        todayList.get(i).getFitnessTime(),
                        todayList.get(i).getDistance(),
                        todayList.get(i).getSpeed(),
                        todayList.get(i).getRpeScore(),
                        todayList.get(i).getKcal(),
                        todayList.get(i).getDate(),
                        todayList.get(i).getTime(),
                        todayList.get(i).getTimestamp(),
                        todayList.get(i).getLongTs(),
                        todayList.get(i).getDatetime()));
            }

        } else {
            // TODO: 2018-10-02 값이 없을때 뷰처리
            recyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        }
    }


    private void initSetting() {
        bindView();
        initRealm();
        initToolbar();
        setStatusBar();
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

    private void initToolbar() {
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
    }

    private void setStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.diary_bottom_appbar_color2));
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
                        RealmResults<Fitness> tmp = realm.where(Fitness.class).equalTo("date", eventDate).findAll();

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
                String selectedDate = DateFormat.format("yyyy-MM-dd", date).toString();
                userSelectedGlobalDate = DateFormat.format("yyyy-MM-dd", date).toString();
//                Toast.makeText(DiaryFitnessActivity.this, selectedDate + " selected!", Toast.LENGTH_SHORT).show();
//                Log.e("onDateSelected", selectedDateStr + " - Position = " + position);
                Log.e("onDateSelected", selectedDate + " - Position = " + position);
                sortAndProcessGlucose(selectedDate, true);
                adapter.notifyDataSetChanged();
            }
        });

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
            case R.id.filter:
                final CharSequence[] items = {"오름차순", "내림차순"};
                AlertDialog.Builder builder = new AlertDialog.Builder(DiaryFitnessActivity.this);
                builder.setTitle("Set filter");
                builder.setSingleChoiceItems(items, -1, (dialog, which) -> {
                    Log.e(TAG, "onClick: " + which);
                    switch (which) {
                        case 0:
                            sortAndProcessGlucose(userSelectedGlobalDate, true, Sort.ASCENDING);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                            break;
                        case 1:
                            sortAndProcessGlucose(userSelectedGlobalDate, true, Sort.DESCENDING);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                            break;
                    }
                });

                builder.show();

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
        Intent intent = new Intent(DiaryFitnessActivity.this, WriteFitnessActivity.class);
        startActivity(intent);

    }

    @Override
    public void onItemClick(View v, int position) {

        Log.e(TAG, "onItemClick: " + position);

        bundle.putString("userType", fitnesArrayList.get(position).getType());
        bundle.putString("userDetailType", fitnesArrayList.get(position).getSelectTypeDetail());
        bundle.putString("userREPDetail", fitnesArrayList.get(position).getSelectRpeExpression());
        bundle.putString("userDate", fitnesArrayList.get(position).getDate());
        bundle.putString("userTime", fitnesArrayList.get(position).getTime());
        bundle.putString("userTimestamp", fitnesArrayList.get(position).getTimestamp());
        bundle.putLong("userTimestampLong", fitnesArrayList.get(position).getLongTs());
        bundle.putString("userFitnessTime", fitnesArrayList.get(position).getFitnessTime());
        bundle.putString("userFitnessDistance", fitnesArrayList.get(position).getDistance());
        bundle.putString("userFitnessSpeed", fitnesArrayList.get(position).getSpeed());
        bundle.putString("userREPScore", fitnesArrayList.get(position).getRpeScore());
        bundle.putString("userKcal", fitnesArrayList.get(position).getKcal());
        type = fitnesArrayList.get(position).getType();
        String detailType = fitnesArrayList.get(position).getSelectTypeDetail();

        bundle.putInt("userTypePosition", checkPositionType(type));
        bundle.putInt("userDetailTypePosition", checkPositionDetailType(detailType));

        Intent intent = new Intent(DiaryFitnessActivity.this, EditFitnessActivity.class);
        intent.putExtra(IntentConst.USER_EDIT_FITNESS, bundle);
        startActivity(intent);
    }

    /**
     * 타입에 대해 인덱스 처리하는 메소드
     * @param type
     * @return
     */
    private int checkPositionType(String type) {
        int position = 0;
        switch (type) {
            case "트레드밀":
                position = 0;
                break;
            case "실내자전거":
                position = 1;
                break;
        }
        return position;
    }

    private int checkPositionDetailType(String detailType) {
        int position = 0;
        if (type.equals("트레드밀")) {
            switch (detailType) {
                case "가볍게 걷기":
                    position = 0;
                    break;
                case "일반 걷기":
                    position = 1;
                    break;
                case "달리기":
                    position = 2;
                    break;
            }
        } else if (type.equals("실내자전거")) {
            switch (detailType) {
                case "보통으로":
                    position = 0;
                    break;
                case "빠르게":
                    position = 1;
                    break;
                case "가볍게":
                    position = 2;
                    break;
            }
        }

        return position;
    }


    @Override
    public void onItemLongClick(View v, int position) {

        String timeStamps = fitnesArrayList.get(position).getTimestamp();

        Log.e(TAG, "onItemLongClick: " + position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("경고");
        builder.setMessage("삭제하시겠어요?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final RealmResults<Fitness> results = realm.where(Fitness.class).equalTo("timestamp", timeStamps).findAll();
                Log.e(TAG, "onClick: results size -->" + results.size());
                realm.executeTransaction(realm -> results.deleteAllFromRealm());
                fitnesArrayList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
        builder.show();

    }

    @Override
    protected void onRestart() {
        sortAndProcessGlucose(userSelectedGlobalDate, true);
        adapter = new DiaryFitnessAdapter(this, fitnesArrayList);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);
        super.onRestart();
    }
}
