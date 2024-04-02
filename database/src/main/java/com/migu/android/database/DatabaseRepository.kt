package com.migu.android.database

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.migu.android.core.util.logInfo
import com.migu.android.database.db.LinkYouDatabase
import com.migu.android.database.model.DynamicImages
import kotlinx.coroutines.runBlocking

class DatabaseRepository {

    private val database = LinkYouDatabase.getDatabase()

    private val dynamicDao = database.dynamicDao()

    fun getImagesUrl(objectId: String): List<String> {
        val urls: List<String>
        runBlocking {
//            val urlString = dynamicDao.getImagesUrl(objectId)
//            urls = toListString(urlString)
            urls = dynamicDao.getImagesUrl(objectId)
        }
        return urls
    }

    fun toListString(jsonString: String): List<String> {
        if (jsonString.isEmpty()) {
            return listOf()
        }
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(jsonString, type)
    }

    fun insertImagesUrl(dynamicImages: DynamicImages) {
        runBlocking {
            dynamicDao.insertImagesUrl(dynamicImages)
        }
    }

    companion object {
        private var instance: DatabaseRepository? = null

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

