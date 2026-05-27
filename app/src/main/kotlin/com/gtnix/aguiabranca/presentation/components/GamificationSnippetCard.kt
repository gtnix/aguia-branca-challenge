package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.domain.model.Conquista
import com.gtnix.aguiabranca.domain.model.Estatisticas
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.TipoConquista
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.util.nivelIconAndColor

@Composable
fun GamificationSnippetCard(
    pontuacao: Pontuacao,
    posicaoRanking: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val (levelIcon, levelColor) = nivelIconAndColor(pontuacao.nivel)
    val shape = RoundedCornerShape(20.dp)

    val animatedProgress by animateFloatAsState(
        targetValue = pontuacao.progressoProximoNivel,
        animationSpec = tween(durationMillis = 700),
        label = "snippetProgress"
    )

    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shape = shape,
        shadowElevation = if (isDarkTheme) 0.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(levelColor.copy(alpha = if (isDarkTheme) 0.18f else 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = levelIcon,
                        contentDescription = null,
                        tint = levelColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pontuacao.nivel.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${pontuacao.total} pts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (posicaoRanking != null) {
                    RankingChip(posicao = posicaoRanking)
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = levelColor,
                trackColor = levelColor.copy(alpha = if (isDarkTheme) 0.15f else 0.1f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val proximoNivel = NivelUsuario.proximoNivel(pontuacao.nivel)
            Text(
                text = if (proximoNivel != null) {
                    "Faltam ${pontuacao.pontosParaProximoNivel} pts para ${proximoNivel.label}"
                } else {
                    "Nivel maximo atingido"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RankingChip(posicao: Int) {
    val chipColor = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(chipColor.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Leaderboard,
            contentDescription = null,
            tint = chipColor,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "#$posicao",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = chipColor,
            fontSize = 12.sp
        )
    }
}

@Preview(name = "Iniciante", showBackground = true)
@Preview(name = "Iniciante Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GamificationSnippetCardIniciantePreview() {
    InovagabTheme {
        GamificationSnippetCard(
            pontuacao = Pontuacao(
                total = 30,
                nivel = NivelUsuario.INICIANTE,
                progressoProximoNivel = 0.6f,
                pontosParaProximoNivel = 21,
                conquistas = listOf(
                    Conquista("1", TipoConquista.BEM_VINDO, true),
                    Conquista("2", TipoConquista.PRIMEIRA_IDEIA, true)
                ),
                estatisticas = Estatisticas(totalIdeias = 3, ideiasAprovadas = 1, projetosParticipando = 0)
            ),
            posicaoRanking = 12,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Engajado", showBackground = true)
@Composable
private fun GamificationSnippetCardEngajadoPreview() {
    InovagabTheme {
        GamificationSnippetCard(
            pontuacao = Pontuacao(
                total = 95,
                nivel = NivelUsuario.ENGAJADO,
                progressoProximoNivel = 0.45f,
                pontosParaProximoNivel = 56,
                conquistas = emptyList(),
                estatisticas = Estatisticas()
            ),
            posicaoRanking = 3,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Visionario", showBackground = true)
@Composable
private fun GamificationSnippetCardVisionarioPreview() {
    InovagabTheme {
        GamificationSnippetCard(
            pontuacao = Pontuacao(
                total = 280,
                nivel = NivelUsuario.ENGAJADO,
                progressoProximoNivel = 1f,
                pontosParaProximoNivel = 0,
                conquistas = emptyList(),
                estatisticas = Estatisticas()
            ),
            posicaoRanking = 1,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
