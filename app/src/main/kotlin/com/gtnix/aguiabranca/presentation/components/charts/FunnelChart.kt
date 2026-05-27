package com.gtnix.aguiabranca.presentation.components.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class FunnelStep(val label: String, val value: Int, val color: Color)

@Composable
fun FunnelChart(steps: List<FunnelStep>, modifier: Modifier = Modifier) {
    val maxValue = steps.maxOfOrNull { it.value }?.toFloat() ?: 1f

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        steps.forEach { step ->
            val widthFraction = if (maxValue > 0) (step.value / maxValue).coerceAtLeast(0.1f) else 0.1f

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.label,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .height(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(widthFraction)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(14.dp))
                            .background(step.color)
                            .align(Alignment.CenterStart)
                    )
                    Text(
                        text = step.value.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FunnelChartHorizontal(
    steps: List<FunnelStep>,
    modifier: Modifier = Modifier
) {
    val maxValue = steps.maxOfOrNull { it.value }?.toFloat()?.coerceAtLeast(1f) ?: 1f

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        steps.forEach { step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(90.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(step.color.copy(alpha = 0.12f))
                ) {
                    val fraction = (step.value.toFloat() / maxValue).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .clip(RoundedCornerShape(12.dp))
                            .background(step.color)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = step.value.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = step.color,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
