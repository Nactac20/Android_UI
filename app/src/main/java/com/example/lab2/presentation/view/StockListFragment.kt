package com.example.lab2.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.R
import com.example.lab2.data.repositoryimpl.QuotesRepositoryImpl
import com.example.lab2.databinding.FragmentStockListBinding
import com.example.lab2.domain.entity.Stock
import com.example.lab2.presentation.contract.StockListContract
import com.example.lab2.presentation.presenter.StockListPresenterImpl
import kotlinx.coroutines.launch

class StockListFragment : Fragment(), StockListContract.View {

    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!

    private val symbols = listOf(
        "SBER",
        "GAZP",
        "LKOH",
        "YNDX",
        "ROSN",
        "VTBR",
        "NVTK",
        "MGNT"
    )

    private lateinit var presenter: StockListContract.Presenter
    private lateinit var adapter: StockAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = StockListPresenterImpl(QuotesRepositoryImpl(), symbols)

        binding.stockRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = StockAdapter(emptyList()) { stock ->
            presenter.onStockClicked(stock)
        }
        binding.stockRecyclerView.adapter = adapter
        binding.stockRecyclerView.isEnabled = false
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this, viewLifecycleOwner.lifecycleScope)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                presenter.loadQuotes()
            }
        }
    }

    override fun onStop() {
        presenter.detach()
        super.onStop()
    }

    override fun renderLoading() {
        val b = _binding ?: return
        b.stockRecyclerView.isEnabled = false
    }

    override fun renderStockList(stocks: List<Stock>) {
        adapter.updateItems(stocks)
    }

    override fun showError(message: String) {
        context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }
    }

    override fun setListInteractionEnabled(enabled: Boolean) {
        val b = _binding ?: return
        b.stockRecyclerView.isEnabled = enabled
    }

    override fun showEmptyMessage() {
        if (!isAdded) return
        context?.let { Toast.makeText(it, "Акции не найдены", Toast.LENGTH_SHORT).show() }
    }

    override fun navigateToStockChart(stock: Stock) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, StockChartFragment.newInstance(stock))
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class StockAdapter(
        private var items: List<Stock>,
        private val onItemClick: (Stock) -> Unit
    ) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

        fun updateItems(newItems: List<Stock>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stock, parent, false)
            return StockViewHolder(view)
        }

        override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
            holder.bind(items[position], onItemClick)
        }

        override fun getItemCount(): Int = items.size

        class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val symbolTextView: TextView = itemView.findViewById(R.id.symbolTextView)
            private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
            private val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
            private val changeTextView: TextView = itemView.findViewById(R.id.changeTextView)

            fun bind(stock: Stock, onItemClick: (Stock) -> Unit) {
                symbolTextView.text = stock.symbol
                nameTextView.text = stock.name
                priceTextView.text = stock.price
                changeTextView.text = stock.change

                changeTextView.setTextColor(
                    when {
                        stock.change.startsWith("+") -> ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark)
                        stock.change.startsWith("-") -> ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
                        else -> ContextCompat.getColor(itemView.context, android.R.color.darker_gray)
                    }
                )

                itemView.setOnClickListener { onItemClick(stock) }
            }
        }
    }
}
