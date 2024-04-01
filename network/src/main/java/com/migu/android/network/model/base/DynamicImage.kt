package com.migu.android.network.model.base

import com.google.gson.annotations.SerializedName

data class DynamicImage(
    @SerializedName("imageId") val image:FileImage
)