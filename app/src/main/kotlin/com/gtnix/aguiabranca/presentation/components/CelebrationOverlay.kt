package com.gtnix.aguiabranca.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val CELEBRATION_DURATION_MS = 3000
private const val PARTICLE_COUNT = 60

private data class ConfettiParticle(
    val startX: Float,
    val startY: Float,
    val color: Color,
    val size: Float,
    val isCircle: Boolean,
    val velocityX: Float,
    val velocityY: Float,
    val rotationSpeed: Float,
    val initialRotation: Float
)

@Composable
fun CelebrationOverlay(
    message: String,
    visible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val haptic = LocalHapticFeedback.current

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val brandColors = remember(primary, secondary, tertiary) {
        listOf(primary, secondary, tertiary)
    }

    val particles = remember {
        mutableStateListOf<ConfettiParticle>().apply {
            repeat(PARTICLE_COUNT) {
                add(
                    ConfettiParticle(
                        startX = Random.nextFloat(),
                        startY = Random.nextFloat() * 0.3f,
                        color = brandColors[Random.nextInt(brandColors.size)],
                        size = Random.nextFloat() * 8f + 4f,
                        isCircle = Random.nextBoolean(),
                        velocityX = (Random.nextFloat() - 0.5f) * 0.4f,
                        velocityY = Random.nextFloat() * 0.6f + 0.2f,
                        rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                        initialRotation = Random.nextFloat() * 360f
                    )
                )
            }
        }
    }

    val progress = remember { Animatable(0f) }
    val cardScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "celebrationCardScale"
    )

    LaunchedEffect(Unit) {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = CELEBRATION_DURATION_MS, easing = LinearEasing)
        )
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val overlayAlpha = 0.45f * (1f - progress.value * 0.5f)
            drawRect(Color.Black.copy(alpha = overlayAlpha))

            particles.forEach { particle ->
                val elapsed = progress.value * CELEBRATION_DURATION_MS / 1000f
                val x = (particle.startX + particle.velocityX * elapsed) * size.width
                val y = (particle.startY + particle.velocityY * elapsed) * size.height
                val rotation = particle.initialRotation + particle.rotationSpeed * elapsed
                val alpha = (1f - progress.value).coerceIn(0f, 1f)

                if (y < size.height + 50f) {
                    rotate(rotation, pivot = Offset(x, y)) {
                        if (particle.isCircle) {
                            drawCircle(
                                color = particle.color.copy(alpha = alpha),
                                radius = particle.size,
                                center = Offset(x, y)
                            )
                        } else {
                            drawRect(
                                color = particle.color.copy(alpha = alpha),
                                topLeft = Offset(x - particle.size / 2, y - particle.size),
                                size = Size(particle.size, particle.size * 1.6f)
                            )
                        }
                    }
                }
            }

            repeat(12) { i ->
                val burstProgress = (progress.value * 3f).coerceAtMost(1f)
                val angle = (i * 30f) * (Math.PI / 180f).toFloat()
                val radius = burstProgress * 120f
                val cx = size.width / 2 + cos(angle) * radius
                val cy = size.height / 2 + sin(angle) * radius
                drawCircle(
                    color = brandColors[i % brandColors.size].copy(alpha = (1f - burstProgress) * 0.6f),
                    radius = 4f,
                    center = Offset(cx, cy)
                )
            }
        }

        Surface(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .graphicsLayer {
                    scaleX = cardScale
                    scaleY = cardScale
                },
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 12.dp
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }
}
