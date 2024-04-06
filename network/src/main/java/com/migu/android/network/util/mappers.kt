package com.migu.android.network.util

import com.migu.android.database.model.base.DynamicEntity
import com.migu.android.database.model.base.FileImageEntity
import com.migu.android.database.model.base.UserInfoEntity
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.model.base.FileImage
import com.migu.android.network.model.base.UserInfo

fun Dynamic.toDynamicEntity(): DynamicEntity {
    return DynamicEntity(
        userInfoId?.toUserInfoEntity(),
        likes,
        postText,
        objectId,
        createdAt,
        updatedAt,
        imageCount
    )
}

fun UserInfo.toUserInfoEntity(): UserInfoEntity {
    return UserInfoEntity(
        age,
        briefInfo,
        city,
        gender,
        name,
        createdAt,
        objectId,
        updatedAt,
        avatar?.toFileImageEntity(),
        background?.toFileImageEntity()
    )
}

fun FileImage.toFileImageEntity(): FileImageEntity {
    return FileImageEntity(
        createdAt, key, mimeType, name, objectId, updatedAt, url
    )
}

fun DynamicEntity.toDynamic(): Dynamic {
    return Dynamic(
        userInfoEntityId?.toUserInfo(),
        likes,
        postText,
        objectId,
        createdAt,
        updatedAt,
        imageCount
    )
}

fun UserInfoEntity.toUserInfo(): UserInfo {
    return UserInfo(
        age,
        briefInfo,
        city,
        gender,
        name,
        createdAt,
        objectId,
        updatedAt,
        avatar?.toFileImage(),
        background?.toFileImage()
    )
}

fun FileImageEntity.toFileImage(): FileImage {
    return FileImage(
        createdAt, key, mimeType, name, objectId, updatedAt, url
    )
}