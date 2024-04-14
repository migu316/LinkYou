package com.migu.android.core.util

import com.google.gson.Gson

/**
 * Gson 工具类，用于 JSON 序列化和反序列化。
 */
object GsonUtils {
    /**
     * Gson 实例。
     */
    val gson = Gson()

    /**
     * 将对象转换为 JSON 字符串。
     *
     * @param obj 要序列化的对象
     * @return 序列化后的 JSON 字符串
     */
    fun <T> toJson(obj: T): String {
        return gson.toJson(obj)
    }

    /**
     * 将 JSON 字符串反序列化为指定类型的对象。
     *
     * @param json 要反序列化的 JSON 字符串
     * @return 反序列化后的对象，如果 JSON 字符串为空则返回 null
     */
    inline fun <reified T> fromJsonNormal(json: String): T? {
        if (json.isEmpty()) {
            return null
        }
        return gson.fromJson(json, T::class.java)
    }
}
