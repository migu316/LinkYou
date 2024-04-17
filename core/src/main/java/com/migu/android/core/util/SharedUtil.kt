package com.migu.android.core.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.migu.android.core.LinkYou

object SharedUtil {

    /**
     * 根据给定的名称和模式获取 SharedPreferences 实例，并执行操作。
     *
     * @param name SharedPreferences 文件名
     * @param mode SharedPreferences 模式，默认为 MODE_PRIVATE
     * @param block 要执行的操作，接收 Editor 对象作为参数
     */
    fun Context.getSharedPreferencesByNameExecute(
        name: String,
        mode: Int = MODE_PRIVATE,
        block: SharedPreferences.Editor .() -> Unit
    ) {
        getSharedPreferences(name, mode).apply {
            edit().apply {
                block()
                apply()
            }
        }
    }

    /**
     * 根据给定的名称和模式获取 SharedPreferences 实例。
     *
     * @param name SharedPreferences 文件名
     * @param mode SharedPreferences 模式，默认为 MODE_PRIVATE
     * @return SharedPreferences 实例
     */
    fun Context.getSharedPreferencesObjByName(
        name: String,
        mode: Int = MODE_PRIVATE
    ): SharedPreferences = getSharedPreferences(name, mode)


    /**
     * 清理SharedPreferences文件中所有的值
     * @param shardName SharedPreferences 文件名
     */
    fun clearAll(shardName: String) {
        LinkYou.context.getSharedPreferencesByNameExecute(shardName) {
            clear()
        }
    }

    fun get(spName: String, key: String): Boolean {
        return LinkYou.context.getSharedPreferences(spName, MODE_PRIVATE).getBoolean(key, false)
    }

    /**
     * 将字符串类型的值保存到指定的 SharedPreferences 中
     * @param spName SharedPreferences 文件名
     * @param key 存储键
     * @param value 要保存的字符串值
     */
    fun save(spName: String, key: String, value: String) {
        LinkYou.context.getSharedPreferencesByNameExecute(spName) {
            putString(key, value)
        }
    }

    /**
     * 将长整型类型的值保存到指定的 SharedPreferences 中
     * @param spName SharedPreferences 文件名
     * @param key 存储键
     * @param value 要保存的长整型值
     */
    fun save(spName: String, key: String, value: Long) {
        LinkYou.context.getSharedPreferencesByNameExecute(spName) {
            putLong(key, value)
        }
    }

    /**
     * 将布尔类型的值保存到指定的 SharedPreferences 中
     * @param spName SharedPreferences 文件名
     * @param key 存储键
     * @param value 要保存的布尔值
     */
    fun save(spName: String, key: String, value: Boolean) {
        LinkYou.context.getSharedPreferencesByNameExecute(spName) {
            putBoolean(key, value)
        }
    }

    /**
     * 将浮点型类型的值保存到指定的 SharedPreferences 中
     * @param spName SharedPreferences 文件名
     * @param key 存储键
     * @param value 要保存的浮点型值
     */
    fun save(spName: String, key: String, value: Float) {
        LinkYou.context.getSharedPreferencesByNameExecute(spName) {
            putFloat(key, value)
        }
    }

    /**
     * 将字符串集合类型的值保存到指定的 SharedPreferences 中
     * @param spName SharedPreferences 文件名
     * @param key 存储键
     * @param value 要保存的字符串集合值
     */
    fun save(spName: String, key: String, value: Set<String>) {
        LinkYou.context.getSharedPreferencesByNameExecute(spName) {
            putStringSet(key, value)
        }
    }


}