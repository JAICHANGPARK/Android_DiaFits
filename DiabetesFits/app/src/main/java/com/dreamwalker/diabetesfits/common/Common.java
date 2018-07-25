package com.dreamwalker.diabetesfits.common;

import com.dreamwalker.diabetesfits.remote.IWriteAPI;
import com.dreamwalker.diabetesfits.remote.RetrofitClient;

import static com.dreamwalker.diabetesfits.consts.Url.BASE_URL;

/**
 *
 * JAICHANGPARK (DREAMWALKER)
 *
 */
public class Common {

    public static IWriteAPI getGlucoseWriteServie(){
        return RetrofitClient.getClient(BASE_URL).create(IWriteAPI.class);
    }
}
