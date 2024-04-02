package com.migu.android.database.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.migu.android.core.LinkYou
import com.migu.android.database.model.DynamicImages
import com.migu.android.database.util.DynamicTypeConverters

@Database(version = 1, entities = [DynamicImages::class])
@TypeConverters(DynamicTypeConverters::class)
abstract class LinkYouDatabase : RoomDatabase() {
    abstract fun dynamicDao(): DynamicDao

    companion object {
        private var instance: LinkYouDatabase? = null

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