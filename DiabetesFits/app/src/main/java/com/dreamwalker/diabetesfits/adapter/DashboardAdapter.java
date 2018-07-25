package com.dreamwalker.diabetesfits.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>{

    Context context;
    ArrayList<String> labelList;
    ArrayList<String> valueList;

    public DashboardAdapter(Context context, ArrayList<String> labelList, ArrayList<String> valueList) {
        this.context = context;
        this.labelList = labelList;
        this.valueList = valueList;
    }

    @NonNull
    @Override
    public DashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard, parent, false);
        return new DashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardViewHolder holder, int position) {

        Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSansCJKkr-Thin.otf");
        holder.labelText.setTypeface(font, Typeface.NORMAL);
        holder.valueText.setTypeface(font, Typeface.NORMAL);

        holder.labelText.setText(labelList.get(position));
        holder.valueText.setText(valueList.get(position));

    }

    @Override
    public int getItemCount() {
        return valueList.size();
    }


    public class DashboardViewHolder extends RecyclerView.ViewHolder {
        TextView labelText;
        TextView valueText;


        public DashboardViewHolder(View itemView) {
            super(itemView);


            labelText = itemView.findViewById(R.id.label_text_view);
            valueText = itemView.findViewById(R.id.value_text_view);


        }
    }
}
