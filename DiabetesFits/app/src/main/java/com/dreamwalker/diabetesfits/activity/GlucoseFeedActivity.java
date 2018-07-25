package com.dreamwalker.diabetesfits.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class GlucoseFeedActivity extends AppCompatActivity {
    private static final String TAG = "GlucoseFeedActivity";

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


    RealmResults<Glucose> glucose;
    DashboardAdapter adapter;

    ArrayList<String> labelList = new ArrayList<>();
    ArrayList<String> valueList = new ArrayList<>();

    int dbSize;
    int[] kindCount = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose_feed);
        setStatusBar();
        ButterKnife.bind(this);
        Paper.init(this);
        Realm.init(this);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        Realm realm = Realm.getDefaultInstance();

        glucose = realm.where(Glucose.class).findAll();

// TODO: 2018-07-25 등록된 데이터 없을 떄
        if (glucose.size() == 0) {

            recentValueTextView.setText("Empty");
            recentTypeTextView.setText("Empty");

        } else {

            // TODO: 2018-07-25 등록된 데이터 있을 때
            int lastIndex = glucose.size() - 1;
            dbSize = glucose.size();

            for (int i = 0; i < glucose.size(); i++) {
                switch (glucose.get(i).getType()) {
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
//                Log.e(TAG, "before: " + glucose.get(i).getValue() + " -- > " + glucose.get(i).getTimestamp());
            }

            // TODO: 2018-07-25 재 정렬
            glucose = glucose.sort("timestamp", Sort.ASCENDING);
//            glucose.max("userValue").floatValue();
//            for (int i = 0; i < glucose.size(); i++) {
//                Log.e(TAG, "sort after: " + glucose.get(i).getValue() + " -- > " + glucose.get(i).getTimestamp());
//            }

            String lastValue = glucose.get(lastIndex).getValue() + " " + "mm/dL";
            String lastType = glucose.get(lastIndex).getType();
            timeAgoTextView.setTimeStamp(Long.valueOf(glucose.get(lastIndex).getTimestamp()) / 1000);
            recentValueTextView.setText(lastValue);
            recentTypeTextView.setText(lastType);

            List<Glucose> arrayList = realm.copyFromRealm(glucose);
            ArrayList<Integer> integerArrayList = new ArrayList<>();

            for (Glucose g : arrayList) {
                integerArrayList.add(Integer.valueOf(g.getValue()));
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

        adapter = new DashboardAdapter(this, labelList, valueList);
        recyclerView.setAdapter(adapter);
        setLineGraph();
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
        for (int i = 0; i < glucose.size(); i++) {

            lineEntry.add(new Entry(i, Float.valueOf(glucose.get(i).getValue())));

//            Log.e(TAG, "sort after: " + glucose.get(i).getValue() + " -- > " + glucose.get(i).getTimestamp());
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
}
