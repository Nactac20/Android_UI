package com.example.lab2.presentation.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.ComponentActivity
import com.example.lab2.R
import com.example.lab2.ThemePrefs
import com.example.lab2.presentation.contract.MainContract
import com.example.lab2.presentation.presenter.MainPresenterImpl

class MainActivity : ComponentActivity(), MainContract.View {

    private lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemePrefs.applySaved(this)
        setContentView(R.layout.activity_main)

        presenter = MainPresenterImpl()
        presenter.attach(this)

        val loginEditText = findViewById<EditText>(R.id.loginEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val openProfileButton = findViewById<Button>(R.id.openProfileButton)

        openProfileButton.setOnClickListener {
            presenter.onOpenProfileClicked(
                loginEditText.text.toString().trim(),
                passwordEditText.text.toString().trim()
            )
        }
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun openSecondScreen(username: String) {
        startActivity(
            Intent(this, SecondActivity::class.java).apply {
                putExtra("username", username)
            }
        )
    }
}
