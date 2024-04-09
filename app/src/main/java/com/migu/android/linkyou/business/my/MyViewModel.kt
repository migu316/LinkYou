package com.migu.android.linkyou.business.my

import androidx.lifecycle.ViewModel
import com.migu.android.core.Const
import com.migu.android.core.LinkYou
import com.migu.android.core.util.SharedUtil.getSharedPreferencesByNameExecute
import com.migu.android.core.util.SharedUtil.getSharedPreferencesObjByName
import com.migu.android.network.Repository
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.model.base.FileImage
import com.migu.android.network.model.base.UserInfo

class MyViewModel : ViewModel() {

    // 仅第一次获取该livedata时，发起网络请求
    // 即使多次获取，也不会再发起网络请求
    val userInfoLiveData = Repository.getUserInfo(LinkYou.objectId)
    val userDynamicsLiveData = Repository.getTargetUserDynamics(LinkYou.objectId)
    val dynamicCache = Repository.getDynamicDetail()

    /**
     * 将从服务器获取到的数据存储到SP文件中
     */
    fun saveUserInfo(userInfo: UserInfo) {
        LinkYou.context.getSharedPreferencesByNameExecute(Const.UserInfo.USER_INFO_SP_FILE) {
            putInt(Const.UserInfo.AGE, userInfo.age ?: 0)
            putString(Const.UserInfo.BRIEF_INFO, userInfo.briefInfo)
            putString(Const.UserInfo.CITY, userInfo.city)
            putString(Const.UserInfo.GENDER, userInfo.gender)
            putString(Const.UserInfo.NAME, userInfo.name)
            putString(Const.UserInfo.CREATED_AT, userInfo.createdAt)
            putString(Const.UserInfo.OBJECT_ID, userInfo.objectId)
            putString(Const.UserInfo.UPDATED_AT, userInfo.updatedAt)
            putString(Const.UserInfo.AVATAR_FILE_PATH, userInfo.avatar?.url)
            putString(Const.UserInfo.BACKGROUND_FILE_PATH, userInfo.background?.url)
        }
    }

    /**
     * 将动态数据存储到数据库中
     */
    fun saveDynamicsToDB(dynamic: List<Dynamic>) {
        Repository.saveDynamicsDB(dynamic)
    }

    /**
     * 用于主页从本地获取缓存数据
     */
    fun getUserAllInfoBySp(): UserInfo {
        return Repository.getUserAllInfoBySp()
    }
}