package com.example.lab2.presentation.presenter

import com.example.lab2.R
import com.example.lab2.presentation.contract.SecondContract

class SecondPresenterImpl(
    private var username: String?
) : SecondContract.Presenter {

    private var view: SecondContract.View? = null

    override fun attach(view: SecondContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
    }

    override fun onNavItemSelected(itemId: Int) {
        when (itemId) {
            R.id.nav_profile -> view?.navigateToProfile(username)
            R.id.nav_statistics -> view?.navigateToStatistics()
            R.id.nav_stock_list -> view?.navigateToStockList()
            else -> Unit
        }
    }
}
