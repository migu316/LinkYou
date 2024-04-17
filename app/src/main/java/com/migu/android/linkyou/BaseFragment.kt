package com.migu.android.linkyou

import android.content.Context
import androidx.fragment.app.Fragment
import com.migu.android.core.util.logInfo

open class BaseFragment : Fragment() {

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

    var callbacks: Callbacks? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }
}