package com.migu.android.linkyou.ui.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.migu.android.core.util.SharedUtil.getSharedPreferencesByNameExecute
import com.migu.android.core.util.SharedUtil.getSharedPreferencesObjByName
import com.migu.android.linkyou.R
import com.migu.android.linkyou.ui.front.tagItem.TabItemCategoriesEnum
import com.migu.android.linkyou.ui.front.tagItem.model.ChannelData

private const val TAB_ITEM_LIST = "tab_item_list"

/**
 * 单例对象，用于管理保存和获取标签项列表到 SharedPreferences 中。
 */
object TabItemControl {

    // 保存标签项列表的 LinkedHashSet，使用 lateinit 延迟初始化
//    private lateinit var map: LinkedHashMap<TabItemCategoriesEnum, String>
    private lateinit var channelSet: LinkedHashSet<ChannelData>

    /**
     * 将标签项列表保存到 SharedPreferences 中。
     *
     * @param context 上下文对象
     * @param name SharedPreferences 文件名
     * @param mode SharedPreferences 模式，默认为 Context.MODE_PRIVATE
     */
    fun saveTabItemListToSP(context: Context, name: String, mode: Int = Context.MODE_PRIVATE) {
        // 将标签项列表转换为 JSON 字符串
        val channelMapJson = Gson().toJson(channelSet)
        // 存储 JSON 字符串到 SharedPreferences 中
        context.getSharedPreferencesByNameExecute(name,mode) {
            putString(TAB_ITEM_LIST, channelMapJson)
        }
    }

    /**
     * 从 SharedPreferences 中获取标签项列表。
     * 如果 SharedPreferences 中不存在该列表，则创建一个默认列表并保存到 SharedPreferences 中。
     *
     * @param context 上下文对象
     * @param name SharedPreferences 文件名
     * @param mode SharedPreferences 模式，默认为 Context.MODE_PRIVATE
     * @return 获取到的标签项列表
     */
    fun getTabItemListForSP(
        context: Context,
        name: String,
        mode: Int = Context.MODE_PRIVATE
    ): LinkedHashSet<ChannelData> {
        // 从 SharedPreferences 中获取标签项列表的 JSON 字符串
        context.getSharedPreferencesObjByName(name, mode).apply {
            val mapListString = getString(TAB_ITEM_LIST, "")
            if (mapListString?.isNotBlank() == true) {
                // 如果 JSON 字符串不为空，则解析为 LinkedHashSet
                val type =
                    object : TypeToken<LinkedHashSet<ChannelData>>() {}.type
                channelSet = Gson().fromJson(mapListString, type)
            }
        }

        // 如果 map 未初始化，则创建一个默认列表并保存到 SharedPreferences 中
        if (!TabItemControl::channelSet.isInitialized) {
            channelSet = linkedSetOf()
            initChannel()
            saveTabItemListToSP(context, name, Context.MODE_PRIVATE)
        }
        // 返回获取到的标签项列表
        return channelSet
    }

    private fun initChannel() {
        channelSet.add(ChannelData(TabItemCategoriesEnum.MAIN_PAGE, "主页", R.drawable.package_2_24px))
        channelSet.add(ChannelData(TabItemCategoriesEnum.FOCUS_PAGE, "好友关注", R.drawable.package_2_24px))
        channelSet.add(ChannelData(TabItemCategoriesEnum.HOT_LIST_PAGE, "热榜", R.drawable.package_2_24px))
        channelSet.add(ChannelData(TabItemCategoriesEnum.TOPIC_PAGE, "话题", R.drawable.package_2_24px))
        channelSet.add(ChannelData(TabItemCategoriesEnum.VIDEO_PAGE, "视频", R.drawable.package_2_24px))
        channelSet.add(ChannelData(TabItemCategoriesEnum.INFORMATION_PAGE, "资讯", R.drawable.package_2_24px))
    }
}
