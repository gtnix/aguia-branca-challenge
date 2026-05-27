package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

@Composable
fun VoiceRecordButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val buttonScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.92f
            isRecording -> 1.0f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "buttonScale"
    )
    
    val buttonSize = if (isCompact) 56.dp else 72.dp
    val iconSize = if (isCompact) 24.dp else 28.dp
    val containerSize = if (isCompact) 100.dp else 160.dp
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    Box(
        modifier = modifier.size(containerSize),
        contentAlignment = Alignment.Center
    ) {
        if (!isCompact) {
            PremiumWaveEffect(
                isActive = isRecording,
                primaryColor = primaryColor,
                buttonSize = buttonSize,
                modifier = Modifier.size(containerSize)
            )
        }
        
        Box(
            modifier = Modifier
                .size(buttonSize + 8.dp)
                .clip(CircleShape)
                .background(
                    if (isRecording) {
                        primaryColor.copy(alpha = 0.15f)
                    } else {
                        surfaceColor
                    }
                )
                .border(
                    width = 2.dp,
                    color = if (isRecording) {
                        primaryColor.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(buttonSize)
                    .scale(buttonScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                primaryColor,
                                primaryColor.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = stringResource(
                        if (isRecording) R.string.cd_stop_recording 
                        else R.string.cd_start_recording
                    ),
                    modifier = Modifier.size(iconSize),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun PremiumWaveEffect(
    isActive: Boolean,
    primaryColor: Color,
    buttonSize: Dp,
    modifier: Modifier = Modifier
) {
    val barCount = 24
    val barDurations = remember {
        List(barCount) { index -> 
            350 + (index * 17) % 200
        }
    }
    
    val infiniteTransition = rememberInfiniteTransition(label = "barWaveform")
    
    val barHeights = barDurations.mapIndexed { index, duration ->
        infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = duration,
                    delayMillis = (index * 25) % 180,
                    easing = EaseInOutCubic
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar$index"
        )
    }
    
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )
    
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val innerRadius = buttonSize.toPx() / 2 + 12.dp.toPx()
        val maxBarLength = 20.dp.toPx()
        val minBarLength = 6.dp.toPx()
        val barWidth = 3.dp.toPx()
        
        if (isActive) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = glowAlpha),
                        primaryColor.copy(alpha = glowAlpha * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = innerRadius + maxBarLength + 16.dp.toPx()
                ),
                radius = innerRadius + maxBarLength + 16.dp.toPx(),
                center = Offset(centerX, centerY)
            )
        }
        
        for (i in 0 until barCount) {
            val angle = (i * 360f / barCount) - 90f
            val angleRad = Math.toRadians(angle.toDouble())
            
            val heightFraction = if (isActive) barHeights[i].value else 0.2f
            val barLength = minBarLength + (maxBarLength - minBarLength) * heightFraction
            
            val cos = kotlin.math.cos(angleRad).toFloat()
            val sin = kotlin.math.sin(angleRad).toFloat()
            
            val startX = centerX + innerRadius * cos
            val startY = centerY + innerRadius * sin
            val endX = centerX + (innerRadius + barLength) * cos
            val endY = centerY + (innerRadius + barLength) * sin
            
            val barAlpha = if (isActive) 0.6f + (heightFraction * 0.4f) else 0.25f
            
            drawLine(
                color = primaryColor.copy(alpha = barAlpha),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = barWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Preview(name = "Light Mode - Idle")
@Preview(name = "Dark Mode - Idle", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun VoiceRecordButtonIdlePreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            VoiceRecordButton(
                isRecording = false,
                onClick = {},
                modifier = Modifier.padding(48.dp)
            )
        }
    }
}

@Preview(name = "Light Mode - Recording")
@Preview(name = "Dark Mode - Recording", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun VoiceRecordButtonRecordingPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            VoiceRecordButton(
                isRecording = true,
                onClick = {},
                modifier = Modifier.padding(48.dp)
            )
        }
    }
}
