package com.migu.android.network

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.migu.android.core.util.logInfo
import com.migu.android.database.DatabaseRepository
import java.util.concurrent.ConcurrentHashMap

class GetUrlsHandler<in T>(
    private val responseHandler: Handler,
    private val fragment: Fragment,
    private val onGetUrlsHandler: (T, List<String>) -> Unit
) : HandlerThread(TAG) {

    private val requestMap = ConcurrentHashMap<T, String>()
    private lateinit var requestHandler: Handler
    private var hasQuit = false
    private val databaseRepository = DatabaseRepository.getRepository()


    val fragmentLifecycleEventObserver = object : LifecycleEventObserver {

        init {
            fragment.lifecycle.addObserver(this)
        }

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    start()
                    looper
                }

                Lifecycle.Event.ON_DESTROY -> {
                    quit()
                    requestHandler.removeMessages(MESSAGE_GET_URLS)
                    requestMap.clear()
                }
                else -> {

                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onLooperPrepared() {
        super.onLooperPrepared()

        requestHandler = object : Handler(looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                if (msg.what == MESSAGE_GET_URLS) {
                    val target = msg.obj as T
                    handlerRequest(target)
                }
            }
        }
    }

    // 重写 quit() 方法，用于设置标志位并调用父类的 quit() 方法
    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun handlerRequest(target: T) {
        val objectId = requestMap[target] ?: return

        // 缓存获取位置

        // 先从本地数据库获取
//        var listUrls = databaseRepository.getImagesUrl(objectId)
        var listUrls: List<String> = listOf()
        // 发起网络请求
        if (listUrls.isEmpty()) {
            listUrls = Repository.getDynamicImages(objectId)
        }
        responseHandler.post(Runnable {
            if (requestMap[target] != objectId || hasQuit) {
                return@Runnable
            }

            requestMap.remove(target)
            // 执行回调
            onGetUrlsHandler(target, listUrls)
            // 存入缓存
        })

    }


    fun queueGetUrls(target: T, objectId: String) {
        requestMap[target] = objectId
        requestHandler.obtainMessage(MESSAGE_GET_URLS, target).sendToTarget()
    }

    companion object {
        private const val TAG = "GetUrlsHandler"
        private const val MESSAGE_GET_URLS = 0
    }
}