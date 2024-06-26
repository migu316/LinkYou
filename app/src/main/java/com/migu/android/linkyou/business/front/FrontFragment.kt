package com.migu.android.linkyou.business.front

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.migu.android.linkyou.BaseFragment
import com.migu.android.linkyou.business.ActivitySharedViewModel
import com.migu.android.linkyou.databinding.FragmentFrontBinding
import com.migu.android.linkyou.business.front.tagItem.TabItemControl
import com.migu.android.linkyou.business.front.tagItem.adapter.TabItemFragmentStateAdapter
import com.migu.android.linkyou.business.front.tagItem.fragment.ChangeChannelFragment
import com.migu.android.linkyou.business.front.tagItem.model.ChannelData
import com.migu.android.core.util.BarUtils

class FrontFragment : BaseFragment() {

    private val binding by lazy {
        FragmentFrontBinding.inflate(layoutInflater)
    }

    private val activitySharedViewModel by activityViewModels<ActivitySharedViewModel>()

    // 用于管理ViewPager2中的Fragment
    private lateinit var adapter: TabItemFragmentStateAdapter

    private lateinit var channelSet: LinkedHashSet<ChannelData>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 返回根视图
        return binding.root
    }

    override fun initialize() {
        // 偏移状态栏
        BarUtils.offsetStatusBar(binding.root)

        // 从本地存储中获取TabItem的数据列表
        channelSet = TabItemControl.getTabItemListForSP(requireContext(), TAG_ITEM)
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

        // 刷新头像
        val avatarUrlBySp = activitySharedViewModel.getUserAvatarUrlBySp()
        Glide.with(this).load(avatarUrlBySp).into(binding.frontPageSmallAvatar)
    }

    override fun initializeListener() {
        // 打开频道管理页面
        binding.addFocusTopic.setOnClickListener {
            val fragment = ChangeChannelFragment.newInstance(channelSet)
            callbacks?.onClickChangeFragment(fragment)
        }

        // 打开发布动态页面
        binding.postDynamic.setOnClickListener {
            val fragment = PostDynamicFragment.newInstance()
            callbacks?.onClickChangeFragment(fragment)
        }

        // 打开搜索页面
        binding.search.setOnClickListener {
            val fragment = SearchFragment.newInstance()
            callbacks?.onClickChangeFragment(fragment)
        }
    }

    companion object {
        private const val TAG_ITEM = "tag_item"

        // 创建FrontFragment的新实例
        fun newInstance(): FrontFragment {
            return FrontFragment()
        }
    }
}
