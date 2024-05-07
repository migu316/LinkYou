package com.migu.android.opensource.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.migu.android.core.Const
import com.migu.android.core.LinkYou
import com.migu.android.core.util.SharedUtil
import com.migu.android.network.Repository
import com.migu.android.network.model.LoginUserResponse
import com.migu.android.network.model.base.LoginUserRequestBody
import com.migu.android.network.request.LeanCloudSDKRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginViewModel : ViewModel() {

    // MutableLiveData用于在登录过程中更新UI状态
    private val _isLogin: MutableLiveData<LoginEvent<Pair<Boolean, Boolean>>> = MutableLiveData()

    // 这里的Pair为<界面是否显示，登录是否成功>
    val isLogin: LiveData<LoginEvent<Pair<Boolean, Boolean>>>
        get() = _isLogin

    /**
     * 发起用户登录请求
     * @param username 用户名
     * @param password 密码
     */
    fun loginUserRequest(username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 通知UI正在进行登录操作
                _isLogin.postValue(LoginEvent(true to false))
                // 执行登录请求
                val loginUserData =
                    Repository.loginUser(LoginUserRequestBody(username, password))
                // 登录成功，保存认证数据
                saveAuthData(loginUserData)

                // 在SDK中登录，后期可以修改前面的登录，统一修改为SDK登录
                loginSDK(username, password)

                // 通知UI登录成功
                _isLogin.postValue(LoginEvent(true to true))
            } catch (e: Exception) {
                // 登录失败，通知UI，并打印异常信息
                _isLogin.postValue(LoginEvent(false to false))
                e.printStackTrace()
            }
        }
    }

    private suspend fun loginSDK(username: String, password: String) {
        LeanCloudSDKRequest.loginSDK(username, password)
    }

    /**
     * 保存认证数据
     * @param loginUserResponse 登录用户的认证数据
     */
    private fun saveAuthData(loginUserResponse: LoginUserResponse) {
        Repository.saveAuthData(loginUserResponse)
    }
}
