package com.example.lab2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lab2.databinding.ActivitySecondBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val usernameFromIntent = intent.getStringExtra("username")

        if (savedInstanceState == null) {
            openProfileFragment(usernameFromIntent)
        }

        setupBottomNavigation(binding.bottomNavigation)
    }

    private fun openProfileFragment(username: String?) {
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

    private fun setupBottomNavigation(bottomNav: BottomNavigationView) {
        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_profile -> ProfileFragment()
                R.id.nav_my_stat -> MyStatFragment()
                R.id.nav_stock_list -> StockListFragment()
                R.id.nav_stock_stat -> StockStatFragment()
                else -> null
            }

            fragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, it)
                    .commit()
                true
            } ?: false
        }
    }
}
