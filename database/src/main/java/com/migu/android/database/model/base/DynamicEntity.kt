package com.migu.android.database.model.base

import androidx.room.Entity
import java.util.Date

/**
 * 动态的基类
 */
data class DynamicEntity(
    val userInfoEntityId: UserInfoEntity? = null,
    val likes: Int? = 0,
    val postText: String? = "",
    val objectId: String,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val imageCount: Int? = 0
)