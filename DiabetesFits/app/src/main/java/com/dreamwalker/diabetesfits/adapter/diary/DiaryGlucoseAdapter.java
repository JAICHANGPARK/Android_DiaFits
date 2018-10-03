package com.dreamwalker.diabetesfits.adapter.diary;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.model.diary.Gluco;

import java.text.DateFormat;
import java.util.ArrayList;


public class DiaryGlucoseAdapter extends RecyclerView.Adapter<DiaryGlucoseAdapter.DiaryGlucoseViewHolder> {
    Context context;
    ArrayList<Gluco> glucoArrayList;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;

    ItemClickListener itemClickListener;

    public DiaryGlucoseAdapter(Context context, ArrayList<Gluco> glucoArrayList) {
        this.context = context;
        this.glucoArrayList = glucoArrayList;
    }

    @NonNull
    @Override
    public DiaryGlucoseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_diary_glucose, viewGroup, false);
        return new DiaryGlucoseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryGlucoseViewHolder diaryGlucoseViewHolder, int i) {

        String userType = glucoArrayList.get(i).getType();
        int changeGlucoseValue = glucoArrayList.get(i).getChangeValue();

        setReminderTitle(userType);
        diaryGlucoseViewHolder.mTitleText.setText(userType);
        diaryGlucoseViewHolder.mThumbnailImage.setImageDrawable(mDrawableBuilder);
        diaryGlucoseViewHolder.mDateAndTimeText.setText(DateFormat.getTimeInstance().format(glucoArrayList.get(i).getDatetime()));
        diaryGlucoseViewHolder.glucoseValueTextView.setText(glucoArrayList.get(i).getUserValue());

        if (changeGlucoseValue < 0) {
            String tmp = String.valueOf(changeGlucoseValue);
            tmp = tmp.substring(1) + " 감소";
            diaryGlucoseViewHolder.glucoseChangeTextView.setText(tmp);
        } else if (changeGlucoseValue > 0) {
            String tmp = String.valueOf(changeGlucoseValue) + " 증가";
            diaryGlucoseViewHolder.glucoseChangeTextView.setText(tmp);
        } else if (changeGlucoseValue == 0) {
            String tmp = " - ";
            diaryGlucoseViewHolder.glucoseChangeTextView.setText(tmp);
        }


    }

    @Override
    public int getItemCount() {
        return glucoArrayList.size();
    }

    // Set reminder title view
    public void setReminderTitle(String title) {
        String letter = "G";
        if (title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }
        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder().buildRound(letter, color);

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    class DiaryGlucoseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView mThumbnailImage;
        TextView mTitleText;
        TextView mDateAndTimeText;
        TextView glucoseValueTextView;
        TextView glucoseChangeTextView;


        public DiaryGlucoseViewHolder(@NonNull View itemView) {
            super(itemView);
            mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
            mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
            glucoseValueTextView = (TextView) itemView.findViewById(R.id.glucose_value);
            glucoseChangeTextView = (TextView) itemView.findViewById(R.id.glucose_change);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(v, getAdapterPosition());

            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (itemClickListener != null) {
                itemClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return true;
        }
    }

}
