package com.dreamwalker.diabetesfits.activity.diary;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.common.Common;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_check);
        ButterKnife.bind(this);
        // TODO: 2018-07-25 Retrofit 서비스 생성요 - 박제창  
        writeService = Common.getGlucoseWriteServie();
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
        String[] dateTime = newDate.split(" ");
        dateTextView.setText(dateTime[0]);
        timeTextView.setText(dateTime[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(timestamp));


        Log.e(TAG, "onCreate: " + timestamp + " --> " + newDate  + " == >> " +   calendar.getTime());




    }

    @OnClick(R.id.submit_button)
    public void submitted(){
        Toast.makeText(this, "submit clicked", Toast.LENGTH_SHORT).show();
        submitButton.doResult(false);
    }
}
