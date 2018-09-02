package com.dreamwalker.diabetesfits.activity.diary;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;

import org.angmarch.views.NiceSpinner;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;

public class WriteFitnessActivity extends AppCompatActivity {
    private static final String TAG = "WriteFitnessActivity";

    @BindView(R.id.nice_spinner)
    NiceSpinner niceSpinner;
    @BindView(R.id.nice_spinner_2)
    NiceSpinner niceSpinner2;
    @BindView(R.id.nice_spinner_3)
    NiceSpinner niceSpinner3;


    @BindView(R.id.weight_edt)
    TextInputEditText weightEditText;

    @BindView(R.id.home)
    ImageView backButton;

    @BindView(R.id.done)
    ImageView doneButton;


    String selectType;
    String selectTypeDetail;
    String selectRpeExpression;
    String userWeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_fitness);
        initSetting();

        selectType = "트레드밀";
        selectTypeDetail = "가볍게걷기";
        selectRpeExpression = "전혀 힘들지 않다";

    }

    private void bindView() {
        ButterKnife.bind(this);
    }

    private void initPaper() {
        Paper.init(this);
    }

    private void initSetting() {
        bindView();
        setNiceSpinner();
        initPaper();
        initUserWeight();
    }

    private void initUserWeight() {
        userWeight = readUserWeightFromPaper();
        if (userWeight == null) {
            userWeight = "None";
        } else {
            weightEditText.setText(userWeight);
        }
    }

    private String readUserWeightFromPaper() {
        String read;
        read = Paper.book("user").read("userWeight");
        return read;
    }

    private void setNiceSpinner() {

        List<String> dataset = new LinkedList<>(Arrays.asList("트레드밀", "실내자전거"));
        List<String> treadmillSet = new LinkedList<>(Arrays.asList("가볍게걷기", "일반 걷기", "달리기"));
        List<String> indoorBikeSet = new LinkedList<>(Arrays.asList("보통으로", "빠르게", "가볍게"));
        List<String> rpeSet = new LinkedList<>(Arrays.asList("전혀 힘들지 않다", "힘들지 않다", "보통이다", "약간 힘들다", "힘들다", "매우 힘들다", "매우 매우 힘들다"));

        niceSpinner.attachDataSource(dataset);
        niceSpinner2.attachDataSource(treadmillSet);
        niceSpinner3.attachDataSource(rpeSet);

        niceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectType = "트레드밀";
                        niceSpinner2.attachDataSource(treadmillSet);
                        break;
                    case 1:
                        selectType = "실내자전거";
                        niceSpinner2.attachDataSource(indoorBikeSet);
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
                if (selectType.equals("트레드밀")) {
                    switch (position) {
                        case 0:
                            selectTypeDetail = treadmillSet.get(0);
                            break;
                        case 1:
                            selectTypeDetail = treadmillSet.get(1);
                            break;
                        case 2:
                            selectTypeDetail = treadmillSet.get(2);
                            break;
                    }
                    Log.e(TAG, " niceSpinner2 onItemSelected: " + selectTypeDetail);

                } else if (selectType.equals("실내자전거")) {
                    switch (position) {
                        case 0:
                            selectTypeDetail = indoorBikeSet.get(0);
                            break;
                        case 1:
                            selectTypeDetail = indoorBikeSet.get(1);
                            break;
                        case 2:
                            selectTypeDetail = indoorBikeSet.get(2);
                            break;
                    }
                    Log.e(TAG, " niceSpinner2 onItemSelected: " + selectTypeDetail);
                }
                Log.e(TAG, "niceSpinner2 onItemSelected:  " + position + "," + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        niceSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        selectRpeExpression = rpeSet.get(0);
                        break;
                    case 1:
                        selectRpeExpression = rpeSet.get(1);
                        break;
                    case 2:
                        selectRpeExpression = rpeSet.get(2);
                        break;
                    case 3:
                        selectRpeExpression = rpeSet.get(3);
                        break;
                    case 4:
                        selectRpeExpression = rpeSet.get(4);
                        break;
                    case 5:
                        selectRpeExpression = rpeSet.get(5);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    // TODO: 2018-09-02 이지미 뷰이지만 버튼으로 구성했습니다.- 박제창
    @OnClick(R.id.home)
    public void onClickedBackButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("운동 입력을 종료하시겠어요?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @OnClick(R.id.done)
    public void onClickedDoneButton() {

    }
}
