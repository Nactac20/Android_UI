package com.example.lab2.data.repositoryimpl

import com.example.lab2.data.api.ApiClient
import com.example.lab2.data.api.StockApi
import com.example.lab2.data.api.TransactionRequest
import com.example.lab2.domain.entity.TradeResult
import com.example.lab2.domain.repository.TradeRepository

class TradeRepositoryImpl(
    private val api: StockApi = ApiClient.api
) : TradeRepository {

    override suspend fun buy(symbol: String, quantity: Int): TradeResult {
        val body = TransactionRequest(symbol = symbol, quantity = quantity)
        return api.buy(body).toTradeResult()
    }

    override suspend fun sell(symbol: String, quantity: Int): TradeResult {
        val body = TransactionRequest(symbol = symbol, quantity = quantity)
        return api.sell(body).toTradeResult()
    }
}
