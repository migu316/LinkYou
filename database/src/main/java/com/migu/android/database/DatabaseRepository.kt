package com.migu.android.database

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.migu.android.database.db.LinkYouDatabase
import com.migu.android.database.model.DynamicAndImages
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
    suspend fun getImagesUrlJson(objectId: String): List<String> {
        val urls: List<String> = dynamicDao.getImagesUrl(objectId)
        // 数据库设计问题，当一个list保存到数据库的时候，是List<String> -> String类型，但是取出时，就直接取出
        // String类型了，因此取出的字符串就是"[1,3]"或者"[]"这种形式，因此在外部需要进行处理
        if (urls.isEmpty() || urls[0] == "[]") {
            return listOf()
        }
        return urls
    }


    /**
     * 批量插入插入动态列表到数据库。
     *
     * @param dynamicAndImagesList 包含 objectId 和图片 URL 列表的动态图片对象。
     */
    suspend fun insertDynamicDetail(dynamicAndImagesList: List<DynamicAndImages>) {
        dynamicDao.insertDynamicDetail(dynamicAndImagesList)
    }

    /**
     * 更新指定动态的urls
     */
    suspend fun updateImageUrl(dynamicAndImages: DynamicAndImages) {
        dynamicDao.updateDynamicImageUrls(dynamicAndImages)
    }

    /**
     * 删除所有的动态
     */
    suspend fun deleteAllDynamic() {
        dynamicDao.deleteAllDynamics()
    }

    /**
     * 获取所有的动态
     */
    suspend fun getDynamicDetail(): List<DynamicAndImages> {
        return dynamicDao.getDynamicDetail()
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
