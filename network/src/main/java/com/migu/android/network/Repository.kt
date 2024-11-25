package com.migu.android.network

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import cn.leancloud.LCObject
import com.migu.android.core.Const
import com.migu.android.core.LinkYou
import com.migu.android.core.util.GlobalUtil
import com.migu.android.core.util.SharedUtil
import com.migu.android.core.util.SharedUtil.getSharedPreferencesByNameExecute
import com.migu.android.core.util.SharedUtil.getSharedPreferencesObjByName
import com.migu.android.core.util.showToastOnUiThread
import com.migu.android.database.DatabaseRepository
import com.migu.android.database.model.DynamicAndImages
import com.migu.android.network.model.base.LoginUserRequestBody
import com.migu.android.network.model.LoginUserResponse
import com.migu.android.network.model.DynamicImageResponse
import com.migu.android.network.model.TargetUserDynamicsResponse
import com.migu.android.network.model.UserResultResponse
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.model.base.FileImage
import com.migu.android.network.model.base.UserInfo
import com.migu.android.network.request.DynamicsPagingSource
import com.migu.android.network.request.LeanCloudSDKRequest
import com.migu.android.network.request.LinkYouNetwork
import com.migu.android.network.util.Event
import com.migu.android.network.util.toDynamic
import com.migu.android.network.util.toDynamicEntity
import com.migu.android.network.util.toUserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext

private const val TAG = "LoginViewModel"

/**
 * 仓库层的统一封装入口
 */
object Repository {

    private val databaseRepository = DatabaseRepository.getRepository()

    /*------------------ 网络请求封装 -----------------*/

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
            Log.i(TAG, "getUserInfo: ")
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
     * 后期部分API需要修改为这种形式，使Api端点加上suspend进行调用
     */
    suspend fun getUserInfoDataSuspend(objectId: String): UserResultResponse {
        Log.i("TestTag", "initialize: 请求执行中")
        return LinkYouNetwork.getUserInfoDataSuspend(objectId)
    }

    /**
     * 获取目标用户发布的帖子信息。
     *
     * 之所以不在这里同步获取动态图片，是因为返回的Livedata并不是直接就有数据，需要等待后续的观察
     * @param objectId 目标用户的唯一标识符
     * @return LiveData 包装的帖子信息响应结果
     */
    fun getTargetUserDynamics(objectId: String): LiveData<Result<TargetUserDynamicsResponse>> {
        return fire(Dispatchers.IO) {
            // 添加延迟以防止数据过大导致网络拥塞，后期尝试重构为请求重试
//            delay(500)
            // 发起网络请求获取目标用户发布的动态数据
            val userDynamicsResponse = LinkYouNetwork.getUserDynamicsRequest(objectId)
            // 如果响应结果不为空，则返回成功的 Result 包装
            if (userDynamicsResponse.results.isNotEmpty()) {
                // 进行返回
                Result.success(userDynamicsResponse)
            } else {
                // 如果响应数据为空，则返回包含异常信息的失败 Result 包装
                Result.failure(RuntimeException(GlobalUtil.getString(R.string.response_data_is_empty)))
            }
        }
    }

    /**
     * 通过 objectId 获取动态图片的列表
     * @param objectId 动态对象的 ID
     * @return 返回动态图片的 URL 列表，如果请求失败则返回空列表
     */
    fun getDynamicImages(objectId: String): List<String> {
        return runBlocking {
            try {
                // 发起获取动态图片数据的网络请求
                val dynamicImageResponse = LinkYouNetwork.getDynamicImagesRequest(objectId)
                // 将响应结果中的图片 URL 提取出来并返回
                return@runBlocking dynamicImageResponse.results.map {
                    it.image.url!!
                }
            } catch (e: Exception) {
                // 发生异常时打印错误信息并返回空列表
                e.printStackTrace()
                return@runBlocking listOf()
            }
        }
    }

    /**
     * 获取动态图片的 LiveData 对象
     * @param objectId 动态对象的 ID
     * @return 返回一个 LiveData 对象，用于观察动态图片数据的请求结果
     */
    fun getDynamicImagesLiveData(objectId: String): LiveData<Result<DynamicImageResponse>> {
        return fire(Dispatchers.IO) {
            // 使用协程在 IO 线程发起网络请求获取动态图片数据
            val dynamicImageResponse = LinkYouNetwork.getDynamicImagesRequest(objectId)
            if (dynamicImageResponse.results.isNotEmpty()) {
                // 如果响应结果不为空，则返回成功的结果包装
                Result.success(dynamicImageResponse)
            } else {
                // 如果响应结果为空，可能是正常情况，也可能是出现了异常，这里返回一个带有错误信息的结果包装
                Result.failure(RuntimeException(GlobalUtil.getString(R.string.response_data_is_empty)))
            }
        }
    }

    /**
     * 获取最新的动态数据的 Flow 对象
     * @param limit 每一页的大小
     * @param skip 跳过的动态数量
     * @return 返回一个 Flow 对象，用于观察最新动态数据的请求结果
     */
    fun getTheLatestDynamics(limit: Int = 10): Flow<PagingData<Dynamic>> {
        return Pager(
            config = PagingConfig(limit, initialLoadSize = limit),
            pagingSourceFactory = {
                DynamicsPagingSource()
            }
        ).flow
    }

    /*------------------ 本地数据库封装 -----------------*/

    /**
     * 该函数用于获取数据库中缓存的所有动态，调用数据库模块的函数进行查询，最后将其转换为app模块中能够使用的对象
     */
    fun getDynamicDetailByDB(): LiveData<List<Dynamic>?> {
        return fireNotResult(Dispatchers.IO) {
            return@fireNotResult databaseRepository.getDynamicDetail()
        }.map {
            it?.map { dynamicAndImages ->
                dynamicAndImages.dynamicEntity.toDynamic()
            }
        }
    }

    /**
     * 将需要缓存的数据存储到数据库中
     */
    fun saveDynamicsDB(dynamic: List<Dynamic>) {
        CoroutineScope(Dispatchers.Main).launch {
            // 先删除数据
            databaseRepository.deleteAllDynamic()
            // 将动态数据转换为能够存储再数据库中的类型
            val dynamicEntityList = dynamic.map {
                it.toDynamicEntity()
            }

            // 再将动态数据对象和ID等一起封装到DynamicAndImages
            val dynamicAndImagesList = dynamicEntityList.map {
                DynamicAndImages(it.objectId, it, listOf())
            }
            // 再插入数据
            databaseRepository.insertDynamicDetail(dynamicAndImagesList)
        }
    }

    /**
     * 将图片链接插入到数据库
     */
    fun updateImageUrlToDB(dynamicAndImages: DynamicAndImages) {
        CoroutineScope(Dispatchers.Main).launch {
            val isSuccess = databaseRepository.updateImageUrl(dynamicAndImages)
        }
    }

    /**
     * 获取本地数据中存储的图片Urls的JSON数据
     */
    fun getImagesUrlsJsonByDB(objectId: String): List<String> {
        return runBlocking {
            databaseRepository.getImagesUrlJson(objectId)
        }
    }

    /*------------------ LeanCloud SDK封装 -----------------*/
    /**
     * 上传文件。
     *
     * @param uris 要上传的文件 Uri 列表
     * @param postObjectId 关联的 LCObject
     * @return 上传失败的文件 Uri 列表
     */
    private suspend fun uploadDynamicFile(uris: List<Uri>, postObjectId: LCObject): List<Uri> {
        return LeanCloudSDKRequest.uploadDynamicFile(uris, postObjectId)
    }

    /**
     * 上传动态内容。
     *
     * @param content 动态内容
     * @return 结果对象，包含上传后的 LCObject，可能为 null
     */
    private suspend fun uploadDynamicContent(content: String, imageCount: Int): Result<LCObject?> {
        return LeanCloudSDKRequest.uploadDynamicContent(content, imageCount)
    }


    /**
     * 发布动态。
     *
     * @param content 动态内容
     * @param uris 图片 Uri 列表
     * @return 结果对象，表示发布是否成功
     */
    suspend fun postDynamic(content: String, uris: List<Uri>): Result<Boolean> {
        var postStatus = false

        try {
            withContext(Dispatchers.Main) {
                val dynamicCreateResult = uploadDynamicContent(content, uris.size)
                if (dynamicCreateResult.isFailure) {
                    throw dynamicCreateResult.exceptionOrNull()
                        ?: RuntimeException("登录状态异常，动态发布失败")
                }

                // 需要先创建动态，再创建图片文件，并把动态的id添加到图片记录的字段中
                dynamicCreateResult.getOrNull()?.let {
                    val failedList = uploadDynamicFile(uris, it)
                    if (failedList.isEmpty()) {
                        postStatus = true
                    } else {
                        throw RuntimeException("图片上传出现错误，失败数：${failedList.size}张")
                    }
                } ?: throw RuntimeException("动态构建失败，未知原因")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
        return Result.success(postStatus)
    }

    suspend fun postModifyAvatar(avatarUri: Uri): Result<LCObject?> {
        return LeanCloudSDKRequest.postModifyAvatar(avatarUri)
    }

    suspend fun postModifyBackground(avatarUri: Uri): Result<LCObject?> {
        return LeanCloudSDKRequest.postModifyBackground(avatarUri)
    }
    /*------------------ SharedPreferences 封装 -----------------*/

    /**
     * 用于主页从本地获取全部缓存数据
     */
    fun getUserAllInfoBySp(): UserInfo {
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

    /**
     * 从SP文件中获取头像URL
     */
    fun getUserAvatarUrlBySp(): String? {
        val sharedPreferences =
            LinkYou.context.getSharedPreferencesObjByName(Const.UserInfo.USER_INFO_SP_FILE)
        return sharedPreferences.getString(Const.UserInfo.AVATAR_FILE_PATH, "")
    }

    /**
     * 保存用户资料到SP文件
     * @param userInfo 用户资料对象
     */
    fun saveUserInfoToSp(userInfo: UserInfo) {
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
     * 保存通过api获取的认证数据
     * @param loginUserResponse 登录用户的认证数据
     */
    fun saveAuthData(loginUserResponse: LoginUserResponse) {
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

    /**
     * 用于将SDK获取的用户数据分别保存到两个SP文件中
     *
     * @param lcObject
     */
    fun saveSDKAuthAndUserData(lcObject: LCObject) {
        saveUserInfoToSp(lcObject.toUserInfo())
        SharedUtil.save(
            Const.Auth.LOGIN_STATE_INFO_SHARED,
            Const.Auth.SDK_USER_INFO,
            lcObject.toJSONString()
        )
        LinkYou.refreshLoginState()
    }

    fun saveAvatar(url:String) {
        SharedUtil.save(Const.UserInfo.USER_INFO_SP_FILE, Const.UserInfo.AVATAR_FILE_PATH, url)
        LinkYou.refreshLoginState()
    }

    fun saveBackground(url:String) {
        SharedUtil.save(Const.UserInfo.USER_INFO_SP_FILE, Const.UserInfo.BACKGROUND_FILE_PATH, url)
        LinkYou.refreshLoginState()
    }

    /*------------------ 异步网络请求通用封装 -----------------*/

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


    /**
     * 返回泛型类型直接是响应数据的对象
     */
    private fun <T> fireNotResult(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend () -> T
    ): LiveData<T?> {
        return liveData<T?>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            emit(result)
        }
    }
}