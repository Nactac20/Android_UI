package com.example.lab2.domain.entity

data class TradeResult(
    val success: Boolean,
    val message: String,
    val portfolio: Portfolio?
)
