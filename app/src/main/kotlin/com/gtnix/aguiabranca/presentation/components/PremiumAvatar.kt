package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LightPrimary
import com.gtnix.aguiabranca.presentation.theme.MedalBronze
import com.gtnix.aguiabranca.presentation.theme.MedalGold
import com.gtnix.aguiabranca.presentation.theme.MedalSilver
import com.gtnix.aguiabranca.presentation.theme.levelBrush
import com.gtnix.aguiabranca.presentation.theme.levelGradientColors

@Composable
fun PremiumAvatar(
    nome: String,
    nivel: NivelUsuario,
    size: Dp = 64.dp,
    isInRanking: Boolean = false,
    podiumPosition: Int? = null,
    modifier: Modifier = Modifier
) {
    val ringWidth = 3.5.dp
    val initial = nome.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    val levelColors = levelGradientColors(nivel)
    val glowPadding = if (isInRanking) 8.dp else 0.dp
    val outerSize = size + ringWidth * 2 + glowPadding * 2

    val infiniteTransition = rememberInfiniteTransition(label = "avatarGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = modifier.size(outerSize),
        contentAlignment = Alignment.Center
    ) {
        if (isInRanking) {
            Box(
                modifier = Modifier
                    .size(size + 16.dp)
                    .clip(CircleShape)
                    .background(levelColors.first().copy(alpha = 0.25f * glowScale))
            )
        }

        Box(
            modifier = Modifier
                .size(size + ringWidth * 2)
                .clip(CircleShape)
                .background(levelBrush(nivel))
                .padding(ringWidth),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                LightPrimary.copy(alpha = 0.95f),
                                LightPrimary,
                                Color(0xFF061525)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        if (podiumPosition in 1..3) {
            val medalColor = when (podiumPosition) {
                1 -> MedalGold
                2 -> MedalSilver
                else -> MedalBronze
            }
            val medalSize = 20.dp

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 2.dp, y = 2.dp)
                    .size(medalSize)
                    .shadow(2.dp, CircleShape)
                    .clip(CircleShape)
                    .background(medalColor)
                    .border(1.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = podiumPosition.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = when (podiumPosition) {
                        1 -> Color(0xFF5C4200)
                        2 -> Color(0xFF3D3D3D)
                        else -> Color(0xFF4A2C0A)
                    }
                )
            }
        }
    }
}

@Preview(name = "Perfil Light")
@Preview(name = "Perfil Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PremiumAvatarPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PremiumAvatar(
                nome = "Marcos Silva",
                nivel = NivelUsuario.EM_ASCENSAO,
                size = 96.dp,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}

@Preview(name = "Ranking Podium")
@Composable
private fun PremiumAvatarRankingPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PremiumAvatar(
                nome = "Maria Santos",
                nivel = NivelUsuario.VISIONARIO,
                size = 64.dp,
                isInRanking = true,
                podiumPosition = 1,
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}
