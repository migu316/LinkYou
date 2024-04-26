package com.migu.android.linkyou.business.dynamic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.business.dynamic.DynamicImageFragment
import com.migu.android.linkyou.business.dynamic.ImageViewHolder
import com.migu.android.linkyou.business.dynamic.SharedImageViewHolder
import com.migu.android.linkyou.business.dynamic.SingleImageViewHolder
import com.migu.android.linkyou.databinding.DynamicSharedImageItemBinding
import com.migu.android.linkyou.databinding.DynamicSingleImageItemBinding
import com.migu.android.linkyou.databinding.DynamicsImageItemBinding

/**
 * 用于在 RecyclerView 中显示图片的适配器。
 *
 * @property urls 要显示的图片 URL 列表。
 */
class ImageAdapter(
    private var urls: List<String>,
    private val callbacks: BaseFragment.Callbacks?,
    private val isShared: Boolean = false,
    private val isSingle: Boolean = false
) :
    Adapter<ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        // 是否为单张图片
        if (isSingle) {
            val binding =
                DynamicSingleImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
            val holder = SingleImageViewHolder(binding)
            binding.image.setOnClickListener {
                val fragment = DynamicImageFragment.newInstance(urls, holder.bindingAdapterPosition)
                callbacks?.onClickChangeFragment(fragment)
            }
            return holder
        }

        // 是否为分享图片
        if (!isShared) {
            val binding =
                DynamicsImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
            val holder = ImageViewHolder(binding)
            binding.dynamicsImage.setOnClickListener {
                // 通过回调去切换fragment
                val fragment = DynamicImageFragment.newInstance(urls, holder.bindingAdapterPosition)
                callbacks?.onClickChangeFragment(fragment)
            }
            return holder
        }

        // 为分享图片
        val binding =
            DynamicSharedImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return SharedImageViewHolder(binding)
    }

    override fun getItemCount() = urls.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val url = urls[position]

        if (isSingle) {
            val singleImageViewHolder = holder as SingleImageViewHolder
            singleImageViewHolder.bind(url)
            return
        }
        if (!isShared) {
            val imageViewHolder = holder as ImageViewHolder
            imageViewHolder.bind(url)
            return
        }
        val sharedImageViewHolder = holder as SharedImageViewHolder
        sharedImageViewHolder.bind(url)
    }

    /**
     * 用于覆盖之前的空数据
     */
    fun overwriteData(newUrls: List<String>) {
        urls = newUrls
        notifyItemChanged(0, newUrls.size)
    }
}
