package com.migu.android.network.api

import com.migu.android.core.util.AssetsUtils
import com.migu.android.network.model.TargetUserPostsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface PostServiceInterface {
    @Headers("Content-Type: application/json")
    @GET("classes/Posts")
    fun getUserPostsData(
        @Header("X-LC-Id") xLcId: String = AssetsUtils.getAPPID(),
        @Header("X-LC-Key") xLcKey: String = AssetsUtils.getLCKEY(),
        @Query("where") where: String,
        @Query("include") include:String?="UserInfoId.Avatar",
        @Query("order") order:String? = "-createdAt"
    ): Call<TargetUserPostsResponse>
}