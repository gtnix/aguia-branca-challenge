package com.gtnix.aguiabranca.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Componente que anima a contagem de um número inteiro de 0 até o valor alvo.
 *
 * Usado para dar feedback visual de métricas no Dashboard,
 * seguindo as diretrizes de Material Motion.
 *
 * @param targetValue Valor final da contagem
 * @param durationMillis Duração da animação em milissegundos
 * @param modifier Modifier opcional
 * @param style Estilo do texto
 * @param color Cor do texto
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    durationMillis: Int = 1200,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = Color.Unspecified
) {
    var animatedValue by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(targetValue) {
        animatedValue = targetValue.toFloat()
    }

    val displayValue by animateFloatAsState(
        targetValue = animatedValue,
        animationSpec = tween(durationMillis = durationMillis),
        label = "counterAnimation"
    )

    Text(
        text = displayValue.roundToInt().toString(),
        modifier = modifier,
        style = style,
        fontWeight = fontWeight,
        color = color
    )
}

/**
 * Componente que anima a contagem de um valor monetário de 0 até o valor alvo.
 *
 * Formata automaticamente o valor como moeda brasileira (R$).
 *
 * @param targetValue Valor final da contagem
 * @param durationMillis Duração da animação em milissegundos
 * @param modifier Modifier opcional
 * @param style Estilo do texto
 * @param color Cor do texto
 */
@Composable
fun AnimatedCurrencyCounter(
    targetValue: Double,
    modifier: Modifier = Modifier,
    durationMillis: Int = 1500,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = Color.Unspecified
) {
    var animatedValue by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(targetValue) {
        animatedValue = targetValue.toFloat()
    }

    val displayValue by animateFloatAsState(
        targetValue = animatedValue,
        animationSpec = tween(durationMillis = durationMillis),
        label = "currencyAnimation"
    )

    val formattedValue = remember(displayValue) {
        val formatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        formatter.format(displayValue.toDouble())
    }

    Text(
        text = formattedValue,
        modifier = modifier,
        style = style,
        fontWeight = fontWeight,
        color = color
    )
}

/**
 * Componente que anima a contagem de uma porcentagem de 0 até o valor alvo.
 *
 * @param targetValue Valor final da porcentagem (ex: 64.5 para 64.5%)
 * @param durationMillis Duração da animação em milissegundos
 * @param modifier Modifier opcional
 * @param style Estilo do texto
 * @param color Cor do texto
 */
@Composable
fun AnimatedPercentCounter(
    targetValue: Double,
    modifier: Modifier = Modifier,
    durationMillis: Int = 1200,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = Color.Unspecified
) {
    var animatedValue by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(targetValue) {
        animatedValue = targetValue.toFloat()
    }

    val displayValue by animateFloatAsState(
        targetValue = animatedValue,
        animationSpec = tween(durationMillis = durationMillis),
        label = "percentAnimation"
    )

    val formattedValue = remember(displayValue) {
        String.format(Locale.US, "%.1f%%", displayValue)
    }

    Text(
        text = formattedValue,
        modifier = modifier,
        style = style,
        fontWeight = fontWeight,
        color = color
    )
}
