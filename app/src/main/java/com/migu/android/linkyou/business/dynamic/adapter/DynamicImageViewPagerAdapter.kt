package com.migu.android.linkyou.business.dynamic.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.migu.android.core.glide.GlideUtils
import com.migu.android.linkyou.databinding.DynacisImagesViewpagerItemBinding
import com.migu.android.core.util.NetWorkUtil

/**
 * 显示动态图片的viewpager2适配器
 */
class DynamicImageViewPagerAdapter(val saveImageCallback: (bitmap:Bitmap) -> Unit) :
    ListAdapter<String, DynamicImageViewPagerAdapter.DetailImageViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return false
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return false
            }
        }
    }

    private lateinit var context: Context

    inner class DetailImageViewHolder(val binding: DynacisImagesViewpagerItemBinding) :
        ViewHolder(binding.root) {
        init {
            binding.image.setOnLongClickListener {
                saveImageCallback(binding.image.drawable.toBitmap())
                true
            }
        }

        fun bind(url: String) {
            GlideUtils.glide(url,false).into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailImageViewHolder {
        context = parent.context
        val binding =
            DynacisImagesViewpagerItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return DetailImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailImageViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}