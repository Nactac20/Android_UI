package com.example.lab2.data.api

data class PortfolioDto(
    val balance: Double?,
    val stocksCount: Int?,
    val totalValue: Double?,
    val unrealizedProfit: Double? = null,
    val trades: List<TradeDto>? = null,
    val positions: List<PositionDto>? = null
)

data class PositionDto(
    val symbol: String?,
    val quantity: Int?
)

data class TradeDto(
    val id: String?,
    val symbol: String?,
    val type: String?,
    val profit: Double?
)

fun PortfolioDto.toPositionsMap(): Map<String, Int> =
    positions
        .orEmpty()
        .mapNotNull { p ->
            val sym = p.symbol?.trim()
            val qty = p.quantity
            if (sym.isNullOrEmpty() || qty == null) null else sym to qty
        }
        .toMap()
