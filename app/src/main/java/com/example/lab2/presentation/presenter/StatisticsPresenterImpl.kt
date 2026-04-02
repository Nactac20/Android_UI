package com.example.lab2.presentation.presenter

import com.example.lab2.presentation.contract.StatisticsContract

class StatisticsPresenterImpl : StatisticsContract.Presenter {

    private var view: StatisticsContract.View? = null

    override fun attach(view: StatisticsContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
    }
}
