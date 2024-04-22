package com.migu.android.linkyou

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    /**
     * 托管活动所需接口，用于在添加频道时添加主页面
     */
    interface Callbacks {
        fun onClickChangeFragment(fragment: Fragment)
    }

    var callbacks: Callbacks? = null

    /**
     * 用来处理一些数据的加载，控件的初始化(不包括监听器)
     */
    abstract fun initialize()

    /**
     * 处理控件的监听事件，也可以用来添加观察者
     */
    abstract fun initializeListener()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        initializeListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
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
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(0, R.anim.slide_out_left)
            .remove(this)
            .commit()
    }
}