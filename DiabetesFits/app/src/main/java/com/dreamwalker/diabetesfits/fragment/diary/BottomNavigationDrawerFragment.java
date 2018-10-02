package com.dreamwalker.diabetesfits.fragment.diary;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.diary.DiaryFitnessActivity;
import com.dreamwalker.diabetesfits.activity.diary.DiaryGlucoseActivity;

public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {
    private static final String TAG = "BottomNavigationDrawerF";
    NavigationView navigationView;
    public BottomNavigationDrawerFragment() {
    }
    // TODO: Rename and change types and number of parameters
    public static BottomNavigationDrawerFragment newInstance() {
        BottomNavigationDrawerFragment fragment = new BottomNavigationDrawerFragment();

//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.layout_diary_bottom_navigation, container, false);
        navigationView = view.findViewById(R.id.navigation_view);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav1:
                        startActivity(new Intent(getActivity(), DiaryFitnessActivity.class));
                        Log.e(TAG, "onNavigationItemSelected: 운동버튼 클릭");

                        return true;
                    case R.id.nav2:
                        startActivity(new Intent(getActivity(), DiaryGlucoseActivity.class));
                        Log.e(TAG, "onNavigationItemSelected: 혈당버튼 클릭");
                }
                return false;
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

            }
        });
        return dialog;
    }
}
