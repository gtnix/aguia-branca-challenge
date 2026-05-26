package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(
    text: String,
    modifier: Modifier = Modifier,
    charDelay: Long = 30L,
    showCursor: Boolean = true,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    var visibleCharCount by remember(text) { mutableIntStateOf(0) }
    
    LaunchedEffect(text) {
        visibleCharCount = 0
        for (i in text.indices) {
            delay(charDelay)
            visibleCharCount = i + 1
        }
    }
    
    val visibleText = text.take(visibleCharCount)
    val isTyping = visibleCharCount < text.length
    
    Row(modifier = modifier) {
        Text(
            text = visibleText,
            style = style,
            color = color
        )
        
        if (showCursor) {
            BlinkingCursor(
                isVisible = true,
                color = color,
                style = style
            )
        }
    }
}

@Composable
private fun BlinkingCursor(
    isVisible: Boolean,
    color: Color,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    if (!isVisible) return
    
    val infiniteTransition = rememberInfiniteTransition(label = "cursorBlink")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 530),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )
    
    Text(
        text = "|",
        style = style,
        color = color.copy(alpha = alpha),
        modifier = modifier
    )
}

@Composable
fun StaticTextWithCursor(
    text: String,
    modifier: Modifier = Modifier,
    showCursor: Boolean = true,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(modifier = modifier) {
        Text(
            text = text,
            style = style,
            color = color
        )
        
        if (showCursor) {
            BlinkingCursor(
                isVisible = true,
                color = color,
                style = style
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TypewriterTextPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.padding(16.dp)) {
                TypewriterText(
                    text = "Sugiro implementar um sistema de rastreamento por GPS nos veículos da frota para..."
                )
            }
        }
    }
}

@Preview(name = "Static with Cursor")
@Composable
private fun StaticTextWithCursorPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.padding(16.dp)) {
                StaticTextWithCursor(
                    text = "Texto já digitado completamente"
                )
            }
        }
    }
}
