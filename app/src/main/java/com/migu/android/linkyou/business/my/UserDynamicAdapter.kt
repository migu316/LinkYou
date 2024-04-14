package com.migu.android.linkyou.business.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.migu.android.core.util.DateUtil
import com.migu.android.linkyou.databinding.DynamicsNoAvatarItemBinding
import com.migu.android.linkyou.business.dynamic.ImageAdapter
import com.migu.android.network.GetUrlsHandler
import com.migu.android.network.model.base.Dynamic

class UserDynamicAdapter(private val getUrlsHandler: GetUrlsHandler<DynamicViewHolder>) :
    ListAdapter<Dynamic, UserDynamicAdapter.DynamicViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Dynamic>() {
            override fun areItemsTheSame(oldItem: Dynamic, newItem: Dynamic): Boolean {
                return oldItem.objectId == newItem.objectId
            }

            override fun areContentsTheSame(oldItem: Dynamic, newItem: Dynamic): Boolean {
                return oldItem.objectId == newItem.objectId
            }
        }
    }

    // 本来用来缓解bug导致的问题，现在没什么卵用了，留着吧
    private var copyDynamics = if (currentList.size > 10) 10 else currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicViewHolder {
        val binding =
            DynamicsNoAvatarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DynamicViewHolder(binding)
    }

//    override fun getItemCount() = dynamics.size

    override fun onBindViewHolder(holder: DynamicViewHolder, position: Int) {
        val dynamic = currentList[position]
        holder.bind(dynamic)
        // 为视图添加标签
        holder.binding.root.tag = dynamic.objectId
        // 当开始绑定时，先将其adapter设置为null
        // 相当于是清除复用holder中的adapter，并且将其设置为GONE，recyclerView将不会提示跳过布局
        holder.binding.includeContent.userDynamicImagesRecyclerView.apply {
            adapter = if (dynamic.imageCount == 0) {
                // 如果动态携带的图片数量为0，隐藏RecyclerView，并给adapter设置为null
                visibility = View.GONE
                null
            } else {
                // 否则设置显示，并传入无效数据用于显示占位图，
                // 避免后面holder回调方法时，页面view重新测量高度导致视图拉扯卡顿
                visibility = View.VISIBLE
                ImageAdapter(List(dynamic.imageCount ?: 0) { "" })
            }
            layoutManager = GridLayoutManager(context, 3)
        }
        // 提交动态图片拉取请求
        getUrlsHandler.queueGetUrls(holder, dynamic)
    }

    inner class DynamicViewHolder(val binding: DynamicsNoAvatarItemBinding) :
        ViewHolder(binding.root) {

        private lateinit var dynamic: Dynamic

        fun bind(data: Dynamic) {
            dynamic = data
            binding.apply {
                includeContent.releaseTimeTextview.text =
                    data.createdAt?.let { DateUtil.formatDateToString(it) }
                includeContent.contentTextview.text = data.postText
                includeInteractive.likesTextview.text = data.likes.toString()
            }
        }

        fun bindImagesAdapter(urls: List<String>, objectId: String) {
            // 传递过来的objectId，如果和当前视图中的tag相等，说明holder尚未被复用，可以将urls设置到adapter中
            // 如果不相等，那么就不进行设置，将会由下一次回调并且tag相等的来进行设置
            if (binding.root.tag == objectId) {
                binding.includeContent.userDynamicImagesRecyclerView.apply {

                    if (urls.isEmpty() && dynamic.imageCount == 0) {
                        // 如果holder传递过来的url集合为空，并且动态数据附带的图片数量也是0，那么可以隐藏RecyclerView
                        visibility = View.GONE
                        return
                    } else if (urls.isEmpty()) {
                        // 如果holder传递过来的url集合为空，但是动态本身覆盖的图片数量不为0，
                        // 说明holder部分拉取数据失败，需要设置本身数量的图片占位符
                        // 但是注意：在前面调用onBindViewHolder时，
                        // 已经给adapter设置过了用于占位的图片集合了，因此这里只需要退出即可

                        // 新增：如果上传图片中断网导致图片未上传，那么就需要隐藏RV，虽然和前面的解释有一些冲突，
                        // 那也没办法，将就一下，毕竟很少会出现动态纪录的图片数量不为0，但是urls又为空的情况
                        visibility = View.GONE
                        return
                    }
                    visibility = View.VISIBLE
                    if (adapter != null) {
                        // 如果本身存在adapter，直接覆盖内部数据即可，并且通知数据发生改变
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
        return if (proportion > 0.7) true else false
    }

    /**
     * 用于增加数据
     */
    fun addData() {
        // 获取增加数据前的大小
        val oldSize = itemCount
        // 如果当前数据和源数据长度相等，说明数据已经全部添加完毕了，直接退出
        if (currentList.size == itemCount) {
            return
        } else if (currentList.size - copyDynamics > 10) {
            // 如果当前数据长度和源数据长度差值大于5，那就增加5个
            copyDynamics += 10
        } else {
            // 小于5，就全部添加进去
            copyDynamics = currentList.size
        }
        // 通知数据发生改变
        notifyItemRangeChanged(oldSize, itemCount)
    }
}