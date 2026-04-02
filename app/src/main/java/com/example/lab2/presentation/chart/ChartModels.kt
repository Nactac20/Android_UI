package com.example.lab2.presentation.chart

import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

enum class ChartType { LINE, CANDLE }

enum class TimePeriod { WEEK, MONTH, HALF_YEAR, YEAR, ALL_TIME }

data class PricePoint(
    val date: String,
    val price: Double,
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val close: Double = 0.0
)

fun generateChartData(basePrice: Double, period: TimePeriod): List<PricePoint> {
    val random = Random(System.currentTimeMillis())

    val pointsCount = when (period) {
        TimePeriod.WEEK -> 7
        TimePeriod.MONTH -> 30
        TimePeriod.HALF_YEAR -> 60
        TimePeriod.YEAR -> 120
        TimePeriod.ALL_TIME -> 240
    }

    val data = mutableListOf<PricePoint>()
    var currentPrice = basePrice * 0.7

    for (i in 0 until pointsCount) {
        val volatility = when (period) {
            TimePeriod.WEEK -> 0.02
            TimePeriod.MONTH -> 0.03
            TimePeriod.HALF_YEAR -> 0.05
            TimePeriod.YEAR -> 0.08
            TimePeriod.ALL_TIME -> 0.15
        }

        val change = (random.nextDouble() - 0.5) * 2 * volatility * currentPrice
        val open = currentPrice
        val close = currentPrice + change
        val high = max(open, close) + random.nextDouble() * volatility * currentPrice
        val low = min(open, close) - random.nextDouble() * volatility * currentPrice

        val date = when (period) {
            TimePeriod.WEEK -> "Д${i + 1}"
            TimePeriod.MONTH -> "${i + 1}"
            else -> "${i + 1}"
        }

        data.add(PricePoint(date, close, open, high, low, close))
        currentPrice = close
    }

    return data
}
