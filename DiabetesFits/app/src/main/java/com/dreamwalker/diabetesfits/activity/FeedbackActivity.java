package com.dreamwalker.diabetesfits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeedbackActivity extends AppCompatActivity {
    /**
     * The Button contact 0.
     */
    @BindView(R.id.buttonContact0)
    Button buttonContact0;
    /**
     * The Button contact 1.
     */
    @BindView(R.id.buttonContact1)
    Button buttonContact1;
    /**
     * The Button contact 2.
     */
    @BindView(R.id.buttonContact2)
    Button buttonContact2;
    /**
     * The Button contact 3.
     */
    @BindView(R.id.buttonContact3)
    Button buttonContact3;
    /**
     * The Button service 0.
     */
    @BindView(R.id.buttonService0)
    Button buttonService0;
    /**
     * The Button service 1.
     */
    @BindView(R.id.buttonService1)
    Button buttonService1;

    @BindView(R.id.home)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);


    }

    /**
     * On button contact 0 licked.
     *
     * @param v the v
     */
    @OnClick(R.id.buttonContact0)
    public void onButtonContact0licked(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        String[] address = {"itsmejeffrey.dev@gmail.com"};    //이메일 주소 입력
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[개선사항건의]애플리케이션 개선사항 건의");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "개선사항을 건의합니다. 하단에 문제의 내용을 추가해주세요 ");
        startActivity(emailIntent);
    }

    /**
     * On button contact 1 licked.
     *
     * @param v the v
     */
    @OnClick(R.id.buttonContact1)
    public void onButtonContact1licked(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        String[] address = {"itsmejeffrey.dev@gmail.com"};    //이메일 주소 입력
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[이용불편건의]애플리케이션 이용관련 불편 건의");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "이용불편건의 합니다. 하단에 문제의 내용을 추가해주세요 ");
        startActivity(emailIntent);
    }

    /**
     * On button contact 2 licked.
     *
     * @param v the v
     */
    @OnClick(R.id.buttonContact2)
    public void onButtonContact2licked(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        String[] address = {"itsmejeffrey.dev@gmail.com"};    //이메일 주소 입력
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[버그 및 이상 신고]애플리케이션 관련 개선사항 건의");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "버그 및 이상 신고 건의합니다. 하단에 문제의 내용을 추가해주세요 ");
        startActivity(emailIntent);
    }

    /**
     * On button contact 3 licked.
     *
     * @param v the v
     */
    @OnClick(R.id.buttonContact3)
    public void onButtonContact3licked(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        String[] address = {"itsmejeffrey.dev@gmail.com"};    //이메일 주소 입력
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[기타문의]애플리케이션 관련 개선사항 건의");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "기타문의합니다.. 하단에 문제의 내용을 추가해주세요 ");
        startActivity(emailIntent);
    }

    @OnClick(R.id.home)
    public void onButtonClicked(){
        finish();
    }
}
