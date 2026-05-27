package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.MedalBronze
import com.gtnix.aguiabranca.presentation.theme.MedalGold
import com.gtnix.aguiabranca.presentation.theme.NeutralGray
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen

@Composable
fun RankingHeroCard(
    posicao: Int,
    totalParticipantes: Int,
    perfilLabel: String,
    divisaoLabel: String,
    deltaSemanaPosicao: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)
    val colorScheme = MaterialTheme.colorScheme
    val goldBrush = Brush.linearGradient(listOf(MedalGold, MedalBronze))

    val backgroundBrush = Brush.linearGradient(
        colors = listOf(
            colorScheme.primary,
            colorScheme.primary.copy(alpha = 0.8f),
            colorScheme.secondary.copy(alpha = 0.4f)
        ),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    val cardModifier = modifier
        .fillMaxWidth()
        .height(140.dp)
        .shadow(
            elevation = if (isSystemInDarkTheme()) 4.dp else 6.dp,
            shape = shape,
            ambientColor = colorScheme.primary.copy(alpha = 0.25f),
            spotColor = colorScheme.primary.copy(alpha = 0.25f)
        )
        .clip(shape)
        .background(backgroundBrush)
        .clickable(onClick = onClick)

    Box(modifier = cardModifier) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 12.dp)
                .size(120.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MedalGold.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        center = Offset(0.5f, 0.5f),
                        radius = 180f
                    ),
                    shape = CircleShape
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = posicao.toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .graphicsLayer { alpha = 0.99f }
                        .drawWithContent {
                            drawContent()
                            drawRect(brush = goldBrush, blendMode = BlendMode.SrcAtop)
                        }
                )
                Text(
                    text = "º",
                    style = MaterialTheme.typography.titleLarge.copy(
                        baselineShift = BaselineShift.Superscript,
                        fontWeight = FontWeight.Bold,
                        color = MedalGold
                    ),
                    modifier = Modifier.padding(start = 2.dp, top = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "lugar entre $perfilLabel",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.95f)
                )
                Text(
                    text = if (totalParticipantes == 1) {
                        "da $divisaoLabel · $totalParticipantes participante"
                    } else {
                        "da $divisaoLabel · $totalParticipantes participantes"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                deltaSemanaPosicao?.let { delta ->
                    RankingDeltaChip(delta = delta)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun RankingDeltaChip(delta: Int) {
    val (backgroundColor, textColor, label) = when {
        delta > 0 -> Triple(
            SuccessGreen.copy(alpha = 0.22f),
            SuccessGreen,
            "↑ $delta posições esta semana"
        )
        delta < 0 -> Triple(
            MaterialTheme.colorScheme.error.copy(alpha = 0.22f),
            MaterialTheme.colorScheme.error,
            "↓ ${kotlin.math.abs(delta)} posições esta semana"
        )
        else -> Triple(
            NeutralGray.copy(alpha = 0.22f),
            NeutralGray,
            "→ estável"
        )
    }

    Surface(
        shape = RoundedCornerShape(100),
        color = backgroundColor,
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RankingHeroCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RankingHeroCard(
                posicao = 1,
                totalParticipantes = 3,
                perfilLabel = "Estrategistas",
                divisaoLabel = "Logística",
                deltaSemanaPosicao = 2,
                onClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
