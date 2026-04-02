package com.example.lab2.presentation.contract

import kotlinx.coroutines.CoroutineScope

interface BuySellContract {
    interface View {
        fun showToast(message: String)
        fun dismissSuccess()
        fun setConfirmEnabled(enabled: Boolean)
        fun updateTotalLabel(text: String, visible: Boolean)
        fun updateTitleAndButtonLabels(buyMode: Boolean, symbol: String)
        fun readQuantityInput(): String?
        fun refreshConfirmButtonStyle()
    }

    interface Presenter {
        fun attach(view: View, coroutineScope: CoroutineScope)
        fun detach()
        fun onQuantityTextChanged(raw: String?)
        fun onCancelClicked()
        fun onConfirmClicked()
    }
}
