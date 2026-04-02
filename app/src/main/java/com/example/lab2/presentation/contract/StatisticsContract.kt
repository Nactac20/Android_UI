package com.example.lab2.presentation.contract

interface StatisticsContract {
    interface View

    interface Presenter {
        fun attach(view: View)
        fun detach()
    }
}
