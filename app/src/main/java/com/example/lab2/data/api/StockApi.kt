package com.example.lab2.data.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StockApi {
    @POST("/api/transactions/buy")
    suspend fun buy(@Body body: TransactionRequest): TransactionResponseDto

    @POST("/api/transactions/sell")
    suspend fun sell(@Body body: TransactionRequest): TransactionResponseDto

    @GET("/api/profile/statistics")
    suspend fun portfolio(): PortfolioDto
}
