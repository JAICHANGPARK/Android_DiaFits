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
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_FIVE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_THREE_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_THREE_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_THREE_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_THREE_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_TWO_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_TWO_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_TWO_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_TWO_TWO;

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
        bindAllViews();
        int pagePartNumber = getIntent().getIntExtra(EDUCATION_PART, -1);
        String pageTitle = getIntent().getStringExtra(EDUCATION_PART_NAME);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(pageTitle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);


        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageList = new ArrayList<>();

        switch (pagePartNumber) {

            case KDANE_PART_ONE_ONE:
                imageList.add(R.drawable.image_kdane_00_00);
                imageList.add(R.drawable.image_kdane_00_01);
                break;
            case KDANE_PART_ONE_TWO:
                imageList.add(R.drawable.image_kdane_01_00);
                break;
            case KDANE_PART_ONE_THREE:
                imageList.add(R.drawable.image_kdane_02_00);
                break;
            case KDANE_PART_ONE_FOUR:
                imageList.add(R.drawable.image_kdane_03_00);
                break;
            case KDANE_PART_ONE_FIVE:
                imageList.add(R.drawable.image_kdane_04_00);
                break;

            case KDANE_PART_TWO_ONE:
                imageList.add(R.drawable.image_kdane_10_00);
                break;
            case KDANE_PART_TWO_TWO:
                imageList.add(R.drawable.image_kdane_11_00);
                break;
            case KDANE_PART_TWO_THREE:
                imageList.add(R.drawable.image_kdane_12_00);
                break;
            case KDANE_PART_TWO_FOUR:
                imageList.add(R.drawable.image_kdane_13_00);
                break;

            case KDANE_PART_THREE_ONE:
                imageList.add(R.drawable.image_kdane_20_00);
                break;
            case KDANE_PART_THREE_TWO:
                imageList.add(R.drawable.image_kdane_21_00);
                break;
            case KDANE_PART_THREE_THREE:
                imageList.add(R.drawable.image_kdane_22_00);
                break;
            case KDANE_PART_THREE_FOUR:
                imageList.add(R.drawable.image_kdane_23_00);
                break;


            default:
                break;


        }

        adapter = new EduImageAdapter(this, imageList);
        recyclerView.setAdapter(adapter);

    }

    private void bindAllViews() {
        ButterKnife.bind(this);
    }
}
