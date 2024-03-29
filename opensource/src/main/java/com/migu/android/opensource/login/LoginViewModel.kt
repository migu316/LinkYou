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
import com.migu.android.network.model.LoginUserRequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "LoginViewModel"

class LoginViewModel : ViewModel() {

    // MutableLiveData用于在登录过程中更新UI状态
    private val _isLogin: MutableLiveData<LoginEvent<Pair<Boolean, Boolean>>> = MutableLiveData()
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
                // 通知UI登录成功
                _isLogin.postValue(LoginEvent(true to true))
            } catch (e: Exception) {
                // 登录失败，通知UI，并打印异常信息
                _isLogin.postValue(LoginEvent(false to false))
                e.printStackTrace()
            }
        }
    }

    /**
     * 保存认证数据
     * @param loginUserResponse 登录用户的认证数据
     */
    private fun saveAuthData(loginUserResponse: LoginUserResponse) {
        // 保存用户名
        SharedUtil.save(
            Const.Auth.LOGIN_STATE_INFO_SHARED,
            Const.Auth.USER_NAME,
            loginUserResponse.username
        )
        // 保存邮箱
        SharedUtil.save(
            Const.Auth.LOGIN_STATE_INFO_SHARED,
            Const.Auth.EMAIL,
            loginUserResponse.email
        )
        // 保存会话令牌
        SharedUtil.save(
            Const.Auth.LOGIN_STATE_INFO_SHARED,
            Const.Auth.SESSION_TOKEN,
            loginUserResponse.sessionToken
        )
        // 保存创建时间
        SharedUtil.save(
            Const.Auth.LOGIN_STATE_INFO_SHARED,
            Const.Auth.CREATE_AT,
            loginUserResponse.createdAt
        )
        SharedUtil.save(
            Const.Auth.LOGIN_STATE_INFO_SHARED,
            Const.Auth.OBJECT_ID,
            loginUserResponse.objectId
        )
        // 刷新登录状态
        LinkYou.refreshLoginState()
    }
}
