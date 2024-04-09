package com.migu.android.linkyou

import androidx.fragment.app.Fragment

open class BaseFragment:Fragment() {

    /**
     * 托管活动所需接口，用于在添加频道时添加主页面
     */
    interface Callbacks {
        fun onClickChangeFragment(fragment: Fragment)
    }
}