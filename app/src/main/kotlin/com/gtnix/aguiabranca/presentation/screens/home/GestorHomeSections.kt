package com.gtnix.aguiabranca.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.presentation.components.InovagabFlatCard
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.theme.WarningAmber

/** Prazo de avaliação que rende XP cheio ao Gestor (regra do backend). */
internal const val GESTOR_SLA_DIAS = 7

private const val MILLIS_POR_DIA = 24L * 60 * 60 * 1000

private fun diasDesde(timestamp: Long): Int =
    ((System.currentTimeMillis() - timestamp) / MILLIS_POR_DIA).toInt().coerceAtLeast(0)

@Composable
internal fun FilaAvaliacaoCard(
    pendentes: Int,
    fila: List<Ideia>,
    diasMaisAntiga: Int?,
    onIdeiaClick: (String) -> Unit,
    onAvaliarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gab = LocalInovagabColors.current

    InovagabFlatCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        if (pendentes == 0) {
            FilaVazia()
            return@InovagabFlatCard
        }

        val foraDoPrazo = (diasMaisAntiga ?: 0) > GESTOR_SLA_DIAS
        val isDark = LocalDarkTheme.current
        val corSelo = if (foraDoPrazo) WarningAmber else MaterialTheme.colorScheme.primary
        val corNumero = if (!foraDoPrazo && isDark) MaterialTheme.colorScheme.onSurface else corSelo
        val alphaSelo = when {
            foraDoPrazo -> 0.14f
            isDark -> 0.28f
            else -> 0.12f
        }
        val descricao = "$pendentes " + pluralStringResource(R.plurals.home_gestor_fila_aguardando, pendentes)

        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .semantics(mergeDescendants = true) { contentDescription = descricao },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(corSelo.copy(alpha = alphaSelo)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = pendentes.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = corNumero
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.home_gestor_fila_titulo),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        diasMaisAntiga?.let { dias -> SlaLinha(dias = dias) }
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAvaliarClick,
                    shape = CircleShape,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = stringResource(R.string.home_gestor_avaliar),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            fila.take(2).forEach { ideia ->
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = gab.cardBorder
                )
                FilaLinha(ideia = ideia, onClick = { onIdeiaClick(ideia.id) })
            }
        }
    }
}

@Composable
private fun SlaLinha(dias: Int) {
    val foraDoPrazo = dias > GESTOR_SLA_DIAS
    val cor = if (foraDoPrazo) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
    val idade = if (dias == 0) {
        stringResource(R.string.home_gestor_fila_mais_antiga_hoje)
    } else {
        pluralStringResource(R.plurals.home_gestor_fila_mais_antiga, dias, dias)
    }
    val prazo = stringResource(
        if (foraDoPrazo) R.string.home_gestor_fila_fora_prazo else R.string.home_gestor_fila_no_prazo
    )
    Text(
        text = "$idade · $prazo",
        style = MaterialTheme.typography.bodySmall,
        fontWeight = if (foraDoPrazo) FontWeight.SemiBold else FontWeight.Normal,
        color = cor,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun FilaLinha(ideia: Ideia, onClick: () -> Unit) {
    val dias = diasDesde(ideia.dataCriacao)
    val foraDoPrazo = dias > GESTOR_SLA_DIAS
    val idade = if (dias == 0) {
        stringResource(R.string.home_gestor_fila_idade_hoje)
    } else {
        stringResource(R.string.home_gestor_fila_idade, dias)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = ideia.titulo,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = idade,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (foraDoPrazo) FontWeight.SemiBold else FontWeight.Normal,
            color = if (foraDoPrazo) WarningAmber else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun FilaVazia() {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SuccessGreen.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = stringResource(R.string.home_gestor_fila_vazia),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.home_gestor_fila_vazia_sub),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun ProximosPassosSection(
    aprovadasSemProjeto: List<Ideia>,
    projetosAtrasados: Int,
    onCriarProjeto: (Ideia) -> Unit,
    onAbrirProjetos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gab = LocalInovagabColors.current

    Column(modifier = modifier) {
        SectionHeader(title = stringResource(R.string.home_gestor_proximos_passos))
        InovagabFlatCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(0.dp)
        ) {
            Column {
                aprovadasSemProjeto.firstOrNull()?.let { maisAntiga ->
                    PassoLinha(
                        icon = Icons.Default.RocketLaunch,
                        cor = SuccessGreen,
                        titulo = pluralStringResource(
                            R.plurals.home_gestor_aprovadas_sem_projeto,
                            aprovadasSemProjeto.size,
                            aprovadasSemProjeto.size
                        ),
                        acao = stringResource(R.string.home_gestor_criar_projeto),
                        detalhe = maisAntiga.titulo.takeIf { aprovadasSemProjeto.size == 1 },
                        onClick = { onCriarProjeto(maisAntiga) }
                    )
                }
                if (aprovadasSemProjeto.isNotEmpty() && projetosAtrasados > 0) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = gab.cardBorder
                    )
                }
                if (projetosAtrasados > 0) {
                    PassoLinha(
                        icon = Icons.Default.Update,
                        cor = WarningAmber,
                        titulo = pluralStringResource(
                            R.plurals.home_gestor_projetos_atrasados,
                            projetosAtrasados,
                            projetosAtrasados
                        ),
                        acao = stringResource(R.string.home_gestor_atualizar_progresso),
                        detalhe = null,
                        onClick = onAbrirProjetos
                    )
                }
            }
        }
    }
}

@Composable
private fun PassoLinha(
    icon: ImageVector,
    cor: Color,
    titulo: String,
    acao: String,
    detalhe: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(cor.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = cor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = detalhe?.let { "$acao · $it" } ?: acao,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
internal fun PipelineSection(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    ideiasEmProjeto: Int,
    taxaConversao: Int,
    onAbrirProjetos: () -> Unit,
    modifier: Modifier = Modifier
) {
    InovagabFlatCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onAbrirProjetos,
        contentPadding = PaddingValues(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_gestor_pipeline_titulo),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (totalIdeias > 0) {
                        val taxa = "$taxaConversao%"
                        val frase = stringResource(R.string.home_gestor_pipeline_taxa, taxa)
                        val inicio = frase.indexOf(taxa)
                        Text(
                            text = buildAnnotatedString {
                                append(frase)
                                if (inicio >= 0) {
                                    addStyle(
                                        SpanStyle(color = SuccessGreen, fontWeight = FontWeight.SemiBold),
                                        inicio,
                                        inicio + taxa.length
                                    )
                                }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                PipelineEtapa(stringResource(R.string.home_lider_funil_ideias), totalIdeias, Modifier.weight(1f))
                PipelineSeta()
                PipelineEtapa(stringResource(R.string.home_lider_funil_aprovadas), ideiasAprovadas, Modifier.weight(1f))
                PipelineSeta()
                PipelineEtapa(stringResource(R.string.home_lider_funil_em_projeto), ideiasEmProjeto, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PipelineSeta() {
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = Modifier
            .padding(top = 6.dp)
            .size(16.dp)
    )
}

@Composable
private fun PipelineEtapa(label: String, valor: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = valor.toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
