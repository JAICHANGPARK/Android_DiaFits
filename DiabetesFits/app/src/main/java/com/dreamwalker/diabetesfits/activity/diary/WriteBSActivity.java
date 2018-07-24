package com.dreamwalker.diabetesfits.activity.diary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.StaggeredWriteAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WriteBSActivity extends AppCompatActivity {

    private static final String TAG = "WriteBSActivity";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    StaggeredWriteAdapter adapter;

    ArrayList<String> name = new ArrayList<>();
    ArrayList<Integer> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_bs);
        ButterKnife.bind(this);
        initData();
        adapter = new StaggeredWriteAdapter(this, name, imageList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(adapter);


    }


    private void initData() {
        name.add("공복");
        imageList.add(R.drawable.coffee_backgound);

        name.add("아침 식전");
        imageList.add(R.drawable.eat_background);

        name.add("아침 식후");
        imageList.add(R.drawable.eat_01_background);

        name.add("점심 식전");
        imageList.add(R.drawable.eat_background);

        name.add("점심 식후");
        imageList.add(R.drawable.eat_01_background);

        name.add("저녁 식전");
        imageList.add(R.drawable.eat_02_background);

        name.add("저녁 식후");
        imageList.add(R.drawable.eat_02_background);

    }
}
