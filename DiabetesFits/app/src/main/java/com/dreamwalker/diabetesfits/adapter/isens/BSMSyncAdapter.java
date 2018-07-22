package com.dreamwalker.diabetesfits.adapter.isens;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.model.isens.BloodSugar;

import java.util.ArrayList;

/**
 * Created by KNU2017 on 2018-02-27.
 */

class BSMSyncViewHolder extends RecyclerView.ViewHolder {
    TextView subjectText, typeText;
    TextView valueTitleText, valueText;
    TextView dateText, dateTextValue;

    public BSMSyncViewHolder(View itemView) {
        super(itemView);
        subjectText = itemView.findViewById(R.id.subjectText);
        typeText = itemView.findViewById(R.id.typeText);
        valueTitleText = itemView.findViewById(R.id.valueTitleText);
        valueText = itemView.findViewById(R.id.valueText);
        dateText = itemView.findViewById(R.id.dateText);
        dateTextValue = itemView.findViewById(R.id.dateTextValue);
    }
}

public class BSMSyncAdapter extends RecyclerView.Adapter<BSMSyncViewHolder> {
    Context context;
    ArrayList<BloodSugar> bloodSugarArrayList;


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
            holder.typeText.setText("Unknown");
        } else if (bsType == 1) {
            holder.typeText.setText("식전");
        } else if (bsType == 2) {
            holder.typeText.setText("식후");
        } else if (bsType == 3) {
            holder.typeText.setText("공복");
        }

        holder.valueText.setText(bloodSugarArrayList.get(position).getBsValue());
        holder.dateTextValue.setText(bloodSugarArrayList.get(position).getBsTime());

    }

    @Override
    public int getItemCount() {
        return bloodSugarArrayList.size();
    }
}
