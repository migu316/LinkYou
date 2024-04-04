package com.migu.android.network.api

import com.migu.android.core.util.AssetsUtils
import com.migu.android.network.model.DynamicImageResponse
import com.migu.android.network.model.TargetUserDynamicsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface DynamicServiceInterface {
    @Headers("Content-Type: application/json")
    @GET("classes/Posts")
    fun getUserDynamicsData(
        @Query("where") where: String,
        @Query("include") include:String?="UserInfoId.Avatar",
        @Query("order") order:String? = "-createdAt"
    ): Call<TargetUserDynamicsResponse>

    @Headers("Content-Type: application/json")
    @GET("classes/PostImages")
    fun getDynamicImagesData(
        @Query("where") where: String,
        @Query("include") include:String?="imageId"
    ): Call<DynamicImageResponse>
}