package com.gtnix.aguiabranca.presentation.components

import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.DashboardInsight
import com.gtnix.aguiabranca.presentation.theme.AISpark
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

/**
 * Card de insight de IA para o Resumo Executivo.
 *
 * - **Compacto**: origem + título + resumo; o card inteiro alterna a expansão.
 * - **Expandido**: evidências numéricas + ação recomendada.
 * - O estado de loading exibe um skeleton.
 */
@Composable
fun ExecutiveAiInsightCard(
    insight: DashboardInsight,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = LocalDarkTheme.current
    var expanded by remember { mutableStateOf(false) }
    val canExpand = !isLoading && insight.titulo.isNotBlank() &&
        (insight.evidencias.isNotEmpty() || insight.acaoRecomendada.isNotBlank())

    val shape = RoundedCornerShape(16.dp)
    val surface = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surface
    }
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            AISpark.copy(alpha = if (isDarkTheme) 0.08f else 0.05f).compositeOver(surface),
            surface
        )
    )
    val borderColor = AISpark.copy(alpha = if (isDarkTheme) 0.28f else 0.18f)
    val toggleCd = stringResource(
        if (expanded) R.string.leader_insight_recolher else R.string.leader_insight_expandir
    )

    Column(
        modifier = modifier
            .clip(shape)
            .background(backgroundBrush)
            .border(1.dp, borderColor, shape)
            .then(
                if (canExpand) {
                    Modifier
                        .clickable { expanded = !expanded }
                        .semantics { contentDescription = toggleCd }
                } else {
                    Modifier
                }
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLoading) {
                SkeletonText(modifier = Modifier.fillMaxWidth(0.35f), lineHeight = 12.dp)
            } else {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = AISpark,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(
                        if (insight.isFallback) {
                            R.string.leader_insight_badge_automatico
                        } else {
                            R.string.leader_insight_badge_gemini
                        }
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                if (canExpand) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading || insight.titulo.isBlank()) {
            SkeletonText(modifier = Modifier.fillMaxWidth(0.75f), lineHeight = 16.dp)
            Spacer(modifier = Modifier.height(10.dp))
            SkeletonText(lines = 2, lineHeight = 14.dp)
        } else {
            Text(
                text = insight.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics { heading() }
            )
            if (insight.resumo.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = insight.resumo,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 19.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AnimatedVisibility(
            visible = expanded && canExpand,
            enter = fadeIn(tween(200)) + expandVertically(tween(200)),
            exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
        ) {
            Column {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                if (insight.evidencias.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    InsightSubtitulo(stringResource(R.string.leader_insight_evidencias))
                    Spacer(modifier = Modifier.height(6.dp))
                    insight.evidencias.forEach { evidencia ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 7.dp)
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = evidencia,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                if (insight.acaoRecomendada.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    InsightSubtitulo(stringResource(R.string.leader_insight_acao))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = insight.acaoRecomendada,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 19.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightSubtitulo(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Preview(name = "Light Mode — compacto")
@Preview(name = "Dark Mode — compacto", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExecutiveAiInsightCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExecutiveAiInsightCard(
                insight = DashboardInsight(
                    titulo = "ROI sólido com gargalo na conversão",
                    resumo = "O portfólio conta com 7 ideias ativas e ROI de 28% a.a. Há 2 ideias aprovadas aguardando conversão em projeto.",
                    evidencias = listOf("ROI de 28% a.a.", "2 ideias aprovadas sem projeto", "3 projetos no prazo"),
                    acaoRecomendada = "Converter as 2 ideias aprovadas em projetos ainda este trimestre.",
                    modelo = "gemini-3.6-flash",
                    isFallback = false,
                    geradoEm = "2026-09-21T21:59:47Z"
                ),
                isLoading = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Preview(name = "Loading")
@Composable
private fun ExecutiveAiInsightCardLoadingPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExecutiveAiInsightCard(
                insight = DashboardInsight.vazio,
                isLoading = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Preview(name = "Fallback")
@Composable
private fun ExecutiveAiInsightCardFallbackPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExecutiveAiInsightCard(
                insight = DashboardInsight(
                    titulo = "Resumo automático do portfólio",
                    resumo = "O portfólio conta com 7 ideias ativas e ROI de 28% a.a. Acompanhe a conversão de ideias aprovadas.",
                    evidencias = listOf("ROI de 28% a.a.", "7 ideias ativas"),
                    acaoRecomendada = "Converter as ideias aprovadas em projetos.",
                    modelo = "heuristic-local",
                    isFallback = true,
                    geradoEm = null
                ),
                isLoading = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
