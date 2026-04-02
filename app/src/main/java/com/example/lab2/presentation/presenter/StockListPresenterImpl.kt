package com.example.lab2.presentation.presenter

import com.example.lab2.domain.repository.QuotesRepository
import com.example.lab2.presentation.contract.StockListContract
import com.example.lab2.presentation.mapper.toDisplayStock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StockListPresenterImpl(
    private val quotesRepository: QuotesRepository,
    private val symbols: List<String>
) : StockListContract.Presenter {

    private var view: StockListContract.View? = null
    private var scope: CoroutineScope? = null
    private var loadJob: Job? = null

    override fun attach(view: StockListContract.View, coroutineScope: CoroutineScope) {
        this.view = view
        this.scope = coroutineScope
    }

    override fun detach() {
        loadJob?.cancel()
        loadJob = null
        scope = null
        view = null
    }

    override fun loadQuotes() {
        val v = view ?: return
        val sc = scope ?: return
        v.renderLoading()
        v.setListInteractionEnabled(false)
        loadJob?.cancel()
        loadJob = sc.launch {
            val result = runCatching { quotesRepository.getQuotes(symbols) }
            val quotes = result.getOrElse { throwable ->
                v.showError("Ошибка загрузки курсов: ${throwable.message}")
                emptyList()
            }
            val newStocks = quotes.map { it.toDisplayStock() }
            v.renderStockList(newStocks)
            v.setListInteractionEnabled(true)
            if (newStocks.isEmpty() && result.isSuccess) {
                v.showEmptyMessage()
            }
        }
    }

    override fun onStockClicked(stock: com.example.lab2.domain.entity.Stock) {
        view?.navigateToStockChart(stock)
    }
}
