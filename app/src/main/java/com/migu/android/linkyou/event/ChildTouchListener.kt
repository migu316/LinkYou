package com.migu.android.linkyou.event

import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.view.ViewConfiguration
import kotlin.math.abs

class ChildTouchListener(private val view: View) : OnTouchListener {
    private var downX = 0f
    private var downY = 0f
    private val touchSlop: Float = ViewConfiguration.get(view.context).scaledTouchSlop.toFloat()

    override fun onTouch(v: View, event: MotionEvent): Boolean {
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