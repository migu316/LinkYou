package com.migu.android.network.model.base

import com.google.gson.annotations.SerializedName

/**
 * 用户基础信息的基类(非用户资料)
 */
data class User(
    @SerializedName("objectId") val objectId: String
)