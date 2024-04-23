package com.migu.android.linkyou.business.dynamic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.migu.android.core.util.logInfo
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.business.dynamic.DynamicImageFragment
import com.migu.android.linkyou.business.dynamic.ImageViewHolder
import com.migu.android.linkyou.business.dynamic.SharedImageViewHolder
import com.migu.android.linkyou.databinding.DynamicSharedImageItemBinding
import com.migu.android.linkyou.databinding.DynamicsImageItemBinding

/**
 * 用于在 RecyclerView 中显示图片的适配器。
 *
 * @property urls 要显示的图片 URL 列表。
 */
class ImageAdapter(
    private var urls: List<String>,
    private val callbacks: BaseFragment.Callbacks?,
    private val isShared: Boolean = false
) :
    Adapter<ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return if (!isShared) {
            val binding =
                DynamicsImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
            val holder = ImageViewHolder(binding)
            binding.dynamicsImage.setOnClickListener {
                // 通过回调去切换fragment
                val fragment = DynamicImageFragment.newInstance(urls, holder.bindingAdapterPosition)
                callbacks?.onClickChangeFragment(fragment)
            }
            holder
        } else {
            val binding =
                DynamicSharedImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
            SharedImageViewHolder(binding)
        }
    }

    override fun getItemCount() = urls.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!isShared) {
            val imageViewHolder = holder as ImageViewHolder
            val url = urls[position]
            url.let { imageViewHolder.bind(it) }
        } else {
            val sharedImageViewHolder = holder as SharedImageViewHolder
            val url = urls[position]
            url.let { sharedImageViewHolder.bind(it) }
        }

    }

    /**
     * 用于覆盖之前的空数据
     */
    fun overwriteData(newUrls: List<String>) {
        urls = newUrls
        notifyItemChanged(0, newUrls.size)
    }
}
