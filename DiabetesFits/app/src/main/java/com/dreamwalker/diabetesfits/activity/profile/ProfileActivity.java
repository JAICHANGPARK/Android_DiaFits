package com.dreamwalker.diabetesfits.activity.profile;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.CustomItemClickListener;
import com.dreamwalker.diabetesfits.adapter.ProfileAdapter;
import com.dreamwalker.diabetesfits.database.MyMigration;
import com.dreamwalker.diabetesfits.database.model.Glucose;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.shchurov.horizontalwheelview.HorizontalWheelView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class ProfileActivity extends AppCompatActivity implements CustomItemClickListener {

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

    @BindView(R.id.bottomSheet)
    LinearLayout mBottomSheet;

    String userName;
    String helloMessage;

    Realm realm;
    RealmConfiguration realmConfiguration;
    RealmResults<Glucose> glucoses;

    Drawable drawable;

    int lineColorFlag = 0;

    ProfileAdapter adapter;


    ArrayList<String> labelList = new ArrayList<>();
    ArrayList<String> valueList = new ArrayList<>();

    String userGlucoseMin, userGlucoseMax;
    String userHeight, userWeight;

    String userChangeGlucoseMin, userChangeGlucoseMax;
    String userChangeHeight, userChangeWeight;


    BottomSheetBehavior bottomSheetBehavior;


    private RealmConfiguration getRealmConfig() {
        return new RealmConfiguration.Builder().schemaVersion(1).migration(new MyMigration()).build();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Paper.init(this);
        Realm.init(this);
        realmConfiguration = getRealmConfig();
        Realm.setDefaultConfiguration(realmConfiguration);

        setBottomSheetBehavior();


//        Blurry.with(this)
//
//                //style the blur with your color and effects with radius and sampling
//                .radius(10)
//                .sampling(8)
//                .color(Color.argb(66, 255, 255, 0))
//
//                .animate(500) //optional
//                //always use ViewGroup instance, avoid casting other view to viewgroup, it wont work
//                .onto(mBottomSheet); //Use your bottom sheet layout's rootview here.

        setTitleTextView();


        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        userGlucoseMin = Paper.book("user").read("userGlucoseMin");
        userGlucoseMax = Paper.book("user").read("userGlucoseMax");
        userHeight = Paper.book("user").read("userHeight");
        userWeight = Paper.book("user").read("userWeight");


        if (userGlucoseMin == null) {
            userGlucoseMin = "None";
        }

        if (userGlucoseMax == null) {
            userGlucoseMax = "None";
        }

        if (userHeight == null) {
            userHeight = "None";
        }

        if (userWeight == null) {
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

//        realm = Realm.getDefaultInstance();
        realm = Realm.getInstance(realmConfiguration);
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


    private void setBottomSheetBehavior() {

        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    private void setProfileData() {

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
//        finish();
        checkUserChangeData();
    }

    @Override
    public void onItemClick(View v, int position) {
        Snackbar.make(getWindow().getDecorView().getRootView(), "길게 눌러 수정하기", Snackbar.LENGTH_SHORT).show();
    }

    HorizontalWheelView horizontalWheelView;

    // TODO: 2018-08-10 각 분야별로 데이터 처리를 다르게 해야합니다 - 박제창  
    @Override
    public void onItemLongClick(View v, int position) {
        Log.e(TAG, "onItemLongClick: " + position);

        int selectedPosition = position;
        final String[] userChooseValue = {null};

        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_dialog_glucose, null);
        horizontalWheelView = (HorizontalWheelView) view.findViewById(R.id.horizontalWheelView);
        TextView textView = (TextView) view.findViewById(R.id.gluecose_value_text_view);
        TextView unitTextView = view.findViewById(R.id.unit_text_view);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/grobold.ttf");
        textView.setTypeface(font, Typeface.NORMAL);
        textView.setTextSize(60);

        if (selectedPosition == 0) {
            unitTextView.setText(" mm/dL");
        } else if (selectedPosition == 1) {
            unitTextView.setText(" mm/dL");
        } else if (selectedPosition == 2) {
            unitTextView.setText(" kg");
        } else if (selectedPosition == 3) {
            unitTextView.setText(" m");
        }

        horizontalWheelView.setListener(new HorizontalWheelView.Listener() {
            @Override
            public void onRotationChanged(double radians) {
                float angle = 100 + (float) horizontalWheelView.getDegreesAngle();
                String text = String.format(Locale.US, "%.0f", angle);
                userChooseValue[0] = text;
                textView.setText(text);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(view);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (userChooseValue[0] != null) {
                    if (Integer.valueOf(userChooseValue[0]) < 0) {
                        Toasty.warning(ProfileActivity.this, "값이 음수 일 수 없습니다.", Toast.LENGTH_SHORT, true).show();
                    } else {

                        if (selectedPosition == 0) {

                            // TODO: 2018-08-28 최소 혈당 값과 최대 혈당 값 설정 값이 같을 수 없음을 사전에 방지해야한다 - 박제창

                            String tempValue = userChooseValue[0];
                            userChangeGlucoseMin = userChooseValue[0];
                            if (userChangeGlucoseMax != null) {
                                Log.e(TAG, "onClick: " + tempValue + " | " + userChangeGlucoseMax);
                                if (userChangeGlucoseMax.equals(tempValue)) {
                                    userChangeGlucoseMin = null;
                                    userChooseValue[0] = "None";
                                    Toasty.error(ProfileActivity.this, "최고 혈당과 같을 수 없습니다.", Toast.LENGTH_SHORT, true).show();
                                } else if (Integer.valueOf(userChangeGlucoseMax) < Integer.valueOf(tempValue)) {
                                    Toasty.error(ProfileActivity.this, "최소 혈당이 최고 혈당보다 클 수 없습니다..", Toast.LENGTH_SHORT, true).show();
                                    userChangeGlucoseMin = null;
                                    userChooseValue[0] = "None";
                                } else {
                                    userChangeGlucoseMin = userChooseValue[0];
                                }
                            } else {
                                // TODO: 2018-08-28 최고혈당을 설정 ?
//                                Toasty.error(ProfileActivity.this, "최고 혈당을 설정해주세요", Toast.LENGTH_SHORT, true).show();
                            }

                        } else if (selectedPosition == 1) {
                            userChangeGlucoseMax = userChooseValue[0];
                            String tempValue = userChooseValue[0];
                            // TODO: 2018-08-28 최소 혈당 값과 최대 혈당 값 설정 값이 같을 수 없음을 사전에 방지해야한다 - 박제창

                            if (userChangeGlucoseMin != null) {
                                Log.e(TAG, "onClick: " + tempValue + " | " + userChangeGlucoseMin);
                                if (userChangeGlucoseMin.equals(tempValue)) {
                                    Toasty.error(ProfileActivity.this, "최소 혈당과 같을 수 없습니다.", Toast.LENGTH_SHORT, true).show();

                                    userChangeGlucoseMax = null;
                                    userChooseValue[0] = "None";
                                } else if (Integer.valueOf(userChangeGlucoseMin) > Integer.valueOf(tempValue)) {
                                    Toasty.error(ProfileActivity.this, "최고 혈당이 최소 혈당보다 작을 수 없습니다.", Toast.LENGTH_SHORT, true).show();
                                    userChangeGlucoseMax = null;
                                    userChooseValue[0] = "None";
                                } else {
                                    userChangeGlucoseMax = userChooseValue[0];
                                }
                            } else {
                                // TODO: 2018-08-28 최소 혈당을 설정?
//                                Toasty.error(ProfileActivity.this, "최소 혈당을 설정해주세요", Toast.LENGTH_SHORT, true).show();
                            }

                        } else if (selectedPosition == 2) {
                            userChangeWeight = userChooseValue[0];
                        } else if (selectedPosition == 3) {
                            userChangeHeight = userChooseValue[0];
                        }

                        valueList.set(selectedPosition, userChooseValue[0]);
                        adapter.notifyDataSetChanged();
                    }
                }
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        //floatingActionButton.setVisibility(View.GONE);
//        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
    }

    private long time = 0;

    @Override
    public void onBackPressed() {

        userGlucoseMin = Paper.book("user").read("userGlucoseMin");
        userGlucoseMax = Paper.book("user").read("userGlucoseMax");
        userHeight = Paper.book("user").read("userHeight");
        userWeight = Paper.book("user").read("userWeight");

        // TODO: 2018-08-28 변경된 정보가 없을 때 
        if (userChangeGlucoseMin == null && userChangeGlucoseMax == null && userChangeWeight == null && userChangeHeight == null) {
            if (System.currentTimeMillis() - time >= 2000) {
                time = System.currentTimeMillis();
                Toasty.warning(ProfileActivity.this, "변경된 정보 없음 : 뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT, true).show();
//                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            } else if (System.currentTimeMillis() - time < 2000) {
                finish();
            }
        }
        // TODO: 2018-08-28 만약 정보가 변경되었다면
        else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("정보를 저장하시겠습니까?");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO: 2018-08-28 뭐든 하나가 널이 아니기에 이곳에 도달했고  널이 아닌것만 저장하도록 한다. - 박제창
                    if (userChangeGlucoseMin != null) {
                        Paper.book("user").write("userGlucoseMin", userChangeGlucoseMin);
                    }
                    if (userChangeGlucoseMax != null) {
                        Paper.book("user").write("userGlucoseMax", userChangeGlucoseMax);
                    }
                    if (userChangeHeight != null) {
                        Paper.book("user").write("userHeight", userChangeHeight);
                    }
                    if (userChangeWeight != null) {
                        Paper.book("user").write("userWeight", userChangeWeight);
                    }

                    // TODO: 2018-08-28 둘다 null인 경우

                    // TODO: 2018-08-28 하나만 설정 된 경우

//                    if (!userGlucoseMin.equals(userChangeGlucoseMin)) {
//                        Paper.book("user").write("userGlucoseMin", userChangeGlucoseMin);
//                    }
//                    if (!userGlucoseMax.equals(userChangeGlucoseMax)) {
//                        Paper.book("user").write("userGlucoseMax", userChangeGlucoseMin);
//                    }
//                    if (!userHeight.equals(userChangeHeight)) {
//                        Paper.book("user").write("userHeight", userChangeHeight);
//                    }
//                    if (!userWeight.equals(userChangeWeight)) {
//                        Paper.book("user").write("userWeight", userChangeWeight);
//                    }

                    dialog.dismiss();
                    finish();

                }
            });

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            builder.show();


        }


//        if ((userChangeGlucoseMin == null && userChangeGlucoseMax == null && userChangeHeight == null && userChangeWeight == null)
//                && (userGlucoseMin == null && userGlucoseMax == null && userHeight == null && userWeight == null)) {
//            Toasty.error(ProfileActivity.this, "정보 변경사항 없음", Toast.LENGTH_SHORT, true).show();
//        } else {
//
//            if ( userGlucoseMin != null && userChangeGlucoseMin != null ) {
//                if (userGlucoseMin.equals(userChangeGlucoseMin) && userGlucoseMax.equals(userChangeGlucoseMax) && userWeight.equals(userChangeWeight) && userHeight.equals(userChangeHeight)) {
//                    Toasty.error(ProfileActivity.this, "모두 값이 같은 것이 존재함 ", Toast.LENGTH_SHORT, true).show();
//                } else {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setTitle("알림");
//                    builder.setMessage("정보를 저장하시겠습니까?");
//                    builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            if (userGlucoseMin.equals(userGlucoseMax) || userChangeGlucoseMin.equals(userChangeGlucoseMax)) {
//                                Toasty.error(ProfileActivity.this, "최소값과 최대값이 같을 수 없습니다. ", Toast.LENGTH_SHORT, true).show();
//                            } else {
//                                if (!userGlucoseMin.equals(userChangeGlucoseMin)) {
//                                    Paper.book("user").write("userGlucoseMin", userChangeGlucoseMin);
//                                }
//                                if (!userGlucoseMax.equals(userChangeGlucoseMax)) {
//                                    Paper.book("user").write("userGlucoseMax", userChangeGlucoseMin);
//                                }
//                                if (!userHeight.equals(userChangeHeight)) {
//                                    Paper.book("user").write("userHeight", userChangeHeight);
//                                }
//                                if (!userWeight.equals(userChangeWeight)) {
//                                    Paper.book("user").write("userWeight", userChangeWeight);
//                                }
//
//                                dialog.dismiss();
//                                finish();
//                            }
//
//                        }
//                    });
//
//                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            finish();
//                        }
//                    });
//
//                    builder.show();
//                }
//
//
//            }
//        }


//        super.onBackPressed();
    }

    private void checkUserChangeData() {

        userGlucoseMin = Paper.book("user").read("userGlucoseMin");
        userGlucoseMax = Paper.book("user").read("userGlucoseMax");
        userHeight = Paper.book("user").read("userHeight");
        userWeight = Paper.book("user").read("userWeight");

        // TODO: 2018-08-28 변경된 정보가 없을 때
        if (userChangeGlucoseMin == null && userChangeGlucoseMax == null && userChangeWeight == null && userChangeHeight == null) {
            if (System.currentTimeMillis() - time >= 2000) {
                time = System.currentTimeMillis();
                Toasty.warning(ProfileActivity.this, "변경된 정보 없음 : 뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT, true).show();
//                Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            } else if (System.currentTimeMillis() - time < 2000) {
                finish();
            }
        }
        // TODO: 2018-08-28 만약 정보가 변경되었다면
        else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("정보를 저장하시겠습니까?");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO: 2018-08-28 뭐든 하나가 널이 아니기에 이곳에 도달했고  널이 아닌것만 저장하도록 한다. - 박제창
                    if (userChangeGlucoseMin != null) {
                        Paper.book("user").write("userGlucoseMin", userChangeGlucoseMin);
                    }
                    if (userChangeGlucoseMax != null) {
                        Paper.book("user").write("userGlucoseMax", userChangeGlucoseMax);
                    }
                    if (userChangeHeight != null) {
                        Paper.book("user").write("userHeight", userChangeHeight);
                    }
                    if (userChangeWeight != null) {
                        Paper.book("user").write("userWeight", userChangeWeight);
                    }

                    // TODO: 2018-08-28 둘다 null인 경우

                    // TODO: 2018-08-28 하나만 설정 된 경우

//                    if (!userGlucoseMin.equals(userChangeGlucoseMin)) {
//                        Paper.book("user").write("userGlucoseMin", userChangeGlucoseMin);
//                    }
//                    if (!userGlucoseMax.equals(userChangeGlucoseMax)) {
//                        Paper.book("user").write("userGlucoseMax", userChangeGlucoseMin);
//                    }
//                    if (!userHeight.equals(userChangeHeight)) {
//                        Paper.book("user").write("userHeight", userChangeHeight);
//                    }
//                    if (!userWeight.equals(userChangeWeight)) {
//                        Paper.book("user").write("userWeight", userChangeWeight);
//                    }

                    dialog.dismiss();
                    finish();

                }
            });

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });

            builder.show();


        }
    }

    @Override
    protected void onDestroy() {
        Realm.getInstance(realmConfiguration).close();
        realm.close();
        super.onDestroy();
    }
}
