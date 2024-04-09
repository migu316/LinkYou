package com.migu.android.linkyou.business.explore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.migu.android.linkyou.databinding.FragmentExploreBinding
import com.migu.android.linkyou.business.util.BarUtils

private const val TAG = "ExploreFragment"

class ExploreFragment : Fragment() {
    private val binding by lazy {
        FragmentExploreBinding.inflate(layoutInflater)
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
        Log.i(TAG, "onViewCreated: ")
    }

    companion object {
        fun newInstance(): ExploreFragment {
            return ExploreFragment()
        }
    }
}