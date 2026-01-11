package com.example.assignment

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    fun getApi(): HistoryApi {
        return Retrofit.Builder()
            .baseUrl("https://history-links.free.beeceptor.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HistoryApi::class.java)
    }
}