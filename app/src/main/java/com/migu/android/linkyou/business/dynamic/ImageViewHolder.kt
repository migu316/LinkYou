package com.migu.android.linkyou.business.dynamic

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.migu.android.core.util.UiUtils
import com.migu.android.core.util.logInfo
import com.migu.android.linkyou.R
import com.migu.android.linkyou.databinding.DynamicSharedImageItemBinding
import com.migu.android.linkyou.databinding.DynamicSingleImageItemBinding
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
            glide.load(NetWorkUtil.replaceHttps(imageUrl)).into(binding.dynamicsImage)
        }
    }

    fun bindByUri(uri: Uri) {
        glide.load(uri).override(200, 200).into(binding.dynamicsImage)
    }
}

class SingleImageViewHolder(val binding: DynamicSingleImageItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        val params = binding.image.layoutParams
        val uiWidth = UiUtils.getUIWidth(binding.root.context, 70)
        params.apply {
            width = uiWidth - uiWidth / 4
        }
        binding.image.layoutParams = params
    }

    private val glide: RequestManager = Glide.with(binding.root)
    fun bind(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            glide.load(NetWorkUtil.replaceHttps(imageUrl)).into(binding.image)
        }
    }
}

class SharedImageViewHolder(val binding: DynamicSharedImageItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    private val glide: RequestManager = Glide.with(binding.root)
    fun bind(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            glide.load(NetWorkUtil.replaceHttps(imageUrl)).into(binding.sharedImage)
        }
    }
}