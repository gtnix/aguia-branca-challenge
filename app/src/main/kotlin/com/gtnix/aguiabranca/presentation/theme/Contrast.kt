package com.gtnix.aguiabranca.presentation.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.pow

private fun channel(value: Float): Double {
    val srgb = value.toDouble().coerceIn(0.0, 1.0)
    return if (srgb <= 0.04045) srgb / 12.92 else ((srgb + 0.055) / 1.055).pow(2.4)
}

fun relativeLuminance(color: Color): Double {
    return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) + 0.0722 * channel(color.blue)
}

fun contrastRatio(foreground: Color, background: Color): Double {
    val lighter = maxOf(relativeLuminance(foreground), relativeLuminance(background))
    val darker = minOf(relativeLuminance(foreground), relativeLuminance(background))
    return (lighter + 0.05) / (darker + 0.05)
}

fun compositeOver(foreground: Color, background: Color): Color {
    val alpha = foreground.alpha.coerceIn(0f, 1f)
    return Color(
        red = alpha * foreground.red + (1f - alpha) * background.red,
        green = alpha * foreground.green + (1f - alpha) * background.green,
        blue = alpha * foreground.blue + (1f - alpha) * background.blue,
        alpha = 1f
    )
}

/** Tinta com maior contraste sobre o preenchimento. Marinho vence no azul de ação; branco vence na asa. */
fun contentOn(fill: Color): Color {
    val ink = TextoInstitucional
    val paper = Color.White
    return if (contrastRatio(ink, fill) >= contrastRatio(paper, fill)) ink else paper
}
