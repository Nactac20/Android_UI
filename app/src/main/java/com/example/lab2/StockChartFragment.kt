package com.example.lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.lab2.databinding.FragmentStockChartBinding
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class StockChartFragment : Fragment() {

    private var _binding: FragmentStockChartBinding? = null
    private val binding get() = _binding!!
    private lateinit var stock: Stock

    private var currentChartType = ChartType.LINE
    private var currentTimePeriod = TimePeriod.MONTH

    private val sharedViewModel: SharedStatsViewModel by activityViewModels()

    companion object {
        fun newInstance(stock: Stock): StockChartFragment {
            val fragment = StockChartFragment()
            fragment.stock = stock
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStockChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.title = "${stock.symbol} - ${stock.name}"
        binding.toolbar.setNavigationOnClickListener { parentFragmentManager.popBackStack() }

        binding.chartTypeToggle.check(R.id.btnLineChart)
        binding.periodToggle.check(R.id.btnMonth)

        binding.btnBuy.setOnClickListener {
            BuySellDialogFragment.newInstance(stock, "buy").show(parentFragmentManager, "BuyDialog")
        }
        binding.btnSell.setOnClickListener {
            BuySellDialogFragment.newInstance(stock, "sell").show(parentFragmentManager, "SellDialog")
        }

        binding.btnLineChart.setOnClickListener {
            currentChartType = ChartType.LINE
            binding.lineChart.visibility = View.VISIBLE
            binding.candleChart.visibility = View.GONE
            updateChartData(currentChartType, currentTimePeriod)
        }

        binding.btnCandleChart.setOnClickListener {
            currentChartType = ChartType.CANDLE
            binding.lineChart.visibility = View.GONE
            binding.candleChart.visibility = View.VISIBLE
            updateChartData(currentChartType, currentTimePeriod)
        }

        binding.btnWeek.setOnClickListener { currentTimePeriod = TimePeriod.WEEK; updateChartData(currentChartType, currentTimePeriod) }
        binding.btnMonth.setOnClickListener { currentTimePeriod = TimePeriod.MONTH; updateChartData(currentChartType, currentTimePeriod) }
        binding.btnHalfYear.setOnClickListener { currentTimePeriod = TimePeriod.HALF_YEAR; updateChartData(currentChartType, currentTimePeriod) }
        binding.btnYear.setOnClickListener { currentTimePeriod = TimePeriod.YEAR; updateChartData(currentChartType, currentTimePeriod) }
        binding.btnAllTime.setOnClickListener { currentTimePeriod = TimePeriod.ALL_TIME; updateChartData(currentChartType, currentTimePeriod) }

        updateChartData(currentChartType, currentTimePeriod)

        // UI обновляется только от состояния портфеля:
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.positions.collect { renderHoldings() }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.balance.collect { /* если захотите показывать баланс на этом экране */ }
        }

        // Подтянуть портфель при открытии экрана акции
        sharedViewModel.refreshPortfolio()
        renderHoldings()
    }

    private fun renderHoldings() {
        val currentPrice = parsePrice(stock.price)
        val qty = sharedViewModel.getPosition(stock.symbol)

        val averagePrice = currentPrice * 0.95
        val totalValue = currentPrice * qty
        val profit = (currentPrice - averagePrice) * qty

        binding.tvQuantity.text = "$qty шт."
        binding.tvCurrentPrice.text = String.format("%.2f ₽", currentPrice)
        binding.tvAvgPrice.text = String.format("%.2f ₽", averagePrice)
        binding.tvTotalValue.text = String.format("%.2f ₽", totalValue)
        binding.tvProfit.text = String.format("%.2f ₽", profit)

        binding.tvProfit.setTextColor(
            if (profit >= 0) ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
            else ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
        )
    }

    private fun updateChartData(type: ChartType, period: TimePeriod) {
        val data = generateChartData(parsePrice(stock.price), period)
        if (type == ChartType.LINE) (binding.lineChart as LineChartView).setData(data)
        else (binding.candleChart as CandleChartView).setData(data)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


fun parsePrice(priceString: String): Double {
    val s = priceString
        .replace("₽", "")
        .trim()
        .replace("\u00A0", " ")
        .replace(" ", "")
        .replace(",", ".")
    return s.toDoubleOrNull() ?: 0.0
}

enum class ChartType { LINE, CANDLE }
enum class TimePeriod { WEEK, MONTH, HALF_YEAR, YEAR, ALL_TIME }

data class PricePoint(
    val date: String,
    val price: Double,
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val close: Double = 0.0
)


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
            TimePeriod.WEEK -> "Д${i + 1}"
            TimePeriod.MONTH -> "${i + 1}"
            else -> "${i + 1}"
        }

        data.add(PricePoint(date, close, open, high, low, close))
        currentPrice = close
    }

    return data
}