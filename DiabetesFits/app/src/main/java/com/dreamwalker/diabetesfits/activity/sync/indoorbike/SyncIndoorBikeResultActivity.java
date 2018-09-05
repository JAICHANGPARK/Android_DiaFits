package com.dreamwalker.diabetesfits.activity.sync.indoorbike;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.CustomItemClickListener;
import com.dreamwalker.diabetesfits.adapter.machine.FitnessSyncAdapter;
import com.dreamwalker.diabetesfits.model.fitness.Fitness;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class SyncIndoorBikeResultActivity extends AppCompatActivity  implements CustomItemClickListener {
    private static final String TAG = "SyncIndoorBikeResultAct";


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.coordinator)
    RelativeLayout coordinator;
    @BindView(R.id.toolbar)
    Toolbar myToolbar;
    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.animationLayout)
    LinearLayout animationLayout;

    @BindView(R.id.save)
    ImageView saveImageView;

    @BindView(R.id.home)
    ImageView homeImageView;

    ArrayList<Fitness> fitnessArrayList = new ArrayList<>();
    ArrayList<Fitness> subList;

    RecyclerView.LayoutManager layoutManager;
    FitnessSyncAdapter adapter;

    int newIndex;
    int oldIndex = 0;
    int subIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_indoor_bike_result);

        ButterKnife.bind(this);
        Paper.init(this);

        setSupportActionBar(myToolbar);
        setStatusBar();


        myToolbar.inflateMenu(R.menu.sync_bsm_menu);
        myToolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.save:
                    Snackbar.make(getWindow().getDecorView().getRootView(), "툴바저장버튼", Snackbar.LENGTH_SHORT).show();
                    return false;
                case R.id.analysis:
//                    startActivity(new Intent(SyncBMSResultActivity.this, AnalysisBSActivity.class));
                    //Snackbar.make(getWindow().getDecorView().getRootView(),"툴바분석", Snackbar.LENGTH_SHORT).show();
                    return false;
            }
            return false;
        });

        if (Paper.book("syncIndoorBike").read("data") == null) {
            Snackbar.make(getWindow().getDecorView().getRootView(), "데이터가 없어요", Snackbar.LENGTH_SHORT).show();
        } else {
            // TODO: 2018-02-27 동기화된 데이터를 가져옴.
            fitnessArrayList = Paper.book("syncIndoorBike").read("data");
        }

        for (Fitness f : fitnessArrayList){
            Log.e(TAG, "onCreate: " + f.getFitnessTime());
        }


        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        newIndex = fitnessArrayList.size(); //받아온 최신 데이터 사이즈
        Log.e(TAG, "init: newIndex -  " + newIndex);
        oldIndex = 0;
        // TODO: 2018-02-27 기존의 인덱스와 최신 인덱스 비교 저장이 가능해야함.
        if (Paper.book("syncIndoorBike").read("ptr") == null) {
            //Paper.book("syncBms").write("index", newIndex);
            Paper.book("syncIndoorBike").write("ptr", 0);
        } else {
            oldIndex = Paper.book("syncIndoorBike").read("ptr"); // 기존의 데이터 사이즈
            Log.e(TAG, "init: oldIndex -  " + oldIndex);
        }

        subIndex = newIndex - oldIndex; // 새로운 데이터와 기존 데이터 양의 차를 구해 새로 추가해야하는 개수를 구한다. - 박제창

        Log.e(TAG, "init: subIndex -  " + subIndex);
        if (subIndex == 0) {
            runOnUiThread(() -> {
                animationLayout.setVisibility(View.VISIBLE);
                lottieAnimationView.playAnimation();
                fab.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            });

            //dbTextView.setVisibility(View.GONE);
            // TODO: 2018-02-27 같은 데이터 양이라면
//            Log.e(TAG, "init: 같은 데이터 양이라면");
//            subList = new ArrayList<>(mBSList.subList(oldIndex, newIndex));
//            for (int i = 0; i < subList.size(); i++) {
//                Log.e(TAG, "subList - " + subList.get(i).getBsValue());
//            }
//            for (int i = 0; i < subList.size(); i++) {
//                date[i] = subList.get(i).getBsTime().split(",")[0];
//                time[i] = subList.get(i).getBsTime().split(",")[1];
//                Log.e(TAG, "init: mBSList - " + subList.get(i).getBsValue() + subList.get(i).getBsTime());
//            }
//            adapter = new BSMSyncAdapter(this, subList);
//            recyclerView.setAdapter(adapter);
            //추가할 데이터가 없어요
        } else if (subIndex > 0) {

            runOnUiThread(() -> {
                animationLayout.setVisibility(View.GONE);
                lottieAnimationView.cancelAnimation();
                fab.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            });

            // TODO: 2018-02-27 새로운 데이터가 더 많다면
            Log.e(TAG, "새로운 데이터가 더 많다면");
            //List<BloodSugar> subList =  mBSList.subList(oldIndex, newIndex);
            subList = new ArrayList<>(fitnessArrayList.subList(oldIndex, newIndex));
            // subList.add(mBSList.get(mBSList.size() - 1));
            for (int i = 0; i < subList.size(); i++) {
                Log.e(TAG, "subList - " + subList.get(i).getDistance());
            }

//            for (int i = 0; i < subList.size(); i++) {
//                date[i] = subList.get(i).getBsTime().split(",")[0];
//                time[i] = subList.get(i).getBsTime().split(",")[1];
//                Log.e(TAG, "init: mBSList - " + subList.get(i).getBsValue() + subList.get(i).getBsTime());
//            }

            adapter = new FitnessSyncAdapter(this, subList);
            recyclerView.setAdapter(adapter);
            // TODO: 2018-07-25 객체 생성하고 리스너 추가
            adapter.setCustomItemClickListener(this);

        } else if (subIndex < 0) {
            // TODO: 2018-02-27 기존 데이터가 더 많다면 ( 경우가 없을 듯)
        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //tapBarMenu.setVisibility(View.VISIBLE);
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 || dy < 0 && fab.isShown()) {
                    fab.hide();
                    //tapBarMenu.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
    }

    @Override
    public void onItemClick(View v, int position) {

    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sync_bsm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                return true;
            case R.id.analysis:
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
