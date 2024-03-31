package com.migu.android.network.model

import com.google.gson.annotations.SerializedName
import com.migu.android.network.model.base.Post

/**
 * 指定用户的动态数据类
 */
data class TargetUserPostsResponse(
    @SerializedName("results") val results: List<Post>
)
