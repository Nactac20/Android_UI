package com.example.lab2.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.lab2.R
import com.example.lab2.SharedStatsViewModel
import com.example.lab2.data.repositoryimpl.TradeRepositoryImpl
import com.example.lab2.domain.entity.Stock
import com.example.lab2.presentation.contract.BuySellContract
import com.example.lab2.presentation.presenter.BuySellPresenterImpl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText

class BuySellDialogFragment : BottomSheetDialogFragment(), BuySellContract.View {

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

    private lateinit var etQuantity: TextInputEditText
    private lateinit var tvTotal: TextView
    private lateinit var btnConfirm: Button
    private lateinit var btnCancel: Button
    private lateinit var tvTitle: TextView

    private lateinit var presenter: BuySellContract.Presenter
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

        etQuantity = view.findViewById(R.id.etQuantity)
        tvTotal = view.findViewById(R.id.tvTotalAmount)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        btnCancel = view.findViewById(R.id.btnCancel)
        tvTitle = view.findViewById(R.id.tvDialogTitle)

        presenter = BuySellPresenterImpl(stock, mode, sharedViewModel, TradeRepositoryImpl())
        presenter.attach(this, viewLifecycleOwner.lifecycleScope)

        btnConfirm.text = if (mode == "buy") "Купить" else "Продать"

        etQuantity.doAfterTextChanged { editable ->
            presenter.onQuantityTextChanged(editable?.toString())
        }

        btnCancel.setOnClickListener { dismiss() }

        btnConfirm.setOnClickListener {
            presenter.onConfirmClicked()
        }
    }

    override fun onDestroyView() {
        presenter.detach()
        super.onDestroyView()
    }

    override fun showToast(message: String) {
        context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }
    }

    override fun dismissSuccess() {
        dismiss()
    }

    override fun setConfirmEnabled(enabled: Boolean) {
        btnConfirm.isEnabled = enabled
    }

    override fun updateTotalLabel(text: String, visible: Boolean) {
        tvTotal.visibility = if (visible) View.VISIBLE else View.GONE
        tvTotal.text = text
    }

    override fun updateTitleAndButtonLabels(buyMode: Boolean, symbol: String) {
        tvTitle.text = if (buyMode) "Купить $symbol" else "Продать $symbol"
        btnConfirm.text = if (buyMode) "Купить" else "Продать"
    }

    override fun readQuantityInput(): String? = etQuantity.text?.toString()

    override fun refreshConfirmButtonStyle() {
        val enabledColor = requireContext().getColor(android.R.color.white)
        val disabledColor = requireContext().getColor(android.R.color.darker_gray)
        btnConfirm.setTextColor(if (btnConfirm.isEnabled) enabledColor else disabledColor)
    }
}
