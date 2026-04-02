package com.example.lab2.api

data class TransactionRequest(
    val symbol: String,
    val quantity: Int
)

data class TransactionResponseDto(
    val success: Boolean?,
    val message: String?,
    val transaction: TransactionDto?,
    val portfolio: PortfolioDto?
)

data class TransactionDto(
    val id: String?,
    val symbol: String?,
    val quantity: Int?,
    val price: Double?,
    val timestamp: String?
)