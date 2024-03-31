package com.migu.android.linkyou.ui.my

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.migu.android.core.util.DateUtil
import com.migu.android.linkyou.databinding.DynamicsNoAvatarItemBinding
import com.migu.android.network.model.base.Post
import java.util.Date

class UserDynamicAdapter(val posts: List<Post>) : Adapter<UserDynamicAdapter.DynamicViewHolder>() {
    inner class DynamicViewHolder(private val binding: DynamicsNoAvatarItemBinding) :
        ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                includeContent.releaseTimeTextview.text = DateUtil.formatDateToString(post.createdAt)
                includeContent.contentTextview.text = post.postText
                includeInteractive.likesTextview.text = post.likes.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val binding =
            DynamicsNoAvatarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DynamicViewHolder(binding)
    }

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }
}