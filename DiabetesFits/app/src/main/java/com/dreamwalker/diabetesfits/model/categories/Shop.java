package com.dreamwalker.diabetesfits.model.categories;

import android.content.SharedPreferences;

import com.dreamwalker.diabetesfits.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yarolegovich on 07.03.2017.
 */

public class Shop {

    private static final String STORAGE = "shop";

    public static Shop get() {
        return new Shop();
    }

    private SharedPreferences storage;

    private Shop() {
        //storage = AppShop.getInstance().getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
    }

    public List<Item> getData() {
        return Arrays.asList(
                new Item(1, "Treadmill", "Unknown", R.drawable.device_treadmill),
                new Item(2, "Lexpa", "Unknown", R.drawable.device_ergometer),
                new Item(3, "CareSens N Premier", "i-Sens", R.drawable.device_bsm)
//                new Item(3, "Favourite Board", "$265.00 USD", R.drawable.shop3),
//                new Item(4, "Earthenware Bowl", "$18.00 USD", R.drawable.shop4),
//                new Item(5, "Porcelain Dessert Plate", "$36.00 USD", R.drawable.shop5),
//                new Item(6, "Detailed Rolling Pin", "$145.00 USD", R.drawable.shop6)
                );
    }

    public boolean isRated(int itemId) {
//        return storage.getBoolean(String.valueOf(itemId), false);
        return false;
    }

    public void setRated(int itemId, boolean isRated) {
        //storage.edit().putBoolean(String.valueOf(itemId), isRated).apply();
    }
}
