package com.migu.android.linkyou.business.dynamic

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.DynamicsImageItemBinding
import com.migu.android.network.util.NetWorkUtil

/**
 * ImageAdapter 的 ViewHolder 类。
 *
 * @param binding 项目布局的视图绑定。
 */
class ImageViewHolder(val binding: DynamicsImageItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val glide: RequestManager = Glide.with(binding.root)

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

    fun bindByUri(uri: Uri) {
        glide.load(uri).into(binding.dynamicsImage)
    }
}