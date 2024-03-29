package com.migu.android.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.showToastOnUiThread
import com.migu.android.network.model.LeanCloudPointerBaseModel
import com.migu.android.network.model.LoginUserRequestBody
import com.migu.android.network.model.LoginUserResponse
import com.migu.android.network.model.UserResultResponse
import com.migu.android.network.request.LinkYouNetwork
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext

private const val TAG = "LoginViewModel"

/**
 * 仓库层的统一封装入口
 */
object Repository {

    suspend fun loginUser(loginUserRequestBody: LoginUserRequestBody): LoginUserResponse {
        try {
            // 创建用户服务接口实例并调用登录接口
            return LinkYouNetwork.loginUserRequest(loginUserRequestBody)
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

    suspend fun getUserInfo(leanCloudPointerBaseModel: LeanCloudPointerBaseModel): UserResultResponse? {
        try {
            return LinkYouNetwork.getUserInfoRequest(leanCloudPointerBaseModel)
        } catch (e:Exception) {
            e.printStackTrace()
            return null
        }
    }


    private fun <T> fire(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend () -> Result<T>
    ): LiveData<Result<T>> {
        return liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }
    }
}