package com.migu.android.linkyou.business.dynamic

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.migu.android.core.util.logInfo
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.databinding.DynamicsImageItemBinding

/**
 * 用于在 RecyclerView 中显示图片的适配器。
 *
 * @property urls 要显示的图片 URL 列表。
 */
class ImageAdapter(private var urls: List<String>, val callbacks: BaseFragment.Callbacks?) :
    Adapter<ImageViewHolder>() {

    private lateinit var context: Context

    /**
     * 为 RecyclerView 创建并返回一个新的 ViewHolder。
     *
     * @param parent 父 ViewGroup。
     * @param viewType 新 View 的类型。
     * @return 包含给定视图类型的 View 的新 ViewHolder。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        val binding = DynamicsImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
        val holder = ImageViewHolder(binding)
        binding.dynamicsImage.setOnClickListener {
            val fragment = ImageFragment.newInstance(urls, holder.bindingAdapterPosition)
            callbacks?.onClickChangeFragment(fragment)
        }
        return holder
    }

    /**
     * 返回适配器持有的数据集中的总项目数。
     *
     * @return 项目的总数。
     */
    override fun getItemCount() = urls.size

    /**
     * RecyclerView 调用此方法显示指定位置的数据。
     *
     * @param holder 应更新以表示给定位置项目内容的 ViewHolder。
     * @param position 项目在适配器数据集中的位置。
     */
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
