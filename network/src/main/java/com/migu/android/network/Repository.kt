package com.migu.android.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.showToastOnUiThread
import com.migu.android.network.model.base.LoginUserRequestBody
import com.migu.android.network.model.LoginUserResponse
import com.migu.android.network.model.DynamicImageResponse
import com.migu.android.network.model.TargetUserDynamicsResponse
import com.migu.android.network.model.UserResultResponse
import com.migu.android.network.request.LinkYouNetwork
import com.migu.android.network.util.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext

private const val TAG = "LoginViewModel"

/**
 * 仓库层的统一封装入口
 */
object Repository {

    /**
     * 登录用户的挂起函数，通过网络请求登录用户并返回登录响应对象。
     *
     * @param loginUserRequestBody 包含用户登录信息的请求体
     * @return 登录响应对象
     * @throws HttpException 如果网络请求失败，则会抛出 HttpException 异常
     */
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


    /**
     * 获取用户信息的方法，通过网络请求获取用户信息并返回 LiveData<Result<UserResultResponse>> 对象。
     *
     * @param objectId 用户对象的标识符
     * @return 包含用户信息的 LiveData 对象，结果为成功时返回包含用户信息的 Result 对象，结果为失败时返回包含异常信息的 Result 对象
     */
    fun getUserInfo(objectId: String): LiveData<Result<UserResultResponse>> {
        return fire(Dispatchers.IO) {
            val userResultResponse = LinkYouNetwork.getUserInfoRequest(objectId)
            if (userResultResponse.results.isNotEmpty()) {
                Result.success(userResultResponse)
            } else {
                // 如果用户信息为空，则返回一个包含异常信息的失败 Result 对象
                Result.failure(RuntimeException(GlobalUtil.getString(R.string.response_data_is_empty)))
            }
        }
    }

    /**
     * 获取目标用户发布的帖子信息。
     *
     * @param objectId 目标用户的唯一标识符
     * @return LiveData 包装的帖子信息响应结果
     */
    fun getTargetUserDynamics(objectId: String): LiveData<Result<TargetUserDynamicsResponse>> {
        return fire(Dispatchers.IO) {
            // 添加延迟以防止数据过大导致网络拥塞，后期尝试重构为请求重试
            delay(200)
            // 发起网络请求获取目标用户发布的帖子数据
            val userDynamicsResponse = LinkYouNetwork.getUserDynamicsRequest(objectId)
            // 如果响应结果不为空，则返回成功的 Result 包装
            if (userDynamicsResponse.results.isNotEmpty()) {
                Result.success(userDynamicsResponse)
            } else {
                // 如果响应数据为空，则返回包含异常信息的失败 Result 包装
                Result.failure(RuntimeException(GlobalUtil.getString(R.string.response_data_is_empty)))
            }
        }
    }

    fun getDynamicImages(objectId: String): List<String> {
//        return fire(Dispatchers.IO) {
//            val dynamicImageResponse = LinkYouNetwork.getDynamicImagesRequest(objectId)
//            if (dynamicImageResponse.results.isNotEmpty()) {
//                Result.success(dynamicImageResponse)
//            } else {
//                // 结果数据为空时，不一定是错误，可能本身动态就没有图片
//                Result.failure(RuntimeException(GlobalUtil.getString(R.string.response_data_is_empty)))
//            }
//        }

        return runBlocking {
//            delay(10000)
            try {
                val dynamicImageResponse = LinkYouNetwork.getDynamicImagesRequest(objectId)
                return@runBlocking dynamicImageResponse.results.map {
                    it.image.url!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return@runBlocking listOf()
            }
        }
    }


    /**
     * 构建一个通用的用于返回结果或者错误信息的泛型函数
     * 需要返回Result对象，那么就是Result<T>
     */
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

    /**
     * 构建一个通用的用于返回结果或者错误信息的泛型函数，但是这个函数带一个可以被消费的对象返回
     * 需要返回Result对象，那么就是Result<Event<T>>
     */
    private fun <T> fireEvent(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend () -> Result<Event<T>>
    ): LiveData<Result<Event<T>>> {
        return liveData<Result<Event<T>>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<Event<T>>(e)
            }
            emit(result)
        }
    }


//    private fun <T> fire(
//        context: CoroutineContext = Dispatchers.IO,
//        block: suspend () -> T
//    ): LiveData<T?> {
//        return liveData<T?>(context) {
//            val result = try {
//                block()
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//            emit(result)
//        }
//    }
}