package com.migu.android.network.request

import android.net.Uri
import android.util.Log
import cn.leancloud.LCFile
import cn.leancloud.LCObject
import cn.leancloud.LCQuery
import cn.leancloud.LCUser
import com.migu.android.core.Const
import com.migu.android.core.LinkYou
import com.migu.android.core.util.GsonUtils
import com.migu.android.core.util.SharedUtil
import com.migu.android.core.util.logError
import com.migu.android.core.util.logInfo
import com.migu.android.network.Repository
import com.migu.android.network.model.base.UserInfo
import com.migu.android.network.util.LeanCloudUtils
import com.migu.android.network.util.toUserInfo
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "LeanCloudSDKRequest"

object LeanCloudSDKRequest {

    /**
     * 动态提交时上传图片
     * 后期修改为LiveData去返回一个可观察的对象
     *
     * @param uris 要上传的文件 Uri 列表
     * @param postObjectId 关联的 LCObject
     * @return 上传失败的文件 Uri 列表
     */
    suspend fun uploadDynamicFile(uris: List<Uri>, postObjectId: LCObject): List<Uri> {
        val failedUri = mutableListOf<Uri>() // 用于跟踪上传失败的图片数量
        for (uri in uris) {
            val file = LeanCloudUtils.uriToFile(uri)
            val lcFile = LCFile(file.name, LeanCloudUtils.fileToByteArray(file))
            try {
                val userInfoObj = LeanCloudUtils.getUserInfo()
                    ?: throw RuntimeException("用户信息状态异常，尝试重新登录")
                withContext(Dispatchers.IO) {
                    universalLCObjectResult("PostImages", {
                        put("imageId", lcFile)
                        put("postObjectId", postObjectId)
                        put("userInfo", userInfoObj)
                    }, {
                        logInfo(TAG, "上传成功 ")
                    }, {
                        logError("上传失败", this)
                        failedUri.add(uri)
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (file.exists()) {
                    file.delete()
                }
            }
        }

        return failedUri
    }

    /**
     * 上传动态内容。
     *
     * @param content 动态内容
     * @return 结果对象，包含上传后的 LCObject，可能为 null
     */
    suspend fun uploadDynamicContent(content: String, imageCount: Int): Result<LCObject?> {
        var lcObject: LCObject? = null
        try {
            val currentUser = LeanCloudUtils.getLCUser() ?: throw RuntimeException("登录状态异常")
            val userInfoObj = LeanCloudUtils.getUserInfo()
                ?: throw RuntimeException("用户信息状态异常，尝试重新登录")

            withContext(Dispatchers.IO) {
                universalLCObjectResult("Posts", {
                    // 添加账户信息、文本内容、用户资料对象
                    put("author", currentUser)
                    put("postText", content)
                    put("UserInfoId", userInfoObj)
                    put("ImageCount", imageCount)
                }, {
                    // 成功后的逻辑
                    Log.i(TAG, "onNext: 构建动态成功 $this")
                    lcObject = this
                }, {
                    Log.i(TAG, "onError: 构建动态失败 ${this.message}")
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure(e)
        }
        return Result.success(lcObject)
    }

    /**
     * 使用 SDK 进行登录。
     *
     * @param username 用户名
     * @param password 密码
     */
    suspend fun loginSDK(username: String, password: String) {
        val suspendCoroutine = suspendCoroutine<Result<LCUser>> { continuation ->
            LCUser.logIn(username, password).subscribe(object : Observer<LCUser> {
                override fun onSubscribe(d: Disposable) {}
                override fun onNext(t: LCUser) {
                    logInfo("onNext: SDK登录成功")
                    val json = t.toJSONString()
                    LinkYou.sdkLoginLCUserJson = json
                    SharedUtil.save(
                        Const.Auth.LOGIN_STATE_INFO_SHARED,
                        Const.Auth.SDK_LOGIN_INFO,
                        json
                    )
                    LinkYou.refreshLoginState()
                    continuation.resume(Result.success(t))
                }

                override fun onError(e: Throwable) {
                    logError("登录失败", e)
                    continuation.resume(Result.failure(e))
                }

                override fun onComplete() {}
            })
        }
        suspendCoroutine.apply {
            if (isSuccess) {
                getOrNull()?.let {
                    withContext(Dispatchers.IO) {
                        // 完成后再去获取用户信息
                        getUserInfo(it)
                    }
                }
            }
        }
    }

    /**
     * 通过当前用户对象获取用户的资料
     * 即使失败了也没关系
     *
     * @param lcUser 用户对象
     * 备选方式：
     * val lcObject = LCObject.createWithoutData("_User", lcUser.objectId)
     * 参见：[备选方式](https://docs.leancloud.cn/sdk/storage/guide/java/#%E4%B8%80%E5%AF%B9%E4%B8%80%E4%B8%80%E5%AF%B9%E5%A4%9A%E5%85%B3%E7%B3%BB)
     */
    suspend fun getUserInfo(lcUser: LCObject) {
        suspendCoroutine<Boolean> { continuation ->
            LCQuery<LCObject>("UserInfo").apply {
                whereEqualTo("UserObjectId", lcUser)
                include("Avatar")
                include("Background")
                findInBackground().subscribe(object : Observer<List<LCObject>> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: List<LCObject>) {
                        if (t.isNotEmpty()) {
                            t[0].apply {
                                val json = toJSONString()
                                SharedUtil.save(
                                    Const.Auth.LOGIN_STATE_INFO_SHARED,
                                    Const.Auth.SDK_USER_INFO,
                                    json
                                )
                                toUserInfo().apply {
                                    Repository.saveUserInfoToSp(this)
                                }
                                LinkYou.refreshLoginState()
                                continuation.resume(true)
                            }
                        } else {
                            logError(
                                "用户信息获取异常",
                                RuntimeException("用户信息获取异常:未获取到任何对象")
                            )
                            continuation.resume(false)
                        }
                    }

                    override fun onError(e: Throwable) {
                        logError("获取用户信息失败", e)
                        continuation.resume(false)
                    }

                    override fun onComplete() {}
                })
            }
        }
    }

    /**
     * 更新用户资料的函数
     * 1.首先上传头像文件，拿到一个LCObject（或许应该作为参数传递）
     * 2.判断缓存数据中是否存在当前用户信息的JSON对象，a.如果不存在 -> 获取  b.存在  ->  不获取直接执行下一步
     * 3.从缓存数据中获取当前用户信息的JSON对象，转换为LCObject   a.转换错误-> 重新获取  b.成功->下一步
     * 4.上传以进行更新
     */
    suspend fun postModifyUserInfo(avatar: LCFile?, background: LCFile?, userInfo: UserInfo?):Result<LCObject?> {
        val userInfoObj: LCObject?
        val lcUser = GsonUtils.fromJsonNormal<LCUser>(LinkYou.sdkLoginLCUserJson)
        if (LinkYou.sdkUserInfoJson.isNotEmpty()) {
            userInfoObj = GsonUtils.fromJsonNormal<LCObject>(LinkYou.sdkUserInfoJson)
        } else {
            lcUser?.let { user ->
                getUserInfo(user)
                userInfoObj = GsonUtils.fromJsonNormal<LCObject>(LinkYou.sdkUserInfoJson)
                // 如果这次的userInfoObj还是为null，那么登录状态存在问题，抛出错误
                if (userInfoObj == null) {
                    return Result.failure(RuntimeException("登录状态异常"))
                }
            }

            // 继续尝试上传
            
        }

        return Result.failure(RuntimeException("TODO"))
    }


    /**
     * 通用的请求函数
     * @param className 需要操作的表名
     * @param block 需要添加的内容，或者说约束信息
     * @param onNextBlock 请求成功后的调用
     * @param onErrorBlock 请求失败后的调用
     */
    private suspend fun universalLCObjectResult(
        className: String,
        block: LCObject.() -> Unit,
        onNextBlock: (LCObject.() -> Unit)? = null,
        onErrorBlock: (Throwable.() -> Unit)? = null
    ): LCObject {
        return suspendCoroutine { continuation ->
            LCObject(className).apply(block).saveInBackground()
                .subscribe(object : Observer<LCObject> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: LCObject) {
                        if (onNextBlock != null) {
                            t.onNextBlock()
                        }
                        continuation.resume(t)
                    }

                    override fun onError(e: Throwable) {
                        if (onErrorBlock != null) {
                            e.onErrorBlock()
                        }
                        continuation.resumeWithException(e)
                    }

                    override fun onComplete() {}
                })
        }
    }
}