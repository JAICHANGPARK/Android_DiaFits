package com.dreamwalker.diabetesfits.activity.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.paperdb.Paper;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";

    @BindView(R.id.glucose_min_edt)
    TextInputEditText minGlucoseEditText;

    @BindView(R.id.glucose_max_edt)
    TextInputEditText maxGlucoseEditText;

    @BindView(R.id.weight_edt)
    TextInputEditText weightEditText;

    @BindView(R.id.height_edt)
    TextInputEditText heightEditText;

    @BindView(R.id.home)
    ImageView homeButton;

    @BindView(R.id.bmi_text_view)
    TextView bmiTextView;

    @BindView(R.id.bmi_result_text_view)
    TextView bmiResultTextView;

    String minGlucose;
    String maxGlucose;
    String userHeight;
    String userWeight;

    String userChangeGlucoseMin, userChangeGlucoseMax;
    String userChangeHeight, userChangeWeight;

    long time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initSetting();
        minGlucose = Paper.book("user").read("userGlucoseMin");
        maxGlucose = Paper.book("user").read("userGlucoseMax");
        userHeight = Paper.book("user").read("userHeight");
        userWeight = Paper.book("user").read("userWeight");

        if (userWeight != null && userHeight != null) {
            Log.e(TAG, "onCreate: userWeight, userHeight" + userWeight + " , " + userHeight);
            float tmpWeight = ((Float.valueOf(userHeight) / 100) * (Float.valueOf(userHeight) / 100));
            Log.e(TAG, "tmpWeight: " + tmpWeight);
            float userBmi = Float.valueOf(userWeight) / tmpWeight;
            Log.e(TAG, "userBmi: " + userBmi);
            String message = "BMI : " + Math.round(userBmi);
            bmiTextView.setText(message);

            if (userBmi < 18.5f) {
                String resultMessage = "저체중 입니다.";
                bmiResultTextView.setText(resultMessage);
            } else if (userBmi >= 18.5f && userBmi < 23) {
                String resultMessage = "정상 입니다.";
                bmiResultTextView.setText(resultMessage);
            } else if (userBmi >= 23 && userBmi < 25) {
                String resultMessage = "과체중 입니다.";
                bmiResultTextView.setText(resultMessage);
            } else if (userBmi >= 25 && userBmi < 30) {
                String resultMessage = "비만 입니다.";
                bmiResultTextView.setText(resultMessage);
            } else if (userBmi >= 30) {
                String resultMessage = "고도비만 입니다.";
                bmiResultTextView.setText(resultMessage);
            }

        } else {
            if (userWeight == null) {
                String message = "체중 값을 입력 해주세요";
                bmiTextView.setText(message);
            }
            if (userHeight == null) {
                String message = "신장 값을 입력 해주세요";
                bmiTextView.setText(message);
            }
        }

        if (minGlucose != null) {
            minGlucoseEditText.setText(minGlucose);
        } else {
            minGlucose = "None";
        }

        if (maxGlucose != null) {
            maxGlucoseEditText.setText(maxGlucose);
        } else {
            maxGlucose = "None";
        }

        if (userHeight == null) {
            userHeight = "None";
        } else {
            heightEditText.setText(userHeight);
        }

        if (userWeight == null) {
            userWeight = "None";
        } else {
            weightEditText.setText(userWeight);
        }


        setEditTextKeyboardListener();


    }

    private void bindView() {
        ButterKnife.bind(this);
    }

    private void getPaper() {
        Paper.init(this);
    }

    private void initToasty() {
        Toasty.Config.getInstance().apply();
    }

    private void initSetting() {
        bindView();
        getPaper();
        initToasty();
    }


    @OnClick(R.id.home)
    public void onClickedBackButton() {
        finish();
    }

    private void setEditTextKeyboardListener() {
        minGlucoseEditText.setOnKeyListener((v, keyCode, event) -> {
            Log.e(TAG, "setEditTextKeyboardListener: " + keyCode);
            Log.e(TAG, "setEditTextKeyboardListener: " + event.getAction());
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                if (minGlucoseEditText.getText().toString().length() == 0) {
                    if (minGlucose != null) {
                        minGlucoseEditText.setText(minGlucose);
                    } else {
                        Log.e(TAG, "setEditTextKeyboardListener: 최소값 널값임");
                    }

                } else {
                    userChangeGlucoseMin = minGlucoseEditText.getText().toString();
                }

                hideKeyboard(maxGlucoseEditText);
                return true;
            }
            return false;
        });

        maxGlucoseEditText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                if (maxGlucoseEditText.getText().toString().length() == 0) {
                    if (maxGlucose != null) {
                        maxGlucoseEditText.setText(maxGlucose);
                    } else {
                        Log.e(TAG, "setEditTextKeyboardListener: 최대값 널값임");
                    }

                } else {
                    userChangeGlucoseMax = maxGlucoseEditText.getText().toString();
                }

                hideKeyboard(maxGlucoseEditText);
                return true;
            }
            return false;
        });

        heightEditText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                if (heightEditText.getText().toString().length() == 0) {
                    if (userHeight != null) {
                        heightEditText.setText(userHeight);
                    } else {
                        Log.e(TAG, "setEditTextKeyboardListener: 신장 값 널값임");
                    }
                } else {
                    userChangeHeight = heightEditText.getText().toString();
                }
                hideKeyboard(heightEditText);
                return true;
            }
            return false;
        });

        weightEditText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                if (weightEditText.getText().toString().length() == 0) {
                    if (userWeight != null) {
                        weightEditText.setText(userWeight);
                    } else {
                        Log.e(TAG, "setEditTextKeyboardListener: 신장 값 널값임");
                    }
                } else {
                    userChangeWeight = weightEditText.getText().toString();
                }

                hideKeyboard(weightEditText);
                return true;
            }
            return false;
        });

    }

    private void hideKeyboard(TextInputEditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }


    @Override
    public void onBackPressed() {
        // TODO: 2018-08-28 변경된 정보가 없을 때
        if (userChangeGlucoseMin == null && userChangeGlucoseMax == null && userChangeWeight == null && userChangeHeight == null) {
            if (System.currentTimeMillis() - time >= 2000) {
                time = System.currentTimeMillis();
                Toasty.info(EditProfileActivity.this, "변경된 정보 없음 : 뒤로 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT, true).show();
            } else if (System.currentTimeMillis() - time < 2000) {
                finish();
            }
        }
        // TODO: 2018-08-28 만약 정보가 변경되었다면
        else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("알림");
            builder.setMessage("정보를 저장하시겠습니까?");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO: 2018-08-28 뭐든 하나가 널이 아니기에 이곳에 도달했고  널이 아닌것만 저장하도록 한다. - 박제창
                    if (userChangeGlucoseMin != null) {
                        Paper.book("user").write("userGlucoseMin", userChangeGlucoseMin);
                    }
                    if (userChangeGlucoseMax != null) {
                        Paper.book("user").write("userGlucoseMax", userChangeGlucoseMax);
                    }
                    if (userChangeHeight != null) {
                        Paper.book("user").write("userHeight", userChangeHeight);
                    }
                    if (userChangeWeight != null) {
                        Paper.book("user").write("userWeight", userChangeWeight);
                    }

                    Toasty.success(EditProfileActivity.this, "변경 사항 저장 성공", Toast.LENGTH_SHORT, true).show();
                    dialog.dismiss();
                    finish();

                }
            });

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.show();
        }
    }
}
