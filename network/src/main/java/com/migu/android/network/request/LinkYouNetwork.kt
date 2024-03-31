package com.migu.android.network.request

import com.migu.android.network.api.PostServiceInterface
import com.migu.android.network.api.UserServiceInterface
import com.migu.android.network.model.base.LeanCloudPointerBaseModel
import com.migu.android.network.model.base.LoginUserRequestBody
import com.migu.android.network.model.LoginUserResponse
import com.migu.android.network.model.TargetUserPostsResponse
import com.migu.android.network.model.UserResultResponse
import com.migu.android.network.request.ServiceCreator.awaitForRetrofit

/**
 * 封装网络请求API
 */
object LinkYouNetwork {
    /**
     * 执行登录用户请求的函数。
     *
     * @param loginUserRequestBody 登录用户请求的请求体数据。
     * @return 登录用户的数据。
     * @throws Exception 如果请求失败，则抛出异常。
     */
    suspend fun loginUserRequest(loginUserRequestBody: LoginUserRequestBody): LoginUserResponse {
        return ServiceCreator.create<UserServiceInterface>()
            .getLoginUserData(requestBody = loginUserRequestBody).awaitForRetrofit()
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
        // 将指针参数转换为字符串形式
        val pointerWhere = leanCloudPointerBaseModel.toStringSeparateParameters()
        // 发起网络请求获取用户信息数据，并等待响应结果
        return ServiceCreator.create<UserServiceInterface>().getUserInfoData(where = pointerWhere)
            .awaitForRetrofit() // 等待 Retrofit 响应
    }


    /**
     * 通过 objectId 发起获取用户发布的帖子的网络请求，并返回帖子信息响应结果。
     * 注意：由于设计限制，只能获取到当前用户在 _User 表中的ObjectId，该函数不适合通过 UserInfo 的id去获取动态
     *
     * @param objectId 用户对象的唯一标识符
     * @return 包含用户发布的帖子信息的响应结果
     */
    suspend fun getUserPostsRequest(objectId: String): TargetUserPostsResponse {
        // 创建指向 LeanCloud 中用户对象的指针
        val leanCloudPointerBaseModel = LeanCloudPointerBaseModel(objectId).apply {
            pointerName = "author" // 设置指针名称
            className = "_User" // 设置指针指向的类名
        }
        // 将指针参数转换为字符串形式
        val pointerWhere = leanCloudPointerBaseModel.toStringSeparateParameters()
        // 发起网络请求获取用户发布的帖子数据，并等待响应结果
        return ServiceCreator.create<PostServiceInterface>().getUserPostsData(where = pointerWhere)
            .awaitForRetrofit() // 等待 Retrofit 响应
    }

}