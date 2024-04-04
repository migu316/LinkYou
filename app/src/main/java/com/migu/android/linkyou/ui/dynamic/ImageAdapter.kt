package com.migu.android.linkyou.ui.dynamic

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.gson.Gson
import com.migu.android.core.util.logInfo
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.DynamicsImageItemBinding
import com.migu.android.network.util.NetWorkUtil

/**
 * 用于在 RecyclerView 中显示图片的适配器。
 *
 * @property urls 要显示的图片 URL 列表。
 */
class ImageAdapter(private var urls: List<String>) : Adapter<ImageAdapter.ImageViewHolder>() {

    private lateinit var context: Context
    private lateinit var glide: RequestManager

    /**
     * ImageAdapter 的 ViewHolder 类。
     *
     * @param binding 项目布局的视图绑定。
     */
    inner class ImageViewHolder(val binding: DynamicsImageItemBinding) : ViewHolder(binding.root) {
        /**
         * 使用 Glide 将图片 URL 绑定到 ImageView。
         *
         * @param imageUrl 要加载的图片的 URL。
         */
        fun bind(imageUrl: String) {
            if (imageUrl.isEmpty()) {
                glide.load(R.drawable.ic_launcher_background).into(binding.dynamicsImage)
            } else {
                glide.load(NetWorkUtil.replaceHttps(imageUrl))
                    .placeholder(R.drawable.ic_launcher_background).into(binding.dynamicsImage)
            }
        }
    }

    /**
     * 为 RecyclerView 创建并返回一个新的 ViewHolder。
     *
     * @param parent 父 ViewGroup。
     * @param viewType 新 View 的类型。
     * @return 包含给定视图类型的 View 的新 ViewHolder。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        glide = Glide.with(context)
        val binding = DynamicsImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ImageViewHolder(binding)
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
//        if (url.isEmpty()) {
//            return
//        }
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
