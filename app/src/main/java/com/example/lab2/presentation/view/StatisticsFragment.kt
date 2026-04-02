package com.example.lab2.presentation.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.lab2.R
import com.example.lab2.presentation.contract.StatisticsContract
import com.example.lab2.presentation.presenter.StatisticsPresenterImpl

class StatisticsFragment : Fragment(R.layout.fragment_statistics), StatisticsContract.View {

    private lateinit var presenter: StatisticsContract.Presenter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter = StatisticsPresenterImpl()
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
