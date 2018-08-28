package com.dreamwalker.diabetesfits.activity.appinfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.cketti.library.changelog.ChangeLog;
import es.dmoral.toasty.Toasty;

public class DetailAppMenuActivity extends AppCompatActivity {
    private static final String TAG = "DetailAppMenuActivity";

    @BindView(R.id.buttonInformation0)
    Button buttonInformation0;

    @BindView(R.id.buttonInformation1)
    Button buttonInformation1;

    @BindView(R.id.buttonInformation2)
    Button buttonInformation2;

    @BindView(R.id.home)
    ImageView homeButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_app_menu);
        viewBinding();
        initToasty();
    }

    private void viewBinding(){
        ButterKnife.bind(this);
    }

    private void initToasty(){
        Toasty.Config.getInstance().apply();
    }

    @OnClick(R.id.buttonInformation0)
    public void onClickedAppInformation(){
        Toasty.warning(this, getResources().getString(R.string.under_construction), Toast.LENGTH_SHORT,true).show();
    }

    @OnClick(R.id.buttonInformation1)
    public void onClickedAppVersionInformation(){
        startActivity(new Intent(this, AboutAppActivity.class));
    }

    @OnClick(R.id.buttonInformation2)
    public void onClickedChangeLog(){
        ChangeLog cl = new ChangeLog(this);
        cl.getLogDialog().show();
//        Toasty.warning(this,  getResources().getString(R.string.under_construction), Toast.LENGTH_SHORT,true).show();
    }

    @OnClick(R.id.home)
    public void onClickedBackButton(){
        finish();
    }


}
