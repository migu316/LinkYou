package com.migu.android.linkyou.ui.front.tagItem.model

import android.os.Parcelable
import com.migu.android.linkyou.ui.front.tagItem.TabItemCategoriesEnum
import kotlinx.parcelize.Parcelize


@Parcelize
data class ChannelData(
    val type: TabItemCategoriesEnum,
    val channelName: String,
    val channelIcon: Int
) : Parcelable