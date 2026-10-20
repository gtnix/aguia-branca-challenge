package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

data class BarChartData(
    val label: String,
    val value: Float,
    val color: Color
)

/**
 * Calcula o valor máximo do eixo X arredondando para cima em intervalos legíveis (25%, 50%, 75%, etc.).
 */
fun computeBarChartMaxValue(values: List<Float>): Float {
    if (values.isEmpty()) return 100f
    val max = values.max()
    if (max <= 0f) return 25f
    val step = 25f
    val rounded = ((max / step).toInt() + 1) * step
    return rounded.coerceAtLeast(max).coerceAtMost(100f)
}

@Composable
fun HorizontalBarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier,
    barHeight: Dp = 10.dp,
    maxValue: Float? = null,
    showPercentageLabels: Boolean = true,
    showAxisLabels: Boolean = false,
    animationDuration: Int = 800,
    emptyStateMessage: String = "Nenhum dado disponível"
) {
    val effectiveMaxValue = maxValue ?: computeBarChartMaxValue(data.map { it.value })
    val backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f)

    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyStateMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            data.forEach { item ->
                BarChartRow(
                    item = item,
                    maxValue = effectiveMaxValue,
                    barHeight = barHeight,
                    backgroundColor = backgroundColor,
                    showPercentageLabel = showPercentageLabels,
                    animationDuration = animationDuration
                )
            }
            
            if (showAxisLabels) {
                Spacer(modifier = Modifier.height(4.dp))
                AxisLabels(maxValue = effectiveMaxValue)
            }
        }
    }
}

@Composable
private fun BarChartRow(
    item: BarChartData,
    maxValue: Float,
    barHeight: Dp,
    backgroundColor: Color,
    showPercentageLabel: Boolean,
    animationDuration: Int,
    modifier: Modifier = Modifier
) {
    var animatedValue by remember { mutableFloatStateOf(0f) }
    val animatedWidth by animateFloatAsState(
        targetValue = animatedValue,
        animationSpec = tween(durationMillis = animationDuration),
        label = "barWidth"
    )
    
    LaunchedEffect(item.value) {
        animatedValue = (item.value / maxValue).coerceIn(0f, 1f)
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(90.dp)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
                .clip(RoundedCornerShape(barHeight / 2))
                .background(backgroundColor)
        ) {
            if (animatedWidth > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedWidth)
                        .height(barHeight)
                        .clip(RoundedCornerShape(barHeight / 2))
                        .background(item.color)
                )
            }
        }

        if (showPercentageLabel) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${item.value.toInt()}%",
                style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End,
                modifier = Modifier.width(44.dp)
            )
        }
    }
}

@Composable
private fun AxisLabels(
    maxValue: Float,
    modifier: Modifier = Modifier
) {
    val steps = listOf(0f, maxValue * 0.25f, maxValue * 0.5f, maxValue * 0.75f, maxValue)
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 92.dp, end = 52.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEach { value ->
            Text(
                text = "${value.toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HorizontalBarChartPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Desempenho por Área",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "% de ideias com impacto positivo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                HorizontalBarChart(
                    data = listOf(
                        BarChartData(
                            label = "Logística",
                            value = 66f,
                            color = Color(0xFF005FC5)
                        ),
                        BarChartData(
                            label = "Qualidade",
                            value = 33f,
                            color = Color(0xFF008FFF)
                        ),
                        BarChartData(
                            label = "RH",
                            value = 18f,
                            color = Color(0xFFE87C1A)
                        ),
                        BarChartData(
                            label = "TI",
                            value = 12f,
                            color = Color(0xFF7A2E85)
                        )
                    )
                )
            }
        }
    }
}
