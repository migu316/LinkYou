package com.migu.android.network.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataProcessingUtil {

    /**
     * 将 JSON 字符串转换为字符串列表。
     *
     * @param jsonString 要转换的 JSON 字符串。
     * @return 字符串列表。
     */
    fun jsonToListString(jsonString: String): List<String> {
        if (jsonString.isEmpty()) {
            return listOf()
        }
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(jsonString, type)
    }
}