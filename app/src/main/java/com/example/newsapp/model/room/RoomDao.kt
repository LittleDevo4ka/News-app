package com.example.newsapp.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.newsapp.model.retrofit.news.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {
    @Query("SELECT * FROM news")
    fun getAllTopNews(): Flow<List<NewsInfo>>

    @Query("SELECT * FROM news")
    fun gelAllTopNewsHome(): Flow<List<HomeNews>>

    @Insert
    fun addNewsEntity(newsEntity: NewsEntity)

    @Query("DELETE FROM news")
    fun deleteAll()
}