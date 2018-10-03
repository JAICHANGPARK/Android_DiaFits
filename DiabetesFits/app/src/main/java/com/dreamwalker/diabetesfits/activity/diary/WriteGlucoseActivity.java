package com.dreamwalker.diabetesfits.activity.diary;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
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

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class WriteGlucoseActivity extends AppCompatActivity {
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

    Realm realm;
    RealmConfiguration realmConfiguration;


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


    private void setNiceSpinner() {

        List<String> dataset = new LinkedList<>(Arrays.asList("공복", "취침 전", "운동", "아침 식사", "점심 식사", "저녁 식사"));
        List<String> detailDataSet = new LinkedList<>(Arrays.asList("전", "후"));
        List<String> blankDataSet = new LinkedList<>(Arrays.asList("없음"));

        niceSpinner.attachDataSource(dataset);
        niceSpinner2.attachDataSource(blankDataSet);

        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //공복
                        Log.e(TAG, "onItemSelected: " + dataset.get(position));
                        niceSpinner2.attachDataSource(blankDataSet);
                        break;
                    case 1: // 취칮전
                        Log.e(TAG, "onItemSelected: " + dataset.get(position));
                        niceSpinner2.attachDataSource(blankDataSet);
                        break;
                    case 2: // 운동
                        Log.e(TAG, "onItemSelected: " + dataset.get(position));
                        niceSpinner2.attachDataSource(detailDataSet);
                        break;
                    case 3: // 아침 식사
                        Log.e(TAG, "onItemSelected: " + dataset.get(position));
                        niceSpinner2.attachDataSource(detailDataSet);
                        break;
                    case 4: //점심식사
                        Log.e(TAG, "onItemSelected: " + dataset.get(position));
                        niceSpinner2.attachDataSource(detailDataSet);
                        break;
                    case 5: // 저녁 식사
                        Log.e(TAG, "onItemSelected: " + dataset.get(position));
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
                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @OnClick(R.id.fab)
    public void onClickFlotingActionButton() {
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
        finish();
    }


    @Override
    protected void onDestroy() {
        Realm.getInstance(realmConfiguration).close();
        super.onDestroy();
    }

}
