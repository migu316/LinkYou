package com.migu.android.network.model.base

import com.google.gson.annotations.SerializedName
import com.migu.android.network.util.NetWorkUtil

data class FileImage(
    @SerializedName("createdAt") val createdAt: String?="",
    @SerializedName("key") val key: String?="",
    @SerializedName("mime_type") val mimeType: String?="",
    @SerializedName("name") val name: String?="",
    @SerializedName("objectId") val objectId: String?="",
    @SerializedName("updatedAt") val updatedAt: String?="",
    @SerializedName("url") val url: String?=""
)