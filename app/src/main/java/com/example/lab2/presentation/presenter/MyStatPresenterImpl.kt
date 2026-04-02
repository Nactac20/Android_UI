package com.example.lab2.presentation.presenter

import com.example.lab2.presentation.contract.MyStatContract

class MyStatPresenterImpl : MyStatContract.Presenter {

    private var view: MyStatContract.View? = null

    override fun attach(view: MyStatContract.View) {
        this.view = view
    }

    override fun detach() {
        view = null
    }
}
