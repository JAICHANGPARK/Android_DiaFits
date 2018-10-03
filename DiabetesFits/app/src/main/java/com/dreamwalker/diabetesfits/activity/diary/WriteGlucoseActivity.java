package com.dreamwalker.diabetesfits.activity.diary;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

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
import io.realm.Realm;
import io.realm.RealmConfiguration;

import static com.dreamwalker.diabetesfits.consts.IntentConst.USER_WRITE_GLUCOSE;

public class WriteGlucoseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "WriteGlucoseActivity";

    @BindView(R.id.nice_spinner)
    NiceSpinner niceSpinner;
    @BindView(R.id.nice_spinner_2)
    NiceSpinner niceSpinner2;
    @BindView(R.id.bottomAppBar)
    BottomAppBar bottomAppBar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.type_info_button)
    ImageView typeInfoButton;
    @BindView(R.id.done)
    ImageView doneButton;
    @BindView(R.id.home)
    ImageView homeButton;
    @BindView(R.id.glucose_value_edt)
    TextInputEditText glucoseValueEditText;

    Realm realm;
    RealmConfiguration realmConfiguration;

    String userSelectedType;
    String userGlucoseValue;
    DatePickerDialog dpd;
    TimePickerDialog tpd;


    HashMap<String, String> userInputMap = new HashMap<>();

    int y, m, d;
    int h, min;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_glucose);
        initSetting();


    }

    private void initSetting() {
        initRealm();
        bindView();
        setStatusBar();
        initToolbar();
        setNiceSpinner();
        initDateTimePicker();
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
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.default_background));
        }
    }


    private void initDateTimePicker() {
        Calendar now = Calendar.getInstance();
        dpd = DatePickerDialog.newInstance(
                WriteGlucoseActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setOkColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        tpd = TimePickerDialog.newInstance(
                WriteGlucoseActivity.this,
                now.get(Calendar.HOUR_OF_DAY), // Initial year selection
                now.get(Calendar.MINUTE), // Initial month selection
                now.get(Calendar.SECOND),
                true // Inital day selection
        );
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
    }

    private void setNiceSpinner() {

        List<String> dataset = new LinkedList<>(Arrays.asList("공복", "취침 전", "운동", "아침 식사", "점심 식사", "저녁 식사"));
        List<String> detailDataSet = new LinkedList<>(Arrays.asList("전", "후"));
        List<String> blankDataSet = new LinkedList<>(Arrays.asList("없음"));

        niceSpinner.attachDataSource(dataset);
        niceSpinner2.attachDataSource(blankDataSet);

        userSelectedType = "공복";

        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userSelectedType = dataset.get(position);
                Log.e(TAG, "onItemSelected: " + userSelectedType);
                switch (position) {
                    case 0: //공복
                        Log.e(TAG, "onItemSelected: " + userSelectedType);
                        niceSpinner2.attachDataSource(blankDataSet);
                        break;
                    case 1: // 취칮전
                        Log.e(TAG, "onItemSelected: " + userSelectedType);
                        niceSpinner2.attachDataSource(blankDataSet);
                        break;
                    case 2: // 운동
                        userSelectedType = "운동 전";
                        Log.e(TAG, "onItemSelected: " + userSelectedType);
                        niceSpinner2.attachDataSource(detailDataSet);
                        break;
                    case 3: // 아침 식사
                        userSelectedType = "아침 식전";
                        Log.e(TAG, "onItemSelected: " + userSelectedType);
                        niceSpinner2.attachDataSource(detailDataSet);
                        break;
                    case 4: //점심식사
                        userSelectedType = "점심 식전";
                        Log.e(TAG, "onItemSelected: " + userSelectedType);
                        niceSpinner2.attachDataSource(detailDataSet);
                        break;
                    case 5: // 저녁 식사
                        userSelectedType = "저녁 식전";
                        Log.e(TAG, "onItemSelected: " + userSelectedType);
                        niceSpinner2.attachDataSource(detailDataSet);
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
                Log.e(TAG, "niceSpinner2 onItemSelected: -->  " + position );
                Log.e(TAG, "niceSpinner2  onItemSelected: userSelectedType -->  " + userSelectedType );
                switch (position) {
                    case 0:
                        switch (userSelectedType) {
                            case "운동 전":
                                userSelectedType = "운동 전";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "아침 식전":
                                userSelectedType = "아침 식전";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "점심 식전":
                                userSelectedType = "점심 식전";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "저녁 식전":
                                userSelectedType = "저녁 식전";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;

                            case "운동 후":
                                userSelectedType = "운동 전";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "아침 식후":
                                userSelectedType = "아침 식전";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "점심 식후":
                                userSelectedType = "점심 식전";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "저녁 식후":
                                userSelectedType = "저녁 식전";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                        }
                        break;
                    case 1:
                        switch (userSelectedType) {

                            case "운동 전":
                                userSelectedType = "운동 후";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "아침 식전":
                                userSelectedType = "아침 식후";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "점심 식전":
                                userSelectedType = "점심 식후";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "저녁 식전":
                                userSelectedType = "저녁 식후";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;

                            case "운동 후":
                                userSelectedType = "운동 후";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "아침 식후":
                                userSelectedType = "아침 식후";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "점심 식후":
                                userSelectedType = "점심 식후";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                            case "저녁 식후":
                                userSelectedType = "저녁 식후";
                                Log.e(TAG, "onItemSelected: " + userSelectedType);
                                break;
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.fab)
    public void onClickFloatingActionButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("입력을 취소하시겠어요?");
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

    @OnClick(R.id.type_info_button)
    public void onClickTypeInfoButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("측정 유형");
        builder.setMessage("자가 채혈을 수행한 시점을 입력합니다." +
                " 자가채혈은 최소 하루 4번 수행이 필요하며 공복, 취침전, 식전후, 운동전후를 권장합니다." +
                "이곳에는 채혈시점을 선택해주시면 됩니다.");
        builder.setPositiveButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    @OnClick(R.id.home)
    public void onClickHomeButton() {
        finish();
    }

    @OnClick(R.id.done)
    public void onClickDoneButton() {
//        finish();
        boolean checkFlag = false;
        Log.e(TAG, "onClickDoneButton: " + userSelectedType);
        if (glucoseValueEditText.getText().toString().length() != 0) {
            float temp = Float.parseFloat(glucoseValueEditText.getText().toString());
            userGlucoseValue = String.valueOf(temp);
            Log.e(TAG, "onClickDoneButton: " + userGlucoseValue);
            checkFlag = true;

        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("경고");
            builder.setMessage("혈당값을 입력해주세요");
            builder.setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.cancel());
            builder.show();
        }

        if (checkFlag){
            dpd.show(getFragmentManager(), "Datepickerdialog");
        }

    }

    @Override
    protected void onDestroy() {
        Realm.getInstance(realmConfiguration).close();
        super.onDestroy();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Log.e(TAG, "onDateSet: " + year + " | " + monthOfYear + " | " + dayOfMonth);

        y = year;
        m = monthOfYear;
        d = dayOfMonth;

        userInputMap.put("year", String.valueOf(year));
        userInputMap.put("monthOfYear", String.valueOf(monthOfYear));
        userInputMap.put("dayOfMonth", String.valueOf(dayOfMonth));
        tpd.show(getFragmentManager(), "TimePicker");

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        Log.e(TAG, "onDateSet: " + hourOfDay + " | " + minute + " | " + second);
        h = hourOfDay;
        min = minute;

        GregorianCalendar gregorianCalendar = new GregorianCalendar(y, m, d, h, min);
        Log.e(TAG, "onTimeSet: " + gregorianCalendar.getTime());
        Log.e(TAG, "onTimeSet: " + gregorianCalendar.getTimeInMillis());

        userInputMap.put("userType", userSelectedType);
        userInputMap.put("userGlucose", userGlucoseValue);
        userInputMap.put("hourOfDay", String.valueOf(hourOfDay));
        userInputMap.put("minute", String.valueOf(minute));
        userInputMap.put("timestamp", String.valueOf(gregorianCalendar.getTimeInMillis()));

        Intent intent = new Intent(WriteGlucoseActivity.this, WriteCheckActivity.class);
        intent.putExtra(USER_WRITE_GLUCOSE, userInputMap);
        startActivity(intent);

    }
}
