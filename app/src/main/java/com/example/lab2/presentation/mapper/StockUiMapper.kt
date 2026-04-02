package com.example.lab2.presentation.mapper

import com.example.lab2.domain.entity.Stock
import com.example.lab2.domain.entity.StockQuote
import java.util.Locale

fun StockQuote.toDisplayStock(): Stock {
    val priceText = String.format(Locale.US, "%.2f ₽", price)
    val changeText = String.format(Locale.US, "%+.2f ₽ (%+.2f%%)", changeAbs, changePct)
    return Stock(
        symbol = symbol,
        name = name,
        price = priceText,
        change = changeText
    )
}
