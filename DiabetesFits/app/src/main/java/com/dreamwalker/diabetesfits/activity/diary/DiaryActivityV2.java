package com.dreamwalker.diabetesfits.activity.diary;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.diary.GlobalDiaryAdapter;
import com.dreamwalker.diabetesfits.adapter.diary.ItemClickListener;
import com.dreamwalker.diabetesfits.consts.GlobalTag;
import com.dreamwalker.diabetesfits.consts.IntentConst;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diabetesfits.database.model.Fitness;
import com.dreamwalker.diabetesfits.database.model.Glucose;
import com.dreamwalker.diabetesfits.model.diary.Global;
import com.dreamwalker.diarydatepicker.DatePickerTimeline;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
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

    @BindView(R.id.home)
    ImageView homeButton;

    @BindView(R.id.toolbar)
    Toolbar toolbar;


    Realm realm;
    RealmConfiguration realmConfiguration;

    RealmResults<Glucose> glucoseResults;
    RealmResults<Fitness> fitnessesResults;

    LinearLayoutManager layoutManager;
    ArrayList<Global> globalArrayList = new ArrayList<>();
    GlobalDiaryAdapter adapter;
    SimpleDateFormat simpleDateFormat;

    String todayString;
    Bundle bundle = new Bundle();

    String type;
    String userSelectedGlobalDate;

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
        initToasty();
        initTargetView();
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
            userSelectedGlobalDate =  simpleDateFormat.format(calendar.getTime());
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

    private void initToasty() {
        Toasty.Config.getInstance().apply();
    }


    private void initTargetView() {

        final Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics disp = getApplicationContext().getResources().getDisplayMetrics();
        int deviceWidth = disp.widthPixels;
        int deviceHeight = disp.heightPixels;
        Log.e(TAG, "initTargetView: " + deviceWidth + "||" + deviceHeight);
        Log.e(TAG, "initTargetView: " + display.getWidth() + "||" + display.getHeight());
        final Drawable droid = ContextCompat.getDrawable(this, R.drawable.ic_loyalty_white_24dp);// Load our little droid guy
        final Rect droidTarget = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);   // Tell our droid buddy where we want him to appear
        droidTarget.offset(120, deviceHeight - 100); // Using deprecated methods makes you look way cool

        final Drawable droid2 = ContextCompat.getDrawable(this, R.drawable.ic_fitness_center_white_24dp);// Load our little droid guy
        final Rect droidTarget2 = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);   // Tell our droid buddy where we want him to appear
        droidTarget2.offset(220, deviceHeight - 100); // Using deprecated methods makes you look way cool

        final Drawable droid3 = ContextCompat.getDrawable(this, R.drawable.ic_multiline_chart_white_24dp);// Load our little droid guy
        final Rect droidTarget3 = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);   // Tell our droid buddy where we want him to appear
        droidTarget3.offset(320, deviceHeight - 100); // Using deprecated methods makes you look way cool

        final Drawable droid4 = ContextCompat.getDrawable(this, R.drawable.ic_tune_white_24dp);// Load our little droid guy
        final Rect droidTarget4 = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);   // Tell our droid buddy where we want him to appear
        droidTarget4.offset(420, deviceHeight - 100); // Using deprecated methods makes you look way cool

        final TapTargetSequence sequence = new TapTargetSequence(this)
                .targets(
                        TapTarget.forBounds(droidTarget, "혈당 다이어리", "혈당 다이어리는 혈당 기록만 볼 수 있습니다.")
                                .cancelable(false)
                                .icon(droid)
                                .id(1),
                        TapTarget.forBounds(droidTarget2, "운동 다이어리", "운동 다이어리는 혈당 기록만 볼 수 있습니다.")
                                .cancelable(false)
                                .icon(droid2)
                                .id(2),
                        TapTarget.forBounds(droidTarget3, "차트 보기", "다이어리 그래프 보기 ")
                                .cancelable(false)
                                .icon(droid3)
                                .id(3),
                        TapTarget.forBounds(droidTarget4, "데이터 정렬", "시간 순으로 정렬해줍니다.(최신순, 과거순)")
                                .cancelable(false)
                                .icon(droid4)
                                .id(4),

                        TapTarget.forView(findViewById(R.id.home), "되돌아오기", "오늘 날짜로 되돌아올 수 있습니다.")
                                .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                .targetCircleColor(R.color.white)   // Specify a color for the target circle
                                .titleTextSize(20)                  // Specify the size (in sp) of the title text
                                .titleTextColor(R.color.white)      // Specify the color of the title text
                                .descriptionTextSize(18)            // Specify the size (in sp) of the description text
                                .textColor(R.color.diary_bottom_appbar_color2)            // Specify a color for both the title and description text
                                .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                                .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)                   // Whether to tint the target view's color
                                .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                .targetRadius(60)
                                .id(5)
                )
                .listener(new TapTargetSequence.Listener() {
                    @Override
                    public void onSequenceFinish() {
                        Paper.book().write("diary_tutorial", true);
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        Log.d("TapTargetView", "Clicked on " + lastTarget.id());
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                });

        if (Paper.book().read("diary_tutorial") != null) {
            boolean tutorial = Paper.book().read("diary_tutorial");
            if (tutorial) {
                // TODO: 2018-10-05
                Log.e(TAG, "initTargetView: " + "튜토리얼 진행함");
            } else {
                Log.e(TAG, "initTargetView: " + "튜토리얼 진행안함");
                sequence.start();
            }
        } else {
            sequence.start();
        }


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.diary_glucose:
                startActivity(new Intent(DiaryActivityV2.this, DiaryGlucoseActivity.class));
                return true;
            case R.id.diary_fitness:
                startActivity(new Intent(DiaryActivityV2.this, DiaryFitnessActivity.class));
                return true;
            case R.id.chart:
                Toasty.error(this, "공사중..", Toast.LENGTH_SHORT, true).show();
//                Calendar defaultDate = Calendar.getInstance();
//                timeline.setSelectedDate(defaultDate.get(Calendar.YEAR), defaultDate.get(Calendar.MONTH), defaultDate.get(Calendar.DAY_OF_MONTH));
                return true;

            case R.id.filter:
                final CharSequence[] items = {"오름차순", "내림차순"};
                AlertDialog.Builder builder = new AlertDialog.Builder(DiaryActivityV2.this);
                if (globalArrayList.size() != 0) {
                    builder.setTitle("Set filter");
                    builder.setSingleChoiceItems(items, -1, (dialog, which) -> {
                        Log.e(TAG, "onClick: " + which);
                        switch (which) {
                            case 0:
                                // TODO: 2018-10-05 오름차순 정렬
                                Collections.sort(globalArrayList, (o1, o2) -> {
                                    if (o1.getDatetime().getTime() < o2.getDatetime().getTime()) {
                                        return -1;
                                    } else if (o1.getDatetime().getTime() > o2.getDatetime().getTime()) {
                                        return 1;
                                    }
                                    return 0;
                                });
//                            sortAndProcessGlucose(userSelectedGlobalDate, true, Sort.ASCENDING);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                                break;
                            case 1:

                                // TODO: 2018-10-05 내림차순 정렬
                                Collections.sort(globalArrayList, (o1, o2) -> {
                                    if (o1.getDatetime().getTime() < o2.getDatetime().getTime()) {
                                        return 1;
                                    } else if (o1.getDatetime().getTime() > o2.getDatetime().getTime()) {
                                        return -1;
                                    }
                                    return 0;
                                });
//                            sortAndProcessGlucose(userSelectedGlobalDate, true, Sort.DESCENDING);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                                break;
                        }
                    });

                    builder.show();

                } else {

                    builder.setTitle("알림");
                    builder.setMessage("정렬할 데이터가 없습니다.");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                }

                break;

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
        int dataTag = globalArrayList.get(position).getTag();
        String timestamp = globalArrayList.get(position).getTimestamp();
        switch (dataTag) {
            case 0:

                bundle.putString("userType", globalArrayList.get(position).getType());
                bundle.putString("userValue", globalArrayList.get(position).getUserValue());
                bundle.putString("userDate", globalArrayList.get(position).getDate());
                bundle.putString("userTime", globalArrayList.get(position).getTime());
                bundle.putString("userTimestamp", globalArrayList.get(position).getTimestamp());
                bundle.putLong("userTimestampLong", globalArrayList.get(position).getLongTs());

                Intent intent = new Intent(DiaryActivityV2.this, EditGlucoseActivity.class);
                intent.putExtra(IntentConst.USER_EDIT_GLUCOSE, bundle);
                startActivity(intent);
                break;
            case 1:

                Fitness result = realm.where(Fitness.class).equalTo("timestamp", timestamp).findFirst();

                bundle.putString("userType", result.getType());
                bundle.putString("userDetailType", result.getSelectTypeDetail());
                bundle.putString("userREPDetail", result.getSelectRpeExpression());
                bundle.putString("userDate", result.getDate());
                bundle.putString("userTime", result.getTime());
                bundle.putString("userTimestamp", result.getTimestamp());
                bundle.putLong("userTimestampLong", result.getLongTs());
                bundle.putString("userFitnessTime", result.getFitnessTime());
                bundle.putString("userFitnessDistance", result.getDistance());
                bundle.putString("userFitnessSpeed", result.getSpeed());
                bundle.putString("userREPScore", result.getRpeScore());
                bundle.putString("userKcal", result.getKcal());
                type = result.getType();
                String detailType = result.getSelectTypeDetail();

                bundle.putInt("userTypePosition", checkPositionType(type));
                bundle.putInt("userDetailTypePosition", checkPositionDetailType(detailType));

                Intent fitnessIntent = new Intent(DiaryActivityV2.this, EditFitnessActivity.class);
                fitnessIntent.putExtra(IntentConst.USER_EDIT_FITNESS, bundle);
                startActivity(fitnessIntent);
                break;
        }

    }

    @Override
    public void onItemLongClick(View v, int position) {
        int dataTag = globalArrayList.get(position).getTag();
        if (dataTag == 0) {
            String timeStamps = globalArrayList.get(position).getTimestamp();
            Log.e(TAG, "onItemLongClick: " + position);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("경고");
            builder.setMessage("혈당 정보를 삭제하시겠어요?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final RealmResults<Glucose> results = realm.where(Glucose.class).equalTo("timestamp", timeStamps).findAll();
                    Log.e(TAG, "onClick: results size -->" + results.size());
                    realm.executeTransaction(realm -> results.deleteAllFromRealm());
                    globalArrayList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
            builder.show();

        } else if (dataTag == 1) {

            String timeStamps = globalArrayList.get(position).getTimestamp();

            Log.e(TAG, "onItemLongClick: " + position);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("경고");
            builder.setMessage("운동정보를 삭제하시겠어요?");
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final RealmResults<Fitness> results = realm.where(Fitness.class).equalTo("timestamp", timeStamps).findAll();
                    Log.e(TAG, "onClick: results size -->" + results.size());
                    realm.executeTransaction(realm -> results.deleteAllFromRealm());
                    globalArrayList.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss());
            builder.show();

        }


    }

    @OnClick(R.id.fab)
    public void onClickedFabButton() {

        Toasty.error(this, "공사중..", Toast.LENGTH_SHORT, true).show();

    }

    @OnClick(R.id.home)
    public void onClickedHomeButton() {
        Calendar defaultDate = Calendar.getInstance();
        timeline.setSelectedDate(defaultDate.get(Calendar.YEAR), defaultDate.get(Calendar.MONTH), defaultDate.get(Calendar.DAY_OF_MONTH));
    }


    class AscendingTime implements Comparator<Long> {

        @Override
        public int compare(Long o1, Long o2) {
            return o2.compareTo(o1);
        }
    }


    /**
     * 타입에 대해 인덱스 처리하는 메소드
     *
     * @param type
     * @return
     */
    private int checkPositionType(String type) {
        int position = 0;
        if (type == null) {
            position = 0;
        } else {
            switch (type) {
                case "트레드밀":
                    position = 0;
                    break;
                case "실내자전거":
                    position = 1;
                    break;
            }
        }

        return position;
    }

    private int checkPositionDetailType(String detailType) {

        int position = 0;
        if (detailType == null || type == null) {
            position = 0;
        } else {
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
        }
        return position;
    }


    @Override
    protected void onRestart() {
        fetchFromRealm(userSelectedGlobalDate, true);
        adapter = new GlobalDiaryAdapter(this, globalArrayList);
        adapter.setItemClickListener(this);
        recyclerView.setAdapter(adapter);
        super.onRestart();
    }
}
