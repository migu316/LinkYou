package com.migu.android.database

import com.migu.android.database.db.LinkYouDatabase
import com.migu.android.database.model.DynamicImages
import kotlinx.coroutines.runBlocking

class DatabaseRepository {

    private val database = LinkYouDatabase.getDatabase()

    private val dynamicDao = database.dynamicDao()

    fun getImagesUrl(objectId: String): List<String> {
        val urls: List<String>
        runBlocking {
//            urls = dynamicDao.getImagesUrl(objectId)
            urls = listOf()
        }
        return urls
    }

    fun insertImagesUrl(dynamicImages:DynamicImages) {
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

