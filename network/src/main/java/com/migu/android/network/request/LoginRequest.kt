package com.migu.android.network.request

import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.showToastOnUiThread
import com.migu.android.network.R
import com.migu.android.network.ServiceCreator
import com.migu.android.network.api.UserServiceInterface
import com.migu.android.network.model.LoginUserData
import com.migu.android.network.model.LoginUserRequestBody
import retrofit2.HttpException
import retrofit2.await

private const val TAG = "LoginRequest"

object LoginRequest {
    /**
     * 执行登录用户请求的函数。
     *
     * @param loginUserRequestBody 登录用户请求的请求体数据。
     * @return 登录用户的数据。
     * @throws Exception 如果请求失败，则抛出异常。
     */
    suspend fun loginUserRequest(loginUserRequestBody: LoginUserRequestBody): LoginUserData {
        try {
            // 创建用户服务接口实例并调用登录接口
            return ServiceCreator.create<UserServiceInterface>()
                .getLoginUserData(requestBody = loginUserRequestBody).await()
        } catch (e: Exception) {
            // 捕获异常并检查错误码
            val code = (e as? HttpException)?.code()
            when (code) {
                // 如果错误码为 400，显示帐户或密码错误提示
                400 -> showToastOnUiThread(GlobalUtil.getString(R.string.check_your_account_or_password))
                // 其他错误码，显示网络错误提示
                else -> showToastOnUiThread(GlobalUtil.getString(R.string.network_error))
            }
            // 将异常继续抛出
            throw e
        }
    }

}