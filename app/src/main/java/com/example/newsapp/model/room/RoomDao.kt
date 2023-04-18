package com.example.newsapp.model.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RoomDao {

    @Query("SELECT * FROM news")
    fun getAllTopNewsShort(): Flow<List<ShortNews>>

    @Insert
    suspend fun addTopNewsEntity(newsEntity: NewsEntity)

    @Query("DELETE FROM news")
    suspend fun deleteAllTopNews()

    @Query("SELECT * FROM news WHERE id=:id")
    fun getNewsById(id: Int): Flow<NewsInfo>

    @Query("SELECT * FROM articles")
    fun getAllArticlesShort(): Flow<List<ShortNews>>

    @Insert
    suspend fun addArticlesEntity(articlesEntity: ArticlesEntity)

    @Query("DELETE FROM articles")
    suspend fun deleteAllArticles()

    @Query("SELECT * FROM articles WHERE id=:id")
    fun getArticleById(id: Int): Flow<NewsInfo>
}