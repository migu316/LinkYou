package com.migu.android.network.model

import com.google.gson.annotations.SerializedName

data class UserResultResponse(
    @SerializedName("results") val results: List<UserInfo>
)