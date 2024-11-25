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
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "LeanCloudSDKRequest"

object LeanCloudSDKRequest {

    /**
     * 创建一个LCFile
     *
     * @param uri 传递一个uri
     * @return 返回一个LCFile
     */
    fun createSingleLCFile(uri: Uri): LCFile {
        val file = LeanCloudUtils.uriToFile(uri)
        return LCFile(file.name, LeanCloudUtils.fileToByteArray(file))
    }


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
                val result = verifyYourCurrentAccount()
                if (result.isFailure) {
                    throw result.exceptionOrNull()
                        ?: RuntimeException("用户信息状态异常，尝试重新登录")
                }
                val userInfoObj =
                    result.getOrNull() ?: throw RuntimeException("用户信息状态异常，尝试重新登录")
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
     * 通过当前用户对象获取用户的资料并存放到SP缓存中
     * 即使失败了也没关系，因为优先检查的是通过API获取的登录信息
     *
     * @param lcUser 用户对象
     * 备选方式：
     * val lcObject = LCObject.createWithoutData("_User", lcUser.objectId)
     * 参见：[备选方式](https://docs.leancloud.cn/sdk/storage/guide/java/#%E4%B8%80%E5%AF%B9%E4%B8%80%E4%B8%80%E5%AF%B9%E5%A4%9A%E5%85%B3%E7%B3%BB)
     */
    suspend fun getUserInfo(lcUser: LCObject): LCObject? {
        return suspendCoroutine<LCObject?> { continuation ->
            LCQuery<LCObject>("UserInfo").apply {
                whereEqualTo("UserObjectId", lcUser)
                include("Avatar")
                include("Background")
                findInBackground().subscribe(object : Observer<List<LCObject>> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: List<LCObject>) {
                        if (t.isNotEmpty()) {
                            t[0].apply {
                                Repository.saveSDKAuthAndUserData(this)
                            }
                            continuation.resumeWith(Result.success(t[0]))
                        } else {
                            val exception = RuntimeException("用户信息获取异常:未获取到任何对象")
                            logError("用户信息获取异常", exception)
                            continuation.resume(null)
                        }
                    }

                    override fun onError(e: Throwable) {
                        logError("获取用户信息失败", e)
                        continuation.resume(null)
                    }

                    override fun onComplete() {}
                })
            }
        }
    }

    suspend fun postModifyAvatar(avatarUri: Uri): Result<LCObject?> {
        // 验证用户信息
        val result = verifyYourCurrentAccount()
        return result.getOrNull()?.let {lcObject->
            withContext(Dispatchers.IO) {
                val lcFile = createSingleLCFile(avatarUri)
                val lcObjectResult = universalCreateWithoutData("UserInfo", lcObject.objectId, {
                    put("Avatar", lcFile)
                }, {
                    logInfo("修改成功，修改后的对象为:$this")
                }, {
                    logError("用户信息修改错误：", this)
                })
                lcObjectResult
            }
        } ?: run {
            return result
        }
    }

    suspend fun postModifyBackground(avatarUri: Uri): Result<LCObject?> {
        // 验证用户信息
        val result = verifyYourCurrentAccount()
        return result.getOrNull()?.let {lcObject->
            withContext(Dispatchers.IO) {
                val lcFile = createSingleLCFile(avatarUri)
                val lcObjectResult = universalCreateWithoutData("UserInfo", lcObject.objectId, {
                    put("Background", lcFile)
                }, {
                    logInfo("修改成功，修改后的对象为:$this")
                }, {
                    logError("用户信息修改错误：", this)
                })
                lcObjectResult
            }
        } ?: run {
            return result
        }
    }


    /**
     * 验证缓存中是否存在用户信息
     *
     * @return 返回获取到的用户信息对象，或者抛出错误
     */
    suspend fun verifyYourCurrentAccount(): Result<LCObject?> {
        // 先检查是否存在用户信息对象
        if (LinkYou.sdkUserInfoJson.isNotEmpty()) {
            val userInfoObj = GsonUtils.fromJsonNormal<LCObject>(LinkYou.sdkUserInfoJson)
            return Result.success(userInfoObj)
        }

        // 获取SP文件中的SDK用户缓存
        val lcUser = GsonUtils.fromJsonNormal<LCUser>(LinkYou.sdkLoginLCUserJson)
        // 如果不为空就返回解析出来的用户对象
        if (LinkYou.sdkUserInfoJson.isNotEmpty()) {
            val userInfoObj = GsonUtils.fromJsonNormal<LCObject>(LinkYou.sdkUserInfoJson)
            return Result.success(userInfoObj)
        } else {
            lcUser?.let { user ->
                // 发起请求，通过SDK方法获取用户信息
                getUserInfo(user)?.run {
                    return Result.success(this)
                }
            }
            return Result.failure(RuntimeException("登录状态异常"))
        }
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

    private suspend fun universalCreateWithoutData(
        className: String,
        objectId: String,
        block: LCObject.() -> Unit,
        onNextBlock: (LCObject.() -> Unit)? = null,
        onErrorBlock: (Throwable.() -> Unit)? = null
    ): Result<LCObject> {
        return suspendCoroutine { continuation ->
            LCObject.createWithoutData(className, objectId).apply(block).saveInBackground()
                .subscribe(object : Observer<LCObject> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(t: LCObject) {
                        if (onNextBlock != null) {
                            t.onNextBlock()
                        }
                        continuation.resume(Result.success(t))
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