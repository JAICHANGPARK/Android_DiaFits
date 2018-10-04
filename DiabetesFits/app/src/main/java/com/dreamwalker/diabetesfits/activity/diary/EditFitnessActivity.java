package com.dreamwalker.diabetesfits.activity.diary;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.consts.IntentConst;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diabetesfits.database.model.Fitness;
import com.dreamwalker.diabetesfits.utils.met.Met;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.angmarch.views.NiceSpinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class EditFitnessActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "EditFitnessActivity";


    @BindView(R.id.nice_spinner)
    NiceSpinner niceSpinner;
    @BindView(R.id.nice_spinner_2)
    NiceSpinner niceSpinner2;
    @BindView(R.id.nice_spinner_3)
    NiceSpinner niceSpinner3;

    @BindView(R.id.bottomAppBar)
    BottomAppBar bottomAppBar;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.done)
    ImageView doneButton;
    @BindView(R.id.home)
    ImageView homeButton;


    @BindView(R.id.time_edt)
    TextInputEditText timeEditText;
    @BindView(R.id.date_edt)
    TextInputEditText dateEditText;

    @BindView(R.id.time_button)
    ImageView timeButton;
    @BindView(R.id.date_button)
    ImageView dateButton;

    @BindView(R.id.nsv)
    NestedScrollView nestedScrollView;

    @BindView(R.id.fitness_time_edt)
    TextInputEditText fitnessTimeEditText;
    @BindView(R.id.distance_edt)
    TextInputEditText distanceEditText;
    @BindView(R.id.speed_edt)
    TextInputEditText speedEditText;

    @BindView(R.id.score_edt)
    TextInputEditText scoreEditText;
    @BindView(R.id.weight_edt)
    TextInputEditText weightEditText;

    @BindView(R.id.rpe_info_button)
    ImageView rpeInfoButton;

    DatePickerDialog dpd;
    TimePickerDialog tpd;

    Realm realm;
    RealmConfiguration realmConfiguration;

    String selectType;  // 운동 종류 데이터 변수
    String selectTypeDetail; // 운동 강도? 상세 정보
    String selectRpeExpression; // 운동 자각도
    String userWeight;  // 사용자 체중

    String fitnessTime; //운동 시간
    String fitnessDistance; // 이동 거리
    String fitnessSpeed; // 운동 속도

    String rpeScore; //운동 자각도 점수


    Bundle bundle = new Bundle();

    int y, m, d;
    int h, min;
    Date globalDate;
    String[] dateTime;
    Date userDateTimes;
    long userTs;
    String originTimeStamp;
    String timeStamps;

    SimpleDateFormat simpleDateFormat;

    Met met = new Met();
    ArrayList<Met> workMet = new ArrayList<>();

    String userKcal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fitness);

        initSetting();

        bundle = getIntent().getBundleExtra(IntentConst.USER_EDIT_FITNESS);
        Log.e(TAG, "onCreate: userType " + bundle.getString("userType"));
        Log.e(TAG, "onCreate: userDate" + bundle.getString("userDate"));
        Log.e(TAG, "onCreate: userTime" + bundle.getString("userTime"));
        Log.e(TAG, "onCreate: userTimestamp " + bundle.getString("userTimestamp"));
        Log.e(TAG, "onCreate: userTimestampLong" + bundle.getLong("userTimestampLong"));

        userKcal = bundle.getString("userKcal");
        fitnessTimeEditText.setText(bundle.getString("userFitnessTime"));
        distanceEditText.setText(bundle.getString("userFitnessDistance"));
        speedEditText.setText(bundle.getString("userFitnessSpeed"));
        scoreEditText.setText(bundle.getString("userREPScore"));


        Date date = new Date(bundle.getLong("userTimestampLong") * 1000); // 데이트 객체 생성
        globalDate = date; // 맴버 변수에 넣기
        Log.e(TAG, "onCreate: date " + date.getTime());

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA); //데이터 포맷 설정

        String timestamp = bundle.getString("userTimestamp");  // 타임스탬프값 가져오기
        userTs = Long.valueOf(timestamp) / 1000; // 타임스탬프 1000 나누기
        String newDate = simpleDateFormat.format(date); // 데이터포맷에 데이터 넣기
        try {
            userDateTimes = simpleDateFormat.parse(newDate); // 문자여을 가지고 데이터로 파싱하기
            Log.e(TAG, "onCreate: " + userDateTimes);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dateTime = newDate.split(" "); // 데이터포맷의 결과로 나온 문자열을 공백을 기준으로 자르기

        String dateString = DateFormat.getDateInstance().format(date);
        String timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
        originTimeStamp = bundle.getString("userTimestamp");
        timeStamps = bundle.getString("userTimestamp");

        timeEditText.setKeyListener(null);
        dateEditText.setKeyListener(null);

        dateEditText.setText(dateString);
        timeEditText.setText(timeString);
        // TODO: 2018-10-05 앞에서 선택된 타입값에 대해 인덱스 처리된 값을 받는다. 

        niceSpinner.setSelectedIndex(bundle.getInt("userTypePosition"));
        if (bundle.getInt("userTypePosition") == 1) {
            List<String> indoorBikeSet = new LinkedList<>(Arrays.asList("보통으로", "빠르게", "가볍게"));
            niceSpinner2.attachDataSource(indoorBikeSet);
            niceSpinner2.setSelectedIndex(bundle.getInt("userDetailTypePosition"));
        } else {
            niceSpinner2.setSelectedIndex(bundle.getInt("userDetailTypePosition"));
        }
    }

    private void initSetting() {
        initRealm();
        initPaper();
        bindView();
        setStatusBar();
        initToolbar();
        setNiceSpinner();
        initDateTimePicker();
        setNestedScrollView();
        initUserWeight();
    }

    private void initPaper() {
        Paper.init(this);
    }

    private void initUserWeight() {
        userWeight = readUserWeightFromPaper();
        if (userWeight == null) {
            userWeight = "None";
        } else {
            weightEditText.setText(userWeight);
        }
    }

    private String readUserWeightFromPaper() {
        String read;
        read = Paper.book("user").read("userWeight");
        return read;
    }


    private void initToolbar() {
        setSupportActionBar(bottomAppBar);
        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
//        bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
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

    private void setStatusBar() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.default_background));
        }
    }

    private void initDateTimePicker() {
        Calendar now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                EditFitnessActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setOkColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        tpd = TimePickerDialog.newInstance(
                EditFitnessActivity.this,
                now.get(Calendar.HOUR_OF_DAY), // Initial year selection
                now.get(Calendar.MINUTE), // Initial month selection
                now.get(Calendar.SECOND),
                true // Inital day selection
        );
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
    }

    private void setNiceSpinner() {

        List<String> dataset = new LinkedList<>(Arrays.asList("트레드밀", "실내자전거"));
        List<String> treadmillSet = new LinkedList<>(Arrays.asList("가볍게걷기", "일반 걷기", "달리기"));
        List<String> indoorBikeSet = new LinkedList<>(Arrays.asList("보통으로", "빠르게", "가볍게"));
        List<String> rpeSet = new LinkedList<>(Arrays.asList("전혀 힘들지 않다", "힘들지 않다", "보통이다", "약간 힘들다", "힘들다", "매우 힘들다", "매우 매우 힘들다"));

        niceSpinner.attachDataSource(dataset);
        niceSpinner2.attachDataSource(treadmillSet);
        niceSpinner3.attachDataSource(rpeSet);

        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectType = "트레드밀";
                        selectTypeDetail = treadmillSet.get(0);
                        niceSpinner2.attachDataSource(treadmillSet);
                        break;
                    case 1:
                        selectType = "실내자전거";
                        selectTypeDetail = indoorBikeSet.get(0);
                        niceSpinner2.attachDataSource(indoorBikeSet);
                        break;
                }
                Log.e(TAG, "onItemSelected: " + position + "," + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        niceSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectType.equals("트레드밀")) {
                    switch (position) {
                        case 0:
                            selectTypeDetail = treadmillSet.get(0);
                            break;
                        case 1:
                            selectTypeDetail = treadmillSet.get(1);
                            break;
                        case 2:
                            selectTypeDetail = treadmillSet.get(2);
                            break;
                    }
                    Log.e(TAG, " niceSpinner2 onItemSelected: " + selectTypeDetail);

                } else if (selectType.equals("실내자전거")) {
                    switch (position) {
                        case 0:
                            selectTypeDetail = indoorBikeSet.get(0);
                            break;
                        case 1:
                            selectTypeDetail = indoorBikeSet.get(1);
                            break;
                        case 2:
                            selectTypeDetail = indoorBikeSet.get(2);
                            break;
                    }
                    Log.e(TAG, " niceSpinner2 onItemSelected: " + selectTypeDetail);
                }
                Log.e(TAG, "niceSpinner2 onItemSelected:  " + position + "," + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        niceSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectRpeExpression = rpeSet.get(0);
                        break;
                    case 1:
                        selectRpeExpression = rpeSet.get(1);
                        break;
                    case 2:
                        selectRpeExpression = rpeSet.get(2);
                        break;
                    case 3:
                        selectRpeExpression = rpeSet.get(3);
                        break;
                    case 4:
                        selectRpeExpression = rpeSet.get(4);
                        break;
                    case 5:
                        selectRpeExpression = rpeSet.get(5);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setNestedScrollView() {

        nestedScrollView = (NestedScrollView) findViewById(R.id.nsv);
        nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY > oldScrollY) {
                        floatingActionButton.hide();
                    } else {
                        floatingActionButton.show();
                    }
                });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        y = year;
        m = monthOfYear;
        d = dayOfMonth;

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        Log.e(TAG, "onDateSet: calendar.getTimeInMillis() --> " + calendar.getTimeInMillis());
        dateTime = simpleDateFormat.format(calendar.getTime()).split(" ");
        timeStamps = String.valueOf(calendar.getTimeInMillis());
        userTs = calendar.getTimeInMillis() / 1000;
        userDateTimes = calendar.getTime();
        Log.e(TAG, "onDateSet: " + dateTime[0]);
        String dateString = DateFormat.getDateInstance().format(calendar.getTime());
//        String timeString = DateFormat.getTimeInstance().format(date);

        dateEditText.setText(dateString);
//        timeEditText.setText(timeString);

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

        h = hourOfDay;
        min = minute;

        GregorianCalendar gregorianCalendar = new GregorianCalendar(y, m, d, h, min);
        Log.e(TAG, "onTimeSet: " + gregorianCalendar.getTime());
        Log.e(TAG, "onTimeSet: " + gregorianCalendar.getTimeInMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        Log.e(TAG, "onDateSet: calendar.getTime().getTime() --> " + calendar.getTime().getTime());
        Log.e(TAG, "onDateSet: calendar.getTimeInMillis() --> " + calendar.getTimeInMillis());

        dateTime = simpleDateFormat.format(calendar.getTime()).split(" ");
        timeStamps = String.valueOf(calendar.getTimeInMillis());
        userTs = calendar.getTimeInMillis() / 1000;
        userDateTimes = calendar.getTime();
        Log.e(TAG, "onDateSet: " + dateTime[0]);

        String timeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        timeEditText.setText(timeString);

    }

    @OnClick(R.id.date_button)
    public void onClickedDateButton() {
        dpd.show(getFragmentManager(), "datePicker");
    }

    @OnClick(R.id.time_button)
    public void onClickedTimeButton() {
        tpd.show(getFragmentManager(), "timePicker");
    }

    @OnClick(R.id.home)
    public void onClickedBackButton() {

        finish();
    }

    @OnClick(R.id.fab)
    public void onClickedFloatingActionButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("수정을 취소하시겠어요?");
        builder.setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    @OnClick(R.id.done)
    public void onClickedDoneButton() {
        boolean timeCheck = false;
        boolean distanceCheck = false;
        boolean speedCheck = false;
        boolean scoreCheck = false;
        boolean weightCheck = false;

        Log.e(TAG, "onClickedDoneButton: selectType -> " + selectType);
        Log.e(TAG, "onClickedDoneButton: selectTypeDetail -> " + selectTypeDetail);
        Log.e(TAG, "onClickedDoneButton: selectRpeExpression -> " + selectRpeExpression);

        if (fitnessTimeEditText.getText().toString().length() != 0) {
            fitnessTime = fitnessTimeEditText.getText().toString();
            Log.e(TAG, "onClickedDoneButton: fitnessTime -> " + fitnessTime);
            timeCheck = true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("운동시간을 입력해주세요.\n 단위는 [분] 입니다.\n 예) 30분 운동 시 30 입력");
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel());
            builder.show();
        }

        if (distanceEditText.getText().toString().length() != 0) {
            fitnessDistance = distanceEditText.getText().toString();
            Log.e(TAG, "onClickedDoneButton: fitnessDistance -> " + fitnessDistance);
            if (!fitnessDistance.contains(".")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알림");
                builder.setMessage("올바른 운동거리를 입력해주세요. \n 단위는 [km] 입니다. \n\n 예) 4.2km 운동 시 4.2 입력 \n  10km 운동 시 10.0 입력");
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel());
                builder.show();
            } else {
                distanceCheck = true;
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("운동거리를 입력해주세요. \n 단위는 [km] 입니다. \n 예) 4.2km 운동 시 4.2 입력 \n  10km 운동 시 10.0 입력");
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel());
            builder.show();
        }

        if (speedEditText.getText().toString().length() != 0) {
            fitnessSpeed = speedEditText.getText().toString();
            Log.e(TAG, "onClickedDoneButton: fitnessSpeed -> " + fitnessSpeed);
            if (!fitnessSpeed.contains(".")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알림");
                builder.setMessage("올바른 운동 속도를 입력해주세요. \n 단위는 [km/h] 입니다. \n 예) 트레드밀 5km/h 속도로 운동 시 5.0 입력");
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel());
                builder.show();
            } else {
                speedCheck = true;
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("운동 속도를 입력해주세요. \n 단위는 [km/h] 입니다. \n 예) 트레드밀 5km/h 속도로 운동 시 5.0 입력");
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel());
            builder.show();
        }

        if (scoreEditText.getText().toString().length() != 0) {
            String tmpScore = scoreEditText.getText().toString();

            Log.e(TAG, "onClickedDoneButton: rpeScore -> " + tmpScore);
            if (Integer.valueOf(tmpScore) < 6 || Integer.valueOf(tmpScore) > 20) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("알림");
                builder.setMessage("운동 강도(운동자각인지도)는 6점이상 20점이하로 입력해주세요");
                builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel());
                builder.show();
            } else {
                rpeScore = tmpScore;
                scoreCheck = true;
            }

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("운동자각인지도를 입력해주세요.\n 운동자각인지도는 낮을 수록 편안한 상태이며 높을 수록 힘든 상태입니다.");
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel());
            builder.show();
        }

        if (weightEditText.getText().toString().length() != 0) {
            userWeight = weightEditText.getText().toString();
            weightCheck = true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("체중을 입력해주세요.\n 단위는 [kg] 입니다.\n 예) 체중이 70kg 이면 70 입력");
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel());
            builder.show();
        }
//
//        if (fitnessTime != null && fitnessDistance != null && fitnessSpeed != null && rpeScore != null) {
//            dateDialog.show(getSupportFragmentManager(), "2");
//        }

        if (timeCheck && speedCheck && scoreCheck && distanceCheck && weightCheck) {

            //        "가볍게걷기", "일반 걷기", "달리기"
            if (selectTypeDetail.equals("가볍게걷기")) {
                workMet = met.getTreadmillWorkingMetData();
            } else if (selectTypeDetail.equals("일반 걷기")) {
                workMet = met.getTreadmillWorkingMetData();
            } else if (selectTypeDetail.equals("달리기")) {
                workMet = met.getTreadmillRunningMetData();
            } else {
                workMet = met.getIndoorBikeMetData();
            }

//                "보통으로", "빠르게", "가볍게"

            String userFitnessSpeed = fitnessSpeed;
            float fitnessMet = 0.0f;
            for (Met m : workMet) {
                Log.e(TAG, "met Result --> " + m.getStrength());
                if (userFitnessSpeed.equals(m.getStrength())) {
                    fitnessMet = m.getMetValue();
                }else if(m.getStrength().equals("보통으로")){
                    fitnessMet = m.getMetValue();
                }else if(m.getStrength().equals("빠르게")){
                    fitnessMet = m.getMetValue();
                }else if (m.getStrength().equals("가볍게")){
                    fitnessMet = m.getMetValue();
                }
            }

            // TODO: 2018-10-02 MET 계산
            float weight = Float.parseFloat(userWeight);
            Log.e(TAG, "onCreate: weight --> " + weight);
            float userKCal = 3.5f * 0.05f * weight * fitnessMet;
            int kCal = Math.round(userKCal);
            userKcal = String.valueOf(kCal);
            Log.e(TAG, "onCreate: kCal --> " + kCal);
//
            Fitness results = realm.where(Fitness.class).equalTo("timestamp", originTimeStamp).findFirst();
            realm.executeTransaction(realm -> {
                results.setType(selectType);
                results.setSelectTypeDetail(selectTypeDetail);
                results.setSelectRpeExpression(selectRpeExpression);
                results.setFitnessTime(fitnessTime);
                results.setDistance(fitnessDistance);
                results.setSpeed(fitnessSpeed);
                results.setRpeScore(rpeScore);
                results.setKcal(userKcal);

                results.setDate(dateTime[0]);
                results.setTime(dateTime[1]);
                results.setTimestamp(timeStamps);
                results.setLongTs(userTs);
                results.setDatetime(userDateTimes);

            });

//            Toast.makeText(this, "Complete 완벽하다고", Toast.LENGTH_SHORT).show();

            finish();
        }
    }
}
