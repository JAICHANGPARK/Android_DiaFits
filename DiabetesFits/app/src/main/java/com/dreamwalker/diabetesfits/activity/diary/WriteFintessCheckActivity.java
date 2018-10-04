package com.dreamwalker.diabetesfits.activity.diary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.HomeActivity;
import com.dreamwalker.diabetesfits.consts.IntentConst;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diabetesfits.database.model.Fitness;
import com.dreamwalker.diabetesfits.utils.met.Met;
import com.dreamwalker.progresssubmitbutton.SubmitButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WriteFintessCheckActivity extends AppCompatActivity {
    private static final String TAG = "WriteFintessCheckActivi";

    @BindView(R.id.submit_button)
    SubmitButton submitButton;
    @BindView(R.id.type_text_view)
    TextView typeTextView;
    @BindView(R.id.glucose_text_view)
    TextView glucoseTextView;

    @BindView(R.id.date_text_view)
    TextView dateTextView;
    @BindView(R.id.time_text_view)
    TextView timeTextView;


    HashMap<String, String> userInputMap = new HashMap<>();

    Realm realm;
    RealmConfiguration realmConfiguration;

    Handler handler;
    Vibrator vibrator;


    String[] dateTime;
    Date userDateTimes;
    long userTs;
    String userKcal;

    Met met = new Met();
    ArrayList<Met> workMet = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_fintess_check);

        initSetting();


        // TODO: 2018-07-25 폰트 생성용 - 박제창
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NotoSansCJKkr-Thin.otf");
        typeTextView.setTypeface(font, Typeface.NORMAL);
        //chronometer.setTypeface(font, Typeface.NORMAL);


//        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NotoSansCJKkr-Thin.otf");
//        typeTextView.setTypeface(font, Typeface.NORMAL);
        //chronometer.setTypeface(font, Typeface.NORMAL);
        // TODO: 2018-10-02 전달 받은 값 확인하기
        userInputMap = (HashMap<String, String>) getIntent().getSerializableExtra(IntentConst.USER_WRITE_FITNESS);
        for (Map.Entry<String, String> entry : userInputMap.entrySet()) {
            Log.e(TAG, "onCreate: getData ::  " + entry.getKey() + "--> " + entry.getValue());
        }

//        "가볍게걷기", "일반 걷기", "달리기"
        if (userInputMap.get("selectTypeDetail").equals("가볍게걷기")) {
            workMet = met.getTreadmillWorkingMetData();
        } else if (userInputMap.get("selectTypeDetail").equals("일반 걷기")) {
            workMet = met.getTreadmillWorkingMetData();
        } else if (userInputMap.get("selectTypeDetail").equals("달리기")) {
            workMet = met.getTreadmillRunningMetData();
        }

        String userFitnessSpeed = userInputMap.get("fitnessSpeed");
        float fitnessMet = 0.0f;
        for (Met m : workMet){
            Log.e(TAG, "met Result --> " + m.getStrength() );
            if (userFitnessSpeed.equals(m.getStrength())){
                fitnessMet = m.getMetValue();
            }
        }

        // TODO: 2018-10-02 MET 계산
        float weight = Float.parseFloat(userInputMap.get("userWeight"));
        Log.e(TAG, "onCreate: weight --> " + weight );
        float userKCal = 3.5f * 0.05f * weight * fitnessMet;
        int kCal = Math.round(userKCal);
        userKcal = String.valueOf(kCal);
        Log.e(TAG, "onCreate: kCal --> " +  kCal );

        glucoseTextView.setText(userInputMap.get("fitnessTime"));
        typeTextView.setText(userInputMap.get("selectType"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
//        SimpleDateFormat ticketDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//        SimpleDateFormat ticketTimeFormat = new SimpleDateFormat("hh:mm:ss", Locale.KOREA);

        String timestamp = userInputMap.get("timestamp");  // 타임스탬프를 가져온다.
        Date date = new Date(Long.valueOf(timestamp)); // 타임스탬프 값을 기준으로 Date 객체를 생성한다.
        userTs = Long.valueOf(timestamp) / 1000;  // 1000을 나눠 올바른 타임 스탬프를 구한다.


        String newDate = simpleDateFormat.format(date);  // Date 변수를 포맷터에 넣어 yyyy-MM-dd hh:mm:ss을 뽑아낸다.

        try {
            userDateTimes = simpleDateFormat.parse(newDate);
            Log.e(TAG, "onCreate: " + userDateTimes);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateTime = newDate.split(" ");
        dateTextView.setText(dateTime[0]);
        timeTextView.setText(dateTime[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(timestamp));

        Log.e(TAG, "onCreate: " + timestamp + " --> " + newDate + " == >> " + calendar.getTime());
    }

    private void initSetting() {
        bindView();
        initPaper();
        initRealmSetting();
        otherInitSetting();
    }

    private void bindView() {
        ButterKnife.bind(this);
    }

    private void initPaper() {
        Paper.init(this);
    }

    private void initRealmSetting() {
        Realm.init(this);
        realmConfiguration = RealmManagement.getRealmConfiguration();
        Realm.setDefaultConfiguration(realmConfiguration);
        realm = Realm.getInstance(realmConfiguration);
    }


    private void otherInitSetting() {
        handler = new Handler();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }


    @OnClick(R.id.submit_button)
    public void submitted() {

        /**
         * userInputMap.put("selectType", selectType);
         * userInputMap.put("selectTypeDetail", selectTypeDetail);
         * userInputMap.put("selectRpeExpression", selectRpeExpression);
         * userInputMap.put("fitnessTime", fitnessTime);
         * userInputMap.put("fitnessDistance",fitnessDistance);
         * userInputMap.put("fitnessSpeed", fitnessSpeed);
         * userInputMap.put("rpeScore", rpeScore);
         * userInputMap.put("hourOfDay", String.valueOf(hourOfDay));
         * userInputMap.put("minute", String.valueOf(minute));
         * userInputMap.put("timestamp", String.valueOf(gregorianCalendar.getTimeInMillis()));
         */


        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Fitness fitness = realm.createObject(Fitness.class);
                fitness.setType(userInputMap.get("selectType"));
                fitness.setSelectTypeDetail(userInputMap.get("selectTypeDetail"));
                fitness.setSelectRpeExpression(userInputMap.get("selectRpeExpression"));
                fitness.setFitnessTime(userInputMap.get("fitnessTime"));
                fitness.setDistance(userInputMap.get("fitnessDistance"));
                fitness.setSpeed(userInputMap.get("fitnessSpeed"));
                fitness.setRpeScore(userInputMap.get("rpeScore"));
                fitness.setKcal(userKcal);
//                fitness.setFitnessTime(userInputMap.get("fitnessTime"));

//                glucose.setValue(userInputMap.get("userGlucose"));
//                glucose.setType(userInputMap.get("userType"));
                fitness.setDate(dateTime[0]);
                fitness.setTime(dateTime[1]);
                fitness.setTimestamp(userInputMap.get("timestamp"));
                fitness.setLongTs(userTs);
                fitness.setDatetime(userDateTimes);
            }
        });


        long[] pattern = {0, 100, 100, 50};
        int[] amp = {100, 50};
        handler.postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, amp, -1));
            } else {
                vibrator.vibrate(pattern, -1);
            }
            submitButton.doResult(true);
        }, 1500);

        handler.postDelayed(() -> {
            startActivity(new Intent(WriteFintessCheckActivity.this, HomeActivity.class));
            finish();
        }, 2500);


//        Toast.makeText(this, "submit clicked", Toast.LENGTH_SHORT).show();
//
//        writeService.writeGlucose(userID, userInputMap.get("userGlucose"), userInputMap.get("userType"),
//                dateTime[0], dateTime[1], userInputMap.get("timestamp")).enqueue(new Callback<Validate>() {
//            @Override
//            public void onResponse(Call<Validate> call, Response<Validate> response) {
//                String result = response.body().getSuccess();
//                if (result.equals("true")) {
//
//                    realm.executeTransaction(new Realm.Transaction() {
//                        @Override
//                        public void execute(Realm realm) {
//                            Glucose glucose = realm.createObject(Glucose.class);
//                            glucose.setValue(userInputMap.get("userGlucose"));
//                            glucose.setType(userInputMap.get("userType"));
//                            glucose.setDate(dateTime[0]);
//                            glucose.setTime(dateTime[1]);
//                            glucose.setTimestamp(userInputMap.get("timestamp"));
//                            glucose.setLongTs(userTs);
//                            glucose.setDatetime(userDateTimes);
//                        }
//                    });
//
//
//                    long[] pattern = {0, 100, 100, 50};
//                    int[] amp = {100, 50};
//                    handler.postDelayed(() -> {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amp, -1));
//                        } else {
//                            vibrator.vibrate(pattern, -1);
//                        }
//                        submitButton.doResult(true);
//                    }, 1500);
//
//                    handler.postDelayed(() -> {
//                        startActivity(new Intent(WriteFintessCheckActivity.this, HomeActivity.class));
//                        finish();
//                    }, 2500);
//
//
//                } else {
//                    submitButton.doResult(false);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Validate> call, Throwable t) {
//                submitButton.doResult(false);
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        Realm.getInstance(realmConfiguration).close();
        realm.close();
        super.onDestroy();
    }
}
