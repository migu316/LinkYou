package com.migu.android.linkyou.ui.front

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.migu.android.linkyou.databinding.FragmentFrontBinding
import com.migu.android.linkyou.ui.front.tagItem.TabItemControl
import com.migu.android.linkyou.ui.front.tagItem.TabItemFragmentStateAdapter
import com.migu.android.linkyou.ui.util.BarUtils

private const val TAG = "FrontFragment"
private const val TAG_ITEM = "tag_item"

class FrontFragment : Fragment() {

    private val binding by lazy {
        FragmentFrontBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: TabItemFragmentStateAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        BarUtils.offsetStatusBar(binding.root)
        val map = TabItemControl.getTabItemListForSP(requireContext(), TAG_ITEM)

        adapter = TabItemFragmentStateAdapter(requireActivity(), map)
        binding.mainViewpager.isSaveEnabled = false
        if (binding.mainViewpager.adapter == null) {
            binding.mainViewpager.adapter = adapter
        }
        TabLayoutMediator(binding.tabLayout, binding.mainViewpager) { tab, position ->
            tab.text = map.entries.elementAt(position).value
        }.attach()
        return binding.root
    }

    companion object {
        fun newInstance(): FrontFragment {
            return FrontFragment()
        }
    }
}