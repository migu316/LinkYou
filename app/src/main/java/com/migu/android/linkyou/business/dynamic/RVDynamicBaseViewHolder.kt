package com.migu.android.linkyou.business.dynamic

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.event.ChildClickListener
import com.migu.android.linkyou.event.ChildTouchListener
import com.migu.android.network.model.base.Dynamic

open class RVDynamicBaseViewHolder(
    view: View,
    recyclerView: RecyclerView,
    callbacks: BaseFragment.Callbacks?
) : ViewHolder(view) {

    lateinit var mDynamic: Dynamic

    init {
        recyclerView.apply {
            setOnTouchListener(ChildTouchListener(view))
            setOnClickListener(ChildClickListener(view))
        }
        view.setOnClickListener {
            dynamicDetail(mDynamic, callbacks)
        }
    }

    private fun dynamicDetail(dynamic: Dynamic, callbacks: BaseFragment.Callbacks?) {
        callbacks?.onClickChangeFragment(DynamicDetailFragment.newInstance(dynamic))
    }
}