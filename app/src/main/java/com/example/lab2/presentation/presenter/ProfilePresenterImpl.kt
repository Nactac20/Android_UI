package com.example.lab2.presentation.presenter

import android.os.Bundle
import com.example.lab2.SharedStatsViewModel
import com.example.lab2.domain.repository.QuotesRepository
import com.example.lab2.presentation.contract.ProfileContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfilePresenterImpl(
    private val quotesRepository: QuotesRepository,
    private val sharedViewModel: SharedStatsViewModel,
    private val symbols: List<String>
) : ProfileContract.Presenter {

    private var view: ProfileContract.View? = null
    private var scope: CoroutineScope? = null
    private val jobs = mutableListOf<Job>()

    override fun attach(view: ProfileContract.View, coroutineScope: CoroutineScope) {
        this.view = view
        this.scope = coroutineScope
    }

    override fun detach() {
        jobs.forEach { it.cancel() }
        jobs.clear()
        scope = null
        view = null
    }

    override fun onViewReady(username: String?, savedInstanceState: Bundle?) {
        val v = view ?: return
        val sc = scope ?: return
        val name = username ?: "Иван Петров"
        v.bindUserHeader(name, "$name@example.com".lowercase(), "01.01.2024")

        jobs += sc.launch {
            sharedViewModel.balance.collectLatest { balance ->
                v.renderBalance("%.0f ₽".format(balance))
            }
        }
        jobs += sc.launch {
            sharedViewModel.stockCount.collectLatest { count ->
                v.renderStockCount(count.toString())
            }
        }

        if (savedInstanceState == null) {
            sharedViewModel.refreshPortfolio()
        }

        jobs += sc.launch {
            runCatching { quotesRepository.getQuotes(symbols) }.getOrNull()
            v.updateBottomNavStaticTitles()
        }
    }

    override fun onRefreshPortfolioClicked() {
        sharedViewModel.refreshPortfolio()
    }

    override fun onDarkThemeChanged(enabled: Boolean) {
    }
}
