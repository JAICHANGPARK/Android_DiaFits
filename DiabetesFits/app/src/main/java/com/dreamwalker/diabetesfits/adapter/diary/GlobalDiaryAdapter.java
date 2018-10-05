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
import com.dreamwalker.diabetesfits.model.diary.Global;

import java.text.DateFormat;
import java.util.ArrayList;

public class GlobalDiaryAdapter extends RecyclerView.Adapter<GlobalDiaryAdapter.GlobalDiaryViewHolder> {

    Context context;
    ArrayList<Global> globalArrayList = new ArrayList<>();
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;

    ItemClickListener itemClickListener;

    public GlobalDiaryAdapter(Context context, ArrayList<Global> globalArrayList) {
        this.context = context;
        this.globalArrayList = globalArrayList;
    }

    @NonNull
    @Override
    public GlobalDiaryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_diary_global, viewGroup, false);
        return new GlobalDiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlobalDiaryViewHolder globalDiaryViewHolder, int i) {
        int tagNumber = globalArrayList.get(i).getTag();
        setReminderTitle(tagNumber);
        globalDiaryViewHolder.mThumbnailImage.setImageDrawable(mDrawableBuilder);
        globalDiaryViewHolder.mTitleText.setText(globalArrayList.get(i).getType());
        globalDiaryViewHolder.mDateAndTimeText.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(globalArrayList.get(i).getDatetime()));

        if (tagNumber == 0) {
            String valueString = globalArrayList.get(i).getUserValue() + "\n" + "mg/dL";
            globalDiaryViewHolder.globalValueTextView.setText(valueString);
        } else if (tagNumber == 1) {
            String valueString = globalArrayList.get(i).getUserValue() + "분";
            globalDiaryViewHolder.globalValueTextView.setText(valueString);

        }


    }

    @Override
    public int getItemCount() {
        return globalArrayList.size();
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

    public void setReminderTitle(int tag) {
        String letter = "G";

        if (tag == 0) {
            letter = "당";
        } else if (tag == 1) {
            letter = "운";
        }
        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder().buildRound(letter, color);

    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    class GlobalDiaryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        ImageView mThumbnailImage;
        TextView mTitleText;
        TextView mDateAndTimeText;
        TextView globalValueTextView;
        TextView glucoseChangeTextView;


        public GlobalDiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
            mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
            globalValueTextView = (TextView) itemView.findViewById(R.id.global_value);

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
