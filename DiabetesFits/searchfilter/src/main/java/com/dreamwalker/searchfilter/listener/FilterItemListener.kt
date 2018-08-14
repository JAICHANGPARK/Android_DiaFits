package com.dreamwalker.searchfilter.listener

import com.dreamwalker.searchfilter.widget.FilterItem

/**
 * Created by galata on 08.09.16.
 */
interface FilterItemListener {

    fun onItemSelected(item: FilterItem)

    fun onItemDeselected(item: FilterItem)

    fun onItemRemoved(item: FilterItem)

}