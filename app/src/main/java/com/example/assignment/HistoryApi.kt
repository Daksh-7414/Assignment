package com.example.assignment

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface HistoryApi {

    @POST("/history")
    suspend fun uploadHistory(
        @Body historyList: List<HistoryModelAPi>
    ): Response<Unit>
}