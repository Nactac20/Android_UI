package com.example.lab2

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileFragment() {
    val context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Профиль пользователя",
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Имя: Иван Петров",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "Email: ivan@example.com",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Text(
                        text = "Баланс: 10,000 ₽",
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Button(
                        onClick = {
                            val intent = Intent(context, EditProfileActivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Редактировать")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileFragmentPreview() {
    ProfileFragment()
}
