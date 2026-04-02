package com.example.lab2.presentation.contract

import com.example.lab2.domain.entity.Stock
import kotlinx.coroutines.CoroutineScope

interface StockListContract {
    interface View {
        fun renderLoading()
        fun renderStockList(stocks: List<Stock>)
        fun showError(message: String)
        fun setListInteractionEnabled(enabled: Boolean)
        fun showEmptyMessage()
        fun navigateToStockChart(stock: Stock)
    }

    interface Presenter {
        fun attach(view: View, coroutineScope: CoroutineScope)
        fun detach()
        fun loadQuotes()
        fun onStockClicked(stock: Stock)
    }
}
