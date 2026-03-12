package com.example.lab2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Stock(
    val symbol: String,
    val name: String,
    val price: String,
    val change: String
)

class StockListFragment : Fragment(R.layout.fragment_stock_list) {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.stockRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = StockAdapter(stocks)
    }

    private class StockAdapter(
        private val items: List<Stock>
    ) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_list_item_2, parent, false)
            return StockViewHolder(view)
        }

        override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val text1: TextView = itemView.findViewById(android.R.id.text1)
            private val text2: TextView = itemView.findViewById(android.R.id.text2)

            fun bind(stock: Stock) {
                text1.text = "${stock.symbol} - ${stock.name}"
                text2.text = "${stock.price} (${stock.change})"
            }
        }
    }
}
