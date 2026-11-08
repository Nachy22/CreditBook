package com.example.creditbook.ui

import java.math.RoundingMode
import java.util.Locale

// 2550 -> "K25.50"
fun Long.toKwacha(): String = String.format(Locale.US, "K%,.2f", this / 100.0)

// "25.50" -> 2550. Returns null if empty, not a number, zero or negative.
fun parseKwacha(text: String): Long? {
    val number = text.trim().toBigDecimalOrNull() ?: return null
    if (number.signum() <= 0) return null
    return number.setScale(2, RoundingMode.HALF_UP).movePointRight(2).toLong()
}