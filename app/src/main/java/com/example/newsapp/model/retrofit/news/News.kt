package com.example.newsapp.model.retrofit.news

data class News(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)