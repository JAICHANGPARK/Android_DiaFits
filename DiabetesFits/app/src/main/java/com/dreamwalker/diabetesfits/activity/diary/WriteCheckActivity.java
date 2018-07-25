package com.dreamwalker.diabetesfits.activity.diary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.HomeActivity;
import com.dreamwalker.diabetesfits.common.Common;
import com.dreamwalker.diabetesfits.consts.PageConst;
import com.dreamwalker.diabetesfits.database.model.Glucose;
import com.dreamwalker.diabetesfits.model.Validate;
import com.dreamwalker.diabetesfits.remote.IUploadAPI;
import com.dreamwalker.diabetesfits.remote.IWriteAPI;
import com.dreamwalker.progresssubmitbutton.SubmitButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.dreamwalker.diabetesfits.consts.IntentConst.USER_WRITE_GLUCOSE;

public class WriteCheckActivity extends AppCompatActivity {
    private static final String TAG = "WriteCheckActivity";

    @BindView(R.id.submit_button)
    SubmitButton submitButton;
    @BindView(R.id.type_text_view)
    TextView typeTextView;
    @BindView(R.id.glucose_text_view)
    TextView glucoseTextView;

    @BindView(R.id.date_text_view)
    TextView dateTextView;
    @BindView(R.id.time_text_view)
    TextView timeTextView;

    HashMap<String, String> userInputMap = new HashMap<>();

    Retrofit retrofit;
    IWriteAPI writeService;
    IUploadAPI uploadService;
    String userID;

    String[] dateTime;
    String pageNum;

    Handler handler;
    Vibrator vibrator;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_check);
        ButterKnife.bind(this);
        Paper.init(this);

        Realm.init(this);
        realm = Realm.getDefaultInstance();



        handler = new Handler();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        userID = Paper.book("user").read("userID");
        pageNum = String.valueOf(PageConst.HOME_PAGE);
        // TODO: 2018-07-25 Retrofit 서비스 생성요 - 박제창
        writeService = Common.getGlucoseWriteServie();

        uploadService = Common.getUploadService();
        uploadService.userAccess(userID, pageNum).enqueue(new Callback<Validate>() {
            @Override
            public void onResponse(Call<Validate> call, Response<Validate> response) {

            }

            @Override
            public void onFailure(Call<Validate> call, Throwable t) {

            }
        });

        // TODO: 2018-07-25 폰트 생성용 - 박제창
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/NotoSansCJKkr-Thin.otf");
        typeTextView.setTypeface(font, Typeface.NORMAL);
        //chronometer.setTypeface(font, Typeface.NORMAL);
        userInputMap = (HashMap<String, String>) getIntent().getSerializableExtra(USER_WRITE_GLUCOSE);

        for (Map.Entry<String, String> entry : userInputMap.entrySet()) {
            Log.e(TAG, "onCreate: " + entry.getKey() + "--> " + entry.getValue());
        }

        glucoseTextView.setText(userInputMap.get("userGlucose"));
        typeTextView.setText(userInputMap.get("userType"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);
        SimpleDateFormat ticketDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        SimpleDateFormat ticketTimeFormat = new SimpleDateFormat("hh:mm:ss", Locale.KOREA);

        String timestamp = userInputMap.get("timestamp");
        Date date = new Date(Long.valueOf(timestamp));
        String newDate = simpleDateFormat.format(date);
        dateTime = newDate.split(" ");
        dateTextView.setText(dateTime[0]);
        timeTextView.setText(dateTime[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(timestamp));

        Log.e(TAG, "onCreate: " + timestamp + " --> " + newDate + " == >> " + calendar.getTime());

    }

    @OnClick(R.id.submit_button)
    public void submitted() {

        Toast.makeText(this, "submit clicked", Toast.LENGTH_SHORT).show();

        writeService.writeGlucose(userID, userInputMap.get("userGlucose"), userInputMap.get("userType"),
                dateTime[0], dateTime[1], userInputMap.get("timestamp")).enqueue(new Callback<Validate>() {
            @Override
            public void onResponse(Call<Validate> call, Response<Validate> response) {
                String result = response.body().getSuccess();
                if (result.equals("true")) {

                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            Glucose glucose = realm.createObject(Glucose.class);
                            glucose.setValue(userInputMap.get("userGlucose"));
                            glucose.setType(userInputMap.get("userType"));
                            glucose.setDate(dateTime[0]);
                            glucose.setTime(dateTime[1]);
                            glucose.setTimestamp(userInputMap.get("timestamp"));
                        }
                    });


                    long[] pattern = {0, 100, 100, 50};
                    int[] amp = {100, 50};
                    handler.postDelayed(() -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createWaveform(pattern, amp, -1));
                        } else {
                            vibrator.vibrate(pattern, -1);
                        }
                        submitButton.doResult(true);
                    }, 1500);

                    handler.postDelayed(() -> {
                        startActivity(new Intent(WriteCheckActivity.this, HomeActivity.class));
                        finish();
                    }, 2500);
//                    try {
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }

                } else {
                    submitButton.doResult(false);
                }
            }

            @Override
            public void onFailure(Call<Validate> call, Throwable t) {
                submitButton.doResult(false);
            }
        });
    }

    private class MyTask extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            int i = 0;
            while (i <= 100) {
                try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                i++;
                publishProgress(i);
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean == null) {
                // sBtnProgress.reset();
            }
            submitButton.doResult(true);
            //sBtnProgress.doResult(aBoolean);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //sBtnProgress.setProgress(values[0]);
        }
    }
}
