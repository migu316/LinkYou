package com.migu.android.linkyou.ui.my

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.migu.android.core.util.DateUtil
import com.migu.android.core.util.logInfo
import com.migu.android.linkyou.databinding.DynamicsNoAvatarItemBinding
import com.migu.android.linkyou.ui.dynamic.ImageAdapter
import com.migu.android.network.GetUrlsHandler
import com.migu.android.network.model.base.Dynamic

class UserDynamicAdapter(
    private val dynamics: List<Dynamic>,
    private val getUrlsHandler: GetUrlsHandler<DynamicViewHolder>
) :
    Adapter<UserDynamicAdapter.DynamicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val binding =
            DynamicsNoAvatarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DynamicViewHolder(binding)
    }

    override fun getItemCount() = dynamics.size

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val post = dynamics[position]
        holder.bind(post)
        logInfo(position.toString())
        getUrlsHandler.queueGetUrls(holder, post.objectId)
    }

    inner class DynamicViewHolder(private val binding: DynamicsNoAvatarItemBinding) :
        ViewHolder(binding.root) {

        fun bind(dynamic: Dynamic) {
            binding.apply {
                includeContent.releaseTimeTextview.text =
                    DateUtil.formatDateToString(dynamic.createdAt)
                includeContent.contentTextview.text = dynamic.postText
                includeInteractive.likesTextview.text = dynamic.likes.toString()
            }
        }

        fun bindImagesAdapter(urls: List<String>) {
            if (urls.isEmpty()) {
                return
            }
            binding.includeContent.userDynamicImagesRecyclerView.apply {
                if (adapter == null) {
                    adapter = ImageAdapter(urls)
                    layoutManager = GridLayoutManager(context, 3)
                }
            }
        }
    }
}