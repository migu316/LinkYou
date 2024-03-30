package com.migu.android.linkyou.ui.my

import androidx.lifecycle.ViewModel
import com.migu.android.core.Const
import com.migu.android.core.LinkYou
import com.migu.android.core.util.SharedUtil.getSharedPreferencesByNameExecute
import com.migu.android.core.util.SharedUtil.getSharedPreferencesObjByName
import com.migu.android.network.Repository
import com.migu.android.network.model.FileImage
import com.migu.android.network.model.UserInfo

class MyViewModel : ViewModel() {

    // 仅第一次获取该livedata时，发起网络请求
    // 即使多次获取，也不会再发起网络请求
    val userInfoLiveData = Repository.getUserInfo(LinkYou.objectId)


    /**
     * 将从服务器获取到的数据存储到SP文件中
     */
    fun saveUserInfo(userInfo: UserInfo) {
        LinkYou.context.getSharedPreferencesByNameExecute(Const.UserInfo.USER_INFO_SP_FILE) {
            putInt(Const.UserInfo.AGE, userInfo.age)
            putString(Const.UserInfo.BRIEF_INFO, userInfo.briefInfo)
            putString(Const.UserInfo.CITY, userInfo.city)
            putString(Const.UserInfo.GENDER, userInfo.gender)
            putString(Const.UserInfo.NAME, userInfo.name)
            putString(Const.UserInfo.CREATED_AT, userInfo.createdAt)
            putString(Const.UserInfo.OBJECT_ID, userInfo.objectId)
            putString(Const.UserInfo.UPDATED_AT, userInfo.updatedAt)
            putString(Const.UserInfo.AVATAR_FILE_PATH, userInfo.avatar.url)
            putString(Const.UserInfo.BACKGROUND_FILE_PATH, userInfo.background.url)
        }
    }

    /**
     * 用于主页从本地获取缓存数据
     */
    fun getUserInfoBySp(): UserInfo {
        val sharedPreferences =
            LinkYou.context.getSharedPreferencesObjByName(Const.UserInfo.USER_INFO_SP_FILE)
        val age = sharedPreferences.getInt(Const.UserInfo.AGE, 0)
        val briefInfo = sharedPreferences.getString(Const.UserInfo.BRIEF_INFO, "")
        val city = sharedPreferences.getString(Const.UserInfo.CITY, "")
        val gender = sharedPreferences.getString(Const.UserInfo.GENDER, "")
        val name = sharedPreferences.getString(Const.UserInfo.NAME, "")
        val createdAt = sharedPreferences.getString(Const.UserInfo.CREATED_AT, "")
        val objectId = sharedPreferences.getString(Const.UserInfo.OBJECT_ID, "")
        val updatedAt = sharedPreferences.getString(Const.UserInfo.UPDATED_AT, "")
        val avatarUrl = sharedPreferences.getString(Const.UserInfo.AVATAR_FILE_PATH, "")
        val backgroundUrl =
            sharedPreferences.getString(Const.UserInfo.BACKGROUND_FILE_PATH, "")
        return UserInfo(
            age,
            briefInfo!!,
            city!!,
            gender!!,
            name!!,
            createdAt!!,
            objectId!!,
            updatedAt!!,
            FileImage(url = avatarUrl),
            FileImage(url = backgroundUrl)
        )
    }
}