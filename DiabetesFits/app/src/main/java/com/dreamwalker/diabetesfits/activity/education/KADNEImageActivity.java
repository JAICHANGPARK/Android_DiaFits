package com.dreamwalker.diabetesfits.activity.education;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.education.EduImageAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KADNEImageActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    LinearLayoutManager linearLayoutManager;
    ArrayList<Integer> imageList;
    EduImageAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kadneimage);
        bindAllViews();

        recyclerView.setHasFixedSize(true);
        linearLayoutManager  = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageList  = new ArrayList<>();

        adapter = new EduImageAdapter(this, imageList);
        recyclerView.setAdapter(adapter);

    }

    private void bindAllViews(){
        ButterKnife.bind(this);
    }
}
