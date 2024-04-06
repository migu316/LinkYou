package com.migu.android.database.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.migu.android.database.model.DynamicAndImages

@Dao
interface DynamicDao {
    /**
     * 根据传递的动态对象的ID去查询它的图片URL
     */
    @Query("SELECT urls FROM dynamicandimages WHERE objectId=(:objectId)")
    suspend fun getImagesUrl(objectId:String):List<String>

    /**
     * 插入动态数据
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDynamicDetail(dynamicAndImages: DynamicAndImages)

    @Update
    suspend fun updateDynamicImageUrls(dynamicAndImages: DynamicAndImages)
    /**
     * 从数据库中取出动态数据
     * 暂时先取全部
     */
    @Query("SELECT * FROM dynamicandimages")
    suspend fun getDynamicDetail():List<DynamicAndImages>
}