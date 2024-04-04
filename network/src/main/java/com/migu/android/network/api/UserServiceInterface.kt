package com.migu.android.network.api

import com.migu.android.core.util.AssetsUtils
import com.migu.android.network.model.LoginUserResponse
import com.migu.android.network.model.base.LoginUserRequestBody
import com.migu.android.network.model.UserResultResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface UserServiceInterface {

    @POST("login")
    fun getLoginUserData(
        @Body requestBody: LoginUserRequestBody
    ): Call<LoginUserResponse>


    @GET("classes/UserInfo")
    fun getUserInfoData(
        @Query("where") where: String,
        @Query("include") include: String? = "Background,Avatar"
    ): Call<UserResultResponse>

}