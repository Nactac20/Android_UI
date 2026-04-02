package com.example.lab2.data.repositoryimpl

import com.example.lab2.data.api.ApiClient
import com.example.lab2.data.api.StockApi
import com.example.lab2.domain.entity.Portfolio
import com.example.lab2.domain.repository.PortfolioRepository

class PortfolioRepositoryImpl(
    private val api: StockApi = ApiClient.api
) : PortfolioRepository {

    override suspend fun getPortfolio(): Portfolio {
        val dto = api.portfolio()
        return dto.toPortfolio()
    }
}
