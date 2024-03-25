package com.migu.android.linkyou.ui.front.tagItem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.ChooseChannelItemBinding
import com.migu.android.linkyou.ui.front.tagItem.TabItemCategoriesEnum
import com.migu.android.linkyou.ui.front.tagItem.model.ChannelData

class ChangeChannelAdapter(private val channelSet: LinkedHashSet<ChannelData>) :
    RecyclerView.Adapter<ChangeChannelAdapter.ChannelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding =
            ChooseChannelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChannelViewHolder(binding)
    }

    override fun getItemCount() = channelSet.size

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channelData = channelSet.elementAt(position)
        holder.bind(channelData)
    }

    inner class ChannelViewHolder(private val binding: ChooseChannelItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(channelData: ChannelData) {
            binding.channelIcon.setImageResource(channelData.channelIcon)
            binding.channelName.text = channelData.channelName
        }

        override fun onClick(v: View?) {

        }
    }
}