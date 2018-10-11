package com.dreamwalker.diabetesfits.activity.fitnessloadtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.IActivityBasicSetting;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TestStartBeforeActivity extends AppCompatActivity implements IActivityBasicSetting {
    private static final String TAG = "TestStartBeforeActivity";


    @BindView(R.id.top_title_message)
    TextView topTextView;

    @BindView(R.id.mid_title_message)
    TextView midTextView;

    @BindView(R.id.start_button)
    MaterialButton startButton;

    MediaPlayer mediaPlayer;
    AudioManager audioManager;

    BroadcastReceiver audioReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(AudioManager.RINGER_MODE_CHANGED_ACTION)) {
                Log.e(TAG, "onReceive: action changed");
                if (audioManager != null) {
                    switch (audioManager.getRingerMode()) {
                        case AudioManager.RINGER_MODE_SILENT:
                            Log.e(TAG, "RINGER_MODE_SILENT: ");
                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            Log.e(TAG, "RINGER_MODE_SILENT: ");
                            break;
                        case AudioManager.RINGER_MODE_NORMAL:
                            Log.e(TAG, "RINGER_MODE_SILENT: ");

                            break;
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_start_before);

        initSetting();
        registerReceiver(audioReceiver, intentFilter());

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.e(TAG, "onCompletion: " );

                mediaPlayer = MediaPlayer.create(TestStartBeforeActivity.this, R.raw.intro_test_02);

                switch (audioManager.getRingerMode()) {
                    case AudioManager.RINGER_MODE_SILENT:
                        Log.e(TAG, "RINGER_MODE_SILENT: ");
                        break;
                    case AudioManager.RINGER_MODE_VIBRATE:
                        Log.e(TAG, "RINGER_MODE_SILENT: ");
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        Log.e(TAG, "RINGER_MODE_SILENT: ");

                        if (mediaPlayer != null) {
                            mediaPlayer.start();
                        }
                        break;
                }
            }
        });

    }


    @Override
    public void bindView() {
        ButterKnife.bind(this);
    }

    @Override
    public void initSetting() {
        bindView();
        initFontFace();
        initAudioPlay();

    }

    private void initFontFace(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/NotoSansCJKkr-Thin.otf");
        topTextView.setTypeface(typeface);
        midTextView.setTypeface(typeface);
        startButton.setTypeface(typeface);
    }

    private void initAudioPlay() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = MediaPlayer.create(this, R.raw.intro_test);
        // TODO: 2018-10-11 사용자 알람상태에 따라 처리하기
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                Log.e(TAG, "RINGER_MODE_SILENT: ");
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                Log.e(TAG, "RINGER_MODE_SILENT: ");
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                Log.e(TAG, "RINGER_MODE_SILENT: ");

                if (mediaPlayer != null) {
                    mediaPlayer.start();
                }
                break;
        }
    }

    private IntentFilter intentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        unregisterReceiver(audioReceiver);
        super.onDestroy();
    }

    @OnClick(R.id.start_button)
    public void onClickedStartButton(){
        Log.e(TAG, "onClickedStartButton: clicked" );
    }

    @OnClick(R.id.home)
    public void onClickedBackButton(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("검사를 종료하시겠어요?");
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


}
