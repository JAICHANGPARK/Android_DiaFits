package com.dreamwalker.diabetesfits.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
            Intent intent = new Intent(this, HomeActivity.class);
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
                .title("Diabetes Grue Fitness")
                .text("안녕하세요 당뇨그루-피트니스를 설치해주셔서 감사합니다." +
                        "운동부하 자동획득 디바이스와 혈당 입력을 통해 목표 혈당을 유지해보세요!"
                        + " 디바이스가 없어도 괜찮습니다. 꾸준히 운동 입력과 혈당 입력만으로 혈당 관리를 시작해보세요")
                .textSize(16)
                .titleSize(20)
                .textColor(R.color.black)
                .titleColor(R.color.black)
                .build());

        addIntroItem(new VerticalIntroItem.Builder()
                .backgroundColor(R.color.colorPrimary)
                .image(R.drawable.app_intro_02)
                .title("Management Glucose with Fitness Machine")
                .text("목표 혈당 구간을 유지를 위해서 꾸준한 운동이 필요합니다. " +
                " 이제 당뇨그루 운동 부하 자동 기록 디바이스가 여러분의 운동 기록을 편하게 도와줄겁니다. " +
                " 운동 전/후 혈당 기록으로 목표하는 혈당 유지를 이뤄보세요. ")
                .textSize(16)
                .titleSize(20)
                .build());

        addIntroItem(new VerticalIntroItem.Builder()
                .backgroundColor(R.color.colorAccent)
                .image(R.drawable.connection)
                .title("Connect Automatic Fitness Device")
                .text("Treadmill 과 Indoor Bike 중 운동부하 자동 기록기 스캔을 통해 장비를 등록하세요"
                        + " 장비를 검색하기 위해서 1개의 권한 허가가 필요합니다."
                        + " 만약 허용하지 않는다면 장비 검색이 되지 않습니다.")
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

        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
    }
}
