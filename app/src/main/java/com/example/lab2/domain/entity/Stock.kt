package com.example.lab2.domain.entity

import com.example.lab2.domain.util.parsePrice

data class Stock(
    val symbol: String,
    val name: String,
    val price: String,
    val change: String,
    val quantity: Int = 10
) {
    val currentPrice: Double
        get() = parsePrice(price)
}
