package com.migu.android.core.util

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import com.migu.android.core.TabItemCategoriesEnum

/**
 * 工具类，用于操作 SharedPreferences。
 */
object SharedPreferencesUtils {

//    /**
//     * 根据给定的名称和模式获取 SharedPreferences 实例，并执行操作。
//     *
//     * @param name SharedPreferences 文件名
//     * @param mode SharedPreferences 模式，默认为 MODE_PRIVATE
//     * @param block 要执行的操作，接收 Editor 对象作为参数
//     */
//    fun Context.getSharedPreferencesByNameExecute(
//        name: String,
//        mode: Int = MODE_PRIVATE,
//        block: Editor .() -> Unit
//    ) {
//        getSharedPreferences(name, mode).apply {
//            edit().apply {
//                block()
//                apply()
//            }
//        }
//    }
//
//    /**
//     * 根据给定的名称和模式获取 SharedPreferences 实例。
//     *
//     * @param name SharedPreferences 文件名
//     * @param mode SharedPreferences 模式，默认为 MODE_PRIVATE
//     * @return SharedPreferences 实例
//     */
//    fun Context.getSharedPreferencesObjByName(
//        name: String,
//        mode: Int = MODE_PRIVATE
//    ): SharedPreferences = getSharedPreferences(name, mode)

    /**
     * 将枚举类型存储到 SharedPreferences 中。
     *
     * @param key 存储的键名
     * @param value 要存储的枚举值
     */
    fun Editor.putEnum(key: String, value: Enum<*>) {
        putString(key, value.name)
    }

    /**
     * 从 SharedPreferences 中获取枚举类型。
     *
     * @param key 要获取的键名
     * @return 获取到的枚举值，如果获取失败则返回 null
     */
    fun SharedPreferences.getEnum(key: String): TabItemCategoriesEnum? {
        val string = getString(key, "")
        string ?: return null
        return enumValues<TabItemCategoriesEnum>().find { it.name == string }
    }
}
