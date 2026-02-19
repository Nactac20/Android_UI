package com.example.lab2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreenSizes
@Composable
fun SecondActivityContent() {
    var currentDestination by rememberSaveable { mutableStateOf(SecondActivityDestinations.PROFILE) }
    var selectedStock by rememberSaveable { mutableStateOf<Stock?>(null) }

    fun onStockSelected(stock: Stock) {
        selectedStock = stock
        currentDestination = SecondActivityDestinations.STOCK_DETAIL
    }

    fun onBackToStockList() {
        selectedStock = null
        currentDestination = SecondActivityDestinations.STOCK_LIST
    }

    if (currentDestination == SecondActivityDestinations.STOCK_DETAIL && selectedStock != null) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(selectedStock!!.symbol) },
                navigationIcon = {
                    IconButton(onClick = { onBackToStockList() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
            StockChartFragment(
                stock = selectedStock!!,
                onBackClick = { onBackToStockList() }
            )
        }
    } else {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                SecondActivityDestinations.entries.forEach {
                    if (it != SecondActivityDestinations.STOCK_DETAIL) {
                        item(
                            icon = {
                                Icon(
                                    it.icon,
                                    contentDescription = it.label
                                )
                            },
                            label = { Text(it.label) },
                            selected = it == currentDestination,
                            onClick = {
                                currentDestination = it
                                selectedStock = null
                            }
                        )
                    }
                }
            }
        ) {
            when (currentDestination) {
                SecondActivityDestinations.PROFILE -> ProfileFragment()
                SecondActivityDestinations.MY_STAT -> MyStatFragment()
                SecondActivityDestinations.STOCK_LIST -> StockListFragment(
                    onStockClick = { stock -> onStockSelected(stock) }
                )
                SecondActivityDestinations.STOCK_STAT -> StockStatFragment()
                SecondActivityDestinations.STOCK_DETAIL -> {
                }
            }
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
    STOCK_DETAIL("Детали акции", Icons.Default.Info)
}