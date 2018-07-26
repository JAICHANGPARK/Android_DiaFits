package com.dreamwalker.diabetesfits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivityV2 extends AppCompatActivity {


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

    @BindView(R.id.home)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_v2);
        setStatusBar();
        bindView();


    }

    private void bindView(){
        ButterKnife.bind(this);
    }

    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.default_background));
    }

    @OnClick(R.id.edit_profile_button)
    public void onClickEditProfileButton(){

    }

    @OnClick(R.id.feedback_button)
    public void onClickFeedbackButton(){
        startActivity(new Intent(SettingActivityV2.this, FeedbackActivity.class));
    }


    @OnClick(R.id.licenses_button)
    public void onClickLicenseButton(){
        startActivity(new Intent(SettingActivityV2.this, OpenSourceLicenseActivity.class));
    }

    @OnClick(R.id.developer_button)
    public void onClickDeveloperButton(){
        startActivity(new Intent(SettingActivityV2.this, DeveloperActivity.class));
    }

    @OnClick(R.id.logout_button)
    public void onClickLogoutButton(){

    }

    @OnClick(R.id.home)
    public void onClickBackButton(){
        finish();
    }

}
