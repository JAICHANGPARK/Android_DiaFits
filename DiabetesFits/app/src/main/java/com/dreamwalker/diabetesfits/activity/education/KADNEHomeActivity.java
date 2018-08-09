package com.dreamwalker.diabetesfits.activity.education;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.education.EduAdapter;
import com.dreamwalker.diabetesfits.utils.education.KDANEListMaker;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class KADNEHomeActivity extends AppCompatActivity {
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.home)
    ImageView backButton;

    EduAdapter adapter;

    ArrayList<String> bigList = new ArrayList<>();
    ArrayList<String> childList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kadnehome);
        bindAllViews();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof DefaultItemAnimator) {
            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
        }

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                layoutManager.getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter = new EduAdapter(this, new KDANEListMaker(this).makeGenres());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void bindAllViews(){
        ButterKnife.bind(this);
    }

    @OnClick(R.id.home)
    public void onBackButtonClicked(){
        finish();
    }
}
