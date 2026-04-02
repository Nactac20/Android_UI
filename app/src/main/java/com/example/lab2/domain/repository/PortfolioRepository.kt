package com.example.lab2.domain.repository

import com.example.lab2.domain.entity.Portfolio

interface PortfolioRepository {
    suspend fun getPortfolio(): Portfolio
}
