package com.example.lab2.presentation.contract

import android.os.Bundle
import kotlinx.coroutines.CoroutineScope

interface ProfileContract {
    interface View {
        fun bindUserHeader(username: String, email: String, regDate: String)
        fun renderBalance(text: String)
        fun renderStockCount(text: String)
        fun setDarkThemeSwitch(checked: Boolean)
        fun updateBottomNavStaticTitles()
    }

    interface Presenter {
        fun attach(view: View, coroutineScope: CoroutineScope)
        fun detach()
        fun onViewReady(username: String?, savedInstanceState: Bundle?)
        fun onRefreshPortfolioClicked()
        fun onDarkThemeChanged(enabled: Boolean)
    }
}
