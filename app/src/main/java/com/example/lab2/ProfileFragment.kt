package com.example.lab2

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial

class ProfileFragment : Fragment(R.layout.fragment_profile) {


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

        tvUserName.text = username
        tvUserEmail.text = "$username@example.com".lowercase()
        tvBalance.text = "10 000 ₽"
        tvStockCount.text = "5"
        tvRegDate.text = "01.01.2024"


        btnRefreshStats.setOnClickListener {
            refreshStatistics(tvBalance, tvStockCount)
        }
    }

    private fun refreshStatistics(tvBalance: TextView, tvStockCount: TextView) {
        val newBalance = (-5000..50000).random()
        val newCount = (1..20).random()

        tvBalance.text = "$newBalance ₽"
        tvStockCount.text = newCount.toString()

        Toast.makeText(requireContext(), "Статистика обновлена", Toast.LENGTH_SHORT).show()
    }
}