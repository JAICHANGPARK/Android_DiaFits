package com.dreamwalker.diabetesfits.activity.diary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.consts.IntentConst;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.numberpad.NumberPadTimePickerDialog;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class WriteFitnessActivity extends AppCompatActivity
        implements BottomSheetTimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = "WriteFitnessActivity";

    @BindView(R.id.nice_spinner)
    NiceSpinner niceSpinner;
    @BindView(R.id.nice_spinner_2)
    NiceSpinner niceSpinner2;
    @BindView(R.id.nice_spinner_3)
    NiceSpinner niceSpinner3;


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

    @BindView(R.id.home)
    ImageView backButton;

    @BindView(R.id.done)
    ImageView doneButton;
    @BindView(R.id.rpe_info_button)
    ImageView rpeInfoButton;


    String selectType;  // 운동 종류 데이터 변수
    String selectTypeDetail; // 운동 강도? 상세 정보
    String selectRpeExpression; // 운동 자각도
    String userWeight;  // 사용자 체중

    String fitnessTime; //운동 시간
    String fitnessDistance; // 이동 거리
    String fitnessSpeed; // 운동 속도

    String rpeScore; //운동 자각도 점수

    NumberPadTimePickerDialog pad;

    DatePickerDialog dateDialog;
    GridTimePickerDialog gridTimeDialog;

    int y, m, d;
    int h, min;

    HashMap<String, String> userInputMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_fitness);
        initSetting();

        selectType = "트레드밀";
        selectTypeDetail = "가볍게걷기";
        selectRpeExpression = "전혀 힘들지 않다";

    }

    private void bindView() {
        ButterKnife.bind(this);
    }

    private void initPaper() {
        Paper.init(this);
    }

    private void initSetting() {
        bindView();
        setNiceSpinner();
        initPaper();
        initUserWeight();
        setTextInputEditText();
        setBottomSheetPicker();
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

    private void setTextInputEditText() {
//         InputFilter filterAlphaNum = (source, start, end, dest, dstart, dend) -> {
//             Pattern ps = Pattern.compile("[[6]-2[0]]");
//             if (!ps.matcher(source).matches()) {
//                 return "";
//             }
//             return null;
//         };
//         scoreEditText.setFilters(new InputFilter[]{filterAlphaNum});
        scoreEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e(TAG, "onKey: " + keyCode + " ,, " + event);
                return false;
            }
        });

    }


    private void setBottomSheetPicker() {


        Calendar now = Calendar.getInstance();

        // As of version 2.3.0, `BottomSheetDatePickerDialog` is deprecated.
        dateDialog = DatePickerDialog.newInstance(
                WriteFitnessActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        gridTimeDialog = GridTimePickerDialog.newInstance(
                WriteFitnessActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(WriteFitnessActivity.this));

        // Configured according to the system preference for 24-hour time.
        pad = NumberPadTimePickerDialog.newInstance(WriteFitnessActivity.this);

    }


    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        Log.e(TAG, "onDateSet: " + year + "|" + monthOfYear + "|" + dayOfMonth);

        //
        y = year;
        m = monthOfYear;
        d = dayOfMonth;

        userInputMap.put("year", String.valueOf(year));
        userInputMap.put("monthOfYear", String.valueOf(monthOfYear));
        userInputMap.put("dayOfMonth", String.valueOf(dayOfMonth));

        gridTimeDialog.show(getSupportFragmentManager(), "3");

    }

    @Override
    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {

        Log.e(TAG, "onTimeSet: " + hourOfDay + ", " + minute);
        h = hourOfDay;
        min = minute;

        GregorianCalendar gregorianCalendar = new GregorianCalendar(y, m, d, h, min);
        Log.e(TAG, "onTimeSet: " + gregorianCalendar.getTime());
        Log.e(TAG, "onTimeSet: " + gregorianCalendar.getTimeInMillis());

        userInputMap.put("selectType", selectType);
        userInputMap.put("selectTypeDetail", selectTypeDetail);
        userInputMap.put("selectRpeExpression", selectRpeExpression);
        userInputMap.put("fitnessTime", fitnessTime);
        userInputMap.put("fitnessDistance", fitnessDistance);
        userInputMap.put("fitnessSpeed", fitnessSpeed);
        userInputMap.put("rpeScore", rpeScore);
        userInputMap.put("userWeight", userWeight);

        userInputMap.put("hourOfDay", String.valueOf(hourOfDay));
        userInputMap.put("minute", String.valueOf(minute));
        userInputMap.put("timestamp", String.valueOf(gregorianCalendar.getTimeInMillis()));

        Intent intent = new Intent(WriteFitnessActivity.this, WriteFintessCheckActivity.class);
        intent.putExtra(IntentConst.USER_WRITE_FITNESS, userInputMap);
        startActivity(intent);

    }


    // TODO: 2018-09-02 이지미 뷰이지만 버튼으로 구성했습니다.- 박제창
    @OnClick(R.id.home)
    public void onClickedBackButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("운동 입력을 종료하시겠어요?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
//
//        if (fitnessTime != null && fitnessDistance != null && fitnessSpeed != null && rpeScore != null) {
//            dateDialog.show(getSupportFragmentManager(), "2");
//        }

        if (timeCheck && speedCheck && scoreCheck && distanceCheck) {
            dateDialog.show(getSupportFragmentManager(), "2");
        }
    }

    @OnClick(R.id.rpe_info_button)
    public void onClickedRpeInformationButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("운동의 느낌");
        builder.setMessage("저강도 운동의 느낌은 호흡 패턴이 뚜렷하게 변하지 않으며, 땀이 많이 나지 않을 정도이며, 대화를 나눌 수 있으며 노래도 부를 수 있는 정도 입니다.\n" +
                "중강도 운동의 느낌은 호흡이 짧아 지며 약 10분 정도 운동을 하면은 땀이 날 정도이며, 대화를 나눌 수 있지만 노래를 부를 수 없을 정도 입니다.\n" +
                "고강도 운동의 느낌은 호흡이 깊고 빨라지며 짧은 시간 운동을 하면은 땀이 날 정도이며, 대화를 나누기 힘들 정도입니다.\n\n" +
                "출처 : 삼성서울병원");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }
}
