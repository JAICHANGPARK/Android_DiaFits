/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.dreamwalker.diabetesfits.adapter.education;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dreamwalker.diabetesfits.R;
import com.dreamwalker.diabetesfits.activity.education.KADNEImageActivity;
import com.dreamwalker.diabetesfits.model.education.Child;
import com.dreamwalker.diabetesfits.model.education.Parent;
import com.dreamwalker.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.dreamwalker.expandablerecyclerview.models.ExpandableGroup;

import java.util.ArrayList;
import java.util.List;

public class EduAdapter extends ExpandableRecyclerViewAdapter<ParentViewHolder, ChildViewHolder> {
    Context context;
    ArrayList<String> parantList;
    ArrayList<String> childList;

    EduAdapter.OnItemClickListener listener;

    private static final String TAG = "EduAdapter";

    public interface OnItemClickListener {
        void onItemClick(Child items);
    }

    public EduAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.context = context;
//        this.parantList = parantList;
//        this.childList = childList;
    }

    public EduAdapter(List<? extends ExpandableGroup> groups, OnItemClickListener listener) {
        super(groups);
        this.listener = listener;
//        this.context = context;
//        this.parantList = parantList;
//        this.childList = childList;
    }

    @Override
    public ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_genre, parent, false);
        return new ParentViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_artist, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(final ChildViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Child artist = ((Parent) group).getItems().get(childIndex);
        holder.setArtistName(artist.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, KADNEImageActivity.class));

                Log.e(TAG, "onClick: " + artist.getName() );
            }
        });
        //holder.binData(artist, listener);


        // holder.setArtistName(childList.get(childIndex));
    }

    @Override
    public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setGenreTitle(group);
    }
}
