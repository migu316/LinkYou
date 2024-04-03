package com.migu.android.linkyou.ui.my

import android.view.LayoutInflater
import android.view.View
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
    val dynamics: List<Dynamic>,
    private val getUrlsHandler: GetUrlsHandler<DynamicViewHolder>
) :
    Adapter<UserDynamicAdapter.DynamicViewHolder>() {

    private var copyDynamics = if (dynamics.size > 5) 5 else dynamics.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val binding =
            DynamicsNoAvatarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DynamicViewHolder(binding)
    }

    override fun getItemCount() = copyDynamics

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {


        val dynamic = dynamics[position]
        holder.bind(dynamic)
        // 为视图添加标签
        holder.binding.root.tag = dynamic.objectId
        // 当开始绑定时，先将其adapter设置为null
        // 相当于是清除复用holder中的adapter，并且将其设置为GONE，recyclerView将不会提示跳过布局
        holder.binding.includeContent.userDynamicImagesRecyclerView.apply {
            adapter = if (dynamic.imageCount == 0) {
                null
            } else {
                ImageAdapter(List(dynamic.imageCount) { "" })
            }
            visibility = View.GONE
        }
        logInfo(position.toString())
        getUrlsHandler.queueGetUrls(holder, dynamic.objectId)
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
                    if (adapter!= null) {
                        val imageAdapter = adapter as ImageAdapter
                        imageAdapter.overwriteData(urls)
                    } else {
                        adapter = ImageAdapter(urls)
                    }
                    layoutManager = GridLayoutManager(context, 3)
                }
            }
        }
    }

    /**
     * 计算当前position是否即将到达末尾，如果到达末尾，将发送回调调用数据获取方法并添加到当前adapter中
     */
    fun calculate(position: Int): Boolean {
        val proportion = position.toFloat() / itemCount.toFloat()
        return if (proportion > 0.8) true else false
    }

    /**
     * 用于增加数据
     */
    fun addData() {
        // 获取增加数据前的大小
        val oldSize = itemCount
        // 如果当前数据和源数据长度相等，说明数据已经全部添加完毕了，直接退出
        if (dynamics.size == itemCount) {
            return
        } else if (dynamics.size - copyDynamics > 5) {
            // 如果当前数据长度和源数据长度差值大于5，那就增加5个
            copyDynamics += 5
        } else {
            // 小于5，就全部添加进去
            copyDynamics = dynamics.size
        }
        // 通知数据发生改变
        notifyItemRangeChanged(oldSize, itemCount)
    }
}