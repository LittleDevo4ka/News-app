package com.example.newsapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.newsapp.model.Repository
import com.example.newsapp.model.retrofit.news.News
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel(application: Application): AndroidViewModel(application), RepositoryViewModel {

    private val responseCode: MutableStateFlow<Int?> = MutableStateFlow(null)
    private var topNews: News? = null

    private val repository: Repository = Repository(this)


    fun updateTopNews() {
        responseCode.value = null
        repository.updateTopNews()
    }

    fun getResponseCode(): MutableStateFlow<Int?> {
        return responseCode
    }

    fun getTopNews(): News? {
        return topNews
    }


    override fun setTopNews(data: News?, code: Int) {
        responseCode.value = code
        topNews = data
    }

}