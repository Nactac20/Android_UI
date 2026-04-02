package com.example.lab2.presentation.presenter

import com.example.lab2.SharedStatsViewModel
import com.example.lab2.domain.entity.Stock
import com.example.lab2.domain.util.parsePrice
import com.example.lab2.presentation.chart.ChartType
import com.example.lab2.presentation.chart.TimePeriod
import com.example.lab2.presentation.chart.generateChartData
import com.example.lab2.presentation.contract.StockChartContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class StockChartPresenterImpl(
    private val sharedViewModel: SharedStatsViewModel
) : StockChartContract.Presenter {

    private var view: StockChartContract.View? = null
    private var stock: Stock? = null
    private var chartType = ChartType.LINE
    private var period = TimePeriod.MONTH
    private val jobs = mutableListOf<Job>()

    override fun attach(view: StockChartContract.View, stock: Stock, coroutineScope: CoroutineScope) {
        this.view = view
        this.stock = stock
        view.setToolbarTitle("${stock.symbol} - ${stock.name}")
        view.showLineChart()
        jobs += coroutineScope.launch {
            sharedViewModel.positions.collectLatest {
                renderHoldings()
            }
        }
        jobs += coroutineScope.launch {
            sharedViewModel.balance.collectLatest { }
        }
    }

    override fun detach() {
        jobs.forEach { it.cancel() }
        jobs.clear()
        view = null
        stock = null
    }

    override fun onBackClicked() {
        view?.navigateBack()
    }

    override fun onBuyClicked() {
        val s = stock ?: return
        view?.openBuySheet(s)
    }

    override fun onSellClicked() {
        val s = stock ?: return
        view?.openSellSheet(s)
    }

    override fun onChartTypeSelected(type: ChartType) {
        chartType = type
        when (type) {
            ChartType.LINE -> view?.showLineChart()
            ChartType.CANDLE -> view?.showCandleChart()
        }
        pushChartUpdate()
    }

    override fun onPeriodSelected(period: TimePeriod) {
        this.period = period
        pushChartUpdate()
    }

    override fun onViewReady() {
        sharedViewModel.refreshPortfolio()
        renderHoldings()
        pushChartUpdate()
    }

    private fun renderHoldings() {
        val s = stock ?: return
        val v = view ?: return
        val currentPrice = parsePrice(s.price)
        val qty = sharedViewModel.getPosition(s.symbol)
        val averagePrice = currentPrice * 0.95
        val totalValue = currentPrice * qty
        val profit = (currentPrice - averagePrice) * qty
        v.renderHoldings(
            quantityText = "$qty шт.",
            currentPriceText = String.format("%.2f ₽", currentPrice),
            avgPriceText = String.format("%.2f ₽", averagePrice),
            totalValueText = String.format("%.2f ₽", totalValue),
            profitText = String.format("%.2f ₽", profit),
            profitPositive = profit >= 0
        )
    }

    private fun pushChartUpdate() {
        val s = stock ?: return
        val v = view ?: return
        val data = generateChartData(parsePrice(s.price), period)
        v.renderChart(data, chartType)
    }
}
