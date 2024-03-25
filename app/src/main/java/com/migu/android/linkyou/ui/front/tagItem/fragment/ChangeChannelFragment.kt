package com.migu.android.linkyou.ui.front.tagItem.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.migu.android.linkyou.databinding.FragmentChooseChannelBinding
import com.migu.android.linkyou.ui.front.tagItem.adapter.ChangeChannelAdapter
import com.migu.android.linkyou.ui.front.tagItem.model.ChannelData
import com.migu.android.linkyou.ui.util.BarUtils


private const val ARG_MAP_LIST = "arg_map_list"

class ChangeChannelFragment : Fragment() {

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
        BarUtils.offsetStatusBar(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.myFocusChannelRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
            adapter = ChangeChannelAdapter(channelSet)
        }
        binding.noFocusChannelRecyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    companion object {
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