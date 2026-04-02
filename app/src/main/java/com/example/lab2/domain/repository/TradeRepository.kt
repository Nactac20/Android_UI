package com.example.lab2.domain.repository

import com.example.lab2.domain.entity.TradeResult

interface TradeRepository {
    suspend fun buy(symbol: String, quantity: Int): TradeResult
    suspend fun sell(symbol: String, quantity: Int): TradeResult
}
