package com.example.lab2.presentation.view

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.lab2.R
import com.example.lab2.SharedStatsViewModel
import com.example.lab2.ThemePrefs
import com.example.lab2.data.repositoryimpl.QuotesRepositoryImpl
import com.example.lab2.presentation.contract.ProfileContract
import com.example.lab2.presentation.presenter.ProfilePresenterImpl
import com.google.android.material.switchmaterial.SwitchMaterial

class ProfileFragment : Fragment(R.layout.fragment_profile), ProfileContract.View {

    private val symbols = listOf(
        "SBER",
        "GAZP",
        "LKOH",
        "YNDX",
        "ROSN",
        "VTBR",
        "NVTK",
        "MGNT"
    )

    private lateinit var presenter: ProfileContract.Presenter
    private val sharedViewModel: SharedStatsViewModel by activityViewModels()

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvBalance: TextView
    private lateinit var tvStockCount: TextView
    private lateinit var tvRegDate: TextView
    private lateinit var switchDarkTheme: SwitchMaterial
    private lateinit var btnRefreshStats: Button

    private var initialSavedState: Bundle? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialSavedState = savedInstanceState

        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvBalance = view.findViewById(R.id.tvBalance)
        tvStockCount = view.findViewById(R.id.tvStockCount)
        tvRegDate = view.findViewById(R.id.tvRegDate)
        switchDarkTheme = view.findViewById(R.id.switchDarkTheme)
        btnRefreshStats = view.findViewById(R.id.btnRefreshStats)

        presenter = ProfilePresenterImpl(QuotesRepositoryImpl(), sharedViewModel, symbols)

        switchDarkTheme.isChecked = ThemePrefs.isDarkEnabled(requireContext())
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemePrefs.setDarkEnabled(requireContext(), isChecked)
        }

        btnRefreshStats.setOnClickListener {
            presenter.onRefreshPortfolioClicked()
        }

        presenter.attach(this, viewLifecycleOwner.lifecycleScope)
        presenter.onViewReady(arguments?.getString("username"), initialSavedState)
    }

    override fun onDestroyView() {
        presenter.detach()
        super.onDestroyView()
    }

    override fun bindUserHeader(username: String, email: String, regDate: String) {
        tvUserName.text = username
        tvUserEmail.text = email
        tvRegDate.text = regDate
    }

    override fun renderBalance(text: String) {
        tvBalance.text = text
    }

    override fun renderStockCount(text: String) {
        tvStockCount.text = text
    }

    override fun setDarkThemeSwitch(checked: Boolean) {
        switchDarkTheme.isChecked = checked
    }

    override fun updateBottomNavStaticTitles() {
        val bottomNav =
            activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottomNavigation
            ) ?: return
        bottomNav.menu.findItem(R.id.nav_profile)?.title = "Профиль"
        bottomNav.menu.findItem(R.id.nav_statistics)?.title = "Статистика"
        bottomNav.menu.findItem(R.id.nav_stock_list)?.title = "Акции"
    }
}
