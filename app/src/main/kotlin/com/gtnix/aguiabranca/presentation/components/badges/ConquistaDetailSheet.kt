package com.gtnix.aguiabranca.presentation.components.badges

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Conquista
import com.gtnix.aguiabranca.domain.model.Estatisticas
import com.gtnix.aguiabranca.presentation.components.GradientProgressBar
import com.gtnix.aguiabranca.presentation.theme.tierGradientColors
import com.gtnix.aguiabranca.presentation.util.color
import com.gtnix.aguiabranca.presentation.util.conquistaPlaceholderDate
import com.gtnix.aguiabranca.presentation.util.descricao
import com.gtnix.aguiabranca.presentation.util.icon
import com.gtnix.aguiabranca.presentation.util.label
import com.gtnix.aguiabranca.presentation.util.motivationalQuote
import com.gtnix.aguiabranca.presentation.util.simulatedUnlockProgress
import com.gtnix.aguiabranca.presentation.util.titulo
import kotlinx.coroutines.launch

private val BadgeDetailSize = 180.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConquistaDetailSheet(
    conquista: Conquista,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    estatisticas: Estatisticas? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val emBreveMessage = stringResource(R.string.conquista_compartilhar_em_breve)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (conquista.desbloqueada) {
                    UnlockedBadgeDetail(conquista = conquista)
                } else {
                    LockedBadgeDetail(conquista = conquista)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "${conquista.tipo.titulo()} — ${conquista.tier.label()}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (conquista.desbloqueada) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (conquista.desbloqueada) {
                    UnlockedDetailContent(conquista = conquista)
                } else {
                    LockedDetailContent(
                        conquista = conquista,
                        estatisticas = estatisticas
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (conquista.desbloqueada) {
                    Button(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(emBreveMessage)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = stringResource(R.string.conquista_compartilhar),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun UnlockedBadgeDetail(conquista: Conquista) {
    val isDarkTheme = isSystemInDarkTheme()
    val typeColor = conquista.tipo.color()
    val ringWidth = 4.dp
    val innerSize = BadgeDetailSize - ringWidth * 2

    val infiniteTransition = rememberInfiniteTransition(label = "glowPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier.size(BadgeDetailSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(BadgeDetailSize)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        tierGradientColors(conquista.tier).map { it.copy(alpha = glowAlpha) }
                    )
                )
                .padding(ringWidth)
                .clip(CircleShape)
                .background(typeColor.copy(alpha = if (isDarkTheme) 0.18f else 0.12f))
                .drawWithContent {
                    drawContent()
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = if (isDarkTheme) 0.3f else 0.1f)
                            ),
                            radius = this.size.minDimension / 2f
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = conquista.tipo.icon(),
                contentDescription = conquista.tipo.titulo(),
                tint = typeColor,
                modifier = Modifier.size(innerSize * 0.44f)
            )
        }
    }
}

@Composable
private fun LockedBadgeDetail(conquista: Conquista) {
    val isDarkTheme = isSystemInDarkTheme()
    val ringWidth = 4.dp
    val mutedRing = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
    val innerBackground = if (isDarkTheme) Color(0xFF1C1C1E) else Color(0xFFE8E8EA)

    Box(
        modifier = Modifier.size(BadgeDetailSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(BadgeDetailSize)
                .clip(CircleShape)
                .background(mutedRing)
                .padding(ringWidth)
                .clip(CircleShape)
                .background(innerBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = conquista.tipo.icon(),
                contentDescription = conquista.tipo.titulo(),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f),
                modifier = Modifier.size(BadgeDetailSize * 0.38f)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = if (isDarkTheme) 0.35f else 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    modifier = Modifier.size(BadgeDetailSize * 0.22f)
                )
            }
        }
    }
}

@Composable
private fun UnlockedDetailContent(conquista: Conquista) {
    Text(
        text = conquista.tipo.descricao(),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(12.dp))

    Text(
        text = stringResource(
            R.string.conquista_desbloqueada_em,
            conquistaPlaceholderDate(conquista.id)
        ),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "\"${conquista.tipo.motivationalQuote(conquista.id)}\"",
        style = MaterialTheme.typography.bodyMedium,
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
    )
}

@Composable
private fun LockedDetailContent(
    conquista: Conquista,
    estatisticas: Estatisticas?
) {
    Text(
        text = stringResource(R.string.conquista_como_desbloquear),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = conquista.tipo.descricao(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    val progress = conquista.tipo.simulatedUnlockProgress(conquista.id, estatisticas)

    GradientProgressBar(
        progress = progress,
        modifier = Modifier.fillMaxWidth(),
        height = 10.dp,
        gradientColors = tierGradientColors(conquista.tier).map {
            it.copy(alpha = 0.5f)
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "${(progress * 100).toInt()}%",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}
