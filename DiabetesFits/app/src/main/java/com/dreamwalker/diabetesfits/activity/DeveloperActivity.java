package com.dreamwalker.diabetesfits.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.consts.IntentConst;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeveloperActivity extends AppCompatActivity {

    /**
     * The Github button.
     */
    @BindView(R.id.GithubButton)
    Button githubButton;

    /**
     * The Qiita button.
     */
    @BindView(R.id.qiitaButton)
    Button qiitaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer);
        ButterKnife.bind(this);
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
        //setupToolbar();
    }


    /**
     * On github clicked.
     *
     * @param view the view
     */
    @OnClick(R.id.GithubButton)
    void onGithubClicked(View view) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(IntentConst.WEB_URL, "https://github.com/JAICHANGPARK");
        startActivity(intent);
    }

    /**
     * On qitta clicked.
     *
     * @param view the view
     */
    @OnClick(R.id.qiitaButton)
    void onQittaClicked(View view) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(IntentConst.WEB_URL, "https://qiita.com/Dreamwalker");
        startActivity(intent);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            View toolbarView = getLayoutInflater().inflate(R.layout.action_bar, null, false);
            TextView titleView = toolbarView.findViewById(R.id.toolbar_title);
            titleView.setText(Html.fromHtml("<b>Developer</b>"));

            getSupportActionBar().setCustomView(toolbarView, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }
}
