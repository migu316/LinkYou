package com.migu.android.linkyou.business.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.databinding.FragmentMessageBinding
import com.migu.android.core.util.BarUtils

class MessageFragment : BaseFragment() {
    private val binding by lazy {
        FragmentMessageBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun initialize() {
        BarUtils.offsetStatusBar(binding.root)
    }

    override fun initializeListener() {
    }

    companion object {
        fun newInstance(): MessageFragment {
            return MessageFragment()
        }
    }
}