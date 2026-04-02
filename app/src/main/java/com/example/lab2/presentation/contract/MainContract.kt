package com.example.lab2.presentation.contract

interface MainContract {
    interface View {
        fun openSecondScreen(username: String)
    }

    interface Presenter {
        fun attach(view: View)
        fun detach()
        fun onOpenProfileClicked(login: String, password: String)
    }
}
