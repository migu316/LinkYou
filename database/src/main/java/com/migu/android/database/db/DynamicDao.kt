package com.migu.android.database.db

import androidx.room.Dao
import androidx.room.Delete
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
     * 批量插入动态数据
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDynamicDetail(dynamicAndImagesList: List<DynamicAndImages>)

    /**
     * 更新数据库中图片urls的值
     */
    @Update
    suspend fun updateDynamicImageUrls(dynamicAndImages: DynamicAndImages):Int

    /**
     * 删除DynamicAndImages中的所有数据
     */
    @Query("DELETE FROM DynamicAndImages")
    suspend fun deleteAllDynamics()

    /**
     * 从数据库中取出动态数据
     * 暂时先取全部
     */
    @Query("SELECT * FROM dynamicandimages")
    suspend fun getDynamicDetail():List<DynamicAndImages>


}