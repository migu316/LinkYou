package com.migu.android.linkyou.ui.dynamic

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.migu.android.linkyou.databinding.DynamicsImageItemBinding
import com.migu.android.network.util.NetWorkUtil

class ImageAdapter(private val urls: List<String>) : Adapter<ImageAdapter.ImageViewHolder>() {

    private lateinit var context: Context

    inner class ImageViewHolder(val binding: DynamicsImageItemBinding) : ViewHolder(binding.root) {
        fun bind(imageUrl: String) {
            Glide.with(context).load(NetWorkUtil.replaceHttps(imageUrl)).into(binding.dynamicsImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        val binding = DynamicsImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount() = urls.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = urls[position]
        url.let { holder.bind(it) }
    }
}