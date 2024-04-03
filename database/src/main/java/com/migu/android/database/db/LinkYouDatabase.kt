package com.migu.android.database.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.migu.android.core.LinkYou
import com.migu.android.database.model.DynamicImages
import com.migu.android.database.util.DynamicTypeConverters

/**
 * 数据库类，用于定义数据库和提供访问数据库的方法。
 */
@Database(version = 1, entities = [DynamicImages::class])
@TypeConverters(DynamicTypeConverters::class)
abstract class LinkYouDatabase : RoomDatabase() {

    /**
     * 返回一个动态数据访问对象。
     */
    abstract fun dynamicDao(): DynamicDao

    /**
     * 伴生对象，包含数据库的实例化方法。
     */
    companion object {
        private var instance: LinkYouDatabase? = null

        /**
         * 获取数据库实例。
         *
         * @return 数据库实例。
         */
        @Synchronized
        fun getDatabase(): LinkYouDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                LinkYou.context,
                LinkYouDatabase::class.java,
                "dynamicsDatabase"
            ).build().apply {
                instance = this
            }
        }
    }
}
