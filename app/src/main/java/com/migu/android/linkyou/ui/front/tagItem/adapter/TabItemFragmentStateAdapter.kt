package com.migu.android.linkyou.ui.front.tagItem.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.migu.android.linkyou.ui.front.tagItem.TabItemCategoriesEnum
import com.migu.android.linkyou.ui.front.tagItem.fragment.FocusPageFragment
import com.migu.android.linkyou.ui.front.tagItem.fragment.MainPageFragment

private const val TAG = "TabItemFragmentStateAdapter"
class TabItemFragmentStateAdapter(
    activity: FragmentActivity,
    private val itemMap: LinkedHashMap<TabItemCategoriesEnum, String>
) :
    FragmentStateAdapter(activity) {
    override fun getItemCount() = itemMap.size

    override fun createFragment(position: Int): Fragment {

        // 根据position取出对应的enum，再根据enum去创建fragment
        val tabItemCategoriesEnum = itemMap.entries.elementAt(position).key
        return when (tabItemCategoriesEnum) {
            TabItemCategoriesEnum.MAIN_PAGE -> MainPageFragment()
            TabItemCategoriesEnum.FOCUS_PAGE -> FocusPageFragment()
            else -> Fragment()
        }
    }
}