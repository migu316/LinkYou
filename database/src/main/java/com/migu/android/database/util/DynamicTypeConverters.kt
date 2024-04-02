package com.migu.android.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
}