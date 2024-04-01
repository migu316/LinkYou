package com.migu.android.network.model

import com.google.gson.annotations.SerializedName
import com.migu.android.network.model.base.Dynamic

/**
 * 指定用户的动态数据类
 */
data class TargetUserDynamicsResponse(
    @SerializedName("results") val results: List<Dynamic>
)
