package com.migu.android.network

import com.migu.android.core.Const
import com.migu.android.core.LinkYou
import com.migu.android.network.util.LeanCloudUtils

object Interceptor {

    /**
     * 根据app配置返回不同质量的图片url
     */
    fun pictureQualityMode(url: String): String {
        if (url.isEmpty()) {
            return ""
        }
        return when (LinkYou.pictureQuality) {
            Const.PictureQuality.LOW -> LeanCloudUtils.getThumbnailUrl(url, 200, 200)

            Const.PictureQuality.MEDIUM -> LeanCloudUtils.getThumbnailUrl(url, 650, 650)

            Const.PictureQuality.HIGH -> LeanCloudUtils.getThumbnailUrl(url, 800, 800)

            Const.PictureQuality.RAW -> url
        }
    }
}