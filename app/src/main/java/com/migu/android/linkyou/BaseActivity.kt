package com.migu.android.linkyou

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 应用程序中所有activity的基类
 */
private const val TAG = "BaseActivity"

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityController.addActivity(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        ActivityController.removeActivity(this)
    }

    object ActivityController {
        private val activities = ArrayList<Activity>()

        fun addActivity(activity: Activity) {
            if (!hasTargetActivity(activity)) {
                activities.add(activity)
            }
        }

        fun hasTargetActivity(targetActivity: Activity): Boolean {
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
}