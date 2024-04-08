package com.migu.android.linkyou

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import com.migu.android.core.util.logInfo
import kotlin.math.max


class MyNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {
//    override fun measureChildWithMargins(
//        child: View,
//        parentWidthMeasureSpec: Int,
//        widthUsed: Int,
//        parentHeightMeasureSpec: Int,
//        heightUsed: Int
//    ) {
//        val lp = child.layoutParams as MarginLayoutParams
//
//        val childWidthMeasureSpec = getChildMeasureSpec(
//            parentWidthMeasureSpec,
//            paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin
//                    + widthUsed, lp.width
//        )
//        // 这里的写法与 ScrollView 里面的一样
//        val usedTotal = paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin + heightUsed
//        val childHeightMeasureSpec: Int = MeasureSpec.makeMeasureSpec(
//            max(0, MeasureSpec.getSize(parentHeightMeasureSpec) - usedTotal),
//            MeasureSpec.UNSPECIFIED
//        )
//
//        child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
//    }

    private var originScroll: Int = 0
    private var interactiveHeight: Int = 0


    fun setInteractiveHeight(height: Int) {
        this.interactiveHeight = height
    }


    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        logInfo("dx === $dx   dy === $dy")
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
        logInfo("vertical === $t")
        originScroll = t
        super.onScrollChanged(l, t, oldl, oldt)
    }
}