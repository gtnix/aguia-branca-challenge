package com.gtnix.aguiabranca.presentation.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ptBr = Locale("pt", "BR")
private val zoneId = ZoneId.systemDefault()

private val longDateFormatter = DateTimeFormatter
    .ofPattern("dd 'de' MMMM 'de' yyyy", ptBr)
    .withZone(zoneId)

fun formatLongDate(timestamp: Long): String =
    longDateFormatter.format(Instant.ofEpochMilli(timestamp))

fun formatLongDateOrNull(timestamp: Long?): String? =
    timestamp?.let { formatLongDate(it) }
