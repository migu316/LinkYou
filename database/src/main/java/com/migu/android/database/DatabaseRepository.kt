package com.migu.android.database

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.migu.android.core.util.logInfo
import com.migu.android.database.db.LinkYouDatabase
import com.migu.android.database.model.DynamicImages
import kotlinx.coroutines.runBlocking

/**
 * 数据库仓库类，用于封装数据库操作方法。
 */
class DatabaseRepository {

    private val database = LinkYouDatabase.getDatabase()

    private val dynamicDao = database.dynamicDao()

    /**
     * 根据 objectId 获取图片 URL 列表。
     *
     * @param objectId 动态对象的唯一标识符。
     * @return 图片 URL 列表。
     */
    fun getImagesUrl(objectId: String): List<String> {
        val urls: List<String>
        runBlocking {
            urls = dynamicDao.getImagesUrl(objectId)
        }
        return urls
    }

    /**
     * 将 JSON 字符串转换为字符串列表。
     *
     * @param jsonString 要转换的 JSON 字符串。
     * @return 字符串列表。
     */
    fun toListString(jsonString: String): List<String> {
        if (jsonString.isEmpty()) {
            return listOf()
        }
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

    /**
     * 插入图片 URL 列表到数据库。
     *
     * @param dynamicImages 包含 objectId 和图片 URL 列表的动态图片对象。
     */
    fun insertImagesUrl(dynamicImages: DynamicImages) {
        runBlocking {
            dynamicDao.insertImagesUrl(dynamicImages)
        }
    }

    /**
     * 伴生对象，用于获取仓库的单例实例。
     */
    companion object {
        private var instance: DatabaseRepository? = null

        /**
         * 获取数据库仓库实例。
         *
         * @return 数据库仓库实例。
         */
        @Synchronized
        fun getRepository(): DatabaseRepository {
            instance?.let {
                return it
            }
            return DatabaseRepository().apply {
                instance = this
            }
        }
    }
}
