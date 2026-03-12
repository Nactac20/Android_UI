package com.example.lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.databinding.FragmentStockListBinding

class StockListFragment : Fragment() {

    private var _binding: FragmentStockListBinding? = null
    private val binding get() = _binding!!

    private val stocks = listOf(
        Stock("SBER", "Сбербанк", "283.50 ₽", "+1.2%"),
        Stock("GAZP", "Газпром", "198.30 ₽", "-0.5%"),
        Stock("LKOH", "Лукойл", "7,450.00 ₽", "+0.8%"),
        Stock("YNDX", "Яндекс", "3,620.00 ₽", "+2.1%"),
        Stock("ROSN", "Роснефть", "567.80 ₽", "-0.3%"),
        Stock("VTBR", "ВТБ", "0.0235 ₽", "0.0%"),
        Stock("NVTK", "НОВАТЭК", "1,234.50 ₽", "+0.4%"),
        Stock("MGNT", "Магнит", "5,890.00 ₽", "-1.1%")
    )

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

        binding.stockRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.stockRecyclerView.adapter = StockAdapter(stocks) { stock ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, StockChartFragment.newInstance(stock))
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class StockAdapter(
        private val items: List<Stock>,
        private val onItemClick: (Stock) -> Unit
    ) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

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