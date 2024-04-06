package com.migu.android.linkyou.ui.front

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.migu.android.linkyou.databinding.FragmentFrontBinding
import com.migu.android.linkyou.ui.front.tagItem.TabItemCategoriesEnum
import com.migu.android.linkyou.ui.util.TabItemControl
import com.migu.android.linkyou.ui.front.tagItem.adapter.TabItemFragmentStateAdapter
import com.migu.android.linkyou.ui.front.tagItem.model.ChannelData
import com.migu.android.linkyou.ui.util.BarUtils

private const val TAG = "FrontFragment"
private const val TAG_ITEM = "tag_item"

class FrontFragment : Fragment() {


    /**
     * 托管活动所需接口，用于在添加频道时添加主页面
     */
    interface Callbacks {
        fun onClickChannelButton(channelSet: LinkedHashSet<ChannelData>)
    }

    // 使用懒加载委托来获取与该Fragment关联的布局绑定
    private val binding by lazy {
        FragmentFrontBinding.inflate(layoutInflater)
    }

    // 用于管理ViewPager2中的Fragment
    private lateinit var adapter: TabItemFragmentStateAdapter

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        // 偏移状态栏
        BarUtils.offsetStatusBar(binding.root)

        // 从本地存储中获取TabItem的数据列表
        val channelSet = TabItemControl.getTabItemListForSP(requireContext(), TAG_ITEM)

        // 初始化适配器
        adapter = TabItemFragmentStateAdapter(requireActivity(), channelSet)

        // 设置ViewPager2的保存状态为不可用
        binding.mainViewpager.isSaveEnabled = false

        // 如果ViewPager2的适配器为空，则设置适配器
        if (binding.mainViewpager.adapter == null) {
            binding.mainViewpager.adapter = adapter
        }

        // 将TabLayout与ViewPager2关联，并设置选项卡的标题
        TabLayoutMediator(binding.tabLayout, binding.mainViewpager) { tab, position ->
            tab.text = channelSet.elementAt(position).channelName
        }.attach()

        binding.addFocusTopic.setOnClickListener {
            callbacks?.onClickChannelButton(channelSet)
        }

        // 返回根视图
        return binding.root
    }

    companion object {
        // 创建FrontFragment的新实例
        fun newInstance(): FrontFragment {
            return FrontFragment()
        }
    }
}
