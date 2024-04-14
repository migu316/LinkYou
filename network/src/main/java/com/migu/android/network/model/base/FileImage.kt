package com.migu.android.network.model.base

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.migu.android.network.util.NetWorkUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileImage(
    @SerializedName("createdAt") var createdAt: String? = "",
    @SerializedName("key") var key: String? = "",
    @SerializedName("mime_type") var mimeType: String? = "",
    @SerializedName("name") var name: String? = "",
    @SerializedName("objectId") var objectId: String? = "",
    @SerializedName("updatedAt") var updatedAt: String? = "",
    @SerializedName("url") var url: String? = ""
):Parcelable