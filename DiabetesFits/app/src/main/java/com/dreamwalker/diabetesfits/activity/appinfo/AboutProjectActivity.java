package com.dreamwalker.diabetesfits.activity.appinfo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dreamwalker.diabetesfits.R;
import com.mukesh.MarkdownView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutProjectActivity extends AppCompatActivity {

    @BindView(R.id.markdown_view)
    MarkdownView markdownView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_project);

        ButterKnife.bind(this);
        markdownView.loadMarkdownFromAssets("ABOUTAPP.md"); //Loads the markdown file from the assets folder

    }
}
