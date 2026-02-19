package com.example.lab2

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

fun parsePrice(priceString: String): Double {
    return priceString
        .replace("₽", "")
        .replace(",", "")
        .replace(" ", "")
        .trim()
        .toDoubleOrNull() ?: 0.0
}

enum class ChartType {
    LINE,
    CANDLE
}

enum class TimePeriod {
    WEEK,
    MONTH,
    HALF_YEAR,
    YEAR,
    ALL_TIME
}

data class PricePoint(
    val date: String,
    val price: Double,
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val close: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockChartFragment(
    stock: Stock,
    onBackClick: () -> Unit
) {
    var selectedChartType by remember { mutableStateOf(ChartType.LINE) }
    var selectedPeriod by remember { mutableStateOf(TimePeriod.MONTH) }

    val currentPrice = remember(stock.price) { parsePrice(stock.price) }

    val chartData = remember(selectedPeriod, currentPrice) {
        generateChartData(currentPrice, selectedPeriod)
    }

    val quantity = 10
    val averagePrice = currentPrice * 0.95

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${stock.symbol} - ${stock.name}") },
                navigationIcon = {
                    Button(onClick = onBackClick) {
                        Text("←")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            SegmentedButton(
                                selected = selectedChartType == ChartType.LINE,
                                onClick = { selectedChartType = ChartType.LINE },
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                            ) {
                                Text("Линейный")
                            }
                            SegmentedButton(
                                selected = selectedChartType == ChartType.CANDLE,
                                onClick = { selectedChartType = ChartType.CANDLE },
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                            ) {
                                Text("Свечной")
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            TimePeriod.values().forEach { period ->
                                Button(
                                    onClick = { selectedPeriod = period },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 2.dp)
                                ) {
                                    Text(getPeriodName(period))
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            when (selectedChartType) {
                                ChartType.LINE -> LineChart(data = chartData)
                                ChartType.CANDLE -> CandleChart(data = chartData)
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Ваш портфель",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Количество:", color = Color.Gray)
                                Text("Текущая цена:", color = Color.Gray)
                                Text("Средняя цена:", color = Color.Gray)
                                Text("Общая стоимость:", color = Color.Gray)
                                Text("Доход:", color = Color.Gray)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("$quantity шт.")
                                Text("${String.format("%.2f", currentPrice)} ₽")
                                Text("${String.format("%.2f", averagePrice)} ₽")
                                Text("${String.format("%.2f", quantity * currentPrice)} ₽")
                                val profit = (currentPrice - averagePrice) * quantity
                                Text(
                                    text = "${String.format("%.2f", profit)} ₽",
                                    color = if (profit >= 0) Color.Green else Color.Red
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { /* TODO: Купить */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("Купить", fontSize = 18.sp)
                }

                Button(
                    onClick = { /* TODO: Продать */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("Продать", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun LineChart(
    data: List<PricePoint>
) {
    if (data.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val padding = 40f

        val prices = data.map { it.price }
        val minPrice = prices.minOrNull() ?: 0.0
        val maxPrice = prices.maxOrNull() ?: 1.0
        val priceRange = (maxPrice - minPrice).toFloat()

        if (priceRange == 0f) return@Canvas

        drawLine(
            color = Color.Gray,
            start = Offset(padding, 0f),
            end = Offset(padding, height - padding),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(padding, height - padding),
            end = Offset(width - padding, height - padding),
            strokeWidth = 2f
        )

        val points = data.mapIndexed { index, point ->
            val x = padding + (index * (width - 2 * padding) / (data.size - 1).coerceAtLeast(1))
            val y = height - padding - (((point.price - minPrice) / priceRange) * (height - 2 * padding)).toFloat()
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color.Blue,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 3f
            )
        }

        points.forEach { point ->
            drawCircle(
                color = Color.Red,
                center = point,
                radius = 4f
            )
        }
    }
}

@Composable
fun CandleChart(
    data: List<PricePoint>
) {
    if (data.isEmpty()) return

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val padding = 40f
        val candleWidth = ((width - 2 * padding) / data.size) * 0.6f
        val halfCandle = candleWidth / 2

        val allPrices = data.flatMap { listOf(it.high, it.low) }
        val minPrice = allPrices.minOrNull() ?: 0.0
        val maxPrice = allPrices.maxOrNull() ?: 1.0
        val priceRange = (maxPrice - minPrice).toFloat()

        if (priceRange == 0f) return@Canvas

        drawLine(
            color = Color.Gray,
            start = Offset(padding, 0f),
            end = Offset(padding, height - padding),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(padding, height - padding),
            end = Offset(width - padding, height - padding),
            strokeWidth = 2f
        )

        data.forEachIndexed { index, point ->
            val x = padding + (index * (width - 2 * padding) / data.size) + halfCandle

            val openY = height - padding - (((point.open - minPrice) / priceRange) * (height - 2 * padding)).toFloat()
            val closeY = height - padding - (((point.close - minPrice) / priceRange) * (height - 2 * padding)).toFloat()
            val highY = height - padding - (((point.high - minPrice) / priceRange) * (height - 2 * padding)).toFloat()
            val lowY = height - padding - (((point.low - minPrice) / priceRange) * (height - 2 * padding)).toFloat()

            val color = if (point.close >= point.open) Color.Green else Color.Red

            drawLine(
                color = color,
                start = Offset(x, highY),
                end = Offset(x, lowY),
                strokeWidth = 2f
            )

            val topY = min(openY, closeY)
            val bottomY = max(openY, closeY)

            drawRect(
                color = color.copy(alpha = 0.3f),
                topLeft = Offset(x - halfCandle, topY),
                size = androidx.compose.ui.geometry.Size(candleWidth, bottomY - topY)
            )
        }
    }
}

fun getPeriodName(period: TimePeriod): String {
    return when (period) {
        TimePeriod.WEEK -> "Нед"
        TimePeriod.MONTH -> "Меc"
        TimePeriod.HALF_YEAR -> "6м"
        TimePeriod.YEAR -> "Год"
        TimePeriod.ALL_TIME -> "Всё"
    }
}

fun generateChartData(basePrice: Double, period: TimePeriod): List<PricePoint> {
    val random = Random(System.currentTimeMillis())

    val pointsCount = when (period) {
        TimePeriod.WEEK -> 7
        TimePeriod.MONTH -> 30
        TimePeriod.HALF_YEAR -> 60
        TimePeriod.YEAR -> 120
        TimePeriod.ALL_TIME -> 240
    }

    val data = mutableListOf<PricePoint>()
    var currentPrice = basePrice * 0.7

    for (i in 0 until pointsCount) {
        val volatility = when (period) {
            TimePeriod.WEEK -> 0.02
            TimePeriod.MONTH -> 0.03
            TimePeriod.HALF_YEAR -> 0.05
            TimePeriod.YEAR -> 0.08
            TimePeriod.ALL_TIME -> 0.15
        }

        val change = (random.nextDouble() - 0.5) * 2 * volatility * currentPrice
        val open = currentPrice
        val close = currentPrice + change
        val high = max(open, close) + random.nextDouble() * volatility * currentPrice
        val low = min(open, close) - random.nextDouble() * volatility * currentPrice

        val date = when (period) {
            TimePeriod.WEEK -> "Д${i+1}"
            TimePeriod.MONTH -> "${i+1}"
            TimePeriod.HALF_YEAR -> "${i+1}"
            TimePeriod.YEAR -> "${i+1}"
            TimePeriod.ALL_TIME -> "${i+1}"
        }

        data.add(PricePoint(date, close, open, high, low, close))
        currentPrice = close
    }

    return data
}