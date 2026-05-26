package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen

@Composable
fun BentoMetricCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "scale"
    )
    
    val isDarkTheme = isSystemInDarkTheme()
    
    val cardModifier = modifier
        .scale(scale)
        .then(
            if (onClick != null) {
                Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            isPressed = true
                            tryAwaitRelease()
                            isPressed = false
                        },
                        onTap = { onClick() }
                    )
                }
            } else {
                Modifier
            }
        )
    
    if (isDarkTheme) {
        GlassCard(modifier = cardModifier) {
            Box(modifier = Modifier.fillMaxSize()) {
                BentoMetricContent(
                    title = title,
                    value = value,
                    subtitle = subtitle,
                    accentColor = accentColor,
                    icon = icon
                )
            }
        }
    } else {
        Card(
            modifier = cardModifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.medium,
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                BentoMetricContent(
                    title = title,
                    value = value,
                    subtitle = subtitle,
                    accentColor = accentColor,
                    icon = icon
                )
            }
        }
    }
}

@Composable
private fun BentoMetricContent(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun BentoMetricCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BentoMetricCard(
                    title = "Ideias Enviadas",
                    value = "24",
                    subtitle = "+3 este mês",
                    accentColor = MaterialTheme.colorScheme.primary,
                    icon = Icons.Default.Lightbulb,
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
                
                BentoMetricCard(
                    title = "Taxa de Aprovação",
                    value = "87%",
                    subtitle = "Acima da média",
                    accentColor = SuccessGreen,
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f),
                    onClick = {}
                )
            }
        }
    }
}

// =============================================================================
// GRADIENT METRIC CARD - Premium fintech-style card with gradient background
// =============================================================================

object GradientMetricCardDefaults {
    // Unified neutral palette - premium fintech style
    val UnifiedGradientDark = listOf(Color(0xFF1C1C1E), Color(0xFF141416))
    val UnifiedGradientLight = listOf(Color(0xFFFFFFFF), Color(0xFFFAFBFC))
    
    // Legacy aliases for backwards compatibility - all point to unified
    val IdeiasGradientDark = UnifiedGradientDark
    val IdeiasGradientLight = UnifiedGradientLight
    
    val ProjetosGradientDark = UnifiedGradientDark
    val ProjetosGradientLight = UnifiedGradientLight
    
    val EngajamentoGradientDark = UnifiedGradientDark
    val EngajamentoGradientLight = UnifiedGradientLight
}

@Composable
fun GradientMetricCard(
    title: String,
    value: String,
    trend: String,
    trendPositive: Boolean,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    accentColor: Color? = null,
    onClick: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "gradient_card_scale"
    )
    
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(20.dp)
    
    val backgroundBrush = Brush.verticalGradient(
        colors = gradientColors
    )
    
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.06f)
    } else {
        Color(0xFFE5E7EB)
    }
    
    val contentColor = if (isDarkTheme) Color.White else Color(0xFF1A1A1A)
    val secondaryContentColor = if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color(0xFF6B7280)
    
    val iconAccent = accentColor ?: MaterialTheme.colorScheme.primary
    val iconBackgroundColor = iconAccent.copy(alpha = if (isDarkTheme) 0.15f else 0.1f)
    val iconTintColor = iconAccent
    
    Box(
        modifier = modifier
            .scale(scale)
            .then(
                if (!isDarkTheme) {
                    Modifier.shadow(
                        elevation = 2.dp,
                        shape = shape,
                        ambientColor = Color.Black.copy(alpha = 0.04f),
                        spotColor = Color.Black.copy(alpha = 0.04f)
                    )
                } else Modifier
            )
            .clip(shape)
            .background(backgroundBrush)
            .border(1.dp, borderColor, shape)
            .then(
                if (onClick != null) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                isPressed = true
                                tryAwaitRelease()
                                isPressed = false
                            },
                            onTap = { onClick() }
                        )
                    }
                } else {
                    Modifier
                }
            )
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconBackgroundColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTintColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                letterSpacing = (-0.5).sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = secondaryContentColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = if (trendPositive) SuccessGreen else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = trend,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (trendPositive) SuccessGreen else MaterialTheme.colorScheme.error,
                    fontSize = 10.sp,
                    maxLines = 2,
                    lineHeight = 12.sp
                )
            }
        }
    }
}

@Preview(name = "Gradient Cards - Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GradientMetricCardDarkPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientMetricCard(
                    title = "Ideias Ativas",
                    value = "47",
                    trend = "+12 esta semana",
                    trendPositive = true,
                    icon = Icons.Default.Lightbulb,
                    gradientColors = GradientMetricCardDefaults.IdeiasGradientDark,
                    modifier = Modifier.weight(1f)
                )
                
                GradientMetricCard(
                    title = "Projetos",
                    value = "12",
                    trend = "+3 este mês",
                    trendPositive = true,
                    icon = Icons.Default.Folder,
                    gradientColors = GradientMetricCardDefaults.ProjetosGradientDark,
                    modifier = Modifier.weight(1f)
                )
                
                GradientMetricCard(
                    title = "Engajamento",
                    value = "89%",
                    trend = "+8% este mês",
                    trendPositive = true,
                    icon = Icons.Default.Groups,
                    gradientColors = GradientMetricCardDefaults.EngajamentoGradientDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(name = "Gradient Cards - Light Mode")
@Composable
private fun GradientMetricCardLightPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientMetricCard(
                    title = "Ideias Ativas",
                    value = "47",
                    trend = "+12 esta semana",
                    trendPositive = true,
                    icon = Icons.Default.Lightbulb,
                    gradientColors = GradientMetricCardDefaults.IdeiasGradientLight,
                    modifier = Modifier.weight(1f)
                )
                
                GradientMetricCard(
                    title = "Projetos",
                    value = "12",
                    trend = "+3 este mês",
                    trendPositive = true,
                    icon = Icons.Default.Folder,
                    gradientColors = GradientMetricCardDefaults.ProjetosGradientLight,
                    modifier = Modifier.weight(1f)
                )
                
                GradientMetricCard(
                    title = "Engajamento",
                    value = "89%",
                    trend = "+8% este mês",
                    trendPositive = true,
                    icon = Icons.Default.Groups,
                    gradientColors = GradientMetricCardDefaults.EngajamentoGradientLight,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
