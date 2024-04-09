package com.migu.android.linkyou.customize

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import com.migu.android.core.util.logInfo
import kotlin.math.max


class MyNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private var originScroll: Int = 0
    private var interactiveHeight: Int = 0

    fun setInteractiveHeight(height: Int) {
        this.interactiveHeight = height
    }


    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
//        logInfo("dy === $dy")
        if (originScroll < interactiveHeight) {
            scrollBy(dx, dy)
            consumed[0] = dx
            consumed[1] = dy
        }
        super.onNestedPreScroll(target, dx, dy, consumed, type)
    }


    /**
     * 视图滑动时，设置当前垂直滚动原始距离
     */
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
//        logInfo("vertical === $t   oldt === $oldt")
        // 表示当前视图的垂直滚动偏移量（距离顶部的偏移量）
        originScroll = t
        super.onScrollChanged(l, t, oldl, oldt)
    }
}