package com.sscorp.stocks.api

import com.sscorp.stocks.responses.CompaniesResponse
import com.sscorp.stocks.responses.ProfileResponse
import com.sscorp.stocks.responses.StockResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApiService {

    // Запрос на получение тикеров компаний
    @GET("index/constituents")
    suspend fun getCompanies(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): CompaniesResponse

    // Запрос профиля компании
    @GET("stock/profile2")
    suspend fun getProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): ProfileResponse

    // Запрос информации об акциях компании
    @GET("quote")
    suspend fun getStock(
        @Query("symbol") symbol: String,
        @Query("token") token: String
    ): StockResponse
}