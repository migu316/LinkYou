package com.migu.android.network.model

import com.google.gson.annotations.SerializedName

data class LoginUserResponse(
    @SerializedName("sessionToken") var sessionToken: String = "",
    @SerializedName("email") var email: String = "",
    @SerializedName("username") var username: String = "",
    @SerializedName("createdAt") var createdAt: String = "",
    @SerializedName("objectId") var objectId:String = ""
)