package com.migu.android.network.request

import com.migu.android.network.api.UserServiceInterface
import com.migu.android.network.model.LeanCloudPointerBaseModel
import com.migu.android.network.model.LoginUserRequestBody
import com.migu.android.network.model.LoginUserResponse
import com.migu.android.network.model.UserResultResponse
import com.migu.android.network.request.ServiceCreator.await
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
            .getLoginUserData(requestBody = loginUserRequestBody).await()
    }


    suspend fun getUserInfoRequest(leanCloudPointerBaseModel: LeanCloudPointerBaseModel): UserResultResponse {
        leanCloudPointerBaseModel.apply {
            pointerName = "UserObjectId"
            className = "UserInfo"
        }
        val pointerWhere = leanCloudPointerBaseModel.toStringSeparateParameters()
        return ServiceCreator.create<UserServiceInterface>().getUserInfoData(where = pointerWhere).awaitForRetrofit()
    }
}