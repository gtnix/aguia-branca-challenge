package com.gtnix.aguiabranca.presentation.util

import java.text.NumberFormat
import java.util.Locale

private val brLocale = Locale("pt", "BR")
private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(brLocale)

fun formatCurrency(value: Double): String = currencyFormat.format(value)

fun formatPercent(value: Double): String = "%.1f%%".format(value)
