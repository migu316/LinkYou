package com.migu.android.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class LoginUserData(
    @SerializedName("sessionToken") var sessionToken: String = "",
    @SerializedName("email") var email: String = "",
    @SerializedName("username") var username: String = "",
    @SerializedName("createdAt") var createdAt: String = ""
)