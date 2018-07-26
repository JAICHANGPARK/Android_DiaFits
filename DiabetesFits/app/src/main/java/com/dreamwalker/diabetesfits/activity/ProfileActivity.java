package com.dreamwalker.diabetesfits.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.CustomItemClickListener;
import com.dreamwalker.diabetesfits.adapter.ProfileAdapter;
import com.dreamwalker.diabetesfits.database.model.Glucose;
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

public class ProfileActivity extends AppCompatActivity implements CustomItemClickListener{

    private static final String TAG = "ProfileActivity";
    @BindView(R.id.name_text)
    TextView nameTextView;

    @BindView(R.id.line_chart)
    LineChart lineChart;

    @BindView(R.id.home)
    ImageView imageView;


//    @BindView(R.id.card_view)
//    CardView cardView;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    String userName;
    String helloMessage;

    Realm realm;
    RealmResults<Glucose> glucoses;

    Drawable drawable;

    int lineColorFlag = 0;

    ProfileAdapter adapter;


    ArrayList<String> labelList = new ArrayList<>();
    ArrayList<String> valueList = new ArrayList<>();

    String userGlucoseMin, userGlucoseMax;
    String userHeight, userWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Paper.init(this);
        Realm.init(this);
        setTitleTextView();


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        userGlucoseMin = Paper.book("user").read("userGlucoseMin");
        userGlucoseMax = Paper.book("user").read("userGlucoseMax");
        userHeight = Paper.book("user").read("userHeight");
        userWeight = Paper.book("user").read("userWeight");


        if (userGlucoseMin == null){
            userGlucoseMin = "None";
        }

        if (userGlucoseMax == null){
            userGlucoseMax = "None";
        }

        if (userHeight == null){
            userHeight = "None";
        }

        if (userWeight == null){
            userWeight = "None";
        }



        setProfileData();

        Calendar calendar = Calendar.getInstance();
        Date jan1 = new Date(calendar.getTimeInMillis());
        Log.e(TAG, "jan1 time -->" + calendar.getTimeInMillis());
        // calendar.set(Calendar.DAY_OF_MONTH, 2);
        calendar.add(Calendar.DAY_OF_MONTH, -1); //1일 뺀다
        Date jan2 = new Date(calendar.getTimeInMillis());
        Log.e(TAG, "jan2 time -->" + calendar.getTimeInMillis());

        realm = Realm.getDefaultInstance();
        glucoses = realm.where(Glucose.class).greaterThanOrEqualTo("datetime", jan2).lessThan("datetime", jan1).findAll();
//        while (glucoses.size() != 0){
//            calendar.add(Calendar.DAY_OF_MONTH, -1);
//            jan2 = new Date(calendar.getTimeInMillis());
//            glucoses = realm.where(Glucose.class).greaterThanOrEqualTo("datetime", jan2).lessThan("datetime", jan1).findAll();
//        }
        if (glucoses.size() == 0) {
            Log.e(TAG, "onCreate: ");
        } else {
            Log.e(TAG, "onCreate: " + glucoses.size());
            setLineGraph();
        }

    }

    private void setProfileData(){
        labelList.add("최소목표혈당");
        valueList.add(userGlucoseMin);

        labelList.add("최대목표혈당");
        valueList.add(userGlucoseMax);

        labelList.add("체중(kg)");
        valueList.add(userWeight);

        labelList.add("신장(m)");
        valueList.add(userHeight);

        adapter = new ProfileAdapter(this, labelList, valueList);
        adapter.setCustomItemClickListener(this);

        recyclerView.setAdapter(adapter);


    }

    private void setLineGraph() {

        ArrayList<Entry> lineEntry = new ArrayList<>();
        for (int i = 0; i < glucoses.size(); i++) {
            lineEntry.add(new Entry(i, Float.valueOf(glucoses.get(i).getValue())));
        }
        // TODO: 2018-07-26 최대 최소 값을 연산하기 위해 문자열을 정수로 변경해야할 필요가 있었습니다. - 박제창
        List<Glucose> arrayList = realm.copyFromRealm(glucoses);
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
        // TODO: 2018-07-26 기본 오프셋을 지운다.
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        lineChart.getAxisLeft().setAxisMaximum(Collections.max(integerArrayList) + 20.0f);
        lineChart.getAxisLeft().setAxisMinimum(Collections.min(integerArrayList) - 20.0f);

        LineDataSet lineDataSet = new LineDataSet(lineEntry, "mm/dL");
        // TODO: 2018-07-26 line color set
        switch (lineColorFlag) {
            case 1:
                lineDataSet.setColor(getResources().getColor(R.color.line_color_morning));
                lineDataSet.setCircleColor(getResources().getColor(R.color.line_color_morning));
                break;
            case 2:
                lineDataSet.setColor(getResources().getColor(R.color.line_color_afternoon));
                lineDataSet.setCircleColor(getResources().getColor(R.color.line_color_afternoon));
                break;
            case 3:
                lineDataSet.setColor(getResources().getColor(R.color.line_color_evening));
                lineDataSet.setCircleColor(getResources().getColor(R.color.line_color_evening));
                break;
            case 4:
                lineDataSet.setColor(getResources().getColor(R.color.line_color_night));
                lineDataSet.setCircleColor(getResources().getColor(R.color.line_color_night));
                break;
            default:
                lineDataSet.setColor(getResources().getColor(R.color.wave_gradient_amy_crisp_02));
                lineDataSet.setCircleColor(getResources().getColor(R.color.wave_gradient_amy_crisp_02));
                break;
        }

        lineDataSet.setCircleHoleRadius(2.0f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setCubicIntensity(0.3f);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setHighLightColor(Color.BLACK);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setDrawValues(false);

        // TODO: 2018-07-26 그라디언트 설정 - 박제창
        lineDataSet.setFillDrawable(drawable);
        //lineDataSet.setFillColor(getResources().getColor(R.color.wave_gradient_amy_crisp_02));
        lineDataSet.setFillAlpha(80);

        LineData lineData = new LineData(lineDataSet);

        lineChart.setData(lineData);
        lineChart.animateY(1500);
        lineChart.invalidate();

    }

    private void setTitleTextView() {


        // TODO: 2018-07-26 시간별 인사 메시지를 다르게 주고 싶어서 - 박제창
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {
            helloMessage = "Good Morning! ";
            lineColorFlag = 1;
            drawable = ContextCompat.getDrawable(this, R.drawable.morning_chart);
            //Toast.makeText(this, "Good Morning", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            helloMessage = "Good Afternoon! ";
            lineColorFlag = 2;
            drawable = ContextCompat.getDrawable(this, R.drawable.afternoon_chart);
            //Toast.makeText(this, "Good Afternoon", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            helloMessage = "Good Evening! ";
            lineColorFlag = 3;
            drawable = ContextCompat.getDrawable(this, R.drawable.dashboard_gradient);
            //Toast.makeText(this, "Good Evening", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            helloMessage = "Good Night! ";
            lineColorFlag = 4;
            drawable = ContextCompat.getDrawable(this, R.drawable.night_chart);
            //Toast.makeText(this, "Good Night", Toast.LENGTH_SHORT).show();
        } else {
            lineColorFlag = 5;
            drawable = ContextCompat.getDrawable(this, R.drawable.dashboard_gradient);
            helloMessage = "Hi !";
        }

        // TODO: 2018-07-25 폰트 생성용 - 박제창
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NotoSansCJKkr-Thin.otf");
        userName = Paper.book("user").read("userID");
        userName = helloMessage + userName;

        nameTextView.setTypeface(font, Typeface.NORMAL);
        nameTextView.setText(userName);


    }

    @OnClick(R.id.home)
    public void onClickedBackButton() {
        finish();
    }

    @Override
    public void onItemClick(View v, int position) {

        Snackbar.make(getWindow().getDecorView().getRootView(), "길게 눌러 수정하기", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View v, int position) {
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
    }
}
