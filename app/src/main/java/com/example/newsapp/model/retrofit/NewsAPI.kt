package com.example.newsapp.model.retrofit

import com.example.newsapp.model.retrofit.news.News
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface NewsAPI {

    @GET
    fun getNews(@Url url: String): Call<News>


}