package com.example.lab2.presentation.contract

import com.example.lab2.domain.entity.Stock
import com.example.lab2.presentation.chart.ChartType
import com.example.lab2.presentation.chart.PricePoint
import com.example.lab2.presentation.chart.TimePeriod
import kotlinx.coroutines.CoroutineScope

interface StockChartContract {
    interface View {
        fun setToolbarTitle(title: String)
        fun showLineChart()
        fun showCandleChart()
        fun renderHoldings(
            quantityText: String,
            currentPriceText: String,
            avgPriceText: String,
            totalValueText: String,
            profitText: String,
            profitPositive: Boolean
        )
        fun renderChart(points: List<PricePoint>, type: ChartType)
        fun navigateBack()
        fun openBuySheet(stock: Stock)
        fun openSellSheet(stock: Stock)
    }

    interface Presenter {
        fun attach(view: View, stock: Stock, coroutineScope: CoroutineScope)
        fun detach()
        fun onBackClicked()
        fun onBuyClicked()
        fun onSellClicked()
        fun onChartTypeSelected(type: ChartType)
        fun onPeriodSelected(period: TimePeriod)
        fun onViewReady()
    }
}
