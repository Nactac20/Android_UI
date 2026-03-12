package com.example.lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nameTextView: TextView = view.findViewById(R.id.profileNameTextView)

        // Берём логин из аргументов и подставляем в текст
        val username = arguments?.getString("username") ?: "Иван Петров"
        nameTextView.text = "Имя: $username"
    }
}
