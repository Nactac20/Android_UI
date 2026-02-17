package com.example.lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.example.lab2.ui.theme.Lab2Theme

class SecondActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab2Theme {
                SecondActivityContent()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun SecondActivityContent() {
    var currentDestination by rememberSaveable { mutableStateOf(SecondActivityDestinations.PROFILE) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            SecondActivityDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        // Отображаем соответствующий фрагмент в зависимости от выбранной вкладки
        when (currentDestination) {
            SecondActivityDestinations.PROFILE -> ProfileFragment()
            SecondActivityDestinations.MY_STAT -> MyStatFragment()
            SecondActivityDestinations.STOCK_LIST -> StockListFragment()
            SecondActivityDestinations.STOCK_STAT -> StockStatFragment()
        }
    }
}

enum class SecondActivityDestinations(
    val label: String,
    val icon: ImageVector,
) {
    PROFILE("Профиль", Icons.Default.AccountBox),
    MY_STAT("Моя статистика", Icons.Default.BarChart),
    STOCK_LIST("Список акций", Icons.Default.List),
    STOCK_STAT("Статистика акций", Icons.Default.Info),
}
