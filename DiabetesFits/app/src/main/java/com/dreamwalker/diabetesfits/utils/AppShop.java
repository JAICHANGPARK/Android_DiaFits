package com.dreamwalker.diabetesfits.utils;

import android.app.Application;

/**
 * Created by yarolegovich on 08.03.2017.
 */

public class AppShop extends Application {

    private static AppShop instance;

    public static AppShop getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DiscreteScrollViewOptions.init(this);
    }
}
