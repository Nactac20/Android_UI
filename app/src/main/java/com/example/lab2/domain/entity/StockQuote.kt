package com.example.lab2.domain.entity

data class StockQuote(
    val symbol: String,
    val name: String,
    val price: Double,
    val changeAbs: Double,
    val changePct: Double
)
