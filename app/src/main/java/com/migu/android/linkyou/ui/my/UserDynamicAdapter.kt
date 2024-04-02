package com.migu.android.linkyou.ui.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
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
        // 为视图添加标签
        holder.binding.root.tag = post.objectId
        // 当开始绑定时，先将其adapter设置为null
        // 相当于是清除复用holder中的adapter，并且将其设置为GONE，recyclerView将不会提示跳过布局
        holder.binding.includeContent.userDynamicImagesRecyclerView.apply {
            adapter = null
            visibility = View.GONE
        }
        getUrlsHandler.queueGetUrls(holder, post.objectId)
    }

    inner class DynamicViewHolder(val binding: DynamicsNoAvatarItemBinding) :
        ViewHolder(binding.root) {

        fun bind(dynamic: Dynamic) {
            binding.apply {
                includeContent.releaseTimeTextview.text =
                    DateUtil.formatDateToString(dynamic.createdAt)
                includeContent.contentTextview.text = dynamic.postText
                includeInteractive.likesTextview.text = dynamic.likes.toString()
            }
        }

        fun bindImagesAdapter(urls: List<String>, objectId: String) {
            // 传递过来的objectId，如果和当前视图中的tag相等，说明holder尚未被复用，可以将urls设置到adapter中
            // 如果不相等，那么就不进行设置，将会由下一次回调并且tag相等的来进行设置
            if (binding.root.tag == objectId) {
                binding.includeContent.userDynamicImagesRecyclerView.apply {
                    visibility = View.VISIBLE
                    adapter = ImageAdapter(urls)
                    layoutManager = GridLayoutManager(context, 3)
                }
            }
        }
    }
}