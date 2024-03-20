package com.migu.android.linkyou.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.migu.android.linkyou.databinding.FragmentExploreBinding
import com.migu.android.linkyou.ui.main.MainFragment
import com.migu.android.linkyou.ui.util.BarUtils

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

    companion object {
        fun newInstance(): ExploreFragment {
            return ExploreFragment()
        }
    }
}