package com.migu.android.network.request

import com.migu.android.network.ServiceCreator
import com.migu.android.network.api.UserServiceInterface
import com.migu.android.network.model.LoginUserData
import com.migu.android.network.model.LoginUserRequestBody
import retrofit2.await

private const val TAG = "LoginRequest"

object LoginRequest {
    suspend fun loginUserRequest(loginUserRequestBody: LoginUserRequestBody): LoginUserData {
        try {
            return ServiceCreator.create<UserServiceInterface>()
                .getLoginUserData(requestBody = loginUserRequestBody).await()
        } catch (e: Exception) {
            throw e
        }
    }
}