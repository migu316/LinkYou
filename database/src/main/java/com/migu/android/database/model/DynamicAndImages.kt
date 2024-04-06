package com.migu.android.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.migu.android.database.model.base.DynamicEntity

@Entity
data class DynamicAndImages(
    @PrimaryKey
    val objectId: String,
    val dynamicEntity: DynamicEntity,
    val urls: List<String> = listOf()
)