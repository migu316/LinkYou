package com.migu.android.linkyou.business.front.tagItem.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.migu.android.core.TabItemCategoriesEnum
import com.migu.android.linkyou.business.front.tagItem.fragment.FocusPageFragment
import com.migu.android.linkyou.business.front.tagItem.fragment.MainPageFragment
import com.migu.android.linkyou.business.front.tagItem.model.ChannelData

private const val TAG = "TabItemFragmentStateAdapter"

/**
 * 用于在与选项卡项目相关的ViewPager2中管理片段的适配器。
 *
 * @param activity 承载此适配器的 FragmentActivity。
 * @param channelSet 代表选项卡项目的 ChannelData 对象的集合。
 */
class TabItemFragmentStateAdapter(
    activity: FragmentActivity,
    private val channelSet: LinkedHashSet<ChannelData>
) :
    FragmentStateAdapter(activity) {

    /**
     * 返回适配器中的频道总数。
     */
    override fun getItemCount() = channelSet.size

    /**
     * 为给定位置创建一个新的片段。
     *
     * @param position 数据集中的项目位置。
     * @return 与指定位置处的项目相对应的 Fragment。
     */
    override fun createFragment(position: Int): Fragment {

        // 根据位置检索相应的枚举，
        // 然后根据枚举创建一个片段。
        val tabItemCategoriesEnum = channelSet.elementAt(position).type
        return when (tabItemCategoriesEnum) {
            TabItemCategoriesEnum.MAIN_PAGE -> MainPageFragment()
            TabItemCategoriesEnum.FOCUS_PAGE -> FocusPageFragment()
            else -> Fragment()
        }
    }
}
