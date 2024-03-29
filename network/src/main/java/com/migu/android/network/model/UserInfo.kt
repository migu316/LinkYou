package com.migu.android.network.model

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("Age") val age: Int,
    @SerializedName("BriefInfo") val briefInfo: String,
    @SerializedName("City") val city: String,
    @SerializedName("Gender") val gender: String,
    @SerializedName("Name") val name: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("objectId") val objectId: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("Avatar") val avatar: FileImage,
    @SerializedName("Background") val background: FileImage
)