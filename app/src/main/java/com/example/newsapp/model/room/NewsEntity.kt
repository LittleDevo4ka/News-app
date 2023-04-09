package com.example.newsapp.model.room

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "news",
indices = [Index("title")]
)
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String?,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val url: String?,
    val urlToImage: String?
)
