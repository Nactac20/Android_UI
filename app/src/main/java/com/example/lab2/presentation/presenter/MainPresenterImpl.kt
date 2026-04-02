package com.example.lab2.presentation.presenter

import com.example.lab2.presentation.contract.MainContract

class MainPresenterImpl : MainContract.Presenter {

    private var view: MainContract.View? = null

    override fun attach(view: MainContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
    }

    override fun onOpenProfileClicked(login: String, password: String) {
        if (login.isNotEmpty() && password.isNotEmpty()) {
            view?.openSecondScreen(login)
        }
    }
}
