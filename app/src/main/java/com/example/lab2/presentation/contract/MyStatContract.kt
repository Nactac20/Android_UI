package com.example.lab2.presentation.contract

interface MyStatContract {
    interface View

    interface Presenter {
        fun attach(view: View)
        fun detach()
    }
}
