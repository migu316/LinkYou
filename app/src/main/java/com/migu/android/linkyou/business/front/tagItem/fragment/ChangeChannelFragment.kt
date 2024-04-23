package com.migu.android.linkyou.business.front.tagItem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.databinding.FragmentChooseChannelBinding
import com.migu.android.linkyou.business.front.tagItem.adapter.ChangeChannelAdapter
import com.migu.android.linkyou.business.front.tagItem.model.ChannelData
import com.migu.android.core.util.BarUtils

class ChangeChannelFragment : BaseFragment() {

    private lateinit var binding: FragmentChooseChannelBinding
    private lateinit var channelSet: LinkedHashSet<ChannelData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val arrayListChannels =
            arguments?.getParcelableArrayList<ChannelData>(ARG_MAP_LIST)!!
        channelSet = LinkedHashSet(arrayListChannels)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initialize() {
        BarUtils.offsetStatusBar(binding.root)
    }

    override fun initializeListener() {
        binding.myFocusChannelRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = ChangeChannelAdapter(channelSet)
        }
        binding.noFocusChannelRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    companion object {
        private const val ARG_MAP_LIST = "arg_map_list"
        fun newInstance(channelSet: LinkedHashSet<ChannelData>): ChangeChannelFragment {
            val array = ArrayList<ChannelData>(channelSet)
            val args = Bundle().apply {
                putParcelableArrayList(ARG_MAP_LIST, array)
            }
            return ChangeChannelFragment().apply {
                arguments = args
            }
        }
    }
}