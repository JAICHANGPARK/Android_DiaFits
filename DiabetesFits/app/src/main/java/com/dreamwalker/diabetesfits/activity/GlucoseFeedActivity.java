package com.dreamwalker.diabetesfits.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.database.model.Glucose;
import com.dreamwalker.diabetesfits.utils.timeago.ZamanTextView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

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

    RealmResults<Glucose> glucose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose_feed);
        setStatusBar();
        ButterKnife.bind(this);
        Paper.init(this);
        Realm.init(this);

        Realm realm = Realm.getDefaultInstance();
        glucose = realm.where(Glucose.class).findAll();

// TODO: 2018-07-25 등록된 데이터 없을 떄
        if (glucose.size() == 0){

            recentValueTextView.setText("Empty");
            recentTypeTextView.setText("Empty");

        }else {

            // TODO: 2018-07-25 등록된 데이터 있을 때
            int lastIndex = glucose.size() - 1;

            for (int i = 0;  i < glucose.size(); i++){
                Log.e(TAG, "before: " + glucose.get(i).getValue() + " -- > " + glucose.get(i).getTimestamp());
            }
            // TODO: 2018-07-25 재 정렬
            glucose = glucose.sort("timestamp", Sort.ASCENDING);

            for (int i = 0;  i < glucose.size(); i++){
                Log.e(TAG, "sort after: " + glucose.get(i).getValue() + " -- > " + glucose.get(i).getTimestamp());
            }

            String lastValue = glucose.get(lastIndex).getValue() + " " + "mm/dL";
            String lastType = glucose.get(lastIndex).getType();
            timeAgoTextView.setTimeStamp(Long.valueOf(glucose.get(lastIndex).getTimestamp()) / 1000);
            recentValueTextView.setText(lastValue);
            recentTypeTextView.setText(lastType);

            Log.e(TAG, "onCreate: avg " + glucose.average("value"));
            Log.e(TAG, "onCreate: min " + glucose.min("value") );
            Log.e(TAG, "onCreate: max " + glucose.max("value") );


        }

        setLineGraph();
    }
    private void setLineGraph(){

        ArrayList<Entry> lineEntry = new ArrayList<>();
        for (int i = 0;  i < glucose.size(); i++){

            lineEntry.add(new Entry(i, Float.valueOf(glucose.get(i).getValue())));

            Log.e(TAG, "sort after: " + glucose.get(i).getValue() + " -- > " + glucose.get(i).getTimestamp());
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

        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.dashboard_gradient);
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
