package com.example.lab2.presentation.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lab2.R
import com.example.lab2.ThemePrefs
import com.example.lab2.databinding.ActivitySecondBinding
import com.example.lab2.presentation.contract.SecondContract
import com.example.lab2.presentation.presenter.SecondPresenterImpl
import com.google.android.material.bottomnavigation.BottomNavigationView

class SecondActivity : AppCompatActivity(), SecondContract.View {

    private lateinit var binding: ActivitySecondBinding
    private lateinit var presenter: SecondContract.Presenter

    private companion object {
        const val KEY_SELECTED_NAV = "selected_nav"
        const val KEY_USERNAME = "username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemePrefs.applySaved(this)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = savedInstanceState?.getString(KEY_USERNAME) ?: intent.getStringExtra("username")

        presenter = SecondPresenterImpl(username)
        presenter.attach(this)

        setupBottomNavigation(binding.bottomNavigation)

        binding.bottomNavigation.selectedItemId =
            savedInstanceState?.getInt(KEY_SELECTED_NAV, R.id.nav_profile) ?: R.id.nav_profile
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_SELECTED_NAV, binding.bottomNavigation.selectedItemId)
        outState.putString(KEY_USERNAME, (intent.getStringExtra("username") ?: ""))
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun navigateToProfile(username: String?) {
        val fragment = ProfileFragment().apply {
            arguments = Bundle().apply {
                if (!username.isNullOrEmpty()) {
                    putString("username", username)
                }
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun navigateToStatistics() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, StatisticsFragment())
            .commit()
    }

    override fun navigateToStockList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, StockListFragment())
            .commit()
    }

    private fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            presenter.onNavItemSelected(item.itemId)
            true
        }
    }
}
