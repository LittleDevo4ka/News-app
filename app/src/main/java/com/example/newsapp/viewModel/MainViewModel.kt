package com.example.newsapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.newsapp.model.Repository
import com.example.newsapp.model.retrofit.news.News
import com.example.newsapp.model.room.HomeNews
import com.example.newsapp.model.room.NewsInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel(application: Application): AndroidViewModel(application), RepositoryViewModel {

    private val responseCode: MutableStateFlow<Int?> = MutableStateFlow(null)

    private val repository: Repository = Repository(this, application)


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


    override fun setTopNews(code: Int) {
        if (code == 200) {
            repository.getAllTopNews()
        }
        responseCode.value = code
    }

}