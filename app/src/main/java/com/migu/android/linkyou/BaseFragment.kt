package com.migu.android.linkyou

import android.content.Context
import androidx.fragment.app.Fragment
import com.migu.android.linkyou.event.OnBackPressedListener

open class BaseFragment : Fragment(), OnBackPressedListener {

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
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(0, R.anim.slide_out_left)
            .remove(this)
            .commit()
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

    override fun onBackPressed(): Boolean {
        exitFragment()
        return true
    }
}