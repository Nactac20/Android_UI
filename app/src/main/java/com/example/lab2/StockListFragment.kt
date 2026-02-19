package com.example.lab2

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Stock(
    val symbol: String,
    val name: String,
    val price: String,
    val change: String,
    val currentPrice: Double = 0.0,
    val quantity: Int = 0
)


@Composable
fun StockListFragment(
    onStockClick: (Stock) -> Unit = {}
) {
    val stocks = listOf(
        Stock("SBER", "Сбербанк", "283.50 ₽", "+1.2%"),
        Stock("GAZP", "Газпром", "198.30 ₽", "-0.5%"),
        Stock("LKOH", "Лукойл", "7,450.00 ₽", "+0.8%"),
        Stock("YNDX", "Яндекс", "3,620.00 ₽", "+2.1%"),
        Stock("ROSN", "Роснефть", "567.80 ₽", "-0.3%"),
        Stock("VTBR", "ВТБ", "0.0235 ₽", "0.0%"),
        Stock("NVTK", "НОВАТЭК", "1,234.50 ₽", "+0.4%"),
        Stock("MGNT", "Магнит", "5,890.00 ₽", "-1.1%")
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "Список акций",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(stocks) { stock ->
                    StockItem(
                        stock = stock,
                        onClick = { onStockClick(stock) }
                    )
                }
            }
        }
    }
}

@Composable
fun StockItem(
    stock: Stock,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stock.symbol,
                    fontSize = 18.sp
                )
                Text(
                    text = stock.name,
                    fontSize = 14.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stock.price,
                    fontSize = 18.sp
                )
                Text(
                    text = stock.change,
                    fontSize = 14.sp,
                    color = if (stock.change.startsWith("+"))
                        Color.Green
                    else if (stock.change.startsWith("-"))
                        Color.Red
                    else
                        Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockListFragmentPreview() {
    StockListFragment()
}
