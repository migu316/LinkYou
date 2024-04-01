package com.migu.android.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DynamicImages(
    @PrimaryKey
    val objectId:String = "",
    val urls:List<String> = listOf()
)