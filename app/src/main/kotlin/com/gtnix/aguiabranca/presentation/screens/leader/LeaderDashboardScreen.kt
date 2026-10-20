package com.gtnix.aguiabranca.presentation.screens.leader

import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.DashboardInsight
import com.gtnix.aguiabranca.domain.model.FunnelStepMetric
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.usecase.dashboard.AreaDesempenhoMetric
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.BarChartData
import com.gtnix.aguiabranca.presentation.components.EmptyState
import com.gtnix.aguiabranca.presentation.components.ExecutiveAiInsightCard
import com.gtnix.aguiabranca.presentation.components.IaGovernancaCard
import com.gtnix.aguiabranca.presentation.components.HorizontalBarChart
import com.gtnix.aguiabranca.presentation.components.SkeletonLoader
import com.gtnix.aguiabranca.presentation.components.SkeletonText
import com.gtnix.aguiabranca.presentation.components.charts.FunnelChartHorizontal
import com.gtnix.aguiabranca.presentation.components.charts.FunnelStep
import com.gtnix.aguiabranca.presentation.theme.AISpark
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.util.formatPercent
import kotlinx.coroutines.delay
import com.gtnix.aguiabranca.presentation.components.InovagabFlatCard
import com.gtnix.aguiabranca.presentation.components.SectionTextAction
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.util.formatCurrencyCompact
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// Public entry point
// ---------------------------------------------------------------------------

@Composable
fun LeaderDashboardScreen(
    viewModel: LeaderDashboardViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val orientacoes by viewModel.orientacoes.collectAsStateWithLifecycle()
    val selectedOrientacaoId by viewModel.selectedOrientacaoId.collectAsStateWithLifecycle()

    LeaderDashboardContent(
        uiState = uiState,
        orientacoes = orientacoes,
        selectedOrientacaoId = selectedOrientacaoId.takeIf { it.isNotBlank() },
        onFilterSelect = viewModel::selectOrientacao,
        onRefresh = viewModel::refresh,
        onNavigateToHome = onNavigateToHome,
        onNavigateToIdeias = onNavigateToIdeias,
        onNavigateToProjetos = onNavigateToProjetos
    )
}

// ---------------------------------------------------------------------------
// Content
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderDashboardContent(
    uiState: LeaderDashboardUiState,
    orientacoes: List<OrientacaoEstrategica>,
    selectedOrientacaoId: String?,
    onFilterSelect: (String?) -> Unit,
    onRefresh: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit
) {
    val pullRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            uiState.isLoading && !uiState.isRefreshing -> LeaderDashboardSkeleton(
                onNavigateToHome = onNavigateToHome
            )

            uiState.errorMessageRes != null && !uiState.isRefreshing -> LeaderDashboardError(
                messageRes = uiState.errorMessageRes,
                onNavigateToHome = onNavigateToHome,
                onRetry = onRefresh
            )

            else -> LeaderDashboardBody(
                uiState = uiState,
                orientacoes = orientacoes,
                selectedOrientacaoId = selectedOrientacaoId,
                onFilterSelect = onFilterSelect,
                onNavigateToHome = onNavigateToHome,
                onNavigateToIdeias = onNavigateToIdeias,
                onNavigateToProjetos = onNavigateToProjetos
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Main body
// ---------------------------------------------------------------------------

@Composable
private fun LeaderDashboardBody(
    uiState: LeaderDashboardUiState,
    orientacoes: List<OrientacaoEstrategica>,
    selectedOrientacaoId: String?,
    onFilterSelect: (String?) -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit
) {
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    val navigationBarBottom = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()
    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = ScreenPadding.Horizontal)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp + navigationBarBottom)
    ) {
        item {
            AguiaTopBar(
                title = stringResource(R.string.leader_resumo_executivo),
                subtitle = stringResource(R.string.leader_resumo_subtitulo),
                onBackClick = onNavigateToHome
            )
        }

        if (orientacoes.isNotEmpty()) {
            item {
                AnimatedSection(visible = contentVisible, delayMillis = 60) {
                    StrategyFilterRow(
                        orientacoes = orientacoes,
                        selectedId = selectedOrientacaoId,
                        onSelect = onFilterSelect
                    )
                }
            }
            item { Spacer(modifier = Modifier.height(12.dp)) }
        }

        item {
            AnimatedSection(visible = contentVisible, delayMillis = 80) {
                ExecutiveAiInsightCard(
                    insight = uiState.insight,
                    isLoading = uiState.isInsightLoading,
                    modifier = cardModifier
                )
            }
        }

        item { Spacer(modifier = Modifier.height(SectionGap)) }

        item {
            AnimatedSection(visible = contentVisible, delayMillis = 120) {
                ResultadoSection(
                    roi = uiState.roiMedio,
                    investimentoTotal = uiState.investimentoTotal,
                    retornoMensal = uiState.retornoMensal,
                    lucroAnualizado = uiState.lucroAnualizado,
                    modifier = cardModifier
                )
            }
        }

        item { Spacer(modifier = Modifier.height(SectionGap)) }

        item {
            AnimatedSection(visible = contentVisible, delayMillis = 160) {
                FunnelSection(
                    funil = uiState.funil,
                    totalIdeias = uiState.totalIdeias,
                    ideiasAprovadas = uiState.ideiasAprovadas,
                    ideiasEmProjeto = uiState.ideiasEmProjeto,
                    taxaAprovacaoPercent = uiState.taxaAprovacaoPercent,
                    taxaConversaoPercent = uiState.taxaConversaoPercent,
                    tempoMedioDias = uiState.tempoMedio,
                    onNavigateToIdeias = onNavigateToIdeias,
                    modifier = cardModifier
                )
            }
        }

        item { Spacer(modifier = Modifier.height(SectionGap)) }

        item {
            AnimatedSection(visible = contentVisible, delayMillis = 200) {
                PortfolioExecutionSection(
                    projetosNoPrazo = uiState.projetosNoPrazo,
                    projetosAtrasados = uiState.projetosAtrasados,
                    progressoMedioProjetos = uiState.progressoMedioProjetos,
                    projetosAtivos = uiState.projetosAtivos,
                    onNavigateToProjetos = onNavigateToProjetos,
                    modifier = cardModifier
                )
            }
        }

        uiState.metricasIa?.let { metricas ->
            item { Spacer(modifier = Modifier.height(SectionGap)) }
            item {
                AnimatedSection(visible = contentVisible, delayMillis = 220) {
                    IaGovernancaCard(metricas = metricas, modifier = cardModifier)
                }
            }
        }

        if (uiState.desempenhoAreas.isNotEmpty()) {
            item { Spacer(modifier = Modifier.height(SectionGap)) }
            item {
                AnimatedSection(visible = contentVisible, delayMillis = 240) {
                    AreaParticipationSection(
                        areas = uiState.desempenhoAreas,
                        modifier = cardModifier
                    )
                }
            }
        }
    }
}

private val SectionGap = 16.dp
private val CardInset = 20.dp
private val ActionTouchHeight = 48.dp

// ---------------------------------------------------------------------------
// Shared card chrome
// ---------------------------------------------------------------------------

/**
 * Card de seção do Resumo Executivo. Título (e ação opcional) ficam dentro do card; quando há
 * ação, o topo desconta a folga da linha de 48dp para o título ficar a [CardInset] da borda.
 */
@Composable
private fun LeaderSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val hasAction = actionLabel != null && onActionClick != null
    val headerHeight = if (subtitle != null) 42.dp else 24.dp
    val slack = if (hasAction) ((ActionTouchHeight - headerHeight) / 2).coerceAtLeast(0.dp) else 0.dp
    InovagabFlatCard(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = CardInset,
            end = CardInset,
            top = CardInset - slack,
            bottom = CardInset
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.semantics { heading() }
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
                if (hasAction) {
                    SectionTextAction(label = actionLabel!!, onClick = onActionClick!!)
                }
            }
            Spacer(modifier = Modifier.height(16.dp - slack))
            content()
        }
    }
}

@Composable
private fun Hairline() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
    )
}

@Composable
private fun StatCell(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(fontFeatureSettings = "tnum"),
            fontSize = 20.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.3).sp,
            color = valueColor,
            maxLines = 1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// ---------------------------------------------------------------------------
// Section: Strategy filter
// ---------------------------------------------------------------------------

@Composable
private fun StrategyFilterRow(
    orientacoes: List<OrientacaoEstrategica>,
    selectedId: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = ScreenPadding.Horizontal),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StrategyChip(
            label = stringResource(R.string.leader_filtro_portfolio_geral),
            selected = selectedId == null,
            onClick = { onSelect(null) }
        )
        orientacoes.forEach { orientacao ->
            StrategyChip(
                label = orientacao.titulo,
                selected = selectedId == orientacao.id,
                onClick = { onSelect(orientacao.id) }
            )
        }
    }
}

@Composable
private fun StrategyChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val gab = LocalInovagabColors.current
    val container = when {
        selected -> MaterialTheme.colorScheme.primary
        LocalDarkTheme.current -> MaterialTheme.colorScheme.surfaceContainerHigh
        else -> MaterialTheme.colorScheme.surface
    }
    val content = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = Modifier
            .defaultMinSize(minHeight = 48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { role = Role.Tab },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            letterSpacing = 0.sp,
            color = content,
            maxLines = 1,
            modifier = Modifier
                .clip(CircleShape)
                .background(container)
                .then(
                    if (selected) Modifier else Modifier.border(1.dp, gab.cardBorder, CircleShape)
                )
                .padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

// ---------------------------------------------------------------------------
// Section: Resultado
// ---------------------------------------------------------------------------

@Composable
private fun ResultadoSection(
    roi: Double,
    investimentoTotal: Double,
    retornoMensal: Double,
    lucroAnualizado: Double,
    modifier: Modifier = Modifier
) {
    val roiColor = if (roi >= 0) SuccessGreen else MaterialTheme.colorScheme.error
    val lucroColor = if (lucroAnualizado >= 0) SuccessGreen else MaterialTheme.colorScheme.error
    LeaderSectionCard(
        title = stringResource(R.string.leader_resultado_titulo),
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = formatPercent(roi),
                style = MaterialTheme.typography.displayMedium.copy(fontFeatureSettings = "tnum"),
                fontSize = 36.sp,
                lineHeight = 40.sp,
                fontWeight = FontWeight.Bold,
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
        Spacer(modifier = Modifier.height(12.dp))
        ResultadoLinha(
            label = stringResource(R.string.home_lider_investido),
            value = formatCurrencyCompact(investimentoTotal)
        )
        ResultadoLinha(
            label = stringResource(R.string.leader_retorno_mensal),
            value = formatCurrencyCompact(retornoMensal)
        )
        ResultadoLinha(
            label = stringResource(R.string.home_lider_lucro_aa),
            value = formatCurrencyCompact(lucroAnualizado),
            valueColor = lucroColor,
            last = true
        )
    }
}

@Composable
private fun ResultadoLinha(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    last: Boolean = false
) {
    Hairline()
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

// ---------------------------------------------------------------------------
// Section: Funnel
// ---------------------------------------------------------------------------

@Composable
private fun FunnelSection(
    funil: List<FunnelStepMetric>,
    totalIdeias: Int,
    ideiasAprovadas: Int,
    ideiasEmProjeto: Int,
    taxaAprovacaoPercent: Int,
    taxaConversaoPercent: Int,
    tempoMedioDias: Int,
    onNavigateToIdeias: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        SuccessGreen,
        MaterialTheme.colorScheme.tertiary
    )
    val funnelSteps = funil.ifEmpty {
        listOf(
            FunnelStepMetric("IDEIAS", "Ideias", totalIdeias),
            FunnelStepMetric("APROVADAS", "Aprovadas", ideiasAprovadas),
            FunnelStepMetric("EM_PROJETO", "Em projeto", ideiasEmProjeto)
        )
    }

    LeaderSectionCard(
        title = stringResource(R.string.leader_funil_titulo),
        actionLabel = stringResource(R.string.leader_funil_acao),
        onActionClick = onNavigateToIdeias,
        modifier = modifier
    ) {
        FunnelChartHorizontal(
            steps = funnelSteps.mapIndexed { i, step ->
                FunnelStep(
                    label = funilLabel(step),
                    value = step.quantidade,
                    color = colors[i % colors.size]
                )
            },
            neutralValues = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Hairline()
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCell(
                value = "$taxaAprovacaoPercent%",
                label = stringResource(R.string.leader_aprovacao_curto),
                modifier = Modifier.weight(1f)
            )
            StatCell(
                value = "$taxaConversaoPercent%",
                label = stringResource(R.string.leader_conversao_curto),
                modifier = Modifier.weight(1f)
            )
            StatCell(
                value = stringResource(R.string.leader_dias_valor, tempoMedioDias),
                label = stringResource(R.string.leader_tempo_aprovacao_curto),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun funilLabel(step: FunnelStepMetric): String = when (step.etapa) {
    "IDEIAS" -> stringResource(R.string.home_lider_funil_ideias)
    "APROVADAS" -> stringResource(R.string.leader_funil_na_fila)
    "EM_PROJETO" -> stringResource(R.string.home_lider_funil_em_projeto)
    else -> step.label
}

// ---------------------------------------------------------------------------
// Section: Portfolio execution
// ---------------------------------------------------------------------------

@Composable
private fun PortfolioExecutionSection(
    projetosNoPrazo: Int,
    projetosAtrasados: Int,
    progressoMedioProjetos: Double,
    projetosAtivos: Int,
    onNavigateToProjetos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progresso = progressoMedioProjetos.toInt().coerceIn(0, 100)
    val subtitle = if (projetosAtivos == 1) {
        stringResource(R.string.leader_projetos_ativos_single)
    } else {
        stringResource(R.string.leader_projetos_ativos, projetosAtivos)
    }
    LeaderSectionCard(
        title = stringResource(R.string.leader_execucao_carteira),
        subtitle = subtitle,
        actionLabel = stringResource(R.string.leader_execucao_acao),
        onActionClick = onNavigateToProjetos,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCell(
                value = projetosNoPrazo.toString(),
                label = stringResource(R.string.dashboard_no_prazo),
                modifier = Modifier.weight(1f)
            )
            StatCell(
                value = projetosAtrasados.toString(),
                label = stringResource(R.string.dashboard_atrasados),
                valueColor = if (projetosAtrasados > 0) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.weight(1f)
            )
            StatCell(
                value = "$progresso%",
                label = stringResource(R.string.leader_progresso_curto),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = { progresso / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {}
        )
    }
}

// ---------------------------------------------------------------------------
// Section: Area participation
// ---------------------------------------------------------------------------

@Composable
private fun AreaParticipationSection(
    areas: List<AreaDesempenhoMetric>,
    modifier: Modifier = Modifier
) {
    val barColor = if (LocalDarkTheme.current) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.primary
    }
    LeaderSectionCard(
        title = stringResource(R.string.leader_participacao_area),
        modifier = modifier
    ) {
        HorizontalBarChart(
            data = areas.map { BarChartData(label = it.label, value = it.value, color = barColor) },
            showAxisLabels = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ---------------------------------------------------------------------------
// Skeleton loading state
// ---------------------------------------------------------------------------

@Composable
private fun LeaderDashboardSkeleton(onNavigateToHome: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        AguiaTopBar(
            title = stringResource(R.string.leader_resumo_executivo),
            subtitle = stringResource(R.string.leader_resumo_subtitulo),
            onBackClick = onNavigateToHome
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPadding.Horizontal),
            verticalArrangement = Arrangement.spacedBy(SectionGap)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) {
                    SkeletonLoader(
                        modifier = Modifier
                            .width(112.dp)
                            .height(36.dp)
                            .clip(CircleShape)
                    )
                }
            }
            listOf(108.dp, 196.dp, 220.dp).forEach { height ->
                SkeletonLoader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Error state
// ---------------------------------------------------------------------------

@Composable
private fun LeaderDashboardError(
    messageRes: Int,
    onNavigateToHome: () -> Unit,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        AguiaTopBar(
            title = stringResource(R.string.leader_resumo_executivo),
            subtitle = stringResource(R.string.leader_resumo_subtitulo),
            onBackClick = onNavigateToHome
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            EmptyState(
                icon = Icons.Default.Info,
                title = stringResource(messageRes),
                subtitle = "",
                actionLabel = stringResource(R.string.leader_retry),
                onActionClick = onRetry
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Animated section helper
// ---------------------------------------------------------------------------

@Composable
private fun AnimatedSection(
    visible: Boolean,
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    var sectionVisible by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (visible) {
            delay(delayMillis.toLong())
            sectionVisible = true
        }
    }
    AnimatedVisibility(
        visible = sectionVisible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
            initialOffsetY = { 30 },
            animationSpec = tween(300)
        )
    ) {
        Column { content() }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(name = "Success — Light")
@Preview(name = "Success — Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LeaderDashboardBodyPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LeaderDashboardBody(
                uiState = LeaderDashboardUiState(
                    insight = DashboardInsight(
                        titulo = "ROI sólido com gargalo na conversão",
                        resumo = "O portfólio conta com 7 ideias ativas e ROI de 28% a.a. Há 2 ideias aprovadas aguardando conversão em projeto.",
                        evidencias = listOf("ROI de 28%", "2 ideias sem projeto", "3 no prazo"),
                        acaoRecomendada = "Converter as 2 ideias aprovadas em projetos.",
                        modelo = "gemini-3.6-flash",
                        isFallback = false,
                        geradoEm = "2026-09-21T21:59Z"
                    ),
                    funil = listOf(
                        FunnelStepMetric("IDEIAS", "Ideias", 7),
                        FunnelStepMetric("APROVADAS", "Aprovadas", 2),
                        FunnelStepMetric("EM_PROJETO", "Em Projeto", 2)
                    ),
                    taxaAprovacaoPercent = 28,
                    taxaConversaoPercent = 100,
                    roiMedio = 28.0,
                    lucroAnualizado = 62_000.0,
                    tempoMedio = 4,
                    totalIdeias = 7,
                    ideiasAprovadas = 2,
                    ideiasEmProjeto = 2,
                    projetosAtivos = 3,
                    investimentoTotal = 220_000.0,
                    retornoMensal = 23_500.0,
                    projetosNoPrazo = 3,
                    projetosAtrasados = 0,
                    progressoMedioProjetos = 45.0,
                    desempenhoAreas = listOf(
                        AreaDesempenhoMetric("Operações", 85f, 6),
                        AreaDesempenhoMetric("Logística", 14f, 1)
                    )
                ),
                orientacoes = emptyList(),
                selectedOrientacaoId = null,
                onFilterSelect = {},
                onNavigateToHome = {},
                onNavigateToIdeias = {},
                onNavigateToProjetos = {}
            )
        }
    }
}

@Preview(name = "Skeleton loading")
@Composable
private fun LeaderDashboardSkeletonPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LeaderDashboardSkeleton(onNavigateToHome = {})
        }
    }
}

@Preview(name = "Error state")
@Composable
private fun LeaderDashboardErrorPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LeaderDashboardError(
                messageRes = R.string.error_load_dashboard,
                onNavigateToHome = {},
                onRetry = {}
            )
        }
    }
}

@Preview(name = "Filter applied")
@Composable
private fun LeaderDashboardWithFilterPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LeaderDashboardBody(
                uiState = LeaderDashboardUiState(
                    isInsightLoading = true,
                    insight = DashboardInsight.vazio,
                    funil = listOf(
                        FunnelStepMetric("IDEIAS", "Ideias", 3),
                        FunnelStepMetric("APROVADAS", "Aprovadas", 1),
                        FunnelStepMetric("EM_PROJETO", "Em Projeto", 1)
                    ),
                    roiMedio = 15.0,
                    lucroAnualizado = 20_000.0,
                    tempoMedio = 6,
                    projetosAtrasados = 1
                ),
                orientacoes = listOf(
                    OrientacaoEstrategica(
                        id = "ori-1",
                        titulo = "Experiência digital do passageiro",
                        descricao = "Modernizar embarque, bilhetagem e comunicação.",
                        categoria = com.gtnix.aguiabranca.domain.model.CategoriaOrientacao.EFICIENCIA_OPERACIONAL,
                        criadoPor = "user-lider"
                    )
                ),
                selectedOrientacaoId = "ori-1",
                onFilterSelect = {},
                onNavigateToHome = {},
                onNavigateToIdeias = {},
                onNavigateToProjetos = {}
            )
        }
    }
}
