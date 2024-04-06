package com.migu.android.linkyou.ui.front.tagItem.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.migu.android.core.util.DateUtil
import com.migu.android.core.util.logInfo
import com.migu.android.linkyou.databinding.DynamicsHasAvatarItemBinding
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.util.NetWorkUtil

class MainPageAdapter : PagingDataAdapter<Dynamic, MainPageAdapter.DynamicViewHolder>(COMPARATOR) {

    private lateinit var context: Context

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Dynamic>() {
            override fun areItemsTheSame(oldItem: Dynamic, newItem: Dynamic): Boolean {
                return oldItem.objectId == newItem.objectId
            }

            override fun areContentsTheSame(oldItem: Dynamic, newItem: Dynamic): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        context = parent.context
        val binding =
            DynamicsHasAvatarItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return DynamicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class DynamicViewHolder(val binding: DynamicsHasAvatarItemBinding) :
        ViewHolder(binding.root) {

        fun bind(dynamic: Dynamic) {
            binding.includeUserInfo.apply {
                val httpsUrl = dynamic.userInfoId?.avatar?.url?.let {
                    NetWorkUtil.replaceHttps(it)
                }
                Glide.with(context).load(httpsUrl).into(avatar)
                userName.text = dynamic.userInfoId?.name
            }
            binding.includeContent.apply {
                releaseTimeTextview.text =
                    dynamic.createdAt?.let { DateUtil.formatDateToString(it) }
                contentTextview.text = dynamic.postText
            }
        }
    }
}