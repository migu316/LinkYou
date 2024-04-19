package com.migu.android.linkyou.event

interface OnBackPressedListener {
    /**
     * 处理返回键事件
     * @return 返回 true 表示已处理返回事件，否则返回 false
     */
    fun onBackPressed(): Boolean
}
