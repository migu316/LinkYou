package com.migu.android.network.util

import com.migu.android.core.util.AssetsUtils
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class GlobalInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val newRequest: Request = originalRequest.newBuilder()
            .header("X-LC-Id", AssetsUtils.getAPPID())
            .header("X-LC-Key", AssetsUtils.getLCKEY())
            .build()
        return chain.proceed(newRequest)
    }
}