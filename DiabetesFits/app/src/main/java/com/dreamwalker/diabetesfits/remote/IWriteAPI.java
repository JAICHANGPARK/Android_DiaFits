package com.dreamwalker.diabetesfits.remote;

import com.dreamwalker.diabetesfits.model.Validate;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IWriteAPI {

    @FormUrlEncoded
    @POST("diafitness/code/UserWriteGlucose.php")
    Call<Validate> writeGlucose(@Field("userID") String userID,
                                @Field("userValue") String userValue,
                                @Field("userType") String userType,
                                @Field("userDate") String userDate,
                                @Field("userTime") String userTime,
                                @Field("userTimestamp") String userTimestamp);
}
