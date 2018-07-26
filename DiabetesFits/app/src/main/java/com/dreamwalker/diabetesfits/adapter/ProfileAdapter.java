package com.dreamwalker.diabetesfits.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dreamwalker.diabetesfits.R;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>{
    Context context;
    ArrayList<String> labelList;
    ArrayList<String> valueList;
    CustomItemClickListener customItemClickListener;

    public ProfileAdapter(Context context, ArrayList<String> labelList, ArrayList<String> valueList) {
        this.context = context;
        this.labelList = labelList;
        this.valueList = valueList;

    }

    public void setCustomItemClickListener(CustomItemClickListener listener){
        this.customItemClickListener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
       return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {

        holder.labelText.setText(labelList.get(position));
        holder.valueText.setText(valueList.get(position));

    }

    @Override
    public int getItemCount() {
        return valueList.size();
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        CardView container;
        TextView labelText;
        TextView valueText;

        public ProfileViewHolder(View itemView) {
            super(itemView);


            container = itemView.findViewById(R.id.card_view);
            labelText = itemView.findViewById(R.id.label_text_view);
            valueText = itemView.findViewById(R.id.value_text_view);

//            Typeface font = Typeface.createFromAsset(context.getAssets(), "");
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if (customItemClickListener != null){
                customItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (customItemClickListener != null){
                customItemClickListener.onItemLongClick(v, getAdapterPosition());
            }
            return false;
        }
    }
}
