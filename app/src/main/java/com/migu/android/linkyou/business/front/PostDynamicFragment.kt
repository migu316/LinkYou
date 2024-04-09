package com.migu.android.linkyou.business.front

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.databinding.FragmentPostDynamicBinding

class PostDynamicFragment : BaseFragment() {


    private lateinit var binding: FragmentPostDynamicBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostDynamicBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(): Fragment {
            return PostDynamicFragment()
        }
    }
}