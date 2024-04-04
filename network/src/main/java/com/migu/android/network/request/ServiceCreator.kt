package com.migu.android.network.request

import android.util.Log
import com.migu.android.network.util.GlobalInterceptor
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Invocation
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "ServiceCreator"

/**
 * 用于获取retrofit对象
 * 扩展await函数，用于简化回调
 */
object ServiceCreator {
    // 基础URL
    private const val BASE_URL = "https://zp9lzzl0.lc-cn-n1-shared.com/1.1/"

    private val client = OkHttpClient.Builder().addInterceptor(GlobalInterceptor()).build()

    // 创建 Retrofit 实例
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // 设置基础URL
        .addConverterFactory(GsonConverterFactory.create()) // 添加Gson转换器
        .client(client)
        .build()

    /**
     * 创建指定类型的服务
     * @param serviceClass 服务类的类型
     * @return 指定类型的服务实例
     */
    fun <T> create(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    /**
     * 创建泛型类型的服务
     * @return 泛型类型的服务实例
     */
    inline fun <reified T> create(): T {
        return create(T::class.java)
    }

    /**
     * 扩展await函数，用于简化回调的编写，并且该方法将会自动发起异步网络请求
     */
    suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) {
                        continuation.resume(body)
                    } else {
                        continuation.resumeWithException(RuntimeException("响应数据为空"))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    suspend fun <T> Call<T>.awaitForRetrofit(): T {
        return suspendCancellableCoroutine { continuation ->
            // 当协程被取消时，取消当前任务
            continuation.invokeOnCancellation {
                cancel()
            }
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body == null) {
                            val invocation = call.request().tag(Invocation::class.java)!!
                            val service = invocation.service()
                            val method = invocation.method()
                            val e = KotlinNullPointerException(
                                "Response from ${service.name}.${method.name}" +
                                        " was null but response body type was declared as non-null",
                            )
                            continuation.resumeWithException(e)
                        } else {
                            continuation.resume(body)
                        }
                        Log.i(TAG, "onResponse1: ${request().url()}")
                    } else {
                        Log.i(TAG, "onResponse2: ${request().url()}")
                        continuation.resumeWithException(HttpException(response))
                    }
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}
