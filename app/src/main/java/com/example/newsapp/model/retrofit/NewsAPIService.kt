package com.example.newsapp.model.retrofit

import android.util.Log
import com.example.newsapp.BuildConfig
import com.example.newsapp.model.Repository
import com.example.newsapp.model.retrofit.news.News
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class NewsAPIService(private val repository: Repository) {

    private val baseTopNewsURL = "https://newsapi.org/v2/top-headlines?"
    private val baseArticlesURL = "https://newsapi.org/v2/everything?"
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://newsapi.org/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NewsAPI::class.java)

    private val tag = "NewsAPIService"

    fun getTopNews(country: String, category: String) {
        val topNewsUrl: String = if (category.isEmpty()) {
            "${baseTopNewsURL}country=$country&apiKey=${BuildConfig.News_API_KEY}"
        } else {
            "${baseTopNewsURL}country=$country&category=$category" +
                    "&apiKey=${BuildConfig.News_API_KEY}"
        }

        val call = retrofit.getNews(topNewsUrl)
        call.enqueue(object: Callback<News>{
            override fun onResponse(call: Call<News>, response: Response<News>) {
                if (response.isSuccessful) {
                    Log.i(tag, "getTopNews: ${response.body()?.status}")
                    if (response.body()?.articles?.size == 0) {
                        sendTopNews(null ,100)
                    } else {
                        response.body()?.let { sendTopNews(it, 200) }
                    }
                } else {
                    Log.w(tag, "getTopNews: Something went wrong: ${response.code()}")
                    sendTopNews(null, 300)
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.w("tag", "getTopNew: Something really went wrong: ${t.message}")
                sendTopNews(null, 400)
            }

        })
    }

    fun sendTopNews(data: News?, code: Int) {

        repository.setTopNews(data, code)
    }

    fun getArticles(searchQuery: String, language: String, sortBy: String) {
        val articlesURL = if (language.isNotEmpty()) {
            "${baseArticlesURL}q=\"$searchQuery\"&language=$language" +
                    "&sortBy=$sortBy&pageSize=20&apiKey=${BuildConfig.News_API_KEY}"
        } else {
            "${baseArticlesURL}q=\"$searchQuery\"" +
                    "&sortBy=$sortBy&pageSize=20&apiKey=${BuildConfig.News_API_KEY}"
        }


        val call = retrofit.getNews(articlesURL)
        call.enqueue(object: Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                if(response.isSuccessful) {
                    Log.i(tag, "getArticles: ${response.body()?.status}")
                    if (response.body()?.articles?.size == 0) {
                        sendTopNews(null ,100)
                    } else {
                        response.body()?.let { sendArticles(it, 200) }
                    }
                } else {
                    Log.w(tag, "getArticles: Something went wrong: ${response.code()}")
                    sendArticles(null, 300)
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.w("tag", "getArticles: Something really went wrong: ${t.message}")
                sendArticles(null, 400)
            }

        })
    }

    fun sendArticles(data: News?, code: Int) {
        repository.setArticles(data, code)
    }


}