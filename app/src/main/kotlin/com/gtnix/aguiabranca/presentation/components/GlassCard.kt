package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    
    val backgroundColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    val borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
    
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .padding(16.dp)
    ) {
        content()
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GlassCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            GlassCard(
                modifier = Modifier.padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "GlassCard Title",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This is the content inside the glass card with glassmorphism effect.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
