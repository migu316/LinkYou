package com.migu.android.core.util

import com.migu.android.core.LinkYou

object GlobalUtil {
    private const val TAG = "GlobalUtil"

    /**
     * 获取资源文件中定义的字符串。
     *
     * @param resId
     * 字符串资源id
     * @return 字符串资源id对应的字符串内容。
     */
    fun getString(resId: Int): String {
        return LinkYou.context.resources.getString(resId)
    }

    /**
     * 获取格式化后的字符串资源。
     *
     * @param resId 字符串资源的资源ID
     * @param formatArgs 格式化参数，可选
     * @return 格式化后的字符串
     */
    fun getString(resId: Int, vararg formatArgs: Any): String {
        return LinkYou.context.resources.getString(resId, *formatArgs)
    }

}