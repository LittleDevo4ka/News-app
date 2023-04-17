package com.example.newsapp.viewModel

import com.example.newsapp.model.retrofit.news.News

interface RepositoryViewModel {

    fun setTopNews(data: News?, code: Int)

    fun setArticles(data: News?, code: Int)
}