package com.migu.android.core.util

import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.migu.android.core.Const
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

    /**
     * 计算dp转换为px后的大小
     */
    fun dpToPx(context: Context, unitHeightDp: Int): Int {
        val metrics = context.resources.displayMetrics
        val density = metrics.density

        return (unitHeightDp * density).toInt()
    }

    /**
     * 夜间日间模式切换
     */
    private fun switchDarkMode(activity: Activity?) {
        val uiModeManager =
            activity?.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        // 系统设置
        /**
         *     public static final int MODE_NIGHT_AUTO = 0;
         *     public static final int MODE_NIGHT_CUSTOM = 3;
         *     public static final int MODE_NIGHT_NO = 1;
         *     public static final int MODE_NIGHT_YES = 2;
         */
        val systemNightMode = uiModeManager.nightMode
        // app设置
        val isNightMode = AppCompatDelegate.getDefaultNightMode()

        // 如果系统设置为日间，那么可以通过app的设置进行切换
        // 如果系统设置为其他，那么不执行切换
        when (systemNightMode) {
            // 系统夜间关闭
            UiModeManager.MODE_NIGHT_NO -> {
                when (isNightMode) {

                    // app夜间关闭
                    AppCompatDelegate.MODE_NIGHT_NO -> {
                        // 切换为夜间
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        SharedUtil.save(
                            Const.DarkMode.DARK_MODE_SP_FILE,
                            Const.DarkMode.DARK_ON,
                            true
                        )
                    }

                    // app夜间开启
                    AppCompatDelegate.MODE_NIGHT_YES -> {
                        // 切换为日间
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        SharedUtil.save(
                            Const.DarkMode.DARK_MODE_SP_FILE,
                            Const.DarkMode.DARK_ON,
                            false
                        )
                    }

                    // app夜间未指定
                    AppCompatDelegate.MODE_NIGHT_UNSPECIFIED -> {
                        // 切换为夜间
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        SharedUtil.save(
                            Const.DarkMode.DARK_MODE_SP_FILE,
                            Const.DarkMode.DARK_ON,
                            true
                        )
                    }

                    else -> {}
                }
            }

            else -> {}
        }
    }
}