package com.migu.android.database.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.migu.android.database.model.DynamicImages

@Dao
interface DynamicDao {
    /**
     * 根据传递的动态对象的ID去查询它的图片URL
     */
//    @Query("SELECT * FROM dynamicImages WHERE objectId=(:objectId)")
//    suspend fun getImagesUrl(objectId:String):List<String>

    @Insert
    suspend fun insertImagesUrl(dynamicImages: DynamicImages)
}