package com.migu.android.network

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.LruCache
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.migu.android.database.model.DynamicAndImages
import com.migu.android.network.model.base.Dynamic
import com.migu.android.network.util.DataProcessingUtil
import com.migu.android.network.util.toDynamicEntity
import java.util.concurrent.ConcurrentHashMap

/**
 * HandlerThread 子类，用于处理与检索图像 URL 相关的异步任务。
 * 该类管理图像 URL 的请求，以后台线程处理它们，并提供带有结果的回调。
 * @param responseHandler 用于将响应返回到主线程的处理程序。
 * @param fragment 与此处理程序关联的 fragment，用于观察生命周期事件。
 * @param onGetUrlsHandler 用于处理检索到的图像 URL 的回调函数。
 */
class GetUrlsHandler<in T>(
    private val responseHandler: Handler,
    private val fragment: Fragment,
    private val onGetUrlsHandler: (T, List<String>, String) -> Unit
) : HandlerThread(TAG) {

    // 存储目标及其对应的 objectId 的映射
    private val requestMap = ConcurrentHashMap<T, Dynamic>()

    // 用于在后台线程处理请求的处理程序
    private lateinit var requestHandler: Handler

    // 用于指示线程是否已退出的标志
    private var hasQuit = false

    // 用于数据库操作的 Repository
//    private val databaseRepository = DatabaseRepository.getRepository()

    // 用于存储图像 URL 的内存缓存
    private val mMemoryCache: LruCache<String, List<String>>

    /**
     * 用于观察 fragment 生命周期事件的观察者。
     */
    private val fragmentLifecycleEventObserver = object : LifecycleEventObserver {

        init {
            // 在初始化块中，将观察者添加到 fragment 的生命周期中
            fragment.lifecycle.addObserver(this)
        }

        /**
         * 当 fragment 生命周期状态发生变化时调用的方法。
         * @param source 生命周期所有者（即 fragment）
         * @param event 生命周期事件
         */
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    // 在 ON_CREATE 事件发生时，启动 HandlerThread
                    start()
                    // 获取 HandlerThread 的消息循环
                    looper
                }

                Lifecycle.Event.ON_DESTROY -> {
                    // 在 ON_DESTROY 事件发生时，停止 HandlerThread，并清除消息队列和请求映射
                    quit()
                    requestHandler.removeMessages(MESSAGE_GET_URLS)
                    requestMap.clear()
                }

                else -> {
                    // 对于其他生命周期事件，不执行任何操作
                }
            }
        }
    }

    /**
     * 初始化块，用于设置内存缓存大小并创建 LruCache 对象。
     */
    init {
        // 计算可用最大内存，并将其除以 1000，然后转换为整数
        val cacheSize = Runtime.getRuntime().maxMemory().div(1000).toInt()
        // 创建 LruCache 对象，指定缓存大小
        mMemoryCache = LruCache(cacheSize)
    }


    /**
     * 处理程序准备就绪时调用的方法。
     */
    @Suppress("UNCHECKED_CAST")
    override fun onLooperPrepared() {
        super.onLooperPrepared()
        // 创建一个 Handler 对象，并重写 handleMessage 方法处理消息
        requestHandler = object : Handler(looper) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                // 检查消息标识是否为获取图片 URL 的标识
                if (msg.what == MESSAGE_GET_URLS) {
                    // 获取消息中的目标对象，并转换为泛型类型 T
                    val target = msg.obj as T
                    // 调用处理请求的方法
                    handlerRequest(target)
                }
            }
        }
    }


    /**
     * 重写 quit() 方法，用于设置标志位并调用父类的 quit() 方法。
     * @return Boolean 指示退出操作是否成功
     */
    override fun quit(): Boolean {
        // 设置退出标志位为 true
        hasQuit = true
        // 调用父类的 quit() 方法执行退出操作，并返回结果
        return super.quit()
    }


    /**
     * 处理请求的方法。
     * @param target 请求的目标对象
     */
    fun handlerRequest(target: T) {

        val dynamic = requestMap[target]
        // 获取请求对象对应的 objectId，如果为空则直接返回
        val objectId = dynamic?.objectId ?: return
        // 从缓存中获取图片 URL 列表，如果缓存中不存在则返回空集合
        var listUrls = mMemoryCache.get(objectId) ?: listOf()

        // 如果缓存中不存在图片 URL 列表，则尝试从本地数据库获取
        if (listUrls.isEmpty()) {
            // 获取本地数据库中的原始图片 URL 数据，即一个 List<String> 在索引 0 处
            var rawListData = Repository.getImagesUrlsJsonByDB(objectId)
            // 集合不为空时才将其转换为List集合
            if (rawListData.isNotEmpty()) {
                rawListData = DataProcessingUtil.jsonToListString(rawListData[0])
            }
            // 如果获取到的集合为空，即数据库中无数据，执行如下操作
            if (rawListData.isEmpty()) {
                // 从网络请求获取动态图片 URLs
                listUrls = Repository.getDynamicImages(objectId)
                // 将获取到的动态图片 URLs合并动态数据 存入数据库
                // 需要重写：修改为更新指定对象的url，不能在这里往数据库存数据
//                logInfo(dynamic.toString())
                Repository.updateImageUrlToDB(DynamicAndImages(objectId, dynamic.toDynamicEntity(), listUrls))
                // 将获取到的动态图片 URLs 存入缓存
                mMemoryCache.put(objectId, listUrls)
            } else {
                // 如果数据库中存在该动态的图片 URLs 信息，则直接取出并转换为 listUrls
//                listUrls = databaseRepository.toListString(rawListData[0])
                listUrls = rawListData
                // 将数据库中获取到的动态图片 URLs 存入缓存
                mMemoryCache.put(objectId, listUrls)
            }
            // 如果动态的图片 URLs 列表为空，说明无图片，直接返回，避免将任务提交到 UI 线程的消息队列中
//            if (listUrls.isEmpty()) {
//                return
//            }
        }

        // 处理拿到的图片Urls
        responseHandler.post(Runnable {
            // 如果请求对象已经被取消或者已经退出，则直接返回，不执行回调操作
            if (requestMap[target]?.objectId != objectId || hasQuit) {
                return@Runnable
            }
            // 从请求映射中移除已处理完成的请求对象
            requestMap.remove(target)
            // 执行回调操作，将目标对象以及对应的图片 URL 列表传递给回调函数
            onGetUrlsHandler(target, listUrls, objectId)
        })
    }


    /**
     * 将目标对象和对应的 objectId 加入到请求映射中。
     */
    fun queueGetUrls(target: T, dynamic: Dynamic) {
        // 将目标对象和 objectId 加入到请求 Map 中
        requestMap[target] = dynamic
        // 如果请求处理程序已经初始化，则发送消息给请求处理程序
        if (::requestHandler.isInitialized) {
            requestHandler.obtainMessage(MESSAGE_GET_URLS, target).sendToTarget()
        }
    }

    companion object {
        // Handler标签
        private const val TAG = "GetUrlsHandler"

        // 获取图片 URL 消息标识
        private const val MESSAGE_GET_URLS = 0
    }

}