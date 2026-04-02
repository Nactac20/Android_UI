package com.example.lab2

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.lab2.api.ApiClient
import com.example.lab2.api.TransactionRequest
import com.example.lab2.api.toPositionsMap
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class BuySellDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_SYMBOL = "symbol"
        private const val ARG_NAME = "name"
        private const val ARG_PRICE = "price"
        private const val ARG_CHANGE = "change"
        private const val ARG_QUANTITY = "quantity"
        private const val ARG_MODE = "mode"

        fun newInstance(stock: Stock, mode: String) = BuySellDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_SYMBOL, stock.symbol)
                putString(ARG_NAME, stock.name)
                putString(ARG_PRICE, stock.price)
                putString(ARG_CHANGE, stock.change)
                putInt(ARG_QUANTITY, stock.quantity)
                putString(ARG_MODE, mode)
            }
        }
    }

    private lateinit var stock: Stock
    private lateinit var mode: String

    private var quantity: Int = 0
    private var totalAmount: Double = 0.0

    private val sharedViewModel: SharedStatsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = requireArguments()
        stock = Stock(
            symbol = args.getString(ARG_SYMBOL).orEmpty(),
            name = args.getString(ARG_NAME).orEmpty(),
            price = args.getString(ARG_PRICE).orEmpty(),
            change = args.getString(ARG_CHANGE).orEmpty(),
            quantity = args.getInt(ARG_QUANTITY, 10)
        )
        mode = args.getString(ARG_MODE).orEmpty()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_buy_sell_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etQuantity = view.findViewById<TextInputEditText>(R.id.etQuantity)
        val tvTotal = view.findViewById<TextView>(R.id.tvTotalAmount)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val tvTitle = view.findViewById<TextView>(R.id.tvDialogTitle)

        tvTitle.text = if (mode == "buy") "Купить ${stock.symbol}" else "Продать ${stock.symbol}"
        btnConfirm.text = if (mode == "buy") "Купить" else "Продать"

        val pricePerStock = stock.currentPrice

        fun validateAndUpdateUi(inputQty: Int?) {
            val qty = inputQty ?: 0
            if (qty <= 0) {
                quantity = 0
                totalAmount = 0.0
                tvTotal.visibility = View.GONE
                btnConfirm.isEnabled = false
                return
            }

            quantity = qty
            totalAmount = qty * pricePerStock

            // базовый текст
            tvTotal.visibility = View.VISIBLE
            tvTotal.text = "Итого: %.2f ₽".format(totalAmount)

            // правила включения/выключения confirm
            val canConfirm = when (mode) {
                "buy" -> {
                    val balanceNow = sharedViewModel.balance.value
                    val ok = totalAmount <= balanceNow
                    if (!ok) {
                        tvTotal.text = "Недостаточно средств. Нужно: %.2f ₽".format(totalAmount)
                    }
                    ok
                }
                "sell" -> {
                    val have = sharedViewModel.getPosition(stock.symbol)
                    val ok = qty <= have
                    if (!ok) {
                        tvTotal.text = "Нельзя продать $qty шт. Доступно: $have шт."
                    }
                    ok
                }
                else -> false
            }

            btnConfirm.isEnabled = canConfirm
            updateConfirmButtonStyle(btnConfirm)
        }

        // Пересчитывать при вводе qty
        etQuantity.doAfterTextChanged { editable ->
            validateAndUpdateUi(editable?.toString()?.toIntOrNull())
        }

        // И пересчитывать при изменении баланса/позиций (например после другой операции)
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.balance.collect {
                validateAndUpdateUi(etQuantity.text?.toString()?.toIntOrNull())
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.positions.collect {
                validateAndUpdateUi(etQuantity.text?.toString()?.toIntOrNull())
            }
        }

        btnCancel.setOnClickListener { dismiss() }

        btnConfirm.setOnClickListener {
            val qty = etQuantity.text?.toString()?.toIntOrNull()
            validateAndUpdateUi(qty)
            if (!btnConfirm.isEnabled) return@setOnClickListener

            btnConfirm.isEnabled = false // защита от даблклика
            updateConfirmButtonStyle(btnConfirm)
            executeTransaction(stock.symbol, quantity, mode, btnConfirm)

        }
    }

    // ВАЖНО: только изменённый метод executeTransaction (остальное можете оставить как есть)

    private fun executeTransaction(symbol: String, quantity: Int, mode: String, btnConfirm: Button) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val body = TransactionRequest(symbol = symbol, quantity = quantity)

                val response = when (mode) {
                    "buy" -> ApiClient.api.buy(body)
                    "sell" -> ApiClient.api.sell(body)
                    else -> {
                        Toast.makeText(context, "Неизвестный режим операции", Toast.LENGTH_SHORT).show()
                        btnConfirm.isEnabled = true
                        return@launch
                    }
                }

                val msg = response.message.orEmpty()
                val isSuccess = (response.success == true)

                if (!isSuccess) {
                    Toast.makeText(context, msg.ifBlank { "Операция не выполнена" }, Toast.LENGTH_SHORT).show()
                    btnConfirm.isEnabled = true
                    return@launch
                }

                val portfolio = response.portfolio
                if (portfolio == null) {
                    Toast.makeText(context, "Пустой ответ портфеля", Toast.LENGTH_SHORT).show()
                    btnConfirm.isEnabled = true
                    return@launch
                }

                sharedViewModel.updatePortfolio(
                    newBalance = portfolio.balance ?: 0.0,
                    newStockCount = portfolio.stocksCount ?: 0,
                    newPositions = portfolio.toPositionsMap()
                )

                Toast.makeText(context, msg.ifBlank { "Операция выполнена" }, Toast.LENGTH_SHORT).show()
                dismiss()

            } catch (e: Exception) {
                Toast.makeText(context, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                btnConfirm.isEnabled = true
            }
        }

    }

    private fun updateConfirmButtonStyle(btn: Button) {
        val enabledColor = requireContext().getColor(android.R.color.white)
        val disabledColor = requireContext().getColor(android.R.color.darker_gray)
        btn.setTextColor(if (btn.isEnabled) enabledColor else disabledColor)
    }
}