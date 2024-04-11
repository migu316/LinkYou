package com.migu.android.linkyou.business.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.databinding.FragmentMessageBinding
import com.migu.android.linkyou.util.BarUtils

class MessageFragment : BaseFragment() {
    private val binding by lazy {
        FragmentMessageBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        BarUtils.offsetStatusBar(binding.root)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(): MessageFragment {
            return MessageFragment()
        }
    }
}