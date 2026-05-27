package com.gtnix.aguiabranca.presentation.screens.leader

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.usecase.dashboard.AreaDesempenhoMetric
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.AIInsightCard
import com.gtnix.aguiabranca.presentation.components.BarChartData
import com.gtnix.aguiabranca.presentation.components.FinanceiroMiniCard
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.HorizontalBarChart
import com.gtnix.aguiabranca.presentation.components.TypewriterText
import com.gtnix.aguiabranca.presentation.components.charts.FunnelChartHorizontal
import com.gtnix.aguiabranca.presentation.components.charts.FunnelStep
import com.gtnix.aguiabranca.presentation.theme.AISpark
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.util.formatPercent
import kotlinx.coroutines.delay

/**
 * Dashboard executivo do perfil LÍDER.
 *
 * Tela de detalhe acessada a partir da Home via atalho "Resumo Executivo".
 * Usa [AguiaTopBar] com voltar e não exibe a bottom bar do [MainScreen].
 */
@Composable
fun LeaderDashboardScreen(
    viewModel: LeaderDashboardViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LeaderDashboardContent(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onNavigateToHome = onNavigateToHome,
        onNavigateToIdeias = onNavigateToIdeias,
        onNavigateToProjetos = onNavigateToProjetos,
        onNavigateToPerfil = onNavigateToPerfil
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderDashboardContent(
    uiState: LeaderDashboardUiState,
    onRefresh: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val pullRefreshState = rememberPullToRefreshState()
    
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
        if (uiState.isLoading && !uiState.isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.errorMessageRes != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.error_load_dashboard),
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(uiState.errorMessageRes!!),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        } else {
            LeaderDashboardBody(
                uiState = uiState,
                isDarkTheme = isDarkTheme,
                onNavigateToHome = onNavigateToHome
            )
        }
    }
}

@Composable
private fun LeaderDashboardBody(
    uiState: LeaderDashboardUiState,
    isDarkTheme: Boolean,
    onNavigateToHome: () -> Unit
) {
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        contentVisible = true
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            AguiaTopBar(
                title = stringResource(R.string.leader_resumo_executivo),
                onBackClick = onNavigateToHome
            )
        }

        item {
            AnimatedSection(visible = contentVisible, delayMillis = 100) {
                AIInsightSection(
                    narrative = uiState.aiNarrative,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            AnimatedSection(visible = contentVisible, delayMillis = 150) {
                FunnelSection(
                    totalIdeias = uiState.totalIdeias,
                    ideiasAprovadas = uiState.ideiasAprovadas,
                    ideiasEmProjeto = uiState.ideiasEmProjeto,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            AnimatedSection(visible = contentVisible, delayMillis = 200) {
                FinancialSection(
                    investimentoTotal = uiState.investimentoTotal,
                    retornoTotal = uiState.retornoTotal,
                    roiConsolidado = uiState.roiMedio,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            AnimatedSection(visible = contentVisible, delayMillis = 250) {
                KPICardsSection(
                    roiMedio = uiState.roiMedio,
                    tempoMedio = uiState.tempoMedio,
                    ideiasAprovadas = uiState.ideiasAprovadas,
                    projetosAtivos = uiState.projetosAtivos,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            AnimatedSection(visible = contentVisible, delayMillis = 300) {
                DesempenhoAreaSection(
                    data = uiState.desempenhoAreas.toBarChartData(),
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

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
            initialOffsetY = { 40 },
            animationSpec = tween(300)
        )
    ) {
        content()
    }
}

@Composable
private fun FunnelSection(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    ideiasEmProjeto: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Text(
                text = stringResource(R.string.funil_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(20.dp))

            val ideiasLabel = stringResource(R.string.funil_ideias)
            val aprovadasLabel = stringResource(R.string.funil_aprovadas)
            val projetosLabel = stringResource(R.string.funil_em_projeto)

            FunnelChartHorizontal(
                steps = listOf(
                    FunnelStep(ideiasLabel, totalIdeias, MaterialTheme.colorScheme.primary),
                    FunnelStep(aprovadasLabel, ideiasAprovadas, SuccessGreen),
                    FunnelStep(projetosLabel, ideiasEmProjeto, MaterialTheme.colorScheme.tertiary)
                )
            )
        }
    }
}

@Composable
private fun FinancialSection(
    investimentoTotal: Double,
    retornoTotal: Double,
    roiConsolidado: Double,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.dashboard_financeiro),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FinanceiroMiniCard(
                label = stringResource(R.string.dashboard_investimento),
                value = investimentoTotal,
                isNegative = true,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            FinanceiroMiniCard(
                label = stringResource(R.string.dashboard_retorno),
                value = retornoTotal,
                isNegative = false,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val roiColor = if (roiConsolidado >= 0) SuccessGreen else MaterialTheme.colorScheme.error
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.dashboard_roi),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatPercent(roiConsolidado),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = roiColor
                )
            }
        }
    }
}

@Composable
private fun AIInsightSection(
    narrative: String,
    modifier: Modifier = Modifier
) {
    AIInsightCard(modifier = modifier.fillMaxWidth()) {
        if (narrative.isNotEmpty()) {
            TypewriterText(
                text = narrative,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                charDelay = 20
            )
        } else {
            Text(
                text = "Carregando insights...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DesempenhoAreaSection(
    data: List<BarChartData>,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Text(
                text = stringResource(R.string.leader_desempenho_area),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = stringResource(R.string.leader_desempenho_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            HorizontalBarChart(
                data = data,
                showAxisLabels = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun KPICardsSection(
    roiMedio: Double,
    tempoMedio: Int,
    ideiasAprovadas: Int,
    projetosAtivos: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PremiumKPICard(
                title = stringResource(R.string.leader_roi_medio),
                value = "${roiMedio.toInt()}%",
                accentColor = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.TrendingUp,
                isDarkTheme = isDarkTheme,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            PremiumKPICard(
                title = stringResource(R.string.leader_tempo_medio),
                value = "$tempoMedio",
                subtitle = "dias para aprovação",
                accentColor = MaterialTheme.colorScheme.tertiary,
                icon = Icons.Default.Schedule,
                isDarkTheme = isDarkTheme,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PremiumKPICard(
                title = stringResource(R.string.home_metric_aprovadas),
                value = ideiasAprovadas.toString(),
                accentColor = SuccessGreen,
                icon = Icons.Default.Lightbulb,
                isDarkTheme = isDarkTheme,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            PremiumKPICard(
                title = stringResource(R.string.home_metric_projetos),
                value = projetosAtivos.toString(),
                accentColor = MaterialTheme.colorScheme.secondary,
                icon = Icons.Default.Folder,
                isDarkTheme = isDarkTheme,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun PremiumKPICard(
    title: String,
    value: String,
    subtitle: String = "",
    accentColor: Color,
    icon: ImageVector,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.06f)
    } else {
        Color(0xFFE5E7EB)
    }
    
    val cardModifier = if (isDarkTheme) {
        modifier
            .clip(cardShape)
            .border(1.dp, borderColor, cardShape)
            .background(Color(0xFF1C1C1E))
    } else {
        modifier
            .shadow(
                elevation = 2.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(cardShape)
            .border(1.dp, borderColor, cardShape)
            .background(Color.White)
    }
    
    Box(
        modifier = cardModifier.padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = accentColor.copy(alpha = if (isDarkTheme) 0.15f else 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun List<AreaDesempenhoMetric>.toBarChartData(): List<BarChartData> {
    return map { item ->
        BarChartData(label = item.label, value = item.value, color = AISpark)
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LeaderDashboardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LeaderDashboardBody(
                uiState = LeaderDashboardUiState(
                    aiNarrative = "O portfólio de inovação conta com 24 ideias ativas, com ROI consolidado de 22% a.a.",
                    desempenhoAreas = listOf(
                        AreaDesempenhoMetric("Logística", 40f),
                        AreaDesempenhoMetric("Qualidade", 32f),
                        AreaDesempenhoMetric("RH", 18f),
                        AreaDesempenhoMetric("TI", 12f)
                    ),
                    roiMedio = 22.0,
                    tempoMedio = 12,
                    totalIdeias = 24,
                    ideiasAprovadas = 14,
                    ideiasEmProjeto = 8,
                    projetosAtivos = 6,
                    investimentoTotal = 150_000.0,
                    retornoTotal = 183_000.0
                ),
                isDarkTheme = false,
                onNavigateToHome = {}
            )
        }
    }
}
