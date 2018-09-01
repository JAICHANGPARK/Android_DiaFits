package com.dreamwalker.diabetesfits.activity.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    String minGlucose;
    String maxGlucose;
    String userHeight;
    String userWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initSetting();
        minGlucose = Paper.book("user").read("userGlucoseMin");
        maxGlucose = Paper.book("user").read("userGlucoseMax");
        userHeight = Paper.book("user").read("userHeight");
        userWeight = Paper.book("user").read("userWeight");

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
        }else {
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

    private void initSetting() {
        bindView();
        getPaper();

    }

    @OnClick(R.id.home)
    public void onClickedBackButton(){
        finish();
    }

    private void setEditTextKeyboardListener(){
        minGlucoseEditText.setOnKeyListener((v, keyCode, event) -> {
            Log.e(TAG, "setEditTextKeyboardListener: " + keyCode );
            Log.e(TAG, "setEditTextKeyboardListener: " + event.getAction() );
            if ((event.getAction() == KeyEvent.ACTION_DOWN )&& (keyCode == KeyEvent.KEYCODE_ENTER)){
                hideKeyboard(maxGlucoseEditText);
                return true;
            }
            return false;
        });

        maxGlucoseEditText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN )&& (keyCode == KeyEvent.KEYCODE_ENTER)){
                hideKeyboard(maxGlucoseEditText);
                return true;
            }
            return false;
        });
        heightEditText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                hideKeyboard(heightEditText);
                return true;
            }
            return false;
        });

        weightEditText.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                hideKeyboard(weightEditText);
                return true;
            }
            return false;
        });

    }

    private void hideKeyboard(TextInputEditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
