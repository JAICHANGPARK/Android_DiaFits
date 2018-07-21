package com.dreamwalker.diabetesfits.remote;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * ____  ____  _________    __  ____       _____    __    __ __ __________
 * / __ \/ __ \/ ____/   |  /  |/  / |     / /   |  / /   / //_// ____/ __ \
 * / / / / /_/ / __/ / /| | / /|_/ /| | /| / / /| | / /   / ,<  / __/ / /_/ /
 * / /_/ / _, _/ /___/ ___ |/ /  / / | |/ |/ / ___ |/ /___/ /| |/ /___/ _, _/
 * /_____/_/ |_/_____/_/  |_/_/  /_/  |__/|__/_/  |_/_____/_/ |_/_____/_/ |_|
 * <p>
 *
 * 사용자 등록
 *
 *
 *
 * Created by Dreamwalker on 2018-07-21.
 */


public interface IUploadAPI {

    @FormUrlEncoded
    @POST("diafitness/code/userRegister.php")
    Call<ResponseBody> registerUser(@Field("userName") String name, @Field("userPwd") String pwd);


}
