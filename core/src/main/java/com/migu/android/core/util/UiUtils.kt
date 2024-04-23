package com.migu.android.core.util

import android.content.Context
import com.migu.android.core.LinkYou

object UiUtils {


    /**
     * 获取屏幕的宽度dp
     */
    fun getWindowWidth(context: Context): Int {
        val metrics = context.resources.displayMetrics
        val density = metrics.density

        // 屏幕的宽度(dp)
        return (metrics.widthPixels / density).toInt()
    }


    /**
     * 计算当前窗口宽度减去指定dp边距后的px
     * @param totalMargins 需要减去的边距dp
     */
    fun getUIWidth(context: Context, totalMargins: Int): Int {
        val metrics = context.resources.displayMetrics
        val density = metrics.density
        var widthDp = LinkYou.widthDp

        // 减去边距后的宽度
        widthDp -= totalMargins

        // 转换为px
        return (widthDp * density).toInt()
    }

    /**
     * 计算总高度
     * @param unitHeightDp 一个单位控件的高度(dp)
     */
    fun calculateHeight(context: Context, unitHeightDp: Int, count: Int): Int {
        val metrics = context.resources.displayMetrics
        val density = metrics.density

        return (unitHeightDp * density * count).toInt()
    }
}