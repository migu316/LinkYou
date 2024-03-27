package com.migu.android.core.util

import com.migu.android.core.LinkYou
import java.io.IOException
import java.io.InputStream
import java.util.Properties

object AssetsUtils {
    fun readTextFromAssets(fileName: String): String {
        return try {
            val inputStream = LinkYou.context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            "文件读取错误"
        }
    }

    fun getProperties(propertiesName: String): Properties {
        val properties = Properties()
        try {
            val inputStream: InputStream = LinkYou.context.assets.open(propertiesName)
            properties.load(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return properties
    }

    fun getAPPID(): String = getProperties("local.properties").getProperty("APPID")
    fun getRESTAPI(): String = getProperties("local.properties").getProperty("REST_API")
    fun getLCKEY(): String = getProperties("local.properties").getProperty("LCKEY")
}