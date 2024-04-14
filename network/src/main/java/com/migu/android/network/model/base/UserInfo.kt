package com.migu.android.network.model.base

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * 用户资料的基类
 */
@Parcelize
data class UserInfo(
    @SerializedName("Age") var age: Int? = 0,
    @SerializedName("BriefInfo") var briefInfo: String? = "",
    @SerializedName("City") var city: String? = "",
    @SerializedName("Gender") var gender: String? = "",
    @SerializedName("Name") var name: String? = "",
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("objectId") var objectId: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("Avatar") var avatar: FileImage? = null,
    @SerializedName("Background") var background: FileImage? = null
) : Parcelable
