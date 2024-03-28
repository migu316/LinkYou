package com.migu.android.opensource.login

/**
 * 通用事件类，用于处理事件并防止重复处理
 * @param T 事件内容的类型
 * @property content 事件内容
 * @property hasBeenHandled 事件是否已经被处理的标志位
 */
open class LoginEvent<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    /**
     * 获取事件内容，如果事件已经被处理过，则返回null
     * 如果事件未被处理，则将hasBeenHandled标志位设置为true，表示已经处理过
     * @return 事件内容，如果事件已经被处理则返回null，否则返回内容
     */
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * 获取事件内容，不论是否被处理，都返回内容
     * @return 事件内容
     */
    fun peekContent(): T = content
}
