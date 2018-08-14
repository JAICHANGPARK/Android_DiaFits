package com.dreamwalker.searchfilter.adapter

import com.dreamwalker.searchfilter.widget.FilterItem


/**
 * Created by galata on 30.08.16.
 */
abstract class FilterAdapter<T>(open val items: List<T>) {

    abstract fun createView(position: Int, item: T): FilterItem

}