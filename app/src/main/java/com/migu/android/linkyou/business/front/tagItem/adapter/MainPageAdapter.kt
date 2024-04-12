package com.migu.android.linkyou.business.front.tagItem.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.migu.android.core.util.DateUtil
import com.migu.android.linkyou.databinding.DynamicsHasAvatarItemBinding
import com.migu.android.linkyou.business.dynamic.ImageAdapter
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.util.NetWorkUtil

/**
 * 主页适配器，继承自 PagingDataAdapter，用于展示动态列表
 */
class MainPageAdapter :
    PagingDataAdapter<Dynamic, MainPageAdapter.DynamicViewHolder>(COMPARATOR) {

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
        // 使用动态布局绑定生成的 ViewDataBinding 对象
        val binding =
            DynamicsHasAvatarItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return DynamicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val binding = holder.binding
        val dynamicItem = getItem(position)
        if (dynamicItem != null) {
            holder.bind(dynamicItem)
            // 设置用户动态图片列表的可见性
            binding.includeContent.userDynamicImagesRecyclerView.visibility = View.GONE

            dynamicItem.imageUrls?.let {
                if (it.isNotEmpty()) {
                    // 如果图片列表不为空，则显示图片列表
                    binding.includeContent.userDynamicImagesRecyclerView.apply {
                        visibility = View.VISIBLE
                        adapter = ImageAdapter(it) // 使用 ImageAdapter 设置图片列表的适配器
                        layoutManager =
                            GridLayoutManager(context, 3) // 使用 GridLayoutManager 设置图片列表的布局管理器
                    }
                }
            }
        }
    }

    inner class DynamicViewHolder(val binding: DynamicsHasAvatarItemBinding) :
        ViewHolder(binding.root) {

        private lateinit var dynamic: Dynamic
        fun bind(dynamic: Dynamic) {
            this.dynamic = dynamic
            // 设置用户信息部分的数据
            binding.includeUserInfo.apply {
                val httpsUrl = dynamic.userInfoId?.avatar?.url?.let {
                    NetWorkUtil.replaceHttps(it)
                }
                Glide.with(context).load(httpsUrl).into(avatar)
                userName.text = dynamic.userInfoId?.name
            }
            // 设置动态内容部分的数据
            binding.includeContent.apply {
                releaseTimeTextview.text =
                    dynamic.createdAt?.let { DateUtil.formatDateToString(it) }
                contentTextview.text = dynamic.postText
            }
            // 设置互动数据部分的数据
            binding.interactiveData.apply {
                likesTextview.text = dynamic.likes.toString()
            }
        }
    }
}