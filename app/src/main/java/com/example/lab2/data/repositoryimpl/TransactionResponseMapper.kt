package com.example.lab2.data.repositoryimpl

import com.example.lab2.data.api.TransactionResponseDto
import com.example.lab2.domain.entity.Portfolio
import com.example.lab2.domain.entity.TradeResult

internal fun TransactionResponseDto.toTradeResult(): TradeResult {
    val p = portfolio
    val domainPortfolio: Portfolio? =
        if (p != null) p.toPortfolio() else null
    return TradeResult(
        success = success == true,
        message = message.orEmpty(),
        portfolio = domainPortfolio
    )
}
