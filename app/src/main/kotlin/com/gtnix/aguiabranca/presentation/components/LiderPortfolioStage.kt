package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.util.formatCurrencyCompact
import com.gtnix.aguiabranca.presentation.util.formatPercent

data class LiderPortfolioUi(
    val roi: Double,
    val lucroAnualizado: Double,
    val investimentoTotal: Double,
    val retornoMensal: Double,
    val totalIdeias: Int,
    val ideiasAprovadas: Int,
    val ideiasEmProjeto: Int,
    val projetosAtrasados: Int
)

private enum class LiderExcecao {
    ATRASO,
    CONVERSAO
}

private val CardInset = 20.dp
private val HeaderTouchHeight = 48.dp
private val HeaderTopInset = CardInset - (HeaderTouchHeight - 24.dp) / 2
private val ExcecaoTouchHeight = 48.dp

@Composable
fun LiderPortfolioStage(
    snapshot: LiderPortfolioUi,
    onVerIdeiasClick: () -> Unit,
    onVerProjetosClick: () -> Unit,
    onVerAnaliseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (excecao, excecaoCount) = remember(
        snapshot.projetosAtrasados,
        snapshot.ideiasAprovadas
    ) {
        resolverExcecao(snapshot)
    }
    val gab = LocalInovagabColors.current
    val lucroColor = if (snapshot.lucroAnualizado >= 0) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.error
    }

    InovagabFlatCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = CardInset,
                        end = CardInset,
                        top = HeaderTopInset,
                        bottom = if (excecao != null) 8.dp else CardInset
                    )
            ) {
                LiderStageHeader(onAnaliseClick = onVerAnaliseClick)
                Spacer(modifier = Modifier.height(4.dp))
                LiderRoiHero(roi = snapshot.roi)
                Spacer(modifier = Modifier.height(12.dp))
                LiderResultadoLinha(
                    label = stringResource(R.string.home_lider_investido),
                    value = formatCurrencyCompact(snapshot.investimentoTotal)
                )
                LiderResultadoLinha(
                    label = stringResource(R.string.home_lider_retorno_mes),
                    value = formatCurrencyCompact(snapshot.retornoMensal)
                )
                LiderResultadoLinha(
                    label = stringResource(R.string.home_lider_lucro_aa),
                    value = formatCurrencyCompact(snapshot.lucroAnualizado),
                    valueColor = lucroColor,
                    last = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(
                        R.string.home_lider_funil_inline,
                        snapshot.totalIdeias,
                        snapshot.ideiasEmProjeto
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onVerIdeiasClick)
                )
            }
            if (excecao != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = CardInset),
                    color = gab.cardBorder
                )
                LiderExcecaoLinha(
                    excecao = excecao,
                    count = excecaoCount,
                    onProjetosClick = onVerProjetosClick,
                    onIdeiasClick = onVerIdeiasClick,
                    modifier = Modifier.padding(horizontal = CardInset)
                )
            }
        }
    }
}

@Composable
private fun LiderStageHeader(onAnaliseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HeaderTouchHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.home_lider_lente_portfolio),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier
                .weight(1f)
                .semantics { heading() }
        )
        SectionTextAction(
            label = stringResource(R.string.home_lider_cta_analise),
            onClick = onAnaliseClick
        )
    }
}

@Composable
private fun LiderRoiHero(roi: Double) {
    val roiColor = if (roi >= 0) SuccessGreen else MaterialTheme.colorScheme.error
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = formatPercent(roi),
            style = MaterialTheme.typography.displayMedium.copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            lineHeight = 40.sp,
            letterSpacing = (-1).sp,
            color = roiColor,
            maxLines = 1
        )
        Text(
            text = stringResource(R.string.home_lider_roi_aa),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun LiderResultadoLinha(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    last: Boolean = false
) {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = if (last) 0.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            maxLines = 1
        )
    }
}

@Composable
private fun LiderExcecaoLinha(
    excecao: LiderExcecao,
    count: Int,
    onProjetosClick: () -> Unit,
    onIdeiasClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (texto, onClick, urgente) = when (excecao) {
        LiderExcecao.ATRASO -> Triple(
            if (count == 1) {
                stringResource(R.string.home_lider_acao_atrasados_single)
            } else {
                stringResource(R.string.home_lider_acao_atrasados_plural, count)
            },
            onProjetosClick,
            true
        )
        LiderExcecao.CONVERSAO -> Triple(
            if (count == 1) {
                stringResource(R.string.home_lider_acao_conversao_single)
            } else {
                stringResource(R.string.home_lider_acao_conversao_plural, count)
            },
            onIdeiasClick,
            false
        )
    }
    val dotColor = if (urgente) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) { role = Role.Button }
            .defaultMinSize(minHeight = ExcecaoTouchHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (urgente) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun resolverExcecao(snapshot: LiderPortfolioUi): Pair<LiderExcecao?, Int> {
    return when {
        snapshot.projetosAtrasados > 0 -> LiderExcecao.ATRASO to snapshot.projetosAtrasados
        snapshot.ideiasAprovadas > 0 -> LiderExcecao.CONVERSAO to snapshot.ideiasAprovadas
        else -> null to 0
    }
}

@Preview(name = "Sem exceção", showBackground = true)
@Preview(name = "Sem exceção dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LiderPortfolioStageFilaPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LiderPortfolioStage(
                snapshot = previewSnapshot(aprovadas = 0, emProjeto = 3),
                onVerIdeiasClick = {},
                onVerProjetosClick = {},
                onVerAnaliseClick = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview(name = "Conversão pendente", showBackground = true)
@Composable
private fun LiderPortfolioStageConversaoPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LiderPortfolioStage(
                snapshot = previewSnapshot(aprovadas = 4, emProjeto = 2),
                onVerIdeiasClick = {},
                onVerProjetosClick = {},
                onVerAnaliseClick = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview(name = "Projetos atrasados", showBackground = true)
@Composable
private fun LiderPortfolioStageAtrasoPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LiderPortfolioStage(
                snapshot = previewSnapshot(aprovadas = 4, emProjeto = 3, atrasados = 2),
                onVerIdeiasClick = {},
                onVerProjetosClick = {},
                onVerAnaliseClick = {},
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

private fun previewSnapshot(
    aprovadas: Int = 0,
    emProjeto: Int = 3,
    atrasados: Int = 0
) = LiderPortfolioUi(
    roi = 25.6,
    lucroAnualizado = 82_000.0,
    investimentoTotal = 320_000.0,
    retornoMensal = 33_500.0,
    totalIdeias = 8,
    ideiasAprovadas = aprovadas,
    ideiasEmProjeto = emProjeto,
    projetosAtrasados = atrasados
)
