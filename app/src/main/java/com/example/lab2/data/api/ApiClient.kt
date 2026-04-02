package com.example.lab2.data.api

import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://10.119.89.24:8888/"

    private val okHttp: OkHttpClient by lazy {
        val dispatcher = Dispatcher(Executors.newFixedThreadPool(4)).apply {
            maxRequests = 8
            maxRequestsPerHost = 4
        }

        OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .retryOnConnectionFailure(false)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    val api: StockApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StockApi::class.java)
    }
}
