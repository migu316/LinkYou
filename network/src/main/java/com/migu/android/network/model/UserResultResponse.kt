package com.migu.android.network.model

import com.google.gson.annotations.SerializedName
import com.migu.android.network.model.base.UserInfo

data class UserResultResponse(
    @SerializedName("results") val results: List<UserInfo>
)