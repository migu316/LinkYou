package com.migu.android.linkyou.ui.util

import android.content.Context
import java.io.IOException

object AssetsUtils {

    fun readTextFromAssets(context: Context, fileName: String): String {
        return try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e:IOException) {
            e.printStackTrace()
            "文件读取错误"
        }
    }
}