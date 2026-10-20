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
