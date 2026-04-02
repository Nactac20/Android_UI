package com.example.lab2.presentation.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.lab2.R
import com.example.lab2.presentation.contract.MyStatContract
import com.example.lab2.presentation.presenter.MyStatPresenterImpl

class MyStatFragment : Fragment(R.layout.fragment_my_stat), MyStatContract.View {

    private lateinit var presenter: MyStatContract.Presenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = MyStatPresenterImpl()
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
    }

    override fun onStop() {
        presenter.detach()
        super.onStop()
    }
}
