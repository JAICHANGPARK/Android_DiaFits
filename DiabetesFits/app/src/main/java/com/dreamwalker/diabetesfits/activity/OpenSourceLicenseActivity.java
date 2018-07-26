package com.dreamwalker.diabetesfits.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dreamwalker.diabetesfits.R;
import com.mukesh.MarkdownView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpenSourceLicenseActivity extends AppCompatActivity {

    @BindView(R.id.markdown_view)
    MarkdownView markdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source_license);
        ButterKnife.bind(this);
        markdownView.loadMarkdownFromAssets("README.md"); //Loads the markdown file from the assets folder

    }
}
