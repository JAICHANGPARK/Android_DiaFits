package com.dreamwalker.diabetesfits.activity.education;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.education.EduImageAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.dreamwalker.diabetesfits.consts.IntentConst.EDUCATION_PART;
import static com.dreamwalker.diabetesfits.consts.IntentConst.EDUCATION_PART_NAME;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_ONE;

public class KADNEImageActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    LinearLayoutManager linearLayoutManager;
    ArrayList<Integer> imageList;
    EduImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kadneimage);
        setSupportActionBar(toolbar);
        int pagePartNumber = getIntent().getIntExtra(EDUCATION_PART,-1);
        String pageTitle = getIntent().getStringExtra(EDUCATION_PART_NAME);
        getSupportActionBar().setTitle(pageTitle);

        bindAllViews();

        recyclerView.setHasFixedSize(true);
        linearLayoutManager  = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageList  = new ArrayList<>();
        
        switch (pagePartNumber){
            case KDANE_PART_ONE_ONE:
                imageList.add(R.drawable.image_kdane_00_00);
                imageList.add(R.drawable.image_kdane_00_01);
                break;
        }

        adapter = new EduImageAdapter(this, imageList);
        recyclerView.setAdapter(adapter);

    }

    private void bindAllViews(){
        ButterKnife.bind(this);
    }
}
