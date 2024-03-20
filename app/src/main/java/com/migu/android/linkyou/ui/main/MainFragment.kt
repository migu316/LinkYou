package com.migu.android.linkyou.ui.main

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.FragmentMainBinding
import com.migu.android.linkyou.ui.util.BarUtils

private const val TAG = "MainFragment"
class MainFragment:Fragment() {

    private val binding by lazy {
        FragmentMainBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        BarUtils.offsetStatusBar(binding.root)

//        TabLayoutMediator(binding.tabLayout, binding.mainViewpager) {tab, position ->
//            tab.text = position.toString()
//        }.attach()
        return binding.root
    }

    companion object {
        fun newInstance():MainFragment {
            return MainFragment()
        }
    }
}