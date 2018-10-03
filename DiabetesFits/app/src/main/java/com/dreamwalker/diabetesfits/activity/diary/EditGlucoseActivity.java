package com.dreamwalker.diabetesfits.activity.diary;

import android.os.Build;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.consts.IntentConst;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.angmarch.views.NiceSpinner;

import java.util.Calendar;

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

    String userSelectedType;
    String userGlucoseValue;
    DatePickerDialog dpd;
    TimePickerDialog tpd;

    Realm realm;
    RealmConfiguration realmConfiguration;

    Bundle bundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_glucose);
        ButterKnife.bind(this);

        initSetting();

        bundle = getIntent().getBundleExtra(IntentConst.USER_EDIT_GLUCOSE);
        /**
         *    bundle.putString("userType", glucoArrayList.get(position).getType());
         *         bundle.putString("userValue", glucoArrayList.get(position).getUserValue());
         *         bundle.putString("userDate", glucoArrayList.get(position).getDate());
         *         bundle.putString("userTime", glucoArrayList.get(position).getTime());
         *         bundle.putString("userTimestamp", glucoArrayList.get(position).getTimestamp());
         */
        Log.e(TAG, "onCreate: " + bundle.getString("userType") );
        Log.e(TAG, "onCreate: " + bundle.getString("userValue") );
        Log.e(TAG, "onCreate: " + bundle.getString("userDate") );
        Log.e(TAG, "onCreate: " + bundle.getString("userTime") );
        Log.e(TAG, "onCreate: " + bundle.getString("userTimestamp") );

        timeEditText.setKeyListener(null);
        dateEditText.setKeyListener(null);
    }

    private void initSetting() {
        initRealm();
        bindView();
        setStatusBar();
        initToolbar();
//        setNiceSpinner();
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


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

    }

    @OnClick(R.id.date_button)
    public void onClickedDateButton(){
        dpd.show(getFragmentManager(), "datePicker");
    }
    @OnClick(R.id.time_button)
    public void onClickedTimeButton(){
        tpd.show(getFragmentManager(), "timePicker");
    }
}
