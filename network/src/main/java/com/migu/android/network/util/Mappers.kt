package com.migu.android.network.util

import cn.leancloud.LCObject
import com.migu.android.core.util.GsonUtils
import com.migu.android.database.model.base.DynamicEntity
import com.migu.android.database.model.base.FileImageEntity
import com.migu.android.database.model.base.UserInfoEntity
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.model.base.FileImage
import com.migu.android.network.model.base.UserInfo
import java.util.concurrent.ConcurrentMap

// 该KT文件用于处理网络Dynamic和本地数据库Dynamic实体之间的映射，因为存在一些差异，以及模块限制不能互相导入模块

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

/*---------------- 用于将SDK中的LCObject对象转换为和REST API通用的对象 -----------------*/
fun LCObject.toUserInfo(): UserInfo {
    val userInfo = UserInfo()
    serverData.apply {
        userInfo.age = get("Age") as Int
        userInfo.briefInfo = get("BriefInfo") as String
        userInfo.city = get("City") as String
        userInfo.gender = get("Gender") as String
        userInfo.name = get("Name") as String
        userInfo.createdAt = get("createdAt") as String
        userInfo.objectId = get("objectId") as String
        userInfo.updatedAt = get("updatedAt") as String
        userInfo.avatar = getLCObject<LCObject>("Avatar").serverData.toFileImage()
        userInfo.background = getLCObject<LCObject>("Background").serverData.toFileImage()
    }

    return userInfo
}

fun ConcurrentMap<String, Any>.toFileImage(): FileImage {
    return FileImage().apply {
        createdAt = get("createdAt") as String
        key = get("key") as String
        mimeType = get("mime_type") as String
        name = get("name") as String
        objectId = get("objectId") as String
        updatedAt = get("updatedAt") as String
        url = get("url") as String
    }
}