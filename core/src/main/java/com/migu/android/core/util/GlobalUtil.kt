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
}