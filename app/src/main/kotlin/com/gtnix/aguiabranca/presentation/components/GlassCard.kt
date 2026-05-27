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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.GlassBorder
import com.gtnix.aguiabranca.presentation.theme.GlassFill
import com.gtnix.aguiabranca.presentation.theme.GlassFillLight
import com.gtnix.aguiabranca.presentation.theme.GlassBorderLight
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    GlassCardInternal(
        modifier = modifier,
        blurRadius = 80.dp,
        showGradientOverlay = false,
        content = content
    )
}

@Composable
fun BackdropGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    GlassCardInternal(
        modifier = modifier,
        blurRadius = 120.dp,
        showGradientOverlay = true,
        content = content
    )
}

@Composable
private fun GlassCardInternal(
    modifier: Modifier = Modifier,
    blurRadius: Dp,
    showGradientOverlay: Boolean,
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)

    val fillColor = if (isDarkTheme) {
        GlassFill.copy(alpha = 0.78f)
    } else {
        GlassFillLight
    }
    val borderColor = if (isDarkTheme) {
        GlassBorder.copy(alpha = 0.45f)
    } else {
        GlassBorderLight
    }

    val cardModifier = modifier
        .then(
            if (!isDarkTheme) {
                Modifier.shadow(
                    elevation = 2.dp,
                    shape = shape,
                    ambientColor = Color(0x0A0A2540),
                    spotColor = Color(0x140A2540)
                )
            } else {
                Modifier.shadow(
                    elevation = 4.dp,
                    shape = shape,
                    ambientColor = Color.Black.copy(alpha = 0.2f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
            }
        )
        .clip(shape)

    Box(modifier = cardModifier) {
        if (isDarkTheme) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .blur(blurRadius, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
                            )
                        )
                    )
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(fillColor)
                .border(width = 1.dp, color = borderColor, shape = shape)
        )

        if (showGradientOverlay) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = if (isDarkTheme) 0.12f else 0.05f
                                ),
                                Color.Transparent,
                                MaterialTheme.colorScheme.secondary.copy(
                                    alpha = if (isDarkTheme) 0.10f else 0.04f
                                )
                            )
                        )
                    )
            )
        }

        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
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
                        text = "Frosted elevated card with crisp border and soft shadow.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(name = "Backdrop Light")
@Preview(name = "Backdrop Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BackdropGlassCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BackdropGlassCard(
                modifier = Modifier.padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Hero Section",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Stronger blur with gradient overlay for profile hero areas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
