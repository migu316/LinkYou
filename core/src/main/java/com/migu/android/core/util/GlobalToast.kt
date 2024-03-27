package com.migu.android.core.util

import android.os.Looper
import android.widget.Toast
import com.migu.android.core.LinkYou


private var toast: Toast? = null

/**
 * 在 UI 线程显示 Toast 消息。
 *
 * @param content 要显示在 Toast 中的文本内容。
 * @param duration Toast 消息的持续时间。默认为 [Toast.LENGTH_SHORT]。
 */
fun showToast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    // 检查当前线程是否为 UI 线程
    if (Looper.myLooper() == Looper.getMainLooper()) {
        // 如果是 UI 线程，则继续显示 Toast
        // 检查 Toast 实例是否为 null
        if (toast == null) {
            // 如果 Toast 实例为 null，则使用提供的内容和持续时间创建新的 Toast
            toast = Toast.makeText(LinkYou.context, content, duration)
        } else {
            // 如果 Toast 实例不为 null，则更新现有 Toast 的文本内容
            toast?.setText(content)
        }
        // 显示 Toast 消息
        toast?.show()
    }
}

/**
 * 切换到主线程后弹出Toast信息。此方法不管是在子线程还是主线程中，都可以成功弹出Toast信息。
 *
 * @param content
 * Toast中显示的内容
 * @param duration
 * Toast显示的时长
 */
fun showToastOnUiThread(content: String, duration: Int = Toast.LENGTH_SHORT) {
    LinkYou.handler.post {
        if (toast == null) {
            toast = Toast.makeText(LinkYou.context, content, duration)
        } else {
            toast?.setText(content)
        }
        toast?.show()
    }
}
