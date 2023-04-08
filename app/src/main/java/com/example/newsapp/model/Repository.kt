package com.example.newsapp.model

import com.example.newsapp.model.retrofit.NewsAPIService
import com.example.newsapp.model.retrofit.news.News
import com.example.newsapp.viewModel.RepositoryViewModel

class Repository(private val viewModel: RepositoryViewModel) {

    private val newsAPIService = NewsAPIService(this)

    fun updateTopNews() {
        newsAPIService.getTopNews()
    }

    fun setTopNews(data: News?, code: Int) {
        viewModel.setTopNews(data, code)
    }
}