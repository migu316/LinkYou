package com.migu.android.linkyou.ui.front.tagItem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.ChooseChannelItemBinding
import com.migu.android.linkyou.ui.front.tagItem.TabItemCategoriesEnum

class ChangeChannelAdapter(private val mapList: LinkedHashMap<TabItemCategoriesEnum, String>) :
    RecyclerView.Adapter<ChangeChannelAdapter.ChannelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val binding =
            ChooseChannelItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChannelViewHolder(binding)
    }

    override fun getItemCount() = mapList.size

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val entry = mapList.entries.elementAt(position)
        holder.bind(entry.value)
    }

    inner class ChannelViewHolder(val binding: ChooseChannelItemBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(name: String) {
            binding.channelIcon.setImageResource(R.drawable.package_2_24px)
            binding.channelName.text = name
        }

        override fun onClick(v: View?) {

        }
    }
}