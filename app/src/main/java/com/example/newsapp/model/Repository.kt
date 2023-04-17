package com.example.newsapp.model

import android.content.Context
import com.example.newsapp.model.retrofit.NewsAPIService
import com.example.newsapp.model.retrofit.news.Article
import com.example.newsapp.model.retrofit.news.News
import com.example.newsapp.model.room.*
import com.example.newsapp.viewModel.RepositoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language

class Repository(private val viewModel: RepositoryViewModel, context: Context) {

    private val newsAPIService = NewsAPIService(this)

    private val roomDao = AppDatabaseModel.getDatabase(context).getDao()

    fun updateTopNews(country: String, category: String) {
        newsAPIService.getTopNews(country, category)
    }

    fun setTopNews(data: News?, code: Int) {
        viewModel.setTopNews(data, code)
    }

    fun findArticles(searchQuery: String, language: String, sortBy: String) {
        newsAPIService.getArticles(searchQuery, language, sortBy)
    }

    fun setArticles(data: News?, code: Int) {
        viewModel.setArticles(data, code)
    }

    fun getAllTopNewsShort(): Flow<List<ShortNews>> {
        return roomDao.getAllTopNewsShort()
    }

    suspend fun deleteAllTopNews() {
        roomDao.deleteAllTopNews()
    }

    suspend fun insertNews(newsEntity: NewsEntity) {
        roomDao.addTopNewsEntity(newsEntity)
    }

    fun getNewsById(id: Int): Flow<NewsInfo> {
        return roomDao.getNewsById(id)
    }

    fun getAllArticlesShort(): Flow<List<ShortNews>> {
        return roomDao.getAllArticlesShort()
    }

    suspend fun deleteAllArticles() {
        roomDao.deleteAllArticles()
    }

    suspend fun insertArticles(articlesEntity: ArticlesEntity) {
        roomDao.addArticlesEntity(articlesEntity)
    }

    fun getArticleById(articleId: Int): Flow<NewsInfo> {
        return roomDao.getArticleById(articleId)
    }
}