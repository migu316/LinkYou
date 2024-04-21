package com.migu.android.linkyou.business.front

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.databinding.FragmentSearchBinding

class SearchFragment : BaseFragment() {

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initialize() {

    }

    override fun initializeListener() {
    }


    companion object {
        fun newInstance(): Fragment {
            return SearchFragment()
        }
    }
}