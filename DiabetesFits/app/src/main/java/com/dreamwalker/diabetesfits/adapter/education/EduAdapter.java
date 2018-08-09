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

import static com.dreamwalker.diabetesfits.consts.IntentConst.EDUCATION_PART;
import static com.dreamwalker.diabetesfits.consts.IntentConst.EDUCATION_PART_NAME;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_EIGHT_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_EIGHT_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_FIVE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_SIX;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FIVE_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FOUR_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FOUR_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_FOUR_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_NINE_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_NINE_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_NINE_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_NINE_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_FIVE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_ONE_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SEVEN_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SEVEN_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SEVEN_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SEVEN_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_FIVE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_SEVEN;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_SIX;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_SIX_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_THREE_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_THREE_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_THREE_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_THREE_TWO;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_TWO_FOUR;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_TWO_ONE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_TWO_THREE;
import static com.dreamwalker.diabetesfits.consts.KDANEConst.KDANE_PART_TWO_TWO;

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
        holder.itemView.setOnClickListener(view -> {

            String touchString = artist.getName();
            Intent intent = new Intent(context, KADNEImageActivity.class);

            if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_00))){
              intent.putExtra(EDUCATION_PART, KDANE_PART_ONE_ONE);
              intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_01))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_ONE_TWO);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_02))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_ONE_THREE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_03))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_ONE_FOUR);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_04))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_ONE_FIVE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
// TODO: 2018-08-09
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_10))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_TWO_ONE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_11))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_TWO_TWO);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_12))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_TWO_THREE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_13))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_TWO_FOUR);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
// TODO: 2018-08-09
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_20))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_THREE_ONE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_21))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_THREE_TWO);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_22))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_THREE_THREE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_23))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_THREE_FOUR);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }

            // TODO: 2018-08-09
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_30))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_FOUR_ONE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_31))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_FOUR_TWO);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_32))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_FOUR_THREE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            // TODO: 2018-08-09
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_40))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_FIVE_ONE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_41))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_FIVE_TWO);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_42))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_FIVE_THREE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_43))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_FIVE_FOUR);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_44))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_FIVE_FIVE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_45))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_FIVE_SIX);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }

            // TODO: 2018-08-09
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_50))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SIX_ONE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_51))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SIX_TWO);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_52))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SIX_THREE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_53))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SIX_FOUR);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_54))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SIX_FIVE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_55))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SIX_SIX);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_56))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SIX_SEVEN);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }

            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_60))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SEVEN_ONE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_61))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SEVEN_TWO);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_62))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SEVEN_THREE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_63))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_SEVEN_FOUR);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }

            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_70))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_EIGHT_ONE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_71))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_EIGHT_TWO);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }

            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_80))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_NINE_ONE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_81))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_NINE_TWO);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_82))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_NINE_THREE);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }
            else if (touchString.equals(context.getResources().getString(R.string.kdane_child_parent_83))){
                intent.putExtra(EDUCATION_PART, KDANE_PART_NINE_FOUR);
                intent.putExtra(EDUCATION_PART_NAME, touchString);
            }

            context.startActivity(intent);

            Log.e(TAG, "onClick: " + artist.getName());
        });
        //holder.binData(artist, listener);


        // holder.setArtistName(childList.get(childIndex));
    }

    @Override
    public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setGenreTitle(group);
    }
}
