package com.migu.android.linkyou.ui.front

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.migu.android.linkyou.databinding.FragmentFrontBinding
import com.migu.android.linkyou.ui.front.tagItem.TabItemCategoriesEnum
import com.migu.android.linkyou.ui.front.tagItem.TabItemFragmentStateAdapter
import com.migu.android.linkyou.ui.util.BarUtils

private const val TAG = "FrontFragment"

class FrontFragment : Fragment() {

    private val binding by lazy {
        FragmentFrontBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: TabItemFragmentStateAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView: ")
        BarUtils.offsetStatusBar(binding.root)
        val map: LinkedHashMap<TabItemCategoriesEnum, String> = linkedMapOf()
        map[TabItemCategoriesEnum.MAIN_PAGE] = "主页"
        map[TabItemCategoriesEnum.FOCUS_PAGE] = "好友关注"

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onViewCreated: ")
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume: ")
    }


    override fun onPause() {
        super.onPause()
        Log.i(TAG, "onPause: ")
    }


    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }

    override fun onDetach() {
        super.onDetach()
        Log.i(TAG, "onDetach: ")
    }

    companion object {
        fun newInstance(): FrontFragment {
            return FrontFragment()
        }
    }
}