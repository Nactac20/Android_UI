package com.example.lab2.domain.entity

data class Portfolio(
    val balance: Double,
    val stocksCount: Int,
    val positions: Map<String, Int>
)
