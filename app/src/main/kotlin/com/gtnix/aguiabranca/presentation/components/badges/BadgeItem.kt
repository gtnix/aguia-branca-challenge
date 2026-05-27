package com.gtnix.aguiabranca.presentation.components.badges

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
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
import com.gtnix.aguiabranca.presentation.theme.tierBrush
import com.gtnix.aguiabranca.presentation.util.color
import com.gtnix.aguiabranca.presentation.util.icon
import com.gtnix.aguiabranca.presentation.util.titulo
import kotlinx.coroutines.delay

private const val SHIMMER_DURATION_MS = 800L
private val LockedRingColor = Color(0xFFD0D5DD)
private val LockedInnerColor = Color(0xFFF2F4F7)

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
    val isDarkTheme = isSystemInDarkTheme()
    val typeColor = conquista.tipo.color()
    val iconSize = 28.dp
    val ringWidth = 3.dp

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

    val innerBackground = if (conquista.desbloqueada) {
        typeColor.copy(alpha = if (isDarkTheme) 0.22f else 0.18f)
    } else if (isDarkTheme) {
        Color(0xFF1C1C1E)
    } else {
        LockedInnerColor
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
                .then(
                    if (conquista.desbloqueada && !isDarkTheme) {
                        Modifier.shadow(
                            elevation = 4.dp,
                            shape = CircleShape,
                            ambientColor = typeColor.copy(alpha = 0.25f),
                            spotColor = typeColor.copy(alpha = 0.25f)
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(CircleShape)
                .then(
                    if (conquista.desbloqueada) {
                        Modifier.background(tierBrush(tier))
                    } else {
                        Modifier.background(LockedRingColor)
                    }
                )
                .padding(ringWidth)
                .clip(CircleShape)
                .background(innerBackground)
                .then(
                    if (isNewlyUnlocked && shimmerOffset in -0.5f..1.5f) {
                        Modifier.drawWithContent {
                            drawContent()
                            val shimmerWidth = size.toPx() * 0.4f
                            val centerX = shimmerOffset * size.toPx()
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.55f),
                                        Color.Transparent
                                    ),
                                    start = androidx.compose.ui.geometry.Offset(
                                        centerX - shimmerWidth,
                                        0f
                                    ),
                                    end = androidx.compose.ui.geometry.Offset(
                                        centerX + shimmerWidth,
                                        size.toPx()
                                    )
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
                contentDescription = conquista.tipo.titulo(),
                tint = if (conquista.desbloqueada) Color.White else LockedRingColor,
                modifier = Modifier.size(iconSize)
            )

            if (!conquista.desbloqueada) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = LockedRingColor,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                        .size(12.dp)
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = conquista.tipo.titulo(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (conquista.desbloqueada) FontWeight.Medium else FontWeight.Normal,
                color = if (conquista.desbloqueada) {
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
