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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen

enum class TimelineStatus {
    COMPLETED,
    CURRENT,
    FUTURE
}

data class MarcoTimeline(
    val titulo: String,
    val data: String,
    val status: TimelineStatus
)

@Composable
fun ProjectTimeline(
    marcos: List<MarcoTimeline>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        marcos.forEachIndexed { index, marco ->
            TimelineItem(
                marco = marco,
                isLast = index == marcos.lastIndex
            )
        }
    }
}

@Composable
private fun TimelineItem(
    marco: MarcoTimeline,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimelineDot(status = marco.status)
            
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(
                            when (marco.status) {
                                TimelineStatus.COMPLETED -> SuccessGreen.copy(alpha = 0.5f)
                                TimelineStatus.CURRENT -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                                TimelineStatus.FUTURE -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            }
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.padding(top = 2.dp)
        ) {
            Text(
                text = marco.titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = when (marco.status) {
                    TimelineStatus.CURRENT -> FontWeight.Bold
                    else -> FontWeight.Medium
                },
                color = when (marco.status) {
                    TimelineStatus.COMPLETED -> MaterialTheme.colorScheme.onSurface
                    TimelineStatus.CURRENT -> MaterialTheme.colorScheme.primary
                    TimelineStatus.FUTURE -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                }
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = marco.data,
                style = MaterialTheme.typography.labelSmall,
                color = when (marco.status) {
                    TimelineStatus.CURRENT -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.outline
                }
            )
            
            if (!isLast) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TimelineDot(
    status: TimelineStatus,
    modifier: Modifier = Modifier
) {
    val dotSize = 24.dp
    
    when (status) {
        TimelineStatus.COMPLETED -> {
            Box(
                modifier = modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(SuccessGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
        
        TimelineStatus.CURRENT -> {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.4f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseAlpha"
            )
            
            Box(
                modifier = modifier
                    .size(dotSize)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
        
        TimelineStatus.FUTURE -> {
            Box(
                modifier = modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .background(Color.Transparent)
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProjectTimelinePreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProjectTimeline(
                marcos = listOf(
                    MarcoTimeline(
                        titulo = "Levantamento de Requisitos",
                        data = "10/05/2024",
                        status = TimelineStatus.COMPLETED
                    ),
                    MarcoTimeline(
                        titulo = "Desenvolvimento do MVP",
                        data = "05/06/2024",
                        status = TimelineStatus.COMPLETED
                    ),
                    MarcoTimeline(
                        titulo = "Testes e Validação",
                        data = "Em andamento",
                        status = TimelineStatus.CURRENT
                    ),
                    MarcoTimeline(
                        titulo = "Implantação Piloto",
                        data = "20/06/2024",
                        status = TimelineStatus.FUTURE
                    ),
                    MarcoTimeline(
                        titulo = "Lançamento Oficial",
                        data = "30/06/2024",
                        status = TimelineStatus.FUTURE
                    )
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
