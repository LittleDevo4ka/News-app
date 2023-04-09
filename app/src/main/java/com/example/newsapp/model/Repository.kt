package com.example.newsapp.model

import android.content.Context
import com.example.newsapp.model.retrofit.NewsAPIService
import com.example.newsapp.model.retrofit.news.News
import com.example.newsapp.model.room.AppDatabaseModel
import com.example.newsapp.model.room.HomeNews
import com.example.newsapp.model.room.NewsEntity
import com.example.newsapp.model.room.NewsInfo
import com.example.newsapp.viewModel.RepositoryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Repository(private val viewModel: RepositoryViewModel, context: Context) {

    private val newsAPIService = NewsAPIService(this)

    private val roomDao = AppDatabaseModel.getDatabase(context).getDao()

    fun updateTopNews() {
        newsAPIService.getTopNews()
    }

    fun getAllTopNews(): Flow<List<NewsInfo>> {
        return roomDao.getAllTopNews()
    }

    fun getAllHomeNews(): Flow<List<HomeNews>> {
        return roomDao.gelAllTopNewsHome()
    }

    suspend fun deleteAll() {
        roomDao.deleteAll()
    }

    suspend fun insertNews(newsEntity: NewsEntity) {
        roomDao.addNewsEntity(newsEntity)
    }

    fun setTopNews(data: News?, code: Int) {
        viewModel.setTopNews(data, code)
    }

     fun getNewsById(id: Int): Flow<NewsInfo> {
        return roomDao.getNewsById(id)
    }
}