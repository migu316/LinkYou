package com.migu.android.linkyou.business.dynamic

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.migu.android.core.glide.GlideUtils
import com.migu.android.core.util.UiUtils
import com.migu.android.linkyou.databinding.DynamicSharedImageItemBinding
import com.migu.android.linkyou.databinding.DynamicSingleImageItemBinding
import com.migu.android.linkyou.databinding.DynamicsImageItemBinding
import com.migu.android.core.util.NetWorkUtil

/**
 * ImageAdapter 的 ViewHolder 类。
 *
 * @param binding 项目布局的视图绑定。
 */
class MultipleImageViewHolder(val binding: DynamicsImageItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        val params = binding.dynamicsImage.layoutParams
        val uiWidth = UiUtils.getUIWidth(binding.root.context, 70)
        params.apply {
            width = uiWidth / 3 - 10
            height = uiWidth / 3 - 10
        }
        binding.dynamicsImage.layoutParams = params
    }

    /**
     * 使用 Glide 将图片 URL 绑定到 ImageView。
     *
     * @param imageUrl 要加载的图片的 URL。
     */
    fun bind(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            GlideUtils.glide(imageUrl).into(binding.dynamicsImage)
        }
    }

    fun bindByUri(uri: Uri) {
        GlideUtils.glide(uri).into(binding.dynamicsImage)
    }
}

class SingleImageViewHolder(val binding: DynamicSingleImageItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        val params = binding.image.layoutParams
        val uiWidth = UiUtils.getUIWidth(binding.root.context, 70)
        params.apply {
            width = uiWidth - uiWidth / 4
            height = width
        }
        binding.image.layoutParams = params
    }

    fun bind(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            GlideUtils.glide(imageUrl,false).into(binding.image)
        }
    }
}

class SharedImageViewHolder(val binding: DynamicSharedImageItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            GlideUtils.glide(imageUrl,false).into(binding.sharedImage)
        }
    }
}