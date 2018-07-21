package com.dreamwalker.diabetesfits.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import com.dreamwalker.diabetesfits.R;
import com.luseen.verticalintrolibrary.VerticalIntro;
import com.luseen.verticalintrolibrary.VerticalIntroItem;

public class AppIntroVerticalActivity extends VerticalIntro {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_app_intro_vertical);
//    }

    SharedPreferences pref;

    @Override
    protected void onStart() {

        super.onStart();
        pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void init() {
//        else {
//            SharedPreferences.Editor ed = pref.edit();
//            ed.putBoolean("activity_executed", true);
//            ed.apply();
//        }

        addIntroItem(new VerticalIntroItem.Builder()
                .backgroundColor(R.color.white)
                .image(R.drawable.app_intro_03)
                .title(getString(R.string.app_intro_scn0_title))
                .text(getString(R.string.app_intro_scn0))
                .textSize(16)
                .titleSize(20)
                .textColor(R.color.black)
                .titleColor(R.color.black)
                .build());

        addIntroItem(new VerticalIntroItem.Builder()
                .backgroundColor(R.color.colorPrimary)
                .image(R.drawable.app_intro_02)
                .title(getString(R.string.app_intro_scn1_title))
                .text(getString(R.string.app_intro_scn1))
                .textSize(16)
                .titleSize(20)
                .build());

        addIntroItem(new VerticalIntroItem.Builder()
                .backgroundColor(R.color.colorAccent)
                .image(R.drawable.connection)
                .title(getString(R.string.app_intro_scn2_title))
                .text(getString(R.string.app_intro_scn2))
                .textSize(16)
                .titleSize(20)
                .build());

        setDoneText(getString(R.string.app_intro_done));
        setSkipEnabled(true);
        setVibrateEnabled(true);
        setSkipColor(R.color.black);
        setVibrateIntensity(60);
//        setCustomTypeFace(Typeface.createFromAsset(getAssets(), "fonts/NotoSansDisplay-Regular.ttf"));
    }

    @Override
    protected Integer setLastItemBottomViewColor() {
        return R.color.color2;
    }

    @Override
    protected void onSkipPressed(View view) {
        Log.e("onSkipPressed ", "onSkipPressed");
    }

    @Override
    protected void onFragmentChanged(int position) {
        Log.e("onFragmentChanged ", "" + position);
    }

    @Override
    protected void onDonePressed() {

        Intent intent = new Intent(this, DeviceScanActivity.class);
        startActivity(intent);
        finish();

        //Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
    }
}
