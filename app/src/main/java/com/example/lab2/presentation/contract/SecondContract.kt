package com.example.lab2.presentation.contract

interface SecondContract {
    interface View {
        fun navigateToProfile(username: String?)
        fun navigateToStatistics()
        fun navigateToStockList()
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun onNavItemSelected(itemId: Int)
    }
}
