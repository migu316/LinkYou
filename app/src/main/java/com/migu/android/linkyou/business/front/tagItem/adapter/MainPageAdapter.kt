package com.migu.android.linkyou.business.front.tagItem.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.migu.android.core.util.DateUtil
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.business.dynamic.RVDynamicBaseViewHolder
import com.migu.android.linkyou.business.dynamic.SharedDynamicFragment
import com.migu.android.linkyou.databinding.DynamicsHasAvatarItemBinding
import com.migu.android.linkyou.business.dynamic.adapter.ImageAdapter
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.util.NetWorkUtil

/**
 * 主页适配器，继承自 PagingDataAdapter，用于展示动态列表
 */
class MainPageAdapter(private val callbacks: BaseFragment.Callbacks?) :
    PagingDataAdapter<Dynamic, MainPageAdapter.DynamicBaseViewHolder>(COMPARATOR) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicBaseViewHolder {
        context = parent.context
        // 使用动态布局绑定生成的 ViewDataBinding 对象
        val binding =
            DynamicsHasAvatarItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return DynamicBaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DynamicBaseViewHolder, position: Int) {
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
                        layoutManager = if (dynamicItem.imageCount == 1) {
                            adapter = ImageAdapter(it, callbacks, isSingle = true)
                            LinearLayoutManager(context)
                        } else {
                            adapter = ImageAdapter(it, callbacks)
                            GridLayoutManager(context, 3)
                        }
                    }
                }
            }
        }
    }

    inner class DynamicBaseViewHolder(val binding: DynamicsHasAvatarItemBinding) :
        RVDynamicBaseViewHolder(
            binding.root,
            binding.includeContent.userDynamicImagesRecyclerView,
            callbacks
        ) {

        init {
            binding.includeInteractiveData.sharedDynamic.setOnClickListener {
                callbacks?.onClickChangeFragment(SharedDynamicFragment.newInstance(mDynamic))
            }
        }

        fun bind(dynamic: Dynamic) {
            mDynamic = dynamic
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
            binding.includeInteractiveData.apply {
                likesTextview.text = dynamic.likes.toString()
            }
        }
    }
}