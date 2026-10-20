package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AprovacaoPorFaixa
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.FaixaIa
import com.gtnix.aguiabranca.domain.model.GrupoScoreIa
import com.gtnix.aguiabranca.domain.model.MetricasIa
import com.gtnix.aguiabranca.presentation.theme.AISpark
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.util.formatPercent
import com.gtnix.aguiabranca.presentation.util.labelRes
import com.gtnix.aguiabranca.presentation.util.orientacaoDivisaoLabel

/**
 * Governança da IA para o líder: concordância dos gestores, uso de fallback e
 * média de score por área/divisão para revelar se a rubrica favorece algum grupo.
 */
@Composable
fun IaGovernancaCard(
    metricas: MetricasIa,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .border(1.dp, AISpark.copy(alpha = 0.18f), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = AISpark,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.ia_governanca_titulo),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )
        }

        if (metricas.ideiasAnalisadas == 0) {
            Text(
                text = stringResource(R.string.ia_governanca_vazio),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (metricas.decisoesComOpiniao > 0) {
                Indicador(
                    rotulo = stringResource(R.string.ia_governanca_concordancia),
                    valor = metricas.taxaConcordancia?.let(::formatPercent) ?: "—",
                    detalhe = stringResource(
                        R.string.ia_governanca_concordancia_detalhe,
                        metricas.concordancias,
                        metricas.decisoesComOpiniao
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
            if (metricas.execucoes > 0) {
                Indicador(
                    rotulo = stringResource(R.string.ia_governanca_fallback),
                    valor = metricas.percentualFallback?.let(::formatPercent) ?: "—",
                    detalhe = stringResource(R.string.ia_governanca_execucoes, metricas.execucoes),
                    modifier = Modifier.weight(1f)
                )
            }
            Indicador(
                rotulo = stringResource(R.string.ia_governanca_triagem),
                valor = metricas.tempoMedioTriagemHoras
                    ?.let { stringResource(R.string.ia_governanca_horas, it) } ?: "—",
                detalhe = stringResource(R.string.ia_governanca_analisadas, metricas.ideiasAnalisadas),
                modifier = Modifier.weight(1f)
            )
        }

        if (metricas.aprovacaoPorFaixa.any { it.avaliadas > 0 }) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            Secao(stringResource(R.string.ia_governanca_aprovacao_faixa))
            metricas.aprovacaoPorFaixa.filter { it.avaliadas > 0 }.forEach { faixa ->
                LinhaGrupo(
                    nome = faixaLabel(faixa),
                    valor = stringResource(
                        R.string.ia_governanca_aprovacao_valor,
                        faixa.taxaAprovacao?.let(::formatPercent) ?: "—",
                        faixa.avaliadas
                    )
                )
            }
        }

        if (metricas.scorePorArea.isNotEmpty()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            Secao(stringResource(R.string.ia_governanca_score_area))
            metricas.scorePorArea.forEach { grupo ->
                LinhaGrupo(nome = areaLabel(grupo.grupo), valor = mediaLabel(grupo))
            }
        }

        if (metricas.scorePorDivisao.size > 1) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            Secao(stringResource(R.string.ia_governanca_score_divisao))
            metricas.scorePorDivisao.forEach { grupo ->
                LinhaGrupo(nome = divisaoLabel(grupo.grupo), valor = mediaLabel(grupo))
            }
        }

        Text(
            text = stringResource(R.string.ia_governanca_rodape),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Indicador(rotulo: String, valor: String, detalhe: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = rotulo,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = detalhe,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun Secao(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun LinhaGrupo(nome: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = nome,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall.copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun mediaLabel(grupo: GrupoScoreIa): String =
    stringResource(R.string.ia_governanca_media_valor, grupo.mediaScore)

@Composable
private fun faixaLabel(faixa: AprovacaoPorFaixa): String =
    runCatching { FaixaIa.valueOf(faixa.faixa) }.getOrNull()
        ?.let { stringResource(it.labelRes()) }
        ?: faixa.faixa

@Composable
private fun areaLabel(grupo: String): String =
    runCatching { AreaAtuacao.valueOf(grupo) }.getOrNull()
        ?.let { stringResource(it.labelRes()) }
        ?: stringResource(R.string.ia_governanca_sem_area)

private fun divisaoLabel(grupo: String): String =
    orientacaoDivisaoLabel(runCatching { DivisaoNegocio.valueOf(grupo) }.getOrNull())

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun IaGovernancaCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            IaGovernancaCard(
                metricas = MetricasIa(
                    ideiasAnalisadas = 18,
                    decisoesComOpiniao = 12,
                    concordancias = 9,
                    taxaConcordancia = 75.0,
                    execucoes = 22,
                    percentualFallback = 9.1,
                    tempoMedioTriagemHoras = 31.5,
                    scorePorArea = listOf(
                        GrupoScoreIa("OPERACOES", 8, 64.2),
                        GrupoScoreIa("LOGISTICA", 6, 58.0),
                        GrupoScoreIa("RH", 4, 41.5)
                    ),
                    scorePorDivisao = emptyList(),
                    aprovacaoPorFaixa = listOf(
                        AprovacaoPorFaixa("ALTA", 5, 4, 80.0),
                        AprovacaoPorFaixa("MEDIA", 4, 2, 50.0),
                        AprovacaoPorFaixa("BAIXA", 3, 1, 33.3)
                    )
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
