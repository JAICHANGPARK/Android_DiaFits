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
import com.dreamwalker.diabetesfits.model.diary.Fitnes;

import java.text.DateFormat;
import java.util.ArrayList;


public class DiaryFitnessAdapter extends RecyclerView.Adapter<DiaryFitnessAdapter.DiaryFitnessViewHolder> {
    Context context;
    ArrayList<Fitnes> fitnesArrayList;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;

    ItemClickListener itemClickListener;

    public DiaryFitnessAdapter(Context context, ArrayList<Fitnes> fitnesArrayList) {
        this.context = context;
        this.fitnesArrayList = fitnesArrayList;
    }

    @NonNull
    @Override
    public DiaryFitnessViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_diary_fitness, viewGroup, false);
        return new DiaryFitnessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryFitnessViewHolder diaryFitnessViewHolder, int i) {
        String type = fitnesArrayList.get(i).getType();
        String typeDetail = fitnesArrayList.get(i).getSelectTypeDetail();
        String repScore = fitnesArrayList.get(i).getRpeScore();
        String repScoreDetail = fitnesArrayList.get(i).getSelectRpeExpression();
        String fitnessTime = fitnesArrayList.get(i).getFitnessTime() + " 분";
        String userType = type + "(" + typeDetail + ")";
        String repMessage = repScoreDetail + "(" + repScore + ")";
        String fitnessDistance = fitnesArrayList.get(i).getDistance() + " km";
        String fitnessSpeed = fitnesArrayList.get(i).getSpeed() + " km/h";
        setReminderTitle(userType);

        diaryFitnessViewHolder.mTitleText.setText(userType);
        diaryFitnessViewHolder.mThumbnailImage.setImageDrawable(mDrawableBuilder);
        diaryFitnessViewHolder.mDateAndTimeText.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(fitnesArrayList.get(i).getDatetime()));
        diaryFitnessViewHolder.fitnessTimeTextView.setText(fitnessTime);
        diaryFitnessViewHolder.fitnessREPText.setText(repMessage);
        diaryFitnessViewHolder.fitnessDistanceTextView.setText(fitnessDistance);
        diaryFitnessViewHolder.fitnessSpeedTextView.setText(fitnessSpeed);
    }

    @Override
    public int getItemCount() {
        return fitnesArrayList.size();
    }

    // Set reminder title view
    public void setReminderTitle(String title) {
        String letter = "F";
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


    class DiaryFitnessViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView mThumbnailImage;
        TextView mTitleText;
        TextView fitnessREPText;
        TextView fitnessTimeTextView;
        TextView fitnessDistanceTextView;
        TextView fitnessSpeedTextView;
        TextView mDateAndTimeText;



        public DiaryFitnessViewHolder(@NonNull View itemView) {
            super(itemView);
            mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
            mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
            fitnessREPText = (TextView) itemView.findViewById(R.id.recycle_rep_detail);
            fitnessTimeTextView = (TextView) itemView.findViewById(R.id.fitness_time_text_view);
            fitnessDistanceTextView = (TextView) itemView.findViewById(R.id.fitness_distance_text_view);
            fitnessSpeedTextView = (TextView) itemView.findViewById(R.id.fitness_speed_text_view);
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
