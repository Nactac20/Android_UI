package com.example.lab2.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

class MoexStockApi(
    private val baseUrl: String = "https://iss.moex.com/iss/engines/stock/markets/shares/boards/TQBR/securities/"
) {
    private val service: MoexService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(MoexService::class.java)
    }

    interface MoexService {
        @GET("{symbol}.json")
        suspend fun getQuote(@Path("symbol") symbol: String): MoexResponse
    }

    data class MoexResponse(
        val securities: MoexSection,
        val marketdata: MoexSection
    )

    data class MoexSection(
        val columns: List<String>,
        val data: List<List<Any?>>
    )

    suspend fun fetchQuote(symbol: String): StockQuote = withContext(Dispatchers.IO) {
        try {
            val response = service.getQuote(symbol)
            parseResponse(response, symbol)
        } catch (e: Exception) {
            // тут можно сделать нормальную ошибку
            throw e
        }
    }

    private fun parseResponse(root: MoexResponse, symbol: String): StockQuote {
        val secColumns = root.securities.columns
        val secData = root.securities.data
        val secIdx = columnsToIndex(secColumns)

        val boardIdIdxSec = secIdx["BOARDID"] ?: return StockQuote(symbol, symbol, 0.0, 0.0, 0.0)
        val shortNameIdx = secIdx["SHORTNAME"] ?: return StockQuote(symbol, symbol, 0.0, 0.0, 0.0)
        val prevPriceIdx = secIdx["PREVPRICE"] ?: return StockQuote(symbol, symbol, 0.0, 0.0, 0.0)

        val prevRow = findRowWithBoardAndPrevPrice(secData, boardIdIdxSec, prevPriceIdx)
        val prevPrice = prevRow?.let { safeGetDoubleOrNull(it, prevPriceIdx) } ?: 0.0
        val name = prevRow?.let { safeGetString(it, shortNameIdx) } ?: symbol

        val lastColumns = root.marketdata.columns
        val lastData = root.marketdata.data
        val mdIdx = columnsToIndex(lastColumns)

        val boardIdIdxMd = mdIdx["BOARDID"]
        val lastIdx = mdIdx["LAST"]
        if (boardIdIdxMd == null || lastIdx == null) {
            return StockQuote(symbol, name, 0.0, 0.0, 0.0)
        }

        val lastRow = findRowWithBoardId(lastData, boardIdIdxMd, "TQBR")
        val lastPrice = lastRow?.let { safeGetDoubleOrNull(it, lastIdx) } ?: 0.0

        val lastChangeIdx = mdIdx["LASTCHANGE"]
        val lastChangePctIdx = mdIdx["LASTCHANGEPRCNT"]

        val changeAbsFromApi = lastChangeIdx?.let { idx ->
            lastRow?.let { safeGetDoubleOrNull(it, idx) }
        }

        val changeAbs = when {
            changeAbsFromApi != null -> changeAbsFromApi
            prevPrice > 0.0 -> lastPrice - prevPrice
            else -> 0.0
        }

        val changePctFromApi = lastChangePctIdx?.let { idx -> lastRow?.let { safeGetDoubleOrNull(it, idx) } }

        val changePct = when {
            changePctFromApi != null -> changePctFromApi
            prevPrice > 0.0 -> (changeAbs / prevPrice) * 100.0
            else -> 0.0
        }

        return StockQuote(
            symbol = symbol,
            name = name,
            price = lastPrice,
            changeAbs = changeAbs,
            changePct = changePct
        )
    }

    private fun columnsToIndex(columns: List<String>): Map<String, Int> {
        val result = HashMap<String, Int>(columns.size)
        for (i in columns.indices) {
            result[columns[i]] = i
        }
        return result
    }

    private fun findRowWithBoardAndPrevPrice(
        rows: List<List<Any?>>,
        boardIdIdx: Int,
        prevPriceIdx: Int
    ): List<Any?>? {
        for (row in rows) {
            val boardId = safeGetStringOrNull(row, boardIdIdx)
            if (boardId == "TQBR" && !isNull(row, prevPriceIdx)) {
                return row
            }
        }
        // Fallback: first row with a non-null PREVPRICE.
        for (row in rows) {
            if (!isNull(row, prevPriceIdx)) return row
        }
        return null
    }

    private fun findRowWithBoardId(rows: List<List<Any?>>, boardIdIdx: Int, boardId: String): List<Any?>? {
        for (row in rows) {
            val bid = safeGetStringOrNull(row, boardIdIdx)
            if (bid == boardId) return row
        }
        return null
    }

    private fun safeGetDoubleOrNull(row: List<Any?>, idx: Int): Double? {
        if (idx < 0 || idx >= row.size) return null
        val value = row[idx] ?: return null
        return when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
    }

    private fun safeGetString(row: List<Any?>, idx: Int): String {
        if (idx < 0 || idx >= row.size) return ""
        val value = row[idx] ?: return ""
        return value.toString()
    }

    private fun safeGetStringOrNull(row: List<Any?>, idx: Int): String? {
        if (idx < 0 || idx >= row.size) return null
        val value = row[idx] ?: return null
        return value.toString()
    }

    private fun isNull(row: List<Any?>, idx: Int): Boolean {
        if (idx < 0 || idx >= row.size) return true
        return row[idx] == null
    }
}
