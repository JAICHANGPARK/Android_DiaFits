package com.dreamwalker.diabetesfits.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.appinfo.DetailAppMenuActivity;
import com.dreamwalker.diabetesfits.activity.profile.EditProfileActivity;
import com.dreamwalker.diabetesfits.activity.reminder.ReminderActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

public class SettingActivityV2 extends AppCompatActivity {
    private static final String TAG = "SettingActivityV2";


    @BindView(R.id.edit_profile_button)
    Button editProfileButton;
    @BindView(R.id.feedback_button)
    Button feedbackButton;
    @BindView(R.id.db_management_button)
    Button dbManagementButton;
    @BindView(R.id.about_app_button)
    Button aboutAppButton;
    @BindView(R.id.licenses_button)
    Button licenseButton;
    @BindView(R.id.developer_button)
    Button developerButton;
    @BindView(R.id.logout_button)
    Button logoutButton;

    @BindView(R.id.reminder_button)
    Button reminderButton;
    @BindView(R.id.alarm_button)
    Button alarmButton;

    @BindView(R.id.workout_load_test_button)
    Button loadTestButton;

    @BindView(R.id.home)
    ImageView imageView;

    @BindView(R.id.scrollView)
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_v2);
        setStatusBar();
        bindView();
        Paper.init(this);
        initToasty();

        checkScrollView();
    }

    private void bindView() {
        ButterKnife.bind(this);
    }

    private void initToasty() {
        Toasty.Config.getInstance().apply();
    }

    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.default_background));
    }

    @OnClick(R.id.edit_profile_button)
    public void onClickEditProfileButton() {
        Toasty.warning(this, getResources().getString(R.string.under_construction), Toast.LENGTH_SHORT, true).show();
        startActivity(new Intent(SettingActivityV2.this, EditProfileActivity.class));

    }

    @OnClick(R.id.feedback_button)
    public void onClickFeedbackButton() {
        startActivity(new Intent(SettingActivityV2.this, FeedbackActivity.class));
    }

    @OnClick(R.id.db_management_button)
    public void onClickDatabaseButton() {
        startActivity(new Intent(SettingActivityV2.this, DBManagementActivity.class));
    }


    @OnClick(R.id.about_app_button)
    public void onClickedAboutAppButton() {
        startActivity(new Intent(SettingActivityV2.this, DetailAppMenuActivity.class));

    }

    @OnClick(R.id.licenses_button)
    public void onClickLicenseButton() {
        startActivity(new Intent(SettingActivityV2.this, OpenSourceLicenseActivity.class));
    }

    @OnClick(R.id.developer_button)
    public void onClickDeveloperButton() {
        startActivity(new Intent(SettingActivityV2.this, DeveloperActivity.class));
    }

    @OnClick(R.id.logout_button)
    public void onClickLogoutButton() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("로그아웃");
        builder.setMessage("로그아웃 하시겠어요?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Paper.book("user").delete("userID");
                Paper.book("user").delete("userPassword");
                Toasty.success(SettingActivityV2.this, "Logout Completed", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SettingActivityV2.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @OnClick(R.id.home)
    public void onClickBackButton() {
        finish();
    }


    @OnClick(R.id.reminder_button)
    public void onClickedReminderButton() {
        startActivity(new Intent(SettingActivityV2.this, ReminderActivity.class));
//        Toasty.warning(this, getResources().getString(R.string.under_construction), Toast.LENGTH_SHORT, true).show();
    }

    @OnClick(R.id.alarm_button)
    public void onClickedAlarmButton() {
        Toasty.warning(this, getResources().getString(R.string.under_construction), Toast.LENGTH_SHORT, true).show();
    }

    @OnClick(R.id.workout_load_test_button)
    public void onClickedWorkoutLoadTestButton(){
        Toasty.warning(this, "운동부하검사실험-준비중", Toast.LENGTH_SHORT, true).show();
    }

    private void checkScrollView() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    Log.e(TAG, "onScrollChange: " + scrollX + " | " + scrollY + " | " + oldScrollX + " | " + oldScrollY);
                }
            });
        }
    }
}
