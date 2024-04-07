package com.migu.android.network.model.base

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * 动态的基类
 */
@Parcelize
data class Dynamic(
    @SerializedName("UserInfoId") val userInfoId: UserInfo? = null,
    @SerializedName("likes") val likes: Int? = 0,
    @SerializedName("postText") val postText: String? = "",
    @SerializedName("objectId") val objectId: String,
    @SerializedName("createdAt") val createdAt: Date? = null,
    @SerializedName("updatedAt") val updatedAt: Date? = null,
    @SerializedName("ImageCount") val imageCount: Int? = 0
) : Parcelable {
    @IgnoredOnParcel
    var imageUrls:List<String> = listOf()
}