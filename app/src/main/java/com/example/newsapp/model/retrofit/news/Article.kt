package com.example.newsapp.model.retrofit.news

import com.example.newsapp.model.room.NewsEntity

data class Article(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: Source,
    val title: String,
    val url: String?,
    val urlToImage: String?
) {
    fun toNewsEntity(): NewsEntity {
        return NewsEntity(0, title, author, content, description, publishedAt, url, urlToImage)
    }
}