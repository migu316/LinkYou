package com.migu.android.linkyou.util

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
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
     * 在给定的视图中设置顶部状态栏偏移量。
     * @param view 应用顶部状态栏偏移的视图
     *
     * 如果其他布局也使用了同样的方法进行了转换，并且都是从 LinearLayout 转换为 FrameLayout.LayoutParams，
     * 但没有出现问题，那可能是因为这些布局的父容器都是 FrameLayout，
     * 或者这些布局的父容器对于添加 FrameLayout.LayoutParams 参数也没有产生影响。
     */
    fun offsetStatusBar(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            // 获取视图的父容器
            val parent = v.parent as? ViewGroup
            // 如果父容器是 FrameLayout，则设置 FrameLayout.LayoutParams 的 topMargin
            if (parent is FrameLayout) {
                val params = v.layoutParams as FrameLayout.LayoutParams
                params.topMargin = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
                // 如果父容器是 LinearLayout，则设置 LinearLayout.LayoutParams 的 topMargin
            } else if (parent is LinearLayout) {
                val params = v.layoutParams as LinearLayout.LayoutParams
                params.topMargin = insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            }
            insets
        }
    }
}