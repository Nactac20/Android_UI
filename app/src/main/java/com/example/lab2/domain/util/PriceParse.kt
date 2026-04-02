package com.example.lab2.domain.util

fun parsePrice(priceString: String): Double {
    val s = priceString
        .replace("₽", "")
        .trim()
        .replace("\u00A0", " ")
        .replace(" ", "")
        .replace(",", ".")
    return s.toDoubleOrNull() ?: 0.0
}
