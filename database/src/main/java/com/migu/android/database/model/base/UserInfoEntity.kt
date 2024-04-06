package com.migu.android.database.model.base

/**
 * 用户资料的基类
 */
data class UserInfoEntity(
    val age: Int? = 0,
    val briefInfo: String?="",
    val city: String?="",
    val gender: String?="",
    val name: String?="",
    val createdAt: String?="",
    val objectId: String?="",
    val updatedAt: String?="",
    val avatar: FileImageEntity?=null,
    val background: FileImageEntity? = null
)
