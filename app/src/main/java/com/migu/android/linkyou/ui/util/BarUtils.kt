package com.migu.android.linkyou.ui.util

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object BarUtils {
    /**
     * 隐藏系统状态栏
     */
    fun transparentStatusBar(window: Window) {
        window.apply {
            // 清除 Window 的标志，以确保状态栏不再是半透明的。
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // 添加标志，表明窗口将绘制系统状态栏背景。
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // 定义一个标志变量，表示布局时考虑稳定性和全屏布局。
            val option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            // 将之前定义的标志与当前的系统UI可见性标志合并，并设置给 DecorView。
            decorView.systemUiVisibility = option or decorView.systemUiVisibility
            // 将状态栏颜色设置为透明，从而实现沉浸式效果。
            statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 隐藏系统导航栏
     */
    fun transparentNavBar(window: Window) {
        window.apply {
            // 用于指示是否强制对比度，设置为 false 可以避免在导航栏处出现强制对比度的效果。
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            val option =
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option or decorView.systemUiVisibility
            navigationBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 开启沉浸式状态栏
     */
    fun immersiveStatus(window: Window) {
        val decorView = window.decorView
        decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
    }

    /**
     * 让视图向下偏移一个状态栏的高度
     */
    fun offsetStatusBar(view:View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val params = v.layoutParams as FrameLayout.LayoutParams
            params.topMargin = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            insets
        }
    }
}