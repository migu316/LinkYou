package com.migu.android.linkyou.business

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.migu.android.core.LinkYou
import com.migu.android.core.util.GlobalUtil
import com.migu.android.linkyou.R
import com.migu.android.network.Repository
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.model.base.UserInfo
import com.migu.android.network.util.Event
import com.migu.android.core.util.NetWorkUtil
import kotlinx.coroutines.launch

class ActivitySharedViewModel : ViewModel() {

    /**
     * 获取头像URL
     */
    fun getUserAvatarUrlBySp(): String {
        return NetWorkUtil.replaceHttps(Repository.getUserAvatarUrlBySp())
    }

    // 仅第一次获取该livedata时，发起网络请求
    // 即使多次获取，也不会再发起网络请求
    // 获取用户信息
    val userInfoLiveData = Repository.getUserInfo(LinkYou.objectId)

    // 无论false还是true，都会拉取数据
    private val isRefreshing = MutableLiveData(false)

    // 获取当前用户发布的动态
    val userDynamicsLiveData = isRefreshing.switchMap {
        Repository.getTargetUserDynamics(LinkYou.objectId)
    }

    // 获取当前用户发布的动态的缓存
    val dynamicCache = Repository.getDynamicDetailByDB()

    // 发布状态
    private val _postDynamicStatus = MutableLiveData<Event<Result<Boolean>>>()

    // 提供对外用于观察的发布状态
    val postDynamicStatus: LiveData<Event<Result<Boolean>>>
        get() = _postDynamicStatus

    fun startRefreshing() {
        isRefreshing.value = isRefreshing.value?.let { !it } ?: true
    }

    /**
     * 将从服务器获取到的数据存储到SP文件中
     */
    fun saveUserInfo(userInfo: UserInfo) {
        Repository.saveUserInfoToSp(userInfo)
    }

    /**
     * 将动态数据存储到数据库中
     * 调用该方法需要注意：
     * 需要当服务器返回的数据已经显示到界面后再去删除并添加到数据库
     * 因为：如果先删除，此时后台获取图片的holder获取图片urls可能出现问题
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

    /**
     * 发布动态内容。
     *
     * @param postContent 动态内容
     * @param imageList 图片列表
     */
    fun postDynamic(postContent: String, imageList: List<Uri>) {
        viewModelScope.launch {
            var newPostContent = postContent
            if (postContent.isEmpty()) {
                newPostContent = GlobalUtil.getString(R.string.shared_image_string)
            }
            Repository.postDynamic(newPostContent, imageList).apply {
                _postDynamicStatus.value = Event(this)
            }
        }
    }

}