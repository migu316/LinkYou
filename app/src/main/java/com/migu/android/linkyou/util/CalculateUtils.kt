package com.migu.android.linkyou.util

import android.net.Uri



object CalculateUtils {
    /**
     * 取得图片uri末尾的编号
     */
    fun extractNumberFromUri(uri: Uri): Int {
        val uriString = uri.toString()
        val lastIndex = uriString.lastIndexOf("/")
        val numberStr = uriString.substring(lastIndex + 1)
        return numberStr.toInt()
    }
}
