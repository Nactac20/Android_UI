package com.example.lab2.domain.repository

import com.example.lab2.domain.entity.StockQuote

interface QuotesRepository {
    suspend fun getQuotes(symbols: List<String>): List<StockQuote>
}
