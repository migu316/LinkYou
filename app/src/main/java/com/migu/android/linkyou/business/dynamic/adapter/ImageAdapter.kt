package com.migu.android.linkyou.business.dynamic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.business.dynamic.DynamicImageFragment
import com.migu.android.linkyou.business.dynamic.ImageViewHolder
import com.migu.android.linkyou.databinding.DynamicsImageItemBinding

/**
 * 用于在 RecyclerView 中显示图片的适配器。
 *
 * @property urls 要显示的图片 URL 列表。
 */
class ImageAdapter(private var urls: List<String>, private val callbacks: BaseFragment.Callbacks?) :
    Adapter<ImageViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        val binding = DynamicsImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = ImageViewHolder(binding)
        binding.dynamicsImage.setOnClickListener {
            // 通过回调去切换fragment
            val fragment = DynamicImageFragment.newInstance(urls, holder.bindingAdapterPosition)
            callbacks?.onClickChangeFragment(fragment)
        }
        return holder
    }

    override fun getItemCount() = urls.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = urls[position]
        url.let { holder.bind(it) }
    }

    /**
     * 用于覆盖之前的空数据
     */
    fun overwriteData(newUrls: List<String>) {
        urls = newUrls
        notifyItemChanged(0, newUrls.size)
    }
}
