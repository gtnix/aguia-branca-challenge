package com.gtnix.aguiabranca.presentation.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

private val brLocale = Locale("pt", "BR")
private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(brLocale)

fun formatCurrency(value: Double): String = currencyFormat.format(value)

fun formatPercent(value: Double): String = "%.1f%%".format(brLocale, value)

/** Formata valor monetário compacto: R$ 82k, R$ 2,5M. */
fun formatCurrencyCompact(value: Double): String {
    val magnitude = abs(value)
    val sign = if (value < 0) "-" else ""
    return when {
        magnitude >= 1_000_000 -> sign + "R$\u00A0%.1fM".format(brLocale, magnitude / 1_000_000)
        magnitude >= 1_000 -> sign + "R$\u00A0%.0fk".format(brLocale, magnitude / 1_000)
        else -> formatCurrency(value)
    }
}
