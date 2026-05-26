package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

data class BarChartData(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun HorizontalBarChart(
    data: List<BarChartData>,
    modifier: Modifier = Modifier,
    barHeight: Dp = 24.dp,
    maxValue: Float = 100f,
    showPercentageLabels: Boolean = true,
    showAxisLabels: Boolean = true,
    animationDuration: Int = 800,
    emptyStateMessage: String = "Nenhum dado disponível"
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            data.forEach { item ->
                BarChartRow(
                    item = item,
                    maxValue = maxValue,
                    barHeight = barHeight,
                    backgroundColor = backgroundColor,
                    showPercentageLabel = showPercentageLabels,
                    animationDuration = animationDuration
                )
            }
            
            if (showAxisLabels) {
                Spacer(modifier = Modifier.height(4.dp))
                AxisLabels(maxValue = maxValue)
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
            modifier = Modifier.width(80.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Box(
            modifier = Modifier
                .weight(1f)
                .height(barHeight)
                .clip(RoundedCornerShape(4.dp))
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedWidth)
                    .height(barHeight)
                    .clip(RoundedCornerShape(4.dp))
                    .background(item.color)
            )
        }
        
        if (showPercentageLabel) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${item.value.toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(40.dp)
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
                            value = 40f,
                            color = Color(0xFF00D4B2)
                        ),
                        BarChartData(
                            label = "Qualidade",
                            value = 32f,
                            color = Color(0xFF00D4B2)
                        ),
                        BarChartData(
                            label = "RH",
                            value = 18f,
                            color = Color(0xFFFF7A00)
                        ),
                        BarChartData(
                            label = "TI",
                            value = 12f,
                            color = Color(0xFFFF7A00)
                        )
                    ),
                    maxValue = 50f
                )
            }
        }
    }
}
