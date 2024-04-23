package com.migu.android.linkyou.business.my

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.util.TypedValueCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.migu.android.core.LinkYou
import com.migu.android.core.util.DateUtil
import com.migu.android.core.util.UiUtils
import com.migu.android.core.util.logInfo
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.business.dynamic.RVDynamicBaseViewHolder
import com.migu.android.linkyou.business.dynamic.SharedDynamicFragment
import com.migu.android.linkyou.databinding.DynamicsNoAvatarItemBinding
import com.migu.android.linkyou.business.dynamic.adapter.ImageAdapter
import com.migu.android.network.GetUrlsHandler
import com.migu.android.network.model.base.Dynamic

/**
 * 用户页的适配器，和主页的略有差异
 */
class UserDynamicAdapter(
    private val getUrlsHandler: GetUrlsHandler<DynamicBaseViewHolder>,
    val callbacks: BaseFragment.Callbacks?
) :
    ListAdapter<Dynamic, UserDynamicAdapter.DynamicBaseViewHolder>(diffUtil) {
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DynamicBaseViewHolder {
        val binding =
            DynamicsNoAvatarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DynamicBaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DynamicBaseViewHolder, position: Int) {
        val dynamic = currentList[position]
        holder.bind(dynamic)
        // 为视图添加标签
        holder.binding.root.tag = dynamic.objectId
        // 当开始绑定时，先将其adapter设置为null
        // 相当于是清除复用holder中的adapter，并且将其设置为GONE，recyclerView将不会提示跳过布局
        holder.binding.includeContent.userDynamicImagesRecyclerView.apply {
//            adapter = if (dynamic.imageCount == 0) {
//                // 如果动态携带的图片数量为0，隐藏RecyclerView，并给adapter设置为null
//                visibility = View.GONE
//                null
//            } else {
//                // 否则设置显示，并传入无效数据用于显示占位图，
//                // 避免后面holder回调方法时，页面view重新测量高度导致视图拉扯卡顿
//                visibility = View.VISIBLE
//                ImageAdapter(List(dynamic.imageCount ?: 0) { "" }, null)
//            }
//            layoutManager = GridLayoutManager(context, 3)
            // 先隐藏，图片数量不为0再显示
            visibility = View.GONE
            if (dynamic.imageCount != 0) {
                // 否则设置显示，并传入无效数据用于显示占位图，
                // 避免后面holder回调方法时，页面view重新测量高度导致视图拉扯卡顿
                visibility = View.VISIBLE
                layoutManager = GridLayoutManager(context, 3)
                adapter = ImageAdapter(List(dynamic.imageCount ?: 0) { "" }, null)
            }
        }
        // 提交动态图片拉取请求
        getUrlsHandler.queueGetUrls(holder, dynamic)
    }

    inner class DynamicBaseViewHolder(val binding: DynamicsNoAvatarItemBinding) :
        RVDynamicBaseViewHolder(
            binding.root,
            binding.includeContent.userDynamicImagesRecyclerView,
            callbacks
        ) {

        init {
            binding.includeInteractiveData.sharedDynamic.setOnClickListener {
                callbacks?.onClickChangeFragment(SharedDynamicFragment.newInstance(mDynamic))
            }
        }

        fun bind(data: Dynamic) {
            // 必须赋值给mDynamic，以便查看详情
            mDynamic = data
            binding.apply {
                includeContent.releaseTimeTextview.text =
                    data.createdAt?.let { DateUtil.formatDateToString(it) }
                includeContent.contentTextview.text = data.postText
                includeInteractiveData.likesTextview.text = data.likes.toString()
            }
        }

        fun bindImagesAdapter(urls: List<String>, objectId: String) {
            // 传递过来的objectId，如果和当前视图中的tag相等，说明holder尚未被复用，可以将urls设置到adapter中
            // 如果不相等，那么就不进行设置，将会由下一次回调并且tag相等的来进行设置
            if (binding.root.tag == objectId) {
                binding.includeContent.userDynamicImagesRecyclerView.apply {
                    mDynamic.imageUrls = urls
                    // 无数据或动态本身无图片时隐藏RV
                    if (urls.isEmpty() || mDynamic.imageCount == 0) {
                        visibility = View.GONE
                        return
                    }
                    visibility = View.VISIBLE
//                    if (adapter != null) {
//                        // 如果本身存在adapter，直接覆盖内部数据即可，并且通知数据发生改变
//                        val imageAdapter = adapter as ImageAdapter
//                        imageAdapter.overwriteData(urls)
//                    } else {
//                        adapter = ImageAdapter(urls, callbacks)
//                    }
//                    layoutManager = GridLayoutManager(context, 3)

                    if (urls.size == 1) {
                        adapter = ImageAdapter(urls, callbacks, isSingle = true)
                        layoutManager = LinearLayoutManager(context)
                    } else {
                        adapter = ImageAdapter(urls, callbacks)
                        layoutManager = GridLayoutManager(context, 3)
                    }
                }
            }
        }
    }
}