package com.example.lab2.data.repositoryimpl

import com.example.lab2.data.api.PortfolioDto
import com.example.lab2.data.api.toPositionsMap
import com.example.lab2.domain.entity.Portfolio

internal fun PortfolioDto.toPortfolio(): Portfolio =
    Portfolio(
        balance = balance ?: 0.0,
        stocksCount = stocksCount ?: 0,
        positions = toPositionsMap()
    )
