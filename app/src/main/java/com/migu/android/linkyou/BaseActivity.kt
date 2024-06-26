package com.migu.android.linkyou

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.FrameMetrics
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.migu.android.core.util.logWarn

/**
 * 应用程序中所有activity的基类
 */
open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityController.addActivity(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun extracted() {
        window?.addOnFrameMetricsAvailableListener(Window.OnFrameMetricsAvailableListener { window, frameMetrics, dropCountSinceLastInvocation ->
            Log.v(
                "test",
                "measure + layout=${frameMetrics.getMetric(FrameMetrics.LAYOUT_MEASURE_DURATION) / 1000000}, " +
                        "    delay=${frameMetrics.getMetric(FrameMetrics.UNKNOWN_DELAY_DURATION) / 1000000}, " +
                        "    anim=${frameMetrics.getMetric(FrameMetrics.ANIMATION_DURATION) / 1000000}," +
                        "    touch=${frameMetrics.getMetric(FrameMetrics.INPUT_HANDLING_DURATION) / 1000000}, " +
                        "    draw=${frameMetrics.getMetric(FrameMetrics.DRAW_DURATION) / 1000000}, " +
                        "    total=${frameMetrics.getMetric(FrameMetrics.LAYOUT_MEASURE_DURATION) / 1000000}"
            )
        }, Handler())
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityController.removeActivity(this)
    }

    /**
     * 隐藏软键盘
     */
    fun hideSoftKeyboard() {
        try {
            val view = currentFocus
            if (view != null) {
                val iBinder = view.windowToken
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        } catch (e:Exception) {
            logWarn(TAG, e.message, e)
        }
    }

    object ActivityController {
        private val activities = ArrayList<Activity>()

        fun addActivity(activity: Activity) {
            if (!hasTargetActivity(activity)) {
                activities.add(activity)
            }
        }

        private fun hasTargetActivity(targetActivity: Activity): Boolean {
            for (activity in activities) {
                if (activity === targetActivity) {
                    return true
                }
            }
            return false
        }

        fun removeActivity(activity: Activity) {
            activities.remove(activity)
        }

        fun finishAllActivity() {
            for (activity in activities) {
                if (!activity.isFinishing) {
                    activity.finishAndRemoveTask()
                }
            }
            activities.clear()
        }
    }

    companion object {
        private const val TAG = "BaseActivity"
    }
}