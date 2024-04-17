package com.migu.android.network.model

import com.google.gson.annotations.SerializedName
import com.migu.android.network.model.base.DynamicImage

/**
 * 动态图片的响应数据
 */
data class DynamicImageResponse(
    @SerializedName("results") var results: List<DynamicImage>
)