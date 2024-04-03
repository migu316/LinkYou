package com.migu.android.network.model.base

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * 动态的基类
 */
@Parcelize
data class Dynamic(
    @SerializedName("userInfoId") val userInfoId: UserInfo,
    @SerializedName("likes") val likes: Int,
    @SerializedName("postText") val postText: String,
    @SerializedName("objectId") val objectId: String,
    @SerializedName("createdAt") val createdAt: Date,
    @SerializedName("updatedAt") val updatedAt: Date,
    @SerializedName("ImageCount") val imageCount: Int
):Parcelable