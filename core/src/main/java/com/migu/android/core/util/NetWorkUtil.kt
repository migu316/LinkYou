package com.migu.android.core.util

object NetWorkUtil {
    /**
     * 将 HTTP 协议的 URL 转换为 HTTPS
     *
     * @param imageUrl 待转换的图片 URL
     * @return 转换后的 HTTPS URL，如果原始 URL 已经是 HTTPS 开头，则返回原始 URL
     */
    fun replaceHttps(imageUrl: String?): String {
        if (imageUrl == null) return ""
        // 如果 URL 已经是 HTTPS 开头，则不需要转换
        return if (imageUrl.startsWith("https://")) {
            imageUrl // 返回原始 URL
        } else {
            imageUrl.replace("http://", "https://") // 否则，将 HTTP 协议替换为 HTTPS
        }
    }
}
