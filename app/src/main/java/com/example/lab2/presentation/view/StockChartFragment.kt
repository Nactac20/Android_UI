package com.example.lab2.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.lab2.CandleChartView
import com.example.lab2.LineChartView
import com.example.lab2.R
import com.example.lab2.SharedStatsViewModel
import com.example.lab2.databinding.FragmentStockChartBinding
import com.example.lab2.domain.entity.Stock
import com.example.lab2.presentation.chart.ChartType
import com.example.lab2.presentation.chart.PricePoint
import com.example.lab2.presentation.chart.TimePeriod
import com.example.lab2.presentation.contract.StockChartContract
import com.example.lab2.presentation.presenter.StockChartPresenterImpl

class StockChartFragment : Fragment(), StockChartContract.View {

    private var _binding: FragmentStockChartBinding? = null
    private val binding get() = _binding!!
    private lateinit var stock: Stock

    private lateinit var presenter: StockChartContract.Presenter
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

        presenter = StockChartPresenterImpl(sharedViewModel)
        presenter.attach(this, stock, viewLifecycleOwner.lifecycleScope)

        binding.chartTypeToggle.check(R.id.btnLineChart)
        binding.periodToggle.check(R.id.btnMonth)

        binding.toolbar.setNavigationOnClickListener { presenter.onBackClicked() }

        binding.btnBuy.setOnClickListener { presenter.onBuyClicked() }
        binding.btnSell.setOnClickListener { presenter.onSellClicked() }

        binding.btnLineChart.setOnClickListener {
            presenter.onChartTypeSelected(ChartType.LINE)
        }

        binding.btnCandleChart.setOnClickListener {
            presenter.onChartTypeSelected(ChartType.CANDLE)
        }

        binding.btnWeek.setOnClickListener { presenter.onPeriodSelected(TimePeriod.WEEK) }
        binding.btnMonth.setOnClickListener { presenter.onPeriodSelected(TimePeriod.MONTH) }
        binding.btnHalfYear.setOnClickListener { presenter.onPeriodSelected(TimePeriod.HALF_YEAR) }
        binding.btnYear.setOnClickListener { presenter.onPeriodSelected(TimePeriod.YEAR) }
        binding.btnAllTime.setOnClickListener { presenter.onPeriodSelected(TimePeriod.ALL_TIME) }

        presenter.onViewReady()
    }

    override fun onDestroyView() {
        presenter.detach()
        super.onDestroyView()
        _binding = null
    }

    override fun setToolbarTitle(title: String) {
        binding.toolbar.title = title
    }

    override fun showLineChart() {
        binding.lineChart.visibility = View.VISIBLE
        binding.candleChart.visibility = View.GONE
    }

    override fun showCandleChart() {
        binding.lineChart.visibility = View.GONE
        binding.candleChart.visibility = View.VISIBLE
    }

    override fun renderHoldings(
        quantityText: String,
        currentPriceText: String,
        avgPriceText: String,
        totalValueText: String,
        profitText: String,
        profitPositive: Boolean
    ) {
        binding.tvQuantity.text = quantityText
        binding.tvCurrentPrice.text = currentPriceText
        binding.tvAvgPrice.text = avgPriceText
        binding.tvTotalValue.text = totalValueText
        binding.tvProfit.text = profitText
        binding.tvProfit.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                if (profitPositive) android.R.color.holo_green_dark else android.R.color.holo_red_dark
            )
        )
    }

    override fun renderChart(points: List<PricePoint>, type: ChartType) {
        if (type == ChartType.LINE) {
            (binding.lineChart as LineChartView).setData(points)
        } else {
            (binding.candleChart as CandleChartView).setData(points)
        }
    }

    override fun navigateBack() {
        parentFragmentManager.popBackStack()
    }

    override fun openBuySheet(stock: Stock) {
        BuySellDialogFragment.newInstance(stock, "buy").show(parentFragmentManager, "BuyDialog")
    }

    override fun openSellSheet(stock: Stock) {
        BuySellDialogFragment.newInstance(stock, "sell").show(parentFragmentManager, "SellDialog")
    }
}
