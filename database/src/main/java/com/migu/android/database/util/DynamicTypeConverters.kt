package com.migu.android.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.migu.android.database.model.base.DynamicEntity
import java.util.Date

class DynamicTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromListString(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toListString(jsonString: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(jsonString, type)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long?): Date? {
        return millisSinceEpoch?.let {
            Date(it)
        }
    }

    @TypeConverter
    fun fromDynamicEntity(dynamicEntity: DynamicEntity): String? {
        return gson.toJson(dynamicEntity)
    }

    @TypeConverter
    fun toDynamicAndImages(json:String):DynamicEntity {
        return gson.fromJson(json, DynamicEntity::class.java)
    }
}