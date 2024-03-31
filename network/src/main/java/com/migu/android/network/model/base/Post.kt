package com.migu.android.network.model.base

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * 动态的基类
 */
data class Post(
    @SerializedName("userInfoId") val userInfoId: UserInfo,
    @SerializedName("likes") val likes: Int,
    @SerializedName("postText") val postText: String,
    @SerializedName("objectId") val objectId: String,
    @SerializedName("createdAt") val createdAt: Date,
    @SerializedName("updatedAt") val updatedAt: Date
)