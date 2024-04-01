package com.migu.android.database.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DynamicTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun imageUrlsToString(urls: List<String>?): String? {
        return gson.toJson(urls)
    }

    @TypeConverter
    fun fromUrlsString(urlsString: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(urlsString, listType)
    }
}