/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.dreamwalker.diabetesfits.adapter.education;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.photoview.PhotoView;

import java.util.ArrayList;

public class EduImageAdapter extends RecyclerView.Adapter<EduImageAdapter.EduImageViewHolder>{

    Context context;
    ArrayList<Integer> imageList;

    public EduImageAdapter(Context context, ArrayList<Integer> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public EduImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_edu_image, viewGroup, false);
        return new EduImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EduImageViewHolder eduImageViewHolder, int i) {
        Glide.with(context).load(imageList.get(i)).into(eduImageViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    class EduImageViewHolder extends RecyclerView.ViewHolder{
        PhotoView imageView;

        public EduImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
