package com.example.lab2.data

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class StockRepository(
    private val api: MoexStockApi = MoexStockApi()
) {

    suspend fun fetchQuotes(symbols: List<String>): List<StockQuote> = coroutineScope {
        symbols
            .map { symbol ->
                async {
                    runCatching { api.fetchQuote(symbol) }.getOrNull()
                }
            }
            .awaitAll()
            .filterNotNull()
    }
}

