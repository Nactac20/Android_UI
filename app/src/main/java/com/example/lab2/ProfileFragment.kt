package com.example.lab2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.lab2.data.StockRepository
import com.example.lab2.api.ApiClient
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.lab2.api.toPositionsMap

import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

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


    private val stockRepository = StockRepository()

    private var isRefreshing = false

    private val sharedViewModel: SharedStatsViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString("username") ?: "Иван Петров"

        val tvUserName = view.findViewById<TextView>(R.id.tvUserName)
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        val tvBalance = view.findViewById<TextView>(R.id.tvBalance)
        val tvStockCount = view.findViewById<TextView>(R.id.tvStockCount)
        val tvRegDate = view.findViewById<TextView>(R.id.tvRegDate)
        val switchDarkTheme = view.findViewById<SwitchMaterial>(R.id.switchDarkTheme)
        val btnRefreshStats = view.findViewById<Button>(R.id.btnRefreshStats)

        switchDarkTheme.isChecked = ThemePrefs.isDarkEnabled(requireContext())
        switchDarkTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemePrefs.setDarkEnabled(requireContext(), isChecked)
        }

        tvUserName.text = username
        tvUserEmail.text = "$username@example.com".lowercase()
        tvRegDate.text = "01.01.2024"

        btnRefreshStats.setOnClickListener {
            sharedViewModel.refreshPortfolio()
        }

        if (savedInstanceState == null) {
            sharedViewModel.refreshPortfolio()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.balance.collect { balance ->
                tvBalance.text = "%.0f ₽".format(balance)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.stockCount.collect { count ->
                tvStockCount.text = count.toString()
            }
        }

        updateBottomNavQuotes()
    }

    private fun refreshStatisticsFromNetwork() {
        if (isRefreshing) return
        isRefreshing = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val portfolio = ApiClient.api.portfolio()
                val positionsMap = portfolio.toPositionsMap()

                sharedViewModel.updatePortfolio(
                    newBalance = portfolio.balance ?: 0.0,
                    newStockCount = portfolio.stocksCount ?: 0,
                    newPositions = positionsMap,
                )
            } catch (e: Exception) {
                context?.let { Toast.makeText(it, "Ошибка загрузки статистики: ${e.message}", Toast.LENGTH_SHORT).show() }
            } finally {
                isRefreshing = false
            }
        }
    }

    private fun updateBottomNavQuotes() {
        viewLifecycleOwner.lifecycleScope.launch {
            runCatching { stockRepository.fetchQuotes(symbols) }.getOrNull() ?: return@launch
            val bottomNav: BottomNavigationView =
                activity?.findViewById(R.id.bottomNavigation) ?: return@launch

            bottomNav.menu.findItem(R.id.nav_profile)?.title = "Профиль"
            bottomNav.menu.findItem(R.id.nav_statistics)?.title = "Статистика"
            bottomNav.menu.findItem(R.id.nav_stock_list)?.title = "Акции"
        }
    }
}