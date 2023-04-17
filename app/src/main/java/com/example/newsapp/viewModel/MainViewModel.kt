package com.example.newsapp.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import com.example.newsapp.model.Repository
import com.example.newsapp.model.retrofit.news.News
import com.example.newsapp.model.room.NewsInfo
import com.example.newsapp.model.room.ShortNews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application), RepositoryViewModel {

    private val repository: Repository = Repository(this, application)

    private val responseCode: MutableStateFlow<Int?> = MutableStateFlow(null)

    private var savedNews: Flow<NewsInfo>? = null

    private var trendingCountry: String
    private var trendingCategory: String

    private var searchQuery: String
    private var sortBy: String
    private var searchLanguage: String

    private val saveInfo: SharedPreferences

    init {
        saveInfo = application.getSharedPreferences("saveInfo", Context.MODE_PRIVATE)
        trendingCountry = saveInfo.getString("trendingCountry", "us").toString()
        trendingCategory = saveInfo.getString("trendingCategory", "general").toString()

        sortBy = saveInfo.getString("sortBy", "publishedAt").toString()
        searchLanguage = saveInfo.getString("searchLanguage", "en").toString()
        searchQuery = saveInfo.getString("searchQuery", "").toString()
    }

    fun getSortBy(): String {
        return sortBy
    }
    fun setSortBy(sort: String) {
        sortBy = sort

        saveInfo.edit().putString("sortBy", sortBy).apply()
    }

    fun getSearchLanguage(): String {
        return searchLanguage
    }
    fun setSearchLanguage(language: String) {
        searchLanguage = language

        saveInfo.edit().putString("searchLanguage", searchLanguage).apply()
    }

    fun getSearchQuery(): String {
        return searchQuery
    }
    fun setSearchQuery(query: String) {
        searchQuery = query

        saveInfo.edit().putString("searchQuery", searchQuery).apply()
    }

    fun setTrendingCountry(countryCode: String) {
        trendingCountry = countryCode

        saveInfo.edit().putString("trendingCountry", trendingCountry).apply()
    }
    fun getTrendingCountry(): String {
        return trendingCountry
    }

    fun setTrendingCategory(category: String) {
        trendingCategory = category

        saveInfo.edit().putString("trendingCategory", trendingCategory).apply()
    }

    fun getTrendingCategory(): String {
        return trendingCategory
    }

    fun updateTopNews() {
        responseCode.value = null
        repository.updateTopNews(trendingCountry, trendingCategory)
    }

    fun findArticles() {
        responseCode.value = null
        repository.findArticles(searchQuery, searchLanguage, sortBy)
    }

    fun getResponseCode(): MutableStateFlow<Int?> {
        return responseCode
    }


    fun getAllTopNewsShort(): Flow<List<ShortNews>> {
        return repository.getAllTopNewsShort()
    }

    override fun setTopNews(data: News?, code: Int) {
        if(data != null) {
            savedNews = null
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAllTopNews()
            }
            for (i in 0 until  data.articles.size) {
                val newsEntity = data.articles[i].toNewsEntity()
                viewModelScope.launch(Dispatchers.IO) {
                    repository.insertNews(newsEntity)
                }
            }
        }
        responseCode.value = code
    }

    fun getAllArticlesShort(): Flow<List<ShortNews>> {
        return repository.getAllArticlesShort()
    }

    override fun setArticles(data: News?, code: Int) {
        if(data != null) {
            savedNews = null
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAllArticles()
            }
            for (i in 0 until  data.articles.size) {
                val articlesEntity = data.articles[i].toArticlesEntity()
                viewModelScope.launch(Dispatchers.IO) {
                    repository.insertArticles(articlesEntity)
                }
            }
        }
        responseCode.value = code
    }

    fun saveNews(isArticle: Boolean, newsId: Int) {
        savedNews = if (isArticle) {
            repository.getArticleById(newsId)
        } else {
            repository.getNewsById(newsId)
        }
    }

    fun getSavedNews(): Flow<NewsInfo>? {
        return savedNews
    }

}