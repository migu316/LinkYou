package com.migu.android.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.migu.android.core.Const
import com.migu.android.core.LinkYou
import com.migu.android.core.util.GlobalUtil
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
import com.migu.android.network.request.LinkYouNetwork
import com.migu.android.network.util.Event
import com.migu.android.network.util.toDynamic
import com.migu.android.network.util.toDynamicEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.suspendCoroutine

private const val TAG = "LoginViewModel"

/**
 * 仓库层的统一封装入口
 */
object Repository {

    private val databaseRepository = DatabaseRepository.getRepository()

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
    @OptIn(DelicateCoroutinesApi::class)
    fun saveDynamicsDB(dynamic: List<Dynamic>) {
        GlobalScope.launch(Dispatchers.Main) {
            coroutineScope {
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
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun updateImageUrlToDB(dynamicAndImages: DynamicAndImages) {
        GlobalScope.launch(Dispatchers.Main) {
            databaseRepository.updateImageUrl(dynamicAndImages)
        }
    }

    fun getImagesUrlsJsonByDB(objectId: String): List<String> {
        return runBlocking {
            databaseRepository.getImagesUrlJson(objectId)
        }
    }


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