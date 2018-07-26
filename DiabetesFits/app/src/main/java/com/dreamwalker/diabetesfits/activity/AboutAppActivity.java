package com.dreamwalker.diabetesfits.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AboutAppActivity extends AppCompatActivity {


    private static final String TAG = "AboutAppActivity";


    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.textView5)
    TextView textView;

    @BindView(R.id.home)
    ImageView backImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ButterKnife.bind(this);

        //Glide.with(this).load(R.mipmap.ic_launcher_2).into(imageView);

        String version = null;

        // TODO: 2018-07-25 폰트 생성용 - 박제창
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NotoSansCJKkr-Thin.otf");
        textView.setTypeface(font, Typeface.NORMAL);

        try {
            PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = i.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String v = "버전 정보 : " + version;
        textView.setText(v);

        Log.e(TAG, "onCreate: " + version );
    }

    @OnClick(R.id.home)
    public void onClickedBackButton(){
        finish();
    }
}
