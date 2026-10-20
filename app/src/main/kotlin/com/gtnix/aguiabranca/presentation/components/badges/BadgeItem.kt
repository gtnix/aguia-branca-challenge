package com.gtnix.aguiabranca.presentation.components.badges

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.Conquista
import com.gtnix.aguiabranca.domain.model.ConquistaTier
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.tierAccent
import com.gtnix.aguiabranca.presentation.util.icon
import com.gtnix.aguiabranca.presentation.util.tituloExibicao
import kotlinx.coroutines.delay

private const val SHIMMER_DURATION_MS = 800L

@Composable
fun BadgeItem(
    conquista: Conquista,
    tier: ConquistaTier = conquista.tier,
    size: Dp = 72.dp,
    showLabel: Boolean = true,
    isNewlyUnlocked: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = LocalDarkTheme.current
    val desbloqueada = conquista.desbloqueada
    val accent = tierAccent(tier, isDarkTheme)
    val shape = RoundedCornerShape(18.dp)

    val scale = remember { Animatable(if (isNewlyUnlocked) 0.5f else 1f) }
    var shimmerOffset by remember { mutableFloatStateOf(-1f) }

    LaunchedEffect(isNewlyUnlocked) {
        if (isNewlyUnlocked) {
            scale.snapTo(0.5f)
            scale.animateTo(
                targetValue = 1.2f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
            )
            shimmerOffset = -1f
            val steps = 20
            repeat(steps) { step ->
                shimmerOffset = -1f + (step + 1) * 2f / steps
                delay(SHIMMER_DURATION_MS / steps)
            }
        }
    }

    val haptic = LocalHapticFeedback.current

    val clickableModifier = if (onClick != null) {
        Modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            }
    } else {
        Modifier
    }

    val surfaceColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = modifier.then(clickableModifier),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .size(size)
                .clip(shape)
                .then(
                    if (desbloqueada) {
                        Modifier
                            .background(surfaceColor)
                            .drawBehind {
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(accent.copy(alpha = 0.18f), Color.Transparent),
                                        center = Offset(this.size.width / 2f, 0f),
                                        radius = this.size.height
                                    )
                                )
                            }
                            .border(1.5.dp, accent.copy(alpha = 0.7f), shape)
                    } else {
                        Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
                    }
                )
                .then(
                    if (isNewlyUnlocked && shimmerOffset in -0.5f..1.5f) {
                        Modifier.drawWithContent {
                            drawContent()
                            val shimmerWidth = this.size.width * 0.4f
                            val centerX = shimmerOffset * this.size.width
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.45f),
                                        Color.Transparent
                                    ),
                                    start = Offset(centerX - shimmerWidth, 0f),
                                    end = Offset(centerX + shimmerWidth, this.size.height)
                                )
                            )
                        }
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = conquista.tipo.icon(),
                contentDescription = conquista.tituloExibicao(),
                tint = if (desbloqueada) {
                    accent
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                },
                modifier = Modifier.size(26.dp)
            )

            if (desbloqueada) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                        .size(12.dp)
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = conquista.tituloExibicao(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (desbloqueada) FontWeight.Medium else FontWeight.Normal,
                color = if (desbloqueada) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                },
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
