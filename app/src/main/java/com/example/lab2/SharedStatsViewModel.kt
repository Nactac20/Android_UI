package com.example.lab2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab2.data.repositoryimpl.PortfolioRepositoryImpl
import com.example.lab2.domain.repository.PortfolioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedStatsViewModel(
    private val portfolioRepository: PortfolioRepository = PortfolioRepositoryImpl()
) : ViewModel() {

    private val _balance = MutableStateFlow(10000.0)
    val balance: StateFlow<Double> = _balance

    private val _stockCount = MutableStateFlow(5)
    val stockCount: StateFlow<Int> = _stockCount

    private val _positions = MutableStateFlow<Map<String, Int>>(emptyMap())
    val positions: StateFlow<Map<String, Int>> = _positions

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError

    fun updatePortfolio(newBalance: Double, newStockCount: Int, newPositions: Map<String, Int>) {
        _balance.value = newBalance
        _stockCount.value = newStockCount
        _positions.value = newPositions
    }

    fun getPosition(symbol: String): Int = _positions.value[symbol] ?: 0

    fun refreshPortfolio() {
        viewModelScope.launch {
            runCatching { portfolioRepository.getPortfolio() }
                .onSuccess { p ->
                    _lastError.value = null
                    updatePortfolio(
                        newBalance = p.balance,
                        newStockCount = p.stocksCount,
                        newPositions = p.positions
                    )
                }
                .onFailure { e ->
                    _lastError.value = e.message ?: e.toString()
                }
        }
    }
}
