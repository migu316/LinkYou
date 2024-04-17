package com.migu.android.network.request

import com.migu.android.core.Const
import com.migu.android.core.LinkYou
import com.migu.android.core.util.logInfo
import com.migu.android.network.Interceptor
import com.migu.android.network.api.DynamicServiceInterface
import com.migu.android.network.api.UserServiceInterface
import com.migu.android.network.model.base.LeanCloudPointerBaseModel
import com.migu.android.network.model.base.LoginUserRequestBody
import com.migu.android.network.model.LoginUserResponse
import com.migu.android.network.model.DynamicImageResponse
import com.migu.android.network.model.TargetUserDynamicsResponse
import com.migu.android.network.model.UserResultResponse
import com.migu.android.network.request.ServiceCreator.awaitForRetrofit
import com.migu.android.network.util.LeanCloudUtils

/**
 * 封装网络请求API
 */
object LinkYouNetwork {

    private val dynamicServiceInterface = ServiceCreator.create<DynamicServiceInterface>()

    private val userServiceInterface = ServiceCreator.create<UserServiceInterface>()

    /**
     * 执行登录用户请求的函数。
     *
     * @param loginUserRequestBody 登录用户请求的请求体数据。
     * @return 登录用户的数据。
     * @throws Exception 如果请求失败，则抛出异常。
     */
    suspend fun loginUserRequest(loginUserRequestBody: LoginUserRequestBody): LoginUserResponse {
        return userServiceInterface.getLoginUserData(requestBody = loginUserRequestBody)
            .awaitForRetrofit()
    }


    /**
     * 通过 objectId 发起获取用户信息的网络请求，并返回用户信息响应结果。
     *
     * @param objectId 用户对象的唯一标识符
     * @return 包含用户信息的响应结果
     */
    suspend fun getUserInfoRequest(objectId: String): UserResultResponse {
        // 创建指向 LeanCloud 中用户对象的指针
        val leanCloudPointerBaseModel = LeanCloudPointerBaseModel(objectId).apply {
            pointerName = "UserObjectId" // 设置指针名称
            className = "UserInfo" // 设置指针指向的类名
        }
        // 将指针参数转换约束条件
        val pointerWhere = leanCloudPointerBaseModel.toStringSeparateParameters()
        // 发起网络请求获取用户信息数据，并等待响应结果
        return userServiceInterface.getUserInfoData(where = pointerWhere).awaitForRetrofit()
    }


    /**
     * 通过 objectId 发起获取用户发布的帖子的网络请求，并返回帖子信息响应结果。
     * 注意：由于设计限制，只能获取到当前用户在 _User 表中的ObjectId，该函数不适合通过 UserInfo 的id去获取动态
     *
     * @param objectId 用户对象的唯一标识符
     * @return 包含用户发布的帖子信息的响应结果
     */
    suspend fun getUserDynamicsRequest(objectId: String): TargetUserDynamicsResponse {
        // 创建指向 LeanCloud 中用户对象的指针
        val leanCloudPointerBaseModel = LeanCloudPointerBaseModel(objectId).apply {
            pointerName = "author" // 设置指针名称
            className = "_User" // 设置指针指向的类名
        }
        // 将指针参数转换约束条件
        val pointerWhere = leanCloudPointerBaseModel.toStringSeparateParameters()
        // 发起网络请求获取用户发布的动态数据，并等待响应结果
        return dynamicServiceInterface.getUserDynamicsData(where = pointerWhere).awaitForRetrofit()
    }

    /**
     * 通过 objectId 获取动态图片数据的网络请求
     * @param objectId 动态对象的 ID
     * @return 返回动态图片数据的响应结果
     */
    suspend fun getDynamicImagesRequest(objectId: String): DynamicImageResponse {
        // 创建指向 LeanCloud 中动态对象的指针
        val leanCloudPointerBaseModel = LeanCloudPointerBaseModel(objectId).apply {
            pointerName = "postObjectId"
            className = "Posts"
        }
        // 将指针转换为查询条件字符串
        val pointerWhere = leanCloudPointerBaseModel.toStringSeparateParameters()

        val dynamicImageResponse =
            dynamicServiceInterface.getDynamicImagesData(where = pointerWhere).awaitForRetrofit()
        for (dynamicImage in dynamicImageResponse.results) {
            dynamicImage.image.url = Interceptor.pictureQualityMode(dynamicImage.image.url!!)
        }

        return dynamicImageResponse
    }

    /**
     * 分页获取最新的动态
     * @param limit 限制每页数量
     * @param skip 跳过的数量
     */
    suspend fun getTheLatestDynamicsRequest(
        limit: Int = 10,
        skip: Int
    ): TargetUserDynamicsResponse {
        return dynamicServiceInterface.getTheLatestDynamicsData(limit = limit, skip = skip)
            .awaitForRetrofit()
    }
}