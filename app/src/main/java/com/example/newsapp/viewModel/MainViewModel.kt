package com.example.newsapp.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.model.Repository
import com.example.newsapp.model.retrofit.news.News
import com.example.newsapp.model.room.HomeNews
import com.example.newsapp.model.room.NewsInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application), RepositoryViewModel {

    private val repository: Repository = Repository(this, application)

    private val responseCode: MutableStateFlow<Int?> = MutableStateFlow(null)

    private var newsId: Int = -1

    private var trendingCountry: String
    private var trendingCategory: String

    private val saveInfo: SharedPreferences

    init {
        saveInfo = application.getSharedPreferences("saveInfo", Context.MODE_PRIVATE)
        trendingCountry = saveInfo.getString("trendingCountry", "us").toString()
        trendingCategory = saveInfo.getString("trendingCategory", "general").toString()
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

    fun setNewsId(tempId: Int){
        newsId = tempId
    }

    fun getNewsId(): Int {
        return newsId
    }

    fun updateTopNews() {
        responseCode.value = null
        repository.updateTopNews(trendingCountry, trendingCategory)
    }

    fun getResponseCode(): MutableStateFlow<Int?> {
        return responseCode
    }

    fun getAllTopNews(): Flow<List<NewsInfo>> {
        return repository.getAllTopNews()
    }

    fun getAllHomeTopNews(): Flow<List<HomeNews>> {
        return repository.getAllHomeNews()
    }


    override fun setTopNews(data: News?, code: Int) {
        if(data != null) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAll()
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

    fun getNewsById(): Flow<NewsInfo> {
        return repository.getNewsById(newsId)
    }

}