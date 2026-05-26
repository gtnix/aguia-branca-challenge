package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

@Composable
fun InspirationHeroCard(
    headline: String,
    subheadline: String,
    ctaText: String,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(24.dp)
    
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )
    
    val gradientColors = if (isDarkTheme) {
        listOf(
            Color(0xFF0D2818),
            Color(0xFF0A1F14),
            Color(0xFF061210)
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surface
        )
    }
    
    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(colors = gradientColors)
            )
            .border(1.dp, borderColor, shape)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
                .size(120.dp)
                .scale(glowScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset.Unspecified,
                        radius = 200f
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = headline,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = subheadline,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onCtaClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = ctaText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun CompactIdeiaCard(
    titulo: String,
    status: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
    }
    
    Surface(
        modifier = modifier
            .width(160.dp)
            .border(1.dp, borderColor, shape),
        color = MaterialTheme.colorScheme.surface,
        shape = shape,
        shadowElevation = if (isDarkTheme) 0.dp else 2.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddNewIdeiaCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    val borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    
    Surface(
        modifier = modifier
            .width(160.dp)
            .border(2.dp, borderColor, shape),
        color = if (isDarkTheme) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        },
        shape = shape,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Nova Ideia",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InspirationHeroCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            InspirationHeroCard(
                headline = "Sua próxima grande ideia",
                subheadline = "pode transformar a empresa e impactar milhares de pessoas",
                ctaText = "Compartilhar Ideia",
                onCtaClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Compact Cards")
@Composable
private fun CompactCardsPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CompactIdeiaCard(
                    titulo = "Otimização de rotas",
                    status = "Em análise",
                    onClick = {}
                )
                AddNewIdeiaCard(onClick = {})
            }
        }
    }
}
