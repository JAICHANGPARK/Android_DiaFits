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
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_EIGHT_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_EIGHT_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_FIVE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_SIX;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FOUR_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FOUR_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FOUR_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_NINE_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_NINE_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_NINE_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_NINE_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_FIVE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SEVEN_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SEVEN_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SEVEN_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SEVEN_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_FIVE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_SEVEN;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_SIX;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_TWO;
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

            case KDANE_PART_FOUR_ONE:
                imageList.add(R.drawable.image_kdane_30_00);
                break;
            case KDANE_PART_FOUR_TWO:
                imageList.add(R.drawable.image_kdane_31_00);
                break;
            case KDANE_PART_FOUR_THREE:
                imageList.add(R.drawable.image_kdane_32_00);
                break;

            case KDANE_PART_FIVE_ONE:
                imageList.add(R.drawable.image_kdane_40_00);
                break;
            case KDANE_PART_FIVE_TWO:
                imageList.add(R.drawable.image_kdane_41_00);
                break;
            case KDANE_PART_FIVE_THREE:
                imageList.add(R.drawable.image_kdane_42_00);
                break;
            case KDANE_PART_FIVE_FOUR:
                imageList.add(R.drawable.image_kdane_43_00);
                break;
            case KDANE_PART_FIVE_FIVE:
                imageList.add(R.drawable.image_kdane_44_00);
                break;
            case KDANE_PART_FIVE_SIX:
                imageList.add(R.drawable.image_kdane_45_00);
                break;

            case KDANE_PART_SIX_ONE:
                imageList.add(R.drawable.image_kdane_50_00);
                break;
            case KDANE_PART_SIX_TWO:
                imageList.add(R.drawable.image_kdane_51_00);
                break;
            case KDANE_PART_SIX_THREE:
                imageList.add(R.drawable.image_kdane_52_00);
                break;
            case KDANE_PART_SIX_FOUR:
                imageList.add(R.drawable.image_kdane_53_00);
                break;
            case KDANE_PART_SIX_FIVE:
                imageList.add(R.drawable.image_kdane_54_00);
                break;
            case KDANE_PART_SIX_SIX:
                imageList.add(R.drawable.image_kdane_55_00);
                break;
            case KDANE_PART_SIX_SEVEN:
                imageList.add(R.drawable.image_kdane_56_00);
                break;


            case KDANE_PART_SEVEN_ONE:
                imageList.add(R.drawable.image_kdane_60_00);
                break;
            case KDANE_PART_SEVEN_TWO:
                imageList.add(R.drawable.image_kdane_61_00);
                break;
            case KDANE_PART_SEVEN_THREE:
                imageList.add(R.drawable.image_kdane_62_00);
                break;
            case KDANE_PART_SEVEN_FOUR:
                imageList.add(R.drawable.image_kdane_63_00);
                break;

            case KDANE_PART_EIGHT_ONE:
                imageList.add(R.drawable.image_kdane_80_00);
                break;
            case KDANE_PART_EIGHT_TWO:
                imageList.add(R.drawable.image_kdane_81_00);
                break;

            case KDANE_PART_NINE_ONE:
                imageList.add(R.drawable.image_kdane_70_00);
                break;
            case KDANE_PART_NINE_TWO:
                imageList.add(R.drawable.image_kdane_71_00);
                break;
            case KDANE_PART_NINE_THREE:
                imageList.add(R.drawable.image_kdane_72_00);
                break;
            case KDANE_PART_NINE_FOUR:
                imageList.add(R.drawable.image_kdane_73_00);
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
