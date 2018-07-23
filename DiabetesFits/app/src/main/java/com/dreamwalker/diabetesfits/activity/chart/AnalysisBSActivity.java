package com.dreamwalker.diabetesfits.activity.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.model.isens.BloodSugar;
import com.github.mikephil.charting.charts.HorizontalBarChart;
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
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class AnalysisBSActivity extends AppCompatActivity {

    ArrayList<BloodSugar> mBSList;

    @BindView(R.id.analysis_linechart)
    LineChart lineChart;

    @BindView(R.id.horizontal_bar_chart)
    HorizontalBarChart horizontalBarChart;

    int typeNormalCount = 0;
    int typeBeforeMealCount = 0;
    int typeAfterMealCount = 0;
    int gongBokCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_bs);
        setTitle("혈당계 데이터 분석");
        ButterKnife.bind(this);
        Paper.init(this);

        mBSList = new ArrayList<>();
        mBSList = Paper.book("syncBms").read("data");
        setBarChart();
        setLineChart();

    }

    private void setBarChart() {

        for (int k = 0; k < mBSList.size(); k++) {
            if (mBSList.get(k).getTypeValue() == 0) {
                typeNormalCount += 1;
            }
            if (mBSList.get(k).getTypeValue() == 1) {
                typeBeforeMealCount += 1;
            }
            if (mBSList.get(k).getTypeValue() == 2) {
                typeAfterMealCount += 1;
            }
            if (mBSList.get(k).getTypeValue() == 3) {
                gongBokCount += 1;
            }
        }

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, (float) typeNormalCount));
        barEntries.add(new BarEntry(1, (float) typeBeforeMealCount));
        barEntries.add(new BarEntry(2, (float) typeAfterMealCount));
        barEntries.add(new BarEntry(3, (float) gongBokCount));
        BarDataSet barDataSet = new BarDataSet(barEntries, "유형별 데이터 양");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f);
//        horizontalBarChart.getAxisRight().setEnabled(false);
//        horizontalBarChart.getAxisLeft().setEnabled(false);
        horizontalBarChart.animateXY(1500, 1500);
        horizontalBarChart.setData(barData);

        String[] values = new String[]{"일반", "공복", "식전", "식후",};
        XAxis barXAxis = horizontalBarChart.getXAxis();
        barXAxis.setValueFormatter(new XAxisValueFormatter(values));
        barXAxis.setGranularity(1f); // restrict interval to 1 (minimum)

    }

    private void setLineChart() {
        // 라인차트 부분
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

        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.removeAllLimitLines();
        leftYAxis.addLimitLine(upperLine);
        leftYAxis.addLimitLine(lowerLine);
        leftYAxis.addLimitLine(DangerLine);
//        leftYAxis.setAxisMaximum(100f);
        leftYAxis.setAxisMinimum(20f);
        leftYAxis.setDrawLimitLinesBehindData(true);
        XAxis xAxis = lineChart.getXAxis();
        //xAxis.setValueFormatter(new XAxisValueFormatter(values));
        //xAxis.setGranularity(1);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        ArrayList<Entry> yValues = new ArrayList<>();
        for (int i = 0; i < mBSList.size(); i++) {
            float value = Float.valueOf(mBSList.get(i).getBsValue());
            yValues.add(new Entry(i, value));
        }

        LineDataSet lineDataSet = new LineDataSet(yValues, "DataSet01");
        lineDataSet.setFillAlpha(110);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.3f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setColor(getResources().getColor(R.color.colorAccent));
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
