package com.example.newsapp.model

import android.content.Context
import com.example.newsapp.model.retrofit.NewsAPIService
import com.example.newsapp.model.retrofit.news.News
import com.example.newsapp.model.room.AppDatabaseModel
import com.example.newsapp.model.room.HomeNews
import com.example.newsapp.model.room.NewsEntity
import com.example.newsapp.model.room.NewsInfo
import com.example.newsapp.viewModel.RepositoryViewModel
import kotlinx.coroutines.flow.Flow

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

    fun setTopNews(data: News?, code: Int) {
        viewModel.setTopNews(code)
        if(data != null) {
            roomDao.deleteAll()
            for (i in 0 until  data.articles.size) {
                val newsEntity = data.articles[i].toNewsEntity()
                roomDao.addNewsEntity(newsEntity)
            }
        }
    }
}