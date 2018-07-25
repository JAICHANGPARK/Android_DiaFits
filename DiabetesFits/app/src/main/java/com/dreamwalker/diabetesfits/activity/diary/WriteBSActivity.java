package com.dreamwalker.diabetesfits.activity.diary;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.CustomItemClickListener;
import com.dreamwalker.diabetesfits.adapter.StaggeredWriteAdapter;
import com.kevalpatel2106.rulerpicker.RulerValuePicker;
import com.kevalpatel2106.rulerpicker.RulerValuePickerListener;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.numberpad.NumberPadTimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WriteBSActivity extends AppCompatActivity implements CustomItemClickListener,
        BottomSheetTimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "WriteBSActivity";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.bottomSheet)
    LinearLayout mBottomSheet;

    @BindView(R.id.gluecose_value_text_view)
    TextView glucoseValueTextView;

    @BindView(R.id.ruler_picker)
    RulerValuePicker rulerValuePicker;

    @BindView(R.id.glucose_button)
    Button glucoseButton;

    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;

    BottomSheetBehavior bottomSheetBehavior;

    StaggeredWriteAdapter adapter;

    ArrayList<String> name = new ArrayList<>();
    ArrayList<Integer> imageList = new ArrayList<>();

    NumberPadTimePickerDialog pad;

    DatePickerDialog dateDialog;
    GridTimePickerDialog gridTimeDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_bs);
        ButterKnife.bind(this);

        Calendar now = Calendar.getInstance();
// As of version 2.3.0, `BottomSheetDatePickerDialog` is deprecated.
         dateDialog = DatePickerDialog.newInstance(
                WriteBSActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        gridTimeDialog = GridTimePickerDialog.newInstance(
                WriteBSActivity.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(WriteBSActivity.this));

        // Configured according to the system preference for 24-hour time.
        pad = NumberPadTimePickerDialog.newInstance(WriteBSActivity.this);


        rulerValuePicker.selectValue(100 /* Initial value */);

        rulerValuePicker.setValuePickerListener(new RulerValuePickerListener() {
            @Override
            public void onValueChange(int selectedValue) {
//                String value = String.valueOf(selectedValue);
//                glucoseValueTextView.setText(value);
            }

            @Override
            public void onIntermediateValueChange(int selectedValue) {
                String value = String.valueOf(selectedValue);
                glucoseValueTextView.setText(value);

            }
        });

        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        initData();
        adapter = new StaggeredWriteAdapter(this, name, imageList);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setCustomItemClickListener(this);

        floatingActionButton.setVisibility(View.GONE);
//        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
//            floatingActionButton.setVisibility(View.GONE);
//        }

    }

    @OnClick(R.id.fab)
    public void fadClicked() {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        floatingActionButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.glucose_button)
    public void glucoButtonClicked(){
//        pad.show(getSupportFragmentManager(), "1");

        dateDialog.show(getSupportFragmentManager(), "2");
    }

    private void initData() {
        name.add("공복");
        imageList.add(R.drawable.coffee_backgound);

        name.add("취침 전");
        imageList.add(R.drawable.sleep_background);

        name.add("운동 전");
        imageList.add(R.drawable.before_ex_background);

        name.add("운동 후");
        imageList.add(R.drawable.after_ex_background);

        name.add("아침 식전");
        imageList.add(R.drawable.eat_04_background);

        name.add("아침 식후");
        imageList.add(R.drawable.eat_03_background);

        name.add("점심 식전");
        imageList.add(R.drawable.eat_background);

        name.add("점심 식후");
        imageList.add(R.drawable.eat_01_background);

        name.add("저녁 식전");
        imageList.add(R.drawable.eat_02_background);

        name.add("저녁 식후");
        imageList.add(R.drawable.eat_05_background);

    }

    /**
     * STATE_COLLAPSED: The bottom sheet is visible but only showing its peek height. This state is usually the ‘resting position’ of a Bottom Sheet. The peek height is chosen by the developer and should be enough to indicate there is extra content, allow the user to trigger an action or expand the bottom sheet.
     * STATE_EXPANDED: The bottom sheet is visible and its maximum height and it is neither dragging or settling (see below).
     * STATE_DRAGGING: The user is actively dragging the bottom sheet up or down.
     * STATE_SETTLING: The bottom sheet is settling to specific height after a drag/swipe gesture. This will be the peek height, expanded height, or 0, in case the user action caused the bottom sheet to hide.
     * STATE_HIDDEN: The bottom sheet is no longer visible.
     *
     * @param v
     * @param position
     */
    @Override
    public void onItemClick(View v, int position) {
        floatingActionButton.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setPeekHeight(300);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {
        Log.e(TAG, "onTimeSet: " + hourOfDay + ", "+ minute  );

        Intent intent = new Intent(WriteBSActivity.this, WriteCheckActivity.class);
        startActivity(intent);

    }

    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Log.e(TAG, "onDateSet: " + year + "|" + monthOfYear + "|" + dayOfMonth );
        gridTimeDialog.show(getSupportFragmentManager(),"3");
    }
}
