package com.example.newsapp.viewModel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.model.Repository
import com.example.newsapp.model.retrofit.news.News
import com.example.newsapp.model.room.NewsInfo
import com.example.newsapp.model.room.ShortNews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application): AndroidViewModel(application), RepositoryViewModel {

    private val repository: Repository = Repository(this, application)

    private val responseCode: MutableStateFlow<Int?> = MutableStateFlow(null)

    private var savedNews: Flow<NewsInfo>? = null

    private var trendingCountry: String
    private var trendingCountryCode: String
    private var trendingCategory: String
    private var countries = mutableMapOf<String, String>()

    private var searchQuery: String
    private var sortBy: String
    private var searchLanguage: String
    private var searchLanguageCode: String
    private var languages = mutableMapOf<String, String>()

    private val saveInfo: SharedPreferences

    private val defaultCountry = Locale("", "us").displayCountry

    init {
        saveInfo = application.getSharedPreferences("saveInfo", Context.MODE_PRIVATE)
        trendingCountry = saveInfo.getString("trendingCountry", defaultCountry).toString()
        trendingCategory = saveInfo.getString("trendingCategory", "general").toString()
        createCountriesMap()
        trendingCountryCode = countries[trendingCountry] ?: ""


        sortBy = saveInfo.getString("sortBy", "publishedAt").toString()
        searchLanguage = saveInfo.getString("searchLanguage", "-").toString()
        searchQuery = saveInfo.getString("searchQuery", "").toString()
        createLanguageMap()
        searchLanguageCode = languages[searchLanguage] ?: ""
    }

    fun getSortBy(): String {
        return sortBy
    }
    fun setSortBy(sort: String) {
        sortBy = sort

        saveInfo.edit().putString("sortBy", sortBy).apply()
    }

    fun getSearchLanguage(): String {
        return searchLanguage
    }
    fun setSearchLanguage(language: String) {
        if (language == "-") {
            searchLanguage = language
            searchLanguageCode = ""

            saveInfo.edit().putString("searchLanguage", searchLanguage).apply()
            return
        }

        val languageCode = languages[language] ?: ""
        if (languageCode.isNotEmpty()) {
            searchLanguage = language
            searchLanguageCode = languageCode

            saveInfo.edit().putString("searchLanguage", searchLanguage).apply()
        }
    }

    fun getSearchQuery(): String {
        return searchQuery
    }
    fun setSearchQuery(query: String) {
        searchQuery = query

        saveInfo.edit().putString("searchQuery", searchQuery).apply()
    }

    fun setTrendingCountry(country: String) {
        val countryCode = countries[country] ?: ""
        if (countryCode.isNotEmpty()) {
            trendingCountry = country
            trendingCountryCode = countryCode

            saveInfo.edit().putString("trendingCountry", trendingCountry).apply()
        }
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

    fun updateTopNews() {
        responseCode.value = null
        repository.updateTopNews(trendingCountryCode, trendingCategory)
    }

    fun findArticles() {
        responseCode.value = null
        repository.findArticles(searchQuery, searchLanguageCode, sortBy)
    }

    fun getResponseCode(): MutableStateFlow<Int?> {
        return responseCode
    }


    fun getAllTopNewsShort(): Flow<List<ShortNews>> {
        return repository.getAllTopNewsShort()
    }

    override fun setTopNews(data: News?, code: Int) {
        if(data != null) {
            savedNews = null
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAllTopNews()
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

    fun getAllArticlesShort(): Flow<List<ShortNews>> {
        return repository.getAllArticlesShort()
    }

    override fun setArticles(data: News?, code: Int) {
        if(data != null) {
            savedNews = null
            viewModelScope.launch(Dispatchers.IO) {
                repository.deleteAllArticles()
            }
            for (i in 0 until  data.articles.size) {
                val articlesEntity = data.articles[i].toArticlesEntity()
                viewModelScope.launch(Dispatchers.IO) {
                    repository.insertArticles(articlesEntity)
                }
            }
        }
        responseCode.value = code
    }

    fun saveNews(isArticle: Boolean, newsId: Int) {
        savedNews = if (isArticle) {
            repository.getArticleById(newsId)
        } else {
            repository.getNewsById(newsId)
        }
    }

    fun getSavedNews(): Flow<NewsInfo>? {
        return savedNews
    }
    private fun createCountriesMap() {
        val countriesCode = listOf("ae", "ar", "at", "au", "be", "bg", "br", "ca", "ch", "cn",
            "co", "cu", "cz", "de", "eg", "fr", "gb", "gr", "hk", "hu", "id", "ie", "il", "in",
            "it", "jp", "kr", "lt", "lv", "ma", "mx", "my", "ng", "nl", "no", "nz", "ph", "pl",
            "pt", "ro", "rs", "ru", "sa", "se", "sg", "si", "sk", "th", "tr", "tw", "ua", "us",
            "ve", "za")

        countriesCode.forEach {
            countries[Locale("", it).displayCountry] = it
        }
    }

    fun getAllCountries(): Array<String> {
        return countries.keys.toTypedArray()
    }

    private fun createLanguageMap() {
        val languageCodes = listOf("ar", "de", "en", "es", "fr", "he",
            "it", "nl", "no", "pt", "ru", "sv", "zh", "-")
        languageCodes.forEach {
            languages[Locale(it, "").displayLanguage] = it
        }
    }

    fun getAllLanguages(): Array<String> {
        return languages.keys.toTypedArray()
    }


}