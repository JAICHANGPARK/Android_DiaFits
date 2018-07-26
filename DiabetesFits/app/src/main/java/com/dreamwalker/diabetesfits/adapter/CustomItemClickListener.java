package com.dreamwalker.diabetesfits.adapter;

import android.view.View;

public interface CustomItemClickListener {
    public void onItemClick(View v, int position);

    void onItemLongClick(View v, int position);
}
