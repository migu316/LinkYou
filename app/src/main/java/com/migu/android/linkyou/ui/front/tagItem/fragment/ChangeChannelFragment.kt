package com.migu.android.linkyou.ui.front.tagItem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.migu.android.linkyou.databinding.FragmentChooseChannelBinding
import com.migu.android.linkyou.ui.front.tagItem.TabItemCategoriesEnum
import com.migu.android.linkyou.ui.front.tagItem.adapter.ChangeChannelAdapter
import com.migu.android.linkyou.ui.util.BarUtils


private const val ARG_MAP_LIST = "arg_map_list"

class ChangeChannelFragment : Fragment() {

    private lateinit var binding: FragmentChooseChannelBinding
    private lateinit var mapList: LinkedHashMap<TabItemCategoriesEnum, String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapList =
            arguments?.getSerializable(ARG_MAP_LIST) as LinkedHashMap<TabItemCategoriesEnum, String>
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseChannelBinding.inflate(inflater, container, false)
        BarUtils.offsetStatusBar(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.myFocusChannelRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = ChangeChannelAdapter(mapList)
        }
        binding.noFocusChannelRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    companion object {
        fun newInstance(mapList: LinkedHashMap<TabItemCategoriesEnum, String>): ChangeChannelFragment {
            val args = Bundle().apply {
                putSerializable(ARG_MAP_LIST, mapList)
            }
            return ChangeChannelFragment().apply {
                arguments = args
            }
        }
    }
}