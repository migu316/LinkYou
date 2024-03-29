package com.migu.android.network.api

import com.migu.android.core.util.AssetsUtils
import com.migu.android.network.model.LoginUserResponse
import com.migu.android.network.model.LoginUserRequestBody
import com.migu.android.network.model.UserResultResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface UserServiceInterface {

    @Headers("Content-Type: application/json")
    @POST("login")
    fun getLoginUserData(
        @Header("X-LC-Id") xLcId: String = AssetsUtils.getAPPID(),
        @Header("X-LC-Key") xLcKey: String = AssetsUtils.getLCKEY(),
        @Body requestBody: LoginUserRequestBody
    ): Call<LoginUserResponse>


    @Headers("Content-Type: application/json")
    @GET("classes/UserInfo")
    fun getUserInfoData(
        @Header("X-LC-Id") xLcId: String = AssetsUtils.getAPPID(),
        @Header("X-LC-Key") xLcKey: String = AssetsUtils.getLCKEY(),
        @Query("where") where: String,
        @Query("include") include: String? = "Background,Avatar"
    ): Call<UserResultResponse>
}