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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

@Composable
fun GradientProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    gradientColors: List<Color> = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary
    ),
    showLabel: Boolean = false,
    animationDuration: Int = 800
) {
    val shape = RoundedCornerShape(height / 2)
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val progressAnimation by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = animationDuration),
        label = "progress"
    )
    
    LaunchedEffect(progress) {
        animatedProgress = progress.coerceIn(0f, 1f)
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(height)
                .clip(shape)
                .background(backgroundColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progressAnimation)
                    .clip(shape)
                    .background(
                        brush = Brush.horizontalGradient(gradientColors)
                    )
            )
        }
        
        if (showLabel) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GradientProgressBarPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GradientProgressBar(
                    progress = 0.65f,
                    showLabel = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                GradientProgressBar(
                    progress = 0.35f,
                    showLabel = false,
                    modifier = Modifier.fillMaxWidth()
                )
                
                GradientProgressBar(
                    progress = 1f,
                    showLabel = true,
                    height = 12.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
