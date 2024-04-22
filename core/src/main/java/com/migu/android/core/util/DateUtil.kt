package com.migu.android.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtil {

    /**
     * 将给定的日期对象转换为指定格式的字符串表示。
     *
     * @param date 要转换的日期对象
     * @return 格式化后的日期字符串，格式为 "星期几 年-月-日 时:分"
     */
    fun formatDateToString(date: Date?): String {
        // 创建一个 SimpleDateFormat 对象来定义日期时间格式
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        // 将日期对象格式化为字符串并返回
        return date?.let { simpleDateFormat.format(it) } ?: ""
    }

}