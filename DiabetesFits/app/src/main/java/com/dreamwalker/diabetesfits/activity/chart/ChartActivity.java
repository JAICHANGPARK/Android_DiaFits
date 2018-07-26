package com.dreamwalker.diabetesfits.activity.chart;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.model.isens.BloodSugar;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class ChartActivity extends AppCompatActivity {

    private static final String TAG = "ChartActivity";

    @BindView(R.id.analysis_line_chart)
    LineChart lineChart;

    @BindView(R.id.bar_chart)
    BarChart barChart;

    ArrayList<BloodSugar> mBSList;
    ArrayList<Float> diffList;
    String[] values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        initSetting();

        mBSList = new ArrayList<>();

//        mBSList = Paper.book("syncBms").read("data");

        Log.e(TAG, "onCreate: mBSList.size() -> " + mBSList.size());
        if (Paper.book("syncBms").read("data") != null) {
            mBSList = Paper.book("syncBms").read("data");
            Log.e(TAG, "onCreate: mBSList.size() -> " + mBSList.size());

            if (mBSList.size() == 0) {
                showErrorDialog();
            } else {
                values = new String[mBSList.size()];

                for (int i = 0; i < mBSList.size(); i++) {
                    String[] tmp = mBSList.get(i).getBsTime().split(",");

//                    values[i] = mBSList.get(i).getBsTime();
                    values[i] = tmp[0];
                }

                setLineChart();

                int length = mBSList.size();
                int endIndex = length - 1;
                Log.e(TAG, "onCreate: " + length);
                Log.e(TAG, "onCreate: " + mBSList.get(endIndex).getBsValue());
                int count = 0;

                diffList = new ArrayList<>();
                for (int i = 0; i < mBSList.size() - 1; i++) {
                    float tmp_before = Float.valueOf(mBSList.get(i).getBsValue());
                    float tmp_after = Float.valueOf(mBSList.get(i + 1).getBsValue());
                    float diff = tmp_before - tmp_after;
                    diffList.add(diff);
                    Log.e(TAG, "onCreate: " + count + " -> " + diff);
                    count++;
                }



//               for (int i = endIndex; i >= 0; i--){
//
//                    float tmp_before = Float.valueOf(mBSList.get(i).getBsValue());
//                    float tmp_after = Float.valueOf(mBSList.get(i - 1).getBsValue());
//                    float diff = tmp_before - tmp_after;
//
//                    Log.e(TAG, "onCreate: " + count + " -> "+ diff );
//                    count++;
//
//                }

                setBarChart();
            }
        } else {
            showErrorDialog();
        }
    }



    private void showErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChartActivity.this);
        builder.setTitle("알림");
        builder.setMessage("등록된 혈당 데이터가 없습니다. 혈당계를 사용해 데이터를 동기화 하세요");
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }


    private void setBarChart() {

        // TODO: 2018-07-23 X 축 처리
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new XAxisValueFormatter(values));
        xAxis.setGranularity(10.0f);
        xAxis.setLabelRotationAngle(-45);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < diffList.size(); i++){
            barEntries.add(new BarEntry(i, diffList.get(i)));
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "혈당 차");
        BarData barData = new BarData(barDataSet);


        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.setData(barData);
        barChart.invalidate(); // refresh


    }

    private void setLineChart() {

        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);

        LimitLine upperLine = new LimitLine(200f, "Max");
        upperLine.setLineWidth(2f);
        upperLine.setLineColor(Color.GREEN);
        //upperLine.enableDashedLine(10f,10f,5f);
        upperLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upperLine.setTextSize(15f);

        LimitLine lowerLine = new LimitLine(50f, "Min");
        lowerLine.setLineWidth(2f);
        lowerLine.setLineColor(Color.YELLOW);
        //lowerLine.enableDashedLine(10f,10f,5f);
        lowerLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lowerLine.setTextSize(15f);

        LimitLine DangerLine = new LimitLine(60f, "Danger");
        DangerLine.setLineWidth(2f);
        DangerLine.setLineColor(Color.RED);
        //lowerLine.enableDashedLine(10f,10f,5f);
        DangerLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        DangerLine.setTextSize(15f);

        // TODO: 2018-07-23 y 축 처리
        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.removeAllLimitLines();
        leftYAxis.addLimitLine(upperLine);
        leftYAxis.addLimitLine(lowerLine);
        leftYAxis.addLimitLine(DangerLine);
//        leftYAxis.setAxisMaximum(100f);
        leftYAxis.setAxisMinimum(20f);
        leftYAxis.setDrawLimitLinesBehindData(true);

        // TODO: 2018-07-23 X 축 처리 
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new XAxisValueFormatter(values));
        xAxis.setGranularity(10.0f);
        xAxis.setLabelRotationAngle(-45);

        // TODO: 2018-07-23 데이터 처리
        lineChart.getAxisRight().setEnabled(false);
        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i = 0; i < mBSList.size(); i++) {
            float value = Float.valueOf(mBSList.get(i).getBsValue());
            yValues.add(new Entry(i, value));
        }

        LineDataSet lineDataSet = new LineDataSet(yValues, "Glucose");
        lineDataSet.setFillAlpha(110);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.3f);
        lineDataSet.setCircleRadius(2.0f);

//        for (int i = 0 ;  i < mBSList.size(); i++){
//            float tmp = Float.valueOf(mBSList.get(i).getBsValue());
//            if (tmp >= 200f){
//                lineDataSet.setColor(Color.GREEN);
//            }else if (tmp >= 50.0f && tmp <200.0f){
//                lineDataSet.setColor(getResources().getColor(R.color.colorAccent));
//            }
//            else if (tmp < 50.0f){
//                lineDataSet.setColor(Color.YELLOW);
//            }
//        }

        //lineDataSet.setColor(getResources().getColor(R.color.colorAccent));
        lineDataSet.setCircleColor(getResources().getColor(R.color.colorPrimary));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setLineWidth(1.4f);
        lineDataSet.setValueTextSize(11f);

        ArrayList<ILineDataSet> dataSet = new ArrayList<>();
        dataSet.add(lineDataSet);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.animateXY(1500, 1500);

    }

    private void initSetting() {
        viewBinding();
        initPaper();
    }

    private void viewBinding() {
        ButterKnife.bind(this);
    }

    private void initPaper() {
        Paper.init(this);
    }

    public class XAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;

        public XAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            // "value" represents the position of the label on the axis (x or y)
            return mValues[(int) value];
        }
    }
}
