package com.dreamwalker.diabetesfits.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dreamwalker.diabetesfits.R;

import java.util.ArrayList;

public class StaggeredWriteAdapter extends
        RecyclerView.Adapter<StaggeredWriteAdapter.StaggerWriteViewHoler> {
    Context context;
    private ArrayList<String> nName = new ArrayList<>();
    private ArrayList<Integer> mImageUrls = new ArrayList<>();

    private CustomItemClickListener clickListener;

    public StaggeredWriteAdapter(Context context, ArrayList<String> nName, ArrayList<Integer> mImageUrls) {
        this.context = context;
        this.nName = nName;
        this.mImageUrls = mImageUrls;
    }

    @NonNull
    @Override
    public StaggerWriteViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_write, parent, false);
        return new StaggerWriteViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaggerWriteViewHoler holder, int position) {
        Glide.with(context).load(mImageUrls.get(position)).into(holder.imageView);
        holder.gridTextView.setText(nName.get(position));

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        holder.setCustomItemClickListener(new CustomItemClickListener() {
//            @Override
//            public void onItemClick(View v, int position) {
//
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return nName.size();
    }

    public void setCustomItemClickListener(CustomItemClickListener listener){
        this.clickListener = listener;
    }


    public class StaggerWriteViewHoler extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView gridTextView;
        ImageView imageView;
//        CustomItemClickListener customItemClickListener;

        public StaggerWriteViewHoler(View itemView) {
            super(itemView);
            this.gridTextView = itemView.findViewById(R.id.grid_text_view);
            this.imageView = itemView.findViewById(R.id.grid_image_view);
            itemView.setOnClickListener(this);
        }



        @Override
        public void onClick(View v) {
            if (clickListener != null){
              clickListener.onItemClick(v, getAdapterPosition());
            }
//            customItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

}
