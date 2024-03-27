package com.migu.android.network.api

import com.migu.android.core.util.AssetsUtils
import com.migu.android.network.model.LoginUserData
import com.migu.android.network.model.LoginUserRequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserServiceInterface {

    @Headers("Content-Type: application/json")
    @POST("login")
    fun getLoginUserData(
        @Header("X-LC-Id") xLcId: String = AssetsUtils.getAPPID(),
        @Header("X-LC-Key") xLcKey: String = AssetsUtils.getLCKEY(),
        @Body requestBody: LoginUserRequestBody
    ): Call<LoginUserData>

}