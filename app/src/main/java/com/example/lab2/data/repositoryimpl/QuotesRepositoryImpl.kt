package com.example.lab2.data.repositoryimpl

import com.example.lab2.data.MoexStockApi
import com.example.lab2.domain.entity.StockQuote
import com.example.lab2.domain.repository.QuotesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class QuotesRepositoryImpl(
    private val moex: MoexStockApi = MoexStockApi()
) : QuotesRepository {

    override suspend fun getQuotes(symbols: List<String>): List<StockQuote> = coroutineScope {
        symbols
            .map { symbol ->
                async {
                    runCatching { moex.fetchQuote(symbol) }.getOrNull()
                }
            }
            .awaitAll()
            .filterNotNull()
    }
}
