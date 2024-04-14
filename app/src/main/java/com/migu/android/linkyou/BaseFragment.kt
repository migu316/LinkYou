package com.migu.android.linkyou

import androidx.fragment.app.Fragment

open class BaseFragment:Fragment() {

    /**
     * 托管活动所需接口，用于在添加频道时添加主页面
     */
    interface Callbacks {
        fun onClickChangeFragment(fragment: Fragment)
    }

    /**
     * 关闭软键盘
     */
    fun hideSoftKeyboard() {
        (activity as BaseActivity).hideSoftKeyboard()
    }
    /**
     * 退出当前的fragment
     */
    fun exitFragment() {
        // 使用FragmentManager直接弹出返回栈顶部Fragment
        parentFragmentManager.popBackStack()
    }
}