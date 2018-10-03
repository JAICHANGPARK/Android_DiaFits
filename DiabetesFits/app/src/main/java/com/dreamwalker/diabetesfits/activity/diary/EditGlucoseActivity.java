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
import com.dreamwalker.diabetesfits.database.model.Glucose;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.angmarch.views.NiceSpinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class EditGlucoseActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "EditGlucoseActivity";


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

    String userSelectedType;
    String userGlucoseValue;
    DatePickerDialog dpd;
    TimePickerDialog tpd;

    Realm realm;
    RealmConfiguration realmConfiguration;

    Bundle bundle = new Bundle();

    int y, m, d;
    int h, min;
    Date globalDate;
    String[] dateTime;
    Date userDateTimes;
    long userTs;
    String timeStamps;

    SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_glucose);
        ButterKnife.bind(this);

        initSetting();

        bundle = getIntent().getBundleExtra(IntentConst.USER_EDIT_GLUCOSE);

        /**
         *
         *    bundle.putString("userType", glucoArrayList.get(position).getType());
         *    bundle.putString("userValue", glucoArrayList.get(position).getUserValue());
         *    bundle.putString("userDate", glucoArrayList.get(position).getDate());
         *    bundle.putString("userTime", glucoArrayList.get(position).getTime());
         *    bundle.putString("userTimestamp", glucoArrayList.get(position).getTimestamp());
         *
         */
        Log.e(TAG, "onCreate: userType " + bundle.getString("userType"));
        Log.e(TAG, "onCreate: userValue" + bundle.getString("userValue"));
        Log.e(TAG, "onCreate: userDate" + bundle.getString("userDate"));
        Log.e(TAG, "onCreate: userTime" + bundle.getString("userTime"));
        Log.e(TAG, "onCreate: userTimestamp " + bundle.getString("userTimestamp"));
        Log.e(TAG, "onCreate: userTimestampLong" + bundle.getLong("userTimestampLong"));

        glucoseValueEditText.setText(bundle.getString("userValue"));
        Date date = new Date(bundle.getLong("userTimestampLong") * 1000);
        globalDate = date;
        Log.e(TAG, "onCreate: date " + date.getTime());

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);

        String timestamp = bundle.getString("userTimestamp");
        userTs = Long.valueOf(timestamp) / 1000;

        String newDate = simpleDateFormat.format(date);

        try {
            userDateTimes = simpleDateFormat.parse(newDate);
            Log.e(TAG, "onCreate: " + userDateTimes );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dateTime = newDate.split(" ");

        String dateString = DateFormat.getDateInstance().format(date);
        String timeString = DateFormat.getTimeInstance().format(date);
        timeStamps = bundle.getString("userTimestamp");

        timeEditText.setKeyListener(null);
        dateEditText.setKeyListener(null);

        dateEditText.setText(dateString);
        timeEditText.setText(timeString);
//
//        Glucose results = realm.where(Glucose.class).equalTo("timestamp", timeStamps).findFirst();
//        Log.e(TAG, "onCreate: results " + results.getValue() );
//        Log.e(TAG, "onCreate: getType " + results.getType() );
//        Log.e(TAG, "onCreate: getTimestamp " + results.getTimestamp() );
//        Log.e(TAG, "onCreate: getDate " + results.getDate() );
//        Log.e(TAG, "onCreate: getTime " + results.getTime() );

    }

    private void initSetting() {
        initRealm();
        bindView();
        setStatusBar();
        initToolbar();
        setNiceSpinner();
        initDateTimePicker();
        setNestedScrollView();
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
                EditGlucoseActivity.this,
                now.get(Calendar.YEAR), // Initial year selection
                now.get(Calendar.MONTH), // Initial month selection
                now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setOkColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        tpd = TimePickerDialog.newInstance(
                EditGlucoseActivity.this,
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
                Log.e(TAG, "niceSpinner2 onItemSelected: -->  " + position);
                Log.e(TAG, "niceSpinner2  onItemSelected: userSelectedType -->  " + userSelectedType);
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

    private void setNestedScrollView(){

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

    @OnClick(R.id.done)
    public void onClickedDoneButton() {

        boolean checkFlag = false;
        Log.e(TAG, "onClickDoneButton: " + userSelectedType);
        if (glucoseValueEditText.getText().toString().length() != 0) {
            float temp = Float.parseFloat(glucoseValueEditText.getText().toString());
            userGlucoseValue = String.valueOf(temp);

            checkFlag = true;
            Log.e(TAG, "onClickedDoneButton: " + userSelectedType );
            Log.e(TAG, "onClickDoneButton: " + userGlucoseValue);
            Log.e(TAG, "onClickDoneButton: " + globalDate);


        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("경고");
            builder.setMessage("혈당값을 입력해주세요");
            builder.setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.cancel());
            builder.show();
        }

        if (checkFlag){

            Glucose results = realm.where(Glucose.class).equalTo("timestamp", timeStamps).findFirst();
            realm.executeTransaction(realm -> {
                results.setValue(userGlucoseValue);
                results.setType(userSelectedType);
                results.setDate(dateTime[0]);
                results.setTime(dateTime[1]);
                results.setTimestamp(timeStamps);
                results.setLongTs(userTs);
                results.setDatetime(userDateTimes);
            });

            finish();
        }
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

}
