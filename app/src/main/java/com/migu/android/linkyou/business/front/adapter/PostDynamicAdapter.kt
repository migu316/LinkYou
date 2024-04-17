package com.migu.android.linkyou.business.front.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.showToast
import com.migu.android.linkyou.R
import com.migu.android.linkyou.business.dynamic.ImageViewHolder
import com.migu.android.linkyou.databinding.DynamicsImageItemBinding

/**
 * 动态发布适配器，用于显示动态中的图片。
 *
 * @param onItemClick 图片项点击事件的回调函数。
 */
class PostDynamicAdapter(
    private val onItemClick: () -> Unit,
    private val onChangeImagesCallBack: () -> Unit
) :
    Adapter<ImageViewHolder>() {

    // 添加图片的基本 Uri
    private val baseAddUri =
        Uri.parse("android.resource://com.migu.android.linkyou/" + R.drawable.add_20px)

    // 图片列表，包括添加图片的 Uri
    val imageList = mutableListOf<Uri>().apply {
        add(baseAddUri)
    }

    private lateinit var context: Context

    /**
     * 创建 ViewHolder。
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        context = parent.context
        val binding = DynamicsImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ImageViewHolder(binding)
    }

    /**
     * 获取图片项数量。
     */
    override fun getItemCount(): Int {
        return if (imageList.size < 9) {
            imageList.size
        } else {
            9
        }
    }

    /**
     * 绑定 ViewHolder。
     */
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val binding = holder.binding
        holder.bindByUri(imageList[position])
        if (position == itemCount - 1 && imageList.size <= 9) {
            // 在总数(实际集合中的数量)小于等于9个，并且position是最后一个位置（最大为9），隐藏最后一个的删除按钮
            binding.delete.visibility = View.GONE
            binding.root.setOnClickListener {
                // 执行回调:打开图库
                onItemClick()
            }
        } else {
            binding.delete.apply {
                visibility = View.VISIBLE
                setOnClickListener {
                    delete(position)
                }
            }
        }
    }

    /**
     * 删除指定位置的图片。
     *
     * @param position 要删除的图片位置。
     */
    private fun delete(position: Int) {
        // 避免快速点击删除，误删添加按钮
        if (imageList.size == 1) {
            return
        }
        imageList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, imageList.size - position)
        onChangeImagesCallBack()
    }

    /**
     * 添加图片。
     *
     * @param uri 要添加的图片的 Uri。
     */
    fun addImage(uri: Uri) {
        if (imageList.size > 9) {
            showToast(GlobalUtil.getString(R.string.maximum_image_limit))
            return
        }
        // 添加到倒数第二个,也就是添加图标的前面
        imageList.add(imageList.size - 1, uri)
        if (imageList.size <= 9) {
            // 通知倒数2个位置插入了数据
            notifyItemInserted(imageList.size - 2)
        } else {
            // 处理最后一个加入进来的
            notifyItemRangeChanged(imageList.size - 2, 1)
        }
        onChangeImagesCallBack()
    }
}
