package com.dreamwalker.diabetesfits.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.DashboardAdapter;
import com.dreamwalker.diabetesfits.consts.GlucoseType;
import com.dreamwalker.diabetesfits.database.model.Glucose;
import com.dreamwalker.diabetesfits.utils.timeago.ZamanTextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * 제작 : 박제창
 */
public class GlucoseFeedActivity extends AppCompatActivity {
    private static final String TAG = "GlucoseFeedActivity";

    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;

    @BindView(R.id.recent_type_text_view)
    TextView recentTypeTextView;

    @BindView(R.id.recent_value_text_view)
    TextView recentValueTextView;

    @BindView(R.id.time_ago_text_view)
    ZamanTextView timeAgoTextView;

    @BindView(R.id.line_chart)
    LineChart lineChart;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.recommend_text_view)
    TextView recommendTextView;

    RealmResults<Glucose> glucose;
    RealmResults<Glucose> forChartGlucose;
    DashboardAdapter adapter;

    ArrayList<String> labelList = new ArrayList<>();
    ArrayList<String> valueList = new ArrayList<>();

    int dbSize;
    // TODO: 2018-07-26    0,      1,    2 , 3,   4  ,5  ,6  ,7  ,8  ,9,  10  - 박제창
    // TODO: 2018-07-26 FASTING, SLEEP, BB, BA , LB, LA, DB, DA, FB, FA, UNKNOWN   -- 박제창
    int[] kindCount = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};


    String userGlucoseMin;
    String userGlucoseMax;
    boolean userGlucoseValueCheckFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose_feed);
        setStatusBar();
        ButterKnife.bind(this);
        Paper.init(this);
        Realm.init(this);

        userGlucoseMin = Paper.book("user").read("userGlucoseMin");
        userGlucoseMax = Paper.book("user").read("userGlucoseMax");

        if (userGlucoseMin == null && userGlucoseMax != null) {
            recommendTextView.setText("프로필에서 최소 목표 혈당을 설정해주세요");
        }
        if (userGlucoseMin != null && userGlucoseMax == null) {
            recommendTextView.setText("프로필에서 최고 목표 혈당을 설정해주세요");
        }
        if (userGlucoseMin != null && userGlucoseMax != null) {
            userGlucoseValueCheckFlag = true;
        }


        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        Realm realm = Realm.getDefaultInstance();

        Calendar now = Calendar.getInstance();

        long s = now.getTimeInMillis();
        long oneDay = 10 * (60 * 60 * 24);
        long e = s - oneDay;
        Date startDate = new Date(s);
        Date endDate = new Date(e);

        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_MONTH, 1); // make sure month stays valid
//        calendar.set(Calendar.YEAR, 2018);
//        calendar.set(Calendar.MONTH, Calendar.JULY);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
        Date jan1 = new Date(calendar.getTimeInMillis());
        Log.e(TAG, "jan1 time -->" + calendar.getTimeInMillis());
        // calendar.set(Calendar.DAY_OF_MONTH, 2);
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        Date jan2 = new Date(calendar.getTimeInMillis());
        Log.e(TAG, "jan2 time -->" + calendar.getTimeInMillis());


        Log.e(TAG, "start --> " + s);
        Log.e(TAG, "Oneday --> " + oneDay);
        Log.e(TAG, "end --> " + e);
//        glucose = realm.where(Glucose.class).greaterThanOrEqualTo("longTs", e).lessThan("longTs", s).findAll();
        glucose = realm.where(Glucose.class).findAll();
        forChartGlucose = realm.where(Glucose.class).greaterThanOrEqualTo("datetime", jan2).lessThan("datetime", jan1).findAll();

        Log.e(TAG, "all data size --> " + glucose.size());
        Log.e(TAG, "onCreate: forChartGlucose size --> " + forChartGlucose.size());

// TODO: 2018-07-25 등록된 데이터 없을 떄
        try {

            if (glucose.size() == 0 && forChartGlucose.size() == 0) {

                recentValueTextView.setText("Empty");
                recentTypeTextView.setText("Empty");

                runOnUiThread(() -> {
                    lineChart.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                    animationView.setVisibility(View.VISIBLE);
                    animationView.playAnimation();
                });


            } else {

                runOnUiThread(() -> {
                    lineChart.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyLayout.setVisibility(View.GONE);
                    animationView.setVisibility(View.GONE);
                    animationView.cancelAnimation();
                });


                // TODO: 2018-07-25 등록된 데이터 있을 때
                int lastIndex = glucose.size() - 1;
                dbSize = glucose.size();

                for (int i = 0; i < glucose.size(); i++) {
                    switch (glucose.get(i).getType()) {
                        // TODO: 2018-07-26 혈당계로 부터 받아온 유형을 알수 없는 데이터 계수 - 박제창
                        case GlucoseType.BSM_UNKNOWN:
                            kindCount[10] = kindCount[10] + 1;
                            break;
                        case GlucoseType.BSM_BEFORE_MEAL:
                            kindCount[10] = kindCount[10] + 1;
                            break;
                        case GlucoseType.BSM_AFTER_MEAL:
                            kindCount[10] = kindCount[10] + 1;
                            break;
                        // TODO: 2018-07-26 입력으로 알수있는 유형 계수 - 박제창
                        case GlucoseType.FASTING:
                            kindCount[0] = kindCount[0] + 1;
                            break;
                        case GlucoseType.SLEEP:
                            kindCount[1] = kindCount[1] + 1;
                            break;
                        case GlucoseType.BREAKFAST_BEFORE:
                            kindCount[2] = kindCount[2] + 1;
                            break;
                        case GlucoseType.BREAKFAST_AFTER:
                            kindCount[3] = kindCount[3] + 1;
                            break;
                        case GlucoseType.LUNCH_BEFORE:
                            kindCount[4] = kindCount[4] + 1;
                            break;
                        case GlucoseType.LUNCH_AFTER:
                            kindCount[5] = kindCount[5] + 1;
                            break;
                        case GlucoseType.DINNER_BEFORE:
                            kindCount[6] = kindCount[6] + 1;
                            break;
                        case GlucoseType.DINNER_AFTER:
                            kindCount[7] = kindCount[7] + 1;
                            break;
                        case GlucoseType.FITNESS_BEFORE:
                            kindCount[8] = kindCount[8] + 1;
                            break;
                        case GlucoseType.FITNESS_AFTER:
                            kindCount[9] = kindCount[9] + 1;
                            break;
                        default:
                            break;
                    }
//                Log.e(TAG, "onCreate: " + glucose.get(i).getType());
                    //Log.e(TAG, "before: " + glucose.get(i).getValue() + " -- > " + glucose.get(i).getTimestamp() + ", " + glucose.get(i).getType());
                }

                // TODO: 2018-07-25 재 정렬
                glucose = glucose.sort("timestamp", Sort.ASCENDING);

                forChartGlucose = forChartGlucose.sort("timestamp", Sort.ASCENDING);
//            forChartGlucose = realm.where(Glucose.class).between("datetime", endDate, startDate).findAll();
//
//            for (int i = 0; i < forChartGlucose.size(); i++) {
//                Log.e(TAG, "Search --> " + forChartGlucose.get(i).getValue() + " | " + forChartGlucose.get(i).getTimestamp());
//            }
////            glucose.max("userValue").floatValue();
//            for (int i = 0; i < glucose.size(); i++) {
//                Log.e(TAG, "sort after: " + glucose.get(i).getValue() + " -- > " + glucose.get(i).getTimestamp());
//            }

                String lastValue = glucose.get(lastIndex).getValue() + " " + "mm/dL";
                String lastType = glucose.get(lastIndex).getType();
                timeAgoTextView.setTimeStamp(Long.valueOf(glucose.get(lastIndex).getTimestamp()) / 1000);
                recentValueTextView.setText(lastValue);
                recentTypeTextView.setText(lastType);

                // TODO: 2018-08-28 만약 최소, 최대 목표 혈당이 설정되어 있다면 최근 혈당 수치에 따라 운동추천을 표기한다.-박제창
                if (userGlucoseValueCheckFlag){
                    if (Integer.valueOf(lastValue) > Integer.valueOf(userGlucoseMax)){
                        recommendTextView.setText("현재 측정된 혈당이 목표 최고 혈당 수치보다 높습니다. \n 운동을 수행해 목표 혈당 구간 내 유지가 필요한 시점입니다.");
                    } else if (Integer.valueOf(lastValue) < Integer.valueOf(userGlucoseMin)){
                        recommendTextView.setText("최저 목표 혈당 수치보다 높습니다. 저혈당 위험이 있으므로 당분을 섭취하여 목표혈당 구간으로 유지가 필요합니다.");
                    }
                }
                else {
                    Log.e(TAG, "onCreate: " + "사용자 설정된 혈당 최대, 최소 중 무언가 하나가 빠져있음" );
                }

                List<Glucose> arrayList = realm.copyFromRealm(glucose);
                ArrayList<Integer> integerArrayList = new ArrayList<>();

                for (Glucose g : arrayList) {

                    // TODO: 2018-07-26 소수점으로 인한  NumberFormatException 수정 - 박제창
                    String tmp = g.getValue();
                    if (tmp.length() == 5) {
                        tmp = tmp.substring(0, 3);
                        integerArrayList.add(Integer.valueOf(tmp));
                    } else if (tmp.length() == 3) {
                        integerArrayList.add(Integer.valueOf(tmp));
                    }

                    //Log.e(TAG, "onCreate: tmp --> " + tmp);

                }

//            Log.e(TAG, "onCreate: min Value " + Collections.min(integerArrayList));
//            Log.e(TAG, "onCreate: avg Value " + Math.round(calculateAverage(integerArrayList)));
//            Log.e(TAG, "onCreate: max Value " + Collections.max(integerArrayList));
                //Log.e(TAG, "onCreate: avg " + glucose.average("value"));

                labelList.add("평균 혈당");
                valueList.add(String.valueOf(Math.round(calculateAverage(integerArrayList))));
                labelList.add("최대 혈당");
                valueList.add(String.valueOf(Collections.max(integerArrayList)));
                labelList.add("최소 혈당");
                valueList.add(String.valueOf(Collections.min(integerArrayList)));
                labelList.add("총 기록수");
                valueList.add(String.valueOf(dbSize));

                labelList.add("유형 분류 필요 수");
                valueList.add(String.valueOf(kindCount[10]));

                labelList.add("공복 기록 수");
                valueList.add(String.valueOf(kindCount[0]));
                labelList.add("취침 전");
                valueList.add(String.valueOf(kindCount[1]));

                labelList.add("아침 식전");
                valueList.add(String.valueOf(kindCount[2]));
                labelList.add("아침 식후");
                valueList.add(String.valueOf(kindCount[3]));

                labelList.add("점심 식전");
                valueList.add(String.valueOf(kindCount[4]));
                labelList.add("점심 식후");
                valueList.add(String.valueOf(kindCount[5]));

                labelList.add("저녁 식전");
                valueList.add(String.valueOf(kindCount[6]));
                labelList.add("저녁 식후");
                valueList.add(String.valueOf(kindCount[7]));

                labelList.add("운동 전");
                valueList.add(String.valueOf(kindCount[8]));
                labelList.add("운동 후");
                valueList.add(String.valueOf(kindCount[9]));


            }

        } catch (IndexOutOfBoundsException errer) {
            Log.e(TAG, "onCreate: " + errer.getStackTrace());
        }


        adapter = new DashboardAdapter(this, labelList, valueList);
        recyclerView.setAdapter(adapter);

        if (forChartGlucose.size() != 0) {
            setLineGraph();
        } else {
            // TODO: 2018-07-26 예외처리
        }
    }


    private void userGlucoseValue() {
        String userGlucoseMin = Paper.book("user").read("userGlucoseMin");
        String userGlucoseMax = Paper.book("user").read("userGlucoseMax");
//        userHeight = Paper.book("user").read("userHeight");
//        userWeight = Paper.book("user").read("userWeight");
    }

    private double calculateAverage(List<Integer> marks) {
        Integer sum = 0;
        if (!marks.isEmpty()) {
            for (Integer mark : marks) {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    private void setLineGraph() {

        ArrayList<Entry> lineEntry = new ArrayList<>();
//        for (int i = 0; i < glucose.size(); i++) {
//
//            lineEntry.add(new Entry(i, Float.valueOf(glucose.get(i).getValue())));
//
////            Log.e(TAG, "sort after: " + glucose.get(i).getValue() + " -- > " + glucose.get(i).getTimestamp());
//        }

        if (forChartGlucose.size() != 0) {
            for (int i = 0; i < forChartGlucose.size(); i++) {

                lineEntry.add(new Entry(i, Float.valueOf(forChartGlucose.get(i).getValue())));

//            Log.e(TAG, "sort after: " + glucose.get(i).getValue() + " -- > " + glucose.get(i).getTimestamp());
            }
        } else {
            // TODO: 2018-08-07 PASS 
        }


        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDrawBorders(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);

        LineDataSet lineDataSet = new LineDataSet(lineEntry, "mm/dL");
        lineDataSet.setColor(getResources().getColor(R.color.wave_gradient_amy_crisp_02));
        lineDataSet.setCircleColor(getResources().getColor(R.color.wave_gradient_amy_crisp_02));
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.3f);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setHighLightColor(Color.BLACK);
        lineDataSet.setDrawFilled(true);

        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.dashboard_gradient);
        lineDataSet.setFillDrawable(drawable);
        //lineDataSet.setFillColor(getResources().getColor(R.color.wave_gradient_amy_crisp_02));
        lineDataSet.setFillAlpha(80);


        LineData lineData = new LineData(lineDataSet);


        lineChart.setData(lineData);
        lineChart.invalidate();

    }

    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this, R.color.device_scan_background));
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.wave_gradient_amy_crisp_02));
    }


    @OnClick(R.id.recommend_text_view)
    public void onClickRecommendTextView() {
        startActivity(new Intent(GlucoseFeedActivity.this, ProfileActivity.class));
    }
}
