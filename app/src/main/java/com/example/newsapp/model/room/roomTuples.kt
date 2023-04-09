package com.example.newsapp.model.room

data class HomeNews(
    val id: Int,
    val title: String?,
    val author: String?,
    val urlToImage: String?
)

data class NewsInfo(
    val id: Int,
    val title: String?,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val url: String?,
    val urlToImage: String?
)