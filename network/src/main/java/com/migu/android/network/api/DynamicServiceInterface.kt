package com.migu.android.network.api

import com.migu.android.network.model.DynamicImageResponse
import com.migu.android.network.model.TargetUserDynamicsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DynamicServiceInterface {
    @GET("classes/Posts")
    fun getUserDynamicsData(
        @Query("where") where: String,
        @Query("include") include:String?="UserInfoId.Avatar",
        @Query("order") order:String? = "-createdAt"
    ): Call<TargetUserDynamicsResponse>

    @GET("classes/PostImages")
    fun getDynamicImagesData(
        @Query("where") where: String,
        @Query("include") include:String?="imageId"
    ): Call<DynamicImageResponse>

    @GET("classes/Posts")
    fun getTheLatestDynamicsData(
        @Query("order") order:String? = "-createdAt",
        @Query("include") include:String?="UserInfoId.Avatar",
        @Query("limit") limit:Int?=10,
        @Query("skip") skip:Int?=0
    ):Call<TargetUserDynamicsResponse>
}