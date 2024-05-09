package com.migu.android.linkyou.event

import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.view.ViewConfiguration
import kotlin.math.abs

/**
 * 用来处理嵌套的内部RV中空白处的点击事件传递
 * 参见[处理嵌套RV空白处的点击事件](https://future-science.cn/2024/04/21/Android/RV%E5%B5%8C%E5%A5%97-%E5%A4%84%E7%90%86%E7%A9%BA%E7%99%BD%E5%A4%84%E7%9A%84%E7%82%B9%E5%87%BB%E4%BA%8B%E4%BB%B6/)
 */
class ChildTouchListener(private val view: View) : OnTouchListener {
    private var downX = 0f
    private var downY = 0f
    // 系统允许的最小滑动距离，避免误触
    private val touchSlop: Float = ViewConfiguration.get(view.context).scaledTouchSlop.toFloat()

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        // 通过添加onTouch方法，使得在内部RV执行onTouchEvent之前先执行onTouch，
        // 在onTouch中再判断是否是点击，而不是滑动，再执行View的点击事件（即打开动态详情）
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }

            MotionEvent.ACTION_UP -> {
                if (abs((event.y - downY).toDouble()) < touchSlop && abs((event.x - downX).toDouble()) < touchSlop) {
                    view.performClick()
                }
            }
        }
        return false
    }
}

class ChildClickListener(private val view:View) :OnClickListener {
    override fun onClick(v: View?) {
        view.performClick()
    }
}