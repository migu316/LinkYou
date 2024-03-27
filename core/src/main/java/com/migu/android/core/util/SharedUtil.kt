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
    fun clearAll(shardName:String) {
        LinkYou.context.getSharedPreferencesByNameExecute(shardName) {
            clear()
        }
    }
}