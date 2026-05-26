package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import kotlin.random.Random

@Composable
fun WaveformIndicator(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    color: Color = MaterialTheme.colorScheme.primary,
    minHeight: Dp = 8.dp,
    maxHeight: Dp = 24.dp
) {
    val barDurations = remember {
        List(barCount) { Random.nextInt(300, 500) }
    }
    
    Row(
        modifier = modifier.height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { index ->
            WaveformBar(
                isActive = isActive,
                color = color,
                minHeight = minHeight,
                maxHeight = maxHeight,
                durationMillis = barDurations[index],
                delayMillis = index * 50
            )
        }
    }
}

@Composable
private fun WaveformBar(
    isActive: Boolean,
    color: Color,
    minHeight: Dp,
    maxHeight: Dp,
    durationMillis: Int,
    delayMillis: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveformBar")
    
    val heightFraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                delayMillis = delayMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "barHeight"
    )
    
    val currentHeight = if (isActive) {
        minHeight + (maxHeight - minHeight) * heightFraction
    } else {
        minHeight
    }
    
    Box(
        modifier = modifier
            .width(4.dp)
            .height(currentHeight)
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}

@Preview(name = "Light Mode - Active")
@Preview(name = "Dark Mode - Active", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WaveformIndicatorActivePreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            WaveformIndicator(
                isActive = true,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Light Mode - Inactive")
@Preview(name = "Dark Mode - Inactive", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun WaveformIndicatorInactivePreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            WaveformIndicator(
                isActive = false,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
