package com.gtnix.aguiabranca.presentation.util

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Modifier que adiciona efeito de "bounce" (escala) ao clicar em um elemento.
 *
 * Usa Spring Animation para um feedback tátil suave e responsivo,
 * seguindo as diretrizes do Material Motion.
 *
 * @param scaleDown Escala mínima durante o press (0.96f = 96% do tamanho original)
 * @param onClick Callback executado ao soltar o elemento
 */
fun Modifier.bounceClick(
    scaleDown: Float = 0.96f,
    onClick: () -> Unit
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "bounceClickScale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
        .pointerInput(Unit) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                isPressed = true
                waitForUpOrCancellation()
                isPressed = false
            }
        }
}

/**
 * Variante do bounceClick com escala mais sutil para elementos menores.
 */
fun Modifier.subtleBounceClick(
    onClick: () -> Unit
): Modifier = bounceClick(scaleDown = 0.98f, onClick = onClick)

/**
 * Variante do bounceClick com escala mais pronunciada para elementos maiores.
 */
fun Modifier.strongBounceClick(
    onClick: () -> Unit
): Modifier = bounceClick(scaleDown = 0.94f, onClick = onClick)
