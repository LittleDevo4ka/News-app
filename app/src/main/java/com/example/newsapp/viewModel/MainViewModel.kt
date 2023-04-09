package com.example.newsapp.viewModel

import android.app.Application
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

    private val responseCode: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val repository: Repository = Repository(this, application)

    private var newsId: Int = -1

    fun setNewsId(tempId: Int){
        newsId = tempId
    }

    fun getNewsId(): Int {
        return newsId
    }

    fun updateTopNews() {
        responseCode.value = null
        repository.updateTopNews()
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