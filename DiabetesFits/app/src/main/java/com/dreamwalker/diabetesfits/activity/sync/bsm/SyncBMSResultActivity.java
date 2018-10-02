package com.dreamwalker.diabetesfits.activity.sync.bsm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.chart.AnalysisBSActivity;
import com.dreamwalker.diabetesfits.adapter.CustomItemClickListener;
import com.dreamwalker.diabetesfits.adapter.isens.BSMSyncAdapter;
import com.dreamwalker.diabetesfits.consts.GlucoseType;
import com.dreamwalker.diabetesfits.database.MyMigration;
import com.dreamwalker.diabetesfits.database.RealmManagement;
import com.dreamwalker.diabetesfits.database.model.Glucose;
import com.dreamwalker.diabetesfits.model.isens.BloodSugar;
import com.mingle.entity.MenuEntity;
import com.mingle.sweetpick.BlurEffect;
import com.mingle.sweetpick.RecyclerViewDelegate;
import com.mingle.sweetpick.SweetSheet;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.paperdb.Paper;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SyncBMSResultActivity extends AppCompatActivity implements CustomItemClickListener {

    private static final String TAG = "SyncBMSResultActivity";

    @BindView(R.id.bsm_recycler_view)
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

//    @BindView(R.id.tapBarMenu)
//    TapBarMenu tapBarMenu;

    ArrayList<BloodSugar> mBSList;
    RecyclerView.LayoutManager layoutManager;
    BSMSyncAdapter adapter;

    //Bottom Sheet
    SweetSheet mSweetSheet;
    Workbook workbook;
    Sheet sheet; // 새로운 시트 생성

    //BSDBHelper bsdbHelper;
    SQLiteDatabase db;
    String dbName = "bs.db";
    int dbVersion = 1; // 데이터베이스 버전

    int newIndex;
    int oldIndex = 0;
    int subIndex;

    String[] date;
    String[] time;
    ArrayList<BloodSugar> subList;

    ArrayList<Glucose> copyList = new ArrayList<>();

    CharSequence[] values = {GlucoseType.FASTING, GlucoseType.SLEEP, GlucoseType.BREAKFAST_BEFORE,
            GlucoseType.BREAKFAST_AFTER, GlucoseType.LUNCH_BEFORE, GlucoseType.LUNCH_AFTER, GlucoseType.DINNER_BEFORE,
            GlucoseType.DINNER_AFTER, GlucoseType.FITNESS_BEFORE, GlucoseType.FITNESS_AFTER};

    Realm realm;
    RealmConfiguration realmConfiguration;

    private RealmConfiguration getRealmConfig(){
        return new RealmConfiguration.Builder().schemaVersion(1).migration(new MyMigration()).build();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_bmsresult);
        ButterKnife.bind(this);
        Paper.init(this);
        setSupportActionBar(myToolbar);
        setStatusBar();

        Realm.init(this);
//        realmConfiguration = getRealmConfig();
        realmConfiguration = RealmManagement.getRealmConfiguration();
        Realm.setDefaultConfiguration(realmConfiguration);



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
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        //setTitle("동기화 결과");

        init();
        setupRecyclerView();
//        bsdbHelper = new BSDBHelper(this, dbName, null, dbVersion);
//        try {
//            db = bsdbHelper.getWritableDatabase(); // 읽고 쓸수 있는 DB
//        } catch (SQLiteException e) {
//            e.printStackTrace();
//            Log.e(TAG, "데이터베이스를 얻어올 수 없음");
//            finish(); // 액티비티 종료
//        }
    }

//    @OnClick(R.id.tapBarMenu)
//    public void onMenuButtonClick() {
//        tapBarMenu.toggle();
//    }

    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
    }

    @OnClick(R.id.fab)
    public void onFabClicked() {
        mSweetSheet.toggle();
    }


//    @OnClick({ R.id.item1, R.id.item2, R.id.item3, R.id.item4 })
//    public void onMenuItemClick(View view) {
//        tapBarMenu.close();
//        switch (view.getId()) {
//            case R.id.item1:
//                Log.e("TAG", "Item 1 selected");
//                break;
//            case R.id.item2:
//                Log.e("TAG", "Item 2 selected");
//                break;
//            case R.id.item3:
//                Log.e("TAG", "Item 3 selected");
//                break;
//            case R.id.item4:
//                Log.e("TAG", "Item 4 selected");
//                break;
//        }
//    }

    public void init() {

        mBSList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if (Paper.book("syncBms").read("data") == null) {
            Snackbar.make(getWindow().getDecorView().getRootView(), "데이터가 없어요", Snackbar.LENGTH_SHORT).show();
        } else {
            // TODO: 2018-02-27 동기화된 데이터를 가져옴.
            mBSList = Paper.book("syncBms").read("data");
        }

        date = new String[mBSList.size()];
        time = new String[mBSList.size()];

        newIndex = mBSList.size(); //받아온 최신 데이터 사이즈
        Log.e(TAG, "init: newIndex -  " + newIndex);
        oldIndex = 0;
        // TODO: 2018-02-27 기존의 인덱스와 최신 인덱스 비교 저장이 가능해야함.
        if (Paper.book("syncBms").read("ptr") == null) {
            //Paper.book("syncBms").write("index", newIndex);
            Paper.book("syncBms").write("ptr", 0);
        } else {
            oldIndex = Paper.book("syncBms").read("ptr"); // 기존의 데이터 사이즈
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
            subList = new ArrayList<>(mBSList.subList(oldIndex, newIndex));
            // subList.add(mBSList.get(mBSList.size() - 1));
            for (int i = 0; i < subList.size(); i++) {
                Log.e(TAG, "subList - " + subList.get(i).getBsValue());
            }

            for (int i = 0; i < subList.size(); i++) {
                date[i] = subList.get(i).getBsTime().split(",")[0];
                time[i] = subList.get(i).getBsTime().split(",")[1];
                Log.e(TAG, "init: mBSList - " + subList.get(i).getBsValue() + subList.get(i).getBsTime());
            }

            adapter = new BSMSyncAdapter(this, subList);
            recyclerView.setAdapter(adapter);
            // TODO: 2018-07-25 객체 생성하고 리스너 추가
            adapter.setCustomItemClickListener(this);

        } else if (subIndex < 0) {
            // TODO: 2018-02-27 기존 데이터가 더 많다면 ( 경우가 없을 듯)
        }

        // TODO: 2018-02-28 데이터베이스 추가를 위한 처리  

//        for (int i = 0; i < mBSList.size(); i++) {
//            date[i] = mBSList.get(i).getBsTime().split(",")[0];
//            time[i] = mBSList.get(i).getBsTime().split(",")[1];
//            Log.e(TAG, "init: mBSList - " + mBSList.get(i).getBsValue() + mBSList.get(i).getBsTime());
//        }

        for (int i = 0; i < date.length; i++) {
            Log.e(TAG, "init: date - " + date[i] + ", " + time[i]);
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

    /**
     * 리사이클러 Bottom Sheet 생성
     * 공유하기
     * 저장하기
     * 등 그래프를 보고 해야할 일을 처리해야함.
     *
     * @author : JAICHANGPARK(DREAMWALKER)
     */
    private void setupRecyclerView() {

        ArrayList<MenuEntity> list = new ArrayList<>();
        MenuEntity menuEntity1 = new MenuEntity();
        menuEntity1.iconId = R.drawable.ic_save_black_24dp;
        menuEntity1.title = "SAVE";
        MenuEntity menuEntity2 = new MenuEntity();
        menuEntity2.iconId = R.drawable.ic_bubble_chart_black_24dp;
        menuEntity2.title = "Analysis";
        MenuEntity menuEntity3 = new MenuEntity();
        menuEntity3.iconId = R.drawable.ic_menu_share;
        menuEntity3.title = "SHARE";
        MenuEntity menuEntity4 = new MenuEntity();
        menuEntity4.iconId = R.drawable.ic_golf_course_black_24dp;
        menuEntity4.title = "DBEXPORT";

        list.add(menuEntity1);
        list.add(menuEntity2);
        list.add(menuEntity3);
        list.add(menuEntity4);
        mSweetSheet = new SweetSheet(coordinator);
        mSweetSheet.setMenuList(list);
        mSweetSheet.setDelegate(new RecyclerViewDelegate(true));
        mSweetSheet.setBackgroundEffect(new BlurEffect(14));
        mSweetSheet.setOnMenuItemClickListener((position, menuEntity11) ->
        {
            switch (position) {
                case 0:
                   /* for (int k = 0; k < subList.size(); k++) {
                        if (subList.get(k).getTypeValue() == 0) {
                            bsdbHelper.insertBSData("Unknown", subList.get(k).getBsValue(), date[k], time[k]);
                        } else if (subList.get(k).getTypeValue() == 1) {
                            bsdbHelper.insertBSData("식전", subList.get(k).getBsValue(), date[k], time[k]);
                        } else if (subList.get(k).getTypeValue() == 2) {
                            bsdbHelper.insertBSData("식후", subList.get(k).getBsValue(), date[k], time[k]);
                        } else if (subList.get(k).getTypeValue() == 3) {
                            bsdbHelper.insertBSData("공복", subList.get(k).getBsValue(), date[k], time[k]);
                        }
                    }*/
                    new BackgroundTask().execute();
                    Paper.book("syncBms").write("ptr", newIndex);
                    // Snackbar.make(getWindow().getDecorView().getRootView(),"저장완료", Snackbar.LENGTH_SHORT).show();
                    mSweetSheet.dismiss();
                    break;
                case 1:
                    startActivity(new Intent(SyncBMSResultActivity.this, AnalysisBSActivity.class));
                    break;
                case 2:
                    break;
                case 3:
                    sqliteExport();
                    break;
            }
            return false;
        });
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

    @Override
    public void onBackPressed() {
        if (mSweetSheet.isShow()) {
            mSweetSheet.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    public void sqliteExport() {
        //Context ctx = this; // for Activity, or Service. Otherwise simply get the context.
        //String dbname = "mydb.db";
        // dbpath = ctx.getDatabasePath(dbname);
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            Log.e(TAG, "getDataDirectory:  - " + data.toString());
            Log.e(TAG, "getExternalStorageDirectory:  - " + sd.toString());

            if (sd.canWrite()) {
                String currentDBPath = "/data/com.dreamwalker.knu2018.dteacher/databases/bs.db";
                String backupDBPath = "contacts.sqlite";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                if (backupDB.exists()) {
                    Toast.makeText(this, "DB Export Complete!!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int itemPosition;

    /**
     * public BloodSugar(String bsValue, String bsTime, int typeValue) {
     * this.bsValue = bsValue;
     * this.bsTime = bsTime;
     * this.typeValue = typeValue;
     * }
     *
     * @param v
     * @param position
     */
    @Override
    public void onItemClick(View v, int position) {

        //Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
        itemPosition = position;
        String value = subList.get(position).getBsValue();
        int type = subList.get(position).getTypeValue();
        String dt = subList.get(position).getBsTime();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("유형 선택");
        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(SyncBMSResultActivity.this, "" + which, Toast.LENGTH_SHORT).show();

                int reIndexingTypeValue = which + 10;

                subList.set(position, new BloodSugar(value, dt, reIndexingTypeValue));
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        builder.show();

    }

    @Override
    public void onItemLongClick(View v, int position) {

    }

    @OnClick(R.id.save)
    public void onSaveClicked() {

        // TODO: 2018-07-26 데이터베이스 인덱스 저장하여 새로운 데이터가 들어오면 새로운 데이터만 받을수있도록- 박제창
        Paper.book("syncBms").write("ptr", newIndex);
        // TODO: 2018-07-26 Realm 데이터베이스 저장 처리 -박제창
        new BackgroundTask().execute();
    }

    // TODO: 2018-02-27 저장했을 경우와 저장 안했을 경우를 생각해야함
    class BackgroundTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {

            progressDialog = new ProgressDialog(SyncBMSResultActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            //progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("파일 저장중...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids) {

//            realm = Realm.getDefaultInstance();
            realm = Realm.getInstance(realmConfiguration);


            String tmpDateTime;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.KOREA);

            String changeStringKind = null;
            String ts = null;
            Date userDate = null;
            long userTs = 0;
            for (int i = 0; i < subList.size(); i++) {
                // TODO: 2018-07-26 혈당 처리  
                String gValue = subList.get(i).getBsValue();
                // TODO: 2018-07-26 시간 처리
                tmpDateTime = subList.get(i).getBsTime();
                String[] splitDateTime = tmpDateTime.split(",");
                String d = splitDateTime[0];
                String t = splitDateTime[1];
                Log.e(TAG, "tmpDateTime Before -->  " + tmpDateTime);
                tmpDateTime = tmpDateTime.replace(",", " ");
                Log.e(TAG, "tmpDateTime After --> " + tmpDateTime);
                try {
                    Date date = formatter.parse(tmpDateTime);
                    userDate = formatter.parse(tmpDateTime);
                    userTs = date.getTime() / 1000;
                    Log.e(TAG, "date.getTime() -->  " + date.getTime());
                    ts = String.valueOf(date.getTime());
                    Timestamp timestamp = new Timestamp(date.getTime());
                    Log.e(TAG, "timestamp -->  " + timestamp);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                // TODO: 2018-07-26 유형 처리 

                if (subList.get(i).getTypeValue() == 0) {
                    changeStringKind = "Unknown";
                    // bsdbHelper.insertBSData("Unknown", subList.get(k).getBsValue(), date[k], time[k]);
                } else if (subList.get(i).getTypeValue() == 1) {
                    changeStringKind = "식전";
                    //bsdbHelper.insertBSData("식전", subList.get(k).getBsValue(), date[k], time[k]);
                } else if (subList.get(i).getTypeValue() == 2) {
                    changeStringKind = "식후";
                    //bsdbHelper.insertBSData("식후", subList.get(k).getBsValue(), date[k], time[k]);
                } else if (subList.get(i).getTypeValue() == 3) {
                    changeStringKind = "공복";
                    // bsdbHelper.insertBSData("공복", subList.get(k).getBsValue(), date[k], time[k]);
                }

                // TODO: 2018-07-25 수정 및 추가 리사이클러 업데이트를 위해서  - 박제창
                else if (subList.get(i).getTypeValue() == GlucoseType.TYPE_FASTING) {
                    changeStringKind = GlucoseType.FASTING;
                } else if (subList.get(i).getTypeValue() == GlucoseType.TYPE_SLEEP) {
                    changeStringKind = GlucoseType.SLEEP;
                } else if (subList.get(i).getTypeValue() == GlucoseType.TYPE_BREAKFAST_BEFORE) {
                    changeStringKind = GlucoseType.BREAKFAST_BEFORE;
                } else if (subList.get(i).getTypeValue() == GlucoseType.TYPE_BREAKFAST_AFTER) {
                    changeStringKind = GlucoseType.BREAKFAST_AFTER;
                } else if (subList.get(i).getTypeValue() == GlucoseType.TYPE_LUNCH_BEFORE) {
                    changeStringKind = GlucoseType.LUNCH_BEFORE;
                } else if (subList.get(i).getTypeValue() == GlucoseType.TYPE_LUNCH_AFTER) {
                    changeStringKind = GlucoseType.LUNCH_AFTER;
                } else if (subList.get(i).getTypeValue() == GlucoseType.TYPE_DINNER_BEFORE) {
                    changeStringKind = GlucoseType.DINNER_BEFORE;
                } else if (subList.get(i).getTypeValue() == GlucoseType.TYPE_DINNER_AFTER) {
                    changeStringKind = GlucoseType.DINNER_AFTER;
                } else if (subList.get(i).getTypeValue() == GlucoseType.TYPE_FITNESS_BEFORE) {
                    changeStringKind = GlucoseType.FITNESS_BEFORE;
                } else if (subList.get(i).getTypeValue() == GlucoseType.TYPE_FITNESS_AFTER) {
                    changeStringKind = GlucoseType.FITNESS_AFTER;
                }

                String finalChangeStringKind = changeStringKind;
                String finalTs = ts;
                Date finalUserDate = userDate;
                long finalUserTs = userTs;

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Glucose glucose = realm.createObject(Glucose.class);
                        glucose.setValue(gValue);
                        glucose.setType(finalChangeStringKind);
                        glucose.setDate(d);
                        glucose.setTime(t);
                        glucose.setTimestamp(finalTs);
                        glucose.setLongTs(finalUserTs);
                        glucose.setDatetime(finalUserDate);
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            Toast.makeText(SyncBMSResultActivity.this, "저장완료", Toast.LENGTH_SHORT).show();
            Realm.getInstance(realmConfiguration).close();
            finish();
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onDestroy() {


//        realm.close();
        super.onDestroy();
    }
}
