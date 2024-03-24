package com.migu.android.linkyou.ui.front.tagItem.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.migu.android.linkyou.databinding.FragmentTabItemMainPageBinding

private const val TAG = "MainPageFragment"

class MainPageFragment : Fragment() {
    private lateinit var binding: FragmentTabItemMainPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabItemMainPageBinding.inflate(inflater, container, false)
        return binding.root
    }
}