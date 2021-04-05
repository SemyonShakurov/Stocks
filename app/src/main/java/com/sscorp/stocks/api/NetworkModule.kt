package com.sscorp.stocks.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit

class NetworkModule {
    companion object {

        // URL API
        private const val BASE_URL = "https://finnhub.io/api/v1/"

        // API KEY
        const val API_KEY = "c1lfo5237fkodrjd39g0"

        private val json = Json { ignoreUnknownKeys = true }

        private val contentType = "application/json".toMediaType()

        private val httpClient = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        @ExperimentalSerializationApi
        private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        @ExperimentalSerializationApi
        val finnhubApiService: FinnhubApiService =
            retrofit.create()
    }
}