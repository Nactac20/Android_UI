package com.example.lab2.presentation.presenter

import com.example.lab2.SharedStatsViewModel
import com.example.lab2.domain.entity.Stock
import com.example.lab2.domain.repository.TradeRepository
import com.example.lab2.presentation.contract.BuySellContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BuySellPresenterImpl(
    private val stock: Stock,
    private val mode: String,
    private val sharedViewModel: SharedStatsViewModel,
    private val tradeRepository: TradeRepository
) : BuySellContract.Presenter {

    private var view: BuySellContract.View? = null
    private var scope: CoroutineScope? = null
    private val jobs = mutableListOf<Job>()
    private val pricePerStock = stock.currentPrice

    override fun attach(view: BuySellContract.View, coroutineScope: CoroutineScope) {
        this.view = view
        this.scope = coroutineScope
        view.updateTitleAndButtonLabels(mode == "buy", stock.symbol)
        jobs += coroutineScope.launch {
            sharedViewModel.balance.collectLatest {
                onQuantityTextChanged(view.readQuantityInput())
            }
        }
        jobs += coroutineScope.launch {
            sharedViewModel.positions.collectLatest {
                onQuantityTextChanged(view.readQuantityInput())
            }
        }
    }

    override fun detach() {
        jobs.forEach { it.cancel() }
        jobs.clear()
        scope = null
        view = null
    }

    override fun onQuantityTextChanged(raw: String?) {
        val v = view ?: return
        val qty = raw?.toIntOrNull() ?: 0
        if (qty <= 0) {
            v.updateTotalLabel("", visible = false)
            v.setConfirmEnabled(false)
            v.refreshConfirmButtonStyle()
            return
        }
        val totalAmount = qty * pricePerStock
        val canConfirm = when (mode) {
            "buy" -> {
                val balanceNow = sharedViewModel.balance.value
                val ok = totalAmount <= balanceNow
                if (!ok) {
                    v.updateTotalLabel("Недостаточно средств. Нужно: %.2f ₽".format(totalAmount), visible = true)
                } else {
                    v.updateTotalLabel("Итого: %.2f ₽".format(totalAmount), visible = true)
                }
                ok
            }
            "sell" -> {
                val have = sharedViewModel.getPosition(stock.symbol)
                val ok = qty <= have
                if (!ok) {
                    v.updateTotalLabel("Нельзя продать $qty шт. Доступно: $have шт.", visible = true)
                } else {
                    v.updateTotalLabel("Итого: %.2f ₽".format(totalAmount), visible = true)
                }
                ok
            }
            else -> false
        }
        v.setConfirmEnabled(canConfirm)
        v.refreshConfirmButtonStyle()
    }

    override fun onCancelClicked() {
    }

    override fun onConfirmClicked() {
        val v = view ?: return
        val sc = scope ?: return
        onQuantityTextChanged(v.readQuantityInput())
        if (!v.readQuantityInput().orEmpty().toIntOrNull().let { it != null && it > 0 }) return
        val qty = v.readQuantityInput()?.toIntOrNull() ?: return
        onQuantityTextChanged(v.readQuantityInput())
        val canConfirm = when (mode) {
            "buy" -> qty * pricePerStock <= sharedViewModel.balance.value
            "sell" -> qty <= sharedViewModel.getPosition(stock.symbol)
            else -> false
        }
        if (!canConfirm) return
        sc.launch {
            v.setConfirmEnabled(false)
            v.refreshConfirmButtonStyle()
            try {
                val result = when (mode) {
                    "buy" -> tradeRepository.buy(stock.symbol, qty)
                    "sell" -> tradeRepository.sell(stock.symbol, qty)
                    else -> {
                        v.showToast("Неизвестный режим операции")
                        v.setConfirmEnabled(true)
                        v.refreshConfirmButtonStyle()
                        return@launch
                    }
                }
                if (!result.success) {
                    v.showToast(result.message.ifBlank { "Операция не выполнена" })
                    v.setConfirmEnabled(true)
                    v.refreshConfirmButtonStyle()
                    return@launch
                }
                val portfolio = result.portfolio
                if (portfolio == null) {
                    v.showToast("Пустой ответ портфеля")
                    v.setConfirmEnabled(true)
                    v.refreshConfirmButtonStyle()
                    return@launch
                }
                sharedViewModel.updatePortfolio(
                    portfolio.balance,
                    portfolio.stocksCount,
                    portfolio.positions
                )
                v.showToast(result.message.ifBlank { "Операция выполнена" })
                v.dismissSuccess()
            } catch (e: Exception) {
                v.showToast("Ошибка сети: ${e.message}")
                v.setConfirmEnabled(true)
                v.refreshConfirmButtonStyle()
            }
        }
    }
}

