package com.dreamwalker.diabetesfits.adapter.isens;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.adapter.CustomItemClickListener;
import com.dreamwalker.diabetesfits.consts.GlucoseType;
import com.dreamwalker.diabetesfits.model.isens.BloodSugar;

import java.util.ArrayList;

/**
 * Created by KNU2017 on 2018-02-27.
 */


public class BSMSyncAdapter extends RecyclerView.Adapter<BSMSyncAdapter.BSMSyncViewHolder> {

    Context context;
    ArrayList<BloodSugar> bloodSugarArrayList;
    CustomItemClickListener customItemClickListener;


    public BSMSyncAdapter(Context context, ArrayList<BloodSugar> bloodSugarArrayList) {
        this.context = context;
        this.bloodSugarArrayList = bloodSugarArrayList;
    }

    @Override
    public BSMSyncViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_bsm_sync_data, parent, false);
        return new BSMSyncViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(BSMSyncViewHolder holder, int position) {

        int bsType;

        bsType = bloodSugarArrayList.get(position).getTypeValue();

        if (bsType == 0) {
            Glide.with(context).load(R.drawable.img_unknown).into(holder.categoryImage);
            holder.typeText.setText("Unknown");
        } else if (bsType == 1) {
            Glide.with(context).load(R.drawable.apple_before).into(holder.categoryImage);
            holder.typeText.setText("식전");
        } else if (bsType == 2) {
            Glide.with(context).load(R.drawable.apple_after).into(holder.categoryImage);
            holder.typeText.setText("식후");
        } else if (bsType == 3) {
            Glide.with(context).load(R.drawable.img_wake_up).into(holder.categoryImage);
            holder.typeText.setText("공복");
        }
        // TODO: 2018-07-25 수정 및 추가 리사이클러 업데이트를 위해서  - 박제창
        else if (bsType == GlucoseType.TYPE_FASTING) {
            holder.typeText.setText(GlucoseType.FASTING);
            Glide.with(context).load(R.drawable.img_wake_up).into(holder.categoryImage);
        }

        else if (bsType == GlucoseType.TYPE_SLEEP) {
            holder.typeText.setText(GlucoseType.SLEEP);
            Glide.with(context).load(R.drawable.img_wake_up).into(holder.categoryImage);
        }

        else if (bsType == GlucoseType.TYPE_BREAKFAST_BEFORE) {
            holder.typeText.setText(GlucoseType.BREAKFAST_BEFORE);
            Glide.with(context).load(R.drawable.apple_before).into(holder.categoryImage);
        }

        else if (bsType == GlucoseType.TYPE_BREAKFAST_AFTER) {
            holder.typeText.setText(GlucoseType.BREAKFAST_AFTER);
            Glide.with(context).load(R.drawable.apple_after).into(holder.categoryImage);
        }

        else if (bsType == GlucoseType.TYPE_LUNCH_BEFORE) {
            holder.typeText.setText(GlucoseType.LUNCH_BEFORE);
            Glide.with(context).load(R.drawable.apple_before).into(holder.categoryImage);
        }

        else if (bsType == GlucoseType.TYPE_LUNCH_AFTER) {
            holder.typeText.setText(GlucoseType.LUNCH_AFTER);
            Glide.with(context).load(R.drawable.apple_after).into(holder.categoryImage);
        }

        else if (bsType == GlucoseType.TYPE_DINNER_BEFORE) {
            holder.typeText.setText(GlucoseType.DINNER_BEFORE);
            Glide.with(context).load(R.drawable.apple_before).into(holder.categoryImage);
        }

        else if (bsType == GlucoseType.TYPE_DINNER_AFTER) {
            holder.typeText.setText(GlucoseType.DINNER_AFTER);
            Glide.with(context).load(R.drawable.apple_after).into(holder.categoryImage);
        }

        else if (bsType == GlucoseType.TYPE_FITNESS_BEFORE) {
            holder.typeText.setText(GlucoseType.FITNESS_BEFORE);
            Glide.with(context).load(R.drawable.apple_after).into(holder.categoryImage);
        }

        else if (bsType == GlucoseType.TYPE_FITNESS_AFTER) {
            holder.typeText.setText(GlucoseType.FITNESS_AFTER);
            Glide.with(context).load(R.drawable.apple_after).into(holder.categoryImage);
        }


        holder.valueText.setText(bloodSugarArrayList.get(position).getBsValue());
        holder.dateTextValue.setText(bloodSugarArrayList.get(position).getBsTime());

    }

    @Override
    public int getItemCount() {
        return bloodSugarArrayList.size();
    }

    public void setCustomItemClickListener(CustomItemClickListener listener) {
        this.customItemClickListener = listener;
    }


    public class BSMSyncViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView categoryImage;
        //    TextView subjectText, typeText;
//    TextView valueTitleText, valueText;
//    TextView dateText, dateTextValue;
        TextView dateTextValue;
        TextView subjectText, typeText;
        TextView valueText;

        public BSMSyncViewHolder(View itemView) {
            super(itemView);

            categoryImage = itemView.findViewById(R.id.image_view);
            subjectText = itemView.findViewById(R.id.subjectText);
            typeText = itemView.findViewById(R.id.typeText);
            //valueTitleText = itemView.findViewById(R.id.valueTitleText);
            valueText = itemView.findViewById(R.id.valueText);
            //dateText = itemView.findViewById(R.id.dateText);
            dateTextValue = itemView.findViewById(R.id.dateTextValue);

            itemView.setOnClickListener(this);
        }

        // TODO: 2018-07-25 리사이클러 데이터 수정을 위해서 리스너 추가합니다. - 박제창
        @Override
        public void onClick(View v) {
            if (customItemClickListener != null) {
                customItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }
}
