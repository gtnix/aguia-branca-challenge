package com.gtnix.aguiabranca.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.components.AguiaBottomNav
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.AnimatedCurrencyCounter
import com.gtnix.aguiabranca.presentation.components.AnimatedMetricCard
import com.gtnix.aguiabranca.presentation.components.IdeiaStatusBadge
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.components.ShimmerListPlaceholder
import com.gtnix.aguiabranca.presentation.components.charts.DonutChart
import com.gtnix.aguiabranca.presentation.components.charts.FunnelChart
import com.gtnix.aguiabranca.presentation.components.charts.FunnelStep
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaBlue
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaGreen
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaOrange
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaTheme
import com.gtnix.aguiabranca.presentation.util.bounceClick
import com.gtnix.aguiabranca.presentation.util.formatCurrency
import com.gtnix.aguiabranca.presentation.util.formatPercent

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    perfil: String,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToNovaIdeia: () -> Unit,
    onNavigateToRadar: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(perfil) {
        viewModel.carregarDados(perfil)
    }

    HomeScreenContent(
        uiState = uiState,
        onNavigateToIdeias = onNavigateToIdeias,
        onNavigateToProjetos = onNavigateToProjetos,
        onNavigateToOrientacoes = onNavigateToOrientacoes,
        onNavigateToPerfil = onNavigateToPerfil,
        onNavigateToNovaIdeia = onNavigateToNovaIdeia,
        onNavigateToRadar = onNavigateToRadar
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToNovaIdeia: () -> Unit,
    onNavigateToRadar: () -> Unit
) {
    Scaffold(
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.home_welcome, uiState.nomeUsuario),
                subtitle = "Bem-vindo à Plataforma de Inovação",
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = stringResource(R.string.cd_notifications)
                        )
                    }
                }
            )
        },

        bottomBar = {
            AguiaBottomNav(
                currentRoute = "home",
                userProfile = uiState.perfil,
                onNavigate = { route ->
                    when (route) {
                        "ideias" -> onNavigateToIdeias()
                        "projetos" -> onNavigateToProjetos()
                        "perfil" -> onNavigateToPerfil()
                    }
                }
            )
        },

        floatingActionButton = {
            if (uiState.perfil != PerfilUsuario.LIDER) {
                FloatingActionButton(
                    onClick = onNavigateToNovaIdeia,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_idea)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            ShimmerListPlaceholder(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MetricasSection(
                        totalIdeias = uiState.totalIdeias,
                        ideiasAprovadas = uiState.ideiasAprovadas,
                        totalProjetos = uiState.totalProjetos
                    )
                }

                if (uiState.orientacoes.isNotEmpty()) {
                    item {
                        SectionHeader(title = stringResource(R.string.home_section_orientacoes))
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = uiState.orientacoes,
                                key = { it.id }
                            ) { orientacao ->
                                OrientacaoCard(
                                    titulo = orientacao.titulo,
                                    categoria = orientacao.categoria.name
                                )
                            }
                        }
                    }
                }

                if (uiState.perfil == PerfilUsuario.LIDER) {
                    item {
                        SectionHeader(title = stringResource(R.string.dashboard_titulo))
                    }
                    item {
                        DashboardExecutivo(
                            totalIdeias = uiState.totalIdeias,
                            ideiasAprovadas = uiState.ideiasAprovadas,
                            projetosAtivos = uiState.ideiasEmProjeto,
                            investimentoTotal = uiState.investimentoTotal,
                            retornoTotal = uiState.retornoTotal,
                            roi = uiState.roiConsolidado
                        )
                    }
                    item {
                        ActionCard(
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Settings,
                            title = "Gerenciar Estratégia",
                            onClick = onNavigateToOrientacoes
                        )
                    }
                }

                item {
                    SectionHeader(title = "Ações Rápidas")
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.perfil != PerfilUsuario.LIDER) {
                            ActionCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Lightbulb,
                                title = stringResource(R.string.ideias_nova),
                                onClick = onNavigateToNovaIdeia
                            )
                        }
                        if (uiState.perfil != PerfilUsuario.OPERADOR) {
                            ActionCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Folder,
                                title = stringResource(R.string.nav_projetos),
                                onClick = onNavigateToProjetos
                            )
                        }
                        if (uiState.perfil == PerfilUsuario.OPERADOR) {
                            ActionCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Lightbulb,
                                title = stringResource(R.string.nav_ideias),
                                onClick = onNavigateToIdeias
                            )
                        }
                    }
                }

                if (uiState.perfil == PerfilUsuario.LIDER || uiState.perfil == PerfilUsuario.GESTOR) {
                    item {
                        ActionCard(
                            modifier = Modifier.fillMaxWidth(),
                            icon = Icons.Default.Radar,
                            title = "Radar de Inovação",
                            onClick = onNavigateToRadar
                        )
                    }
                }

                if (uiState.perfil != PerfilUsuario.LIDER && uiState.minhasIdeias.isNotEmpty()) {
                    item {
                        SectionHeader(title = stringResource(R.string.home_section_minhas_ideias))
                    }
                    items(
                        items = uiState.minhasIdeias,
                        key = { it.id }
                    ) { ideia ->
                        IdeiaResumoCard(
                            titulo = ideia.titulo,
                            status = ideia.status,
                            area = ideia.area.name
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// Dashboard Executivo — Uses DonutChart + FunnelChart components
// =========================================================================

@Composable
private fun DashboardExecutivo(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    projetosAtivos: Int,
    investimentoTotal: Double,
    retornoTotal: Double,
    roi: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_roi_anual),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val roiColor = if (roi >= 0) AguiaBrancaGreen else MaterialTheme.colorScheme.error
                    DonutChart(
                        percentage = (roi.toFloat() / 100f).coerceIn(0f, 1f),
                        centerText = formatPercent(roi),
                        color = roiColor,
                        size = 100.dp,
                        strokeWidth = 10.dp
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Funil de Inovação",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FunnelChart(
                        steps = listOf(
                            FunnelStep("Ideias", totalIdeias, AguiaBrancaBlue),
                            FunnelStep("Aprovadas", ideiasAprovadas, AguiaBrancaGreen),
                            FunnelStep("Projetos", projetosAtivos, AguiaBrancaOrange)
                        )
                    )
                }
            }
        }

        FinanceiroCard(
            investimentoTotal = investimentoTotal,
            retornoTotal = retornoTotal
        )
    }
}

@Composable
private fun FinanceiroCard(
    investimentoTotal: Double,
    retornoTotal: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "Resumo financeiro: investimento ${formatCurrency(investimentoTotal)}, retorno ${formatCurrency(retornoTotal)}"
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.dashboard_financeiro),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.dashboard_investimento),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    AnimatedCurrencyCounter(
                        targetValue = investimentoTotal,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.dashboard_retorno),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    AnimatedCurrencyCounter(
                        targetValue = retornoTotal,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// =========================================================================
// Shared Components
// =========================================================================

@Composable
private fun MetricasSection(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    totalProjetos: Int
) {
    data class MetricItem(
        val icon: androidx.compose.ui.graphics.vector.ImageVector,
        val valor: Int,
        val label: String
    )

    val ideiasLabel = stringResource(R.string.nav_ideias)
    val aprovadasLabel = stringResource(R.string.dashboard_aprovadas)
    val projetosLabel = stringResource(R.string.nav_projetos)

    val metrics = listOf(
        MetricItem(Icons.Default.Lightbulb, totalIdeias, ideiasLabel),
        MetricItem(Icons.Default.Star, ideiasAprovadas, aprovadasLabel),
        MetricItem(Icons.AutoMirrored.Filled.TrendingUp, totalProjetos, projetosLabel)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val isExpanded = maxWidth > 600.dp

        if (isExpanded) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(metrics) { metric ->
                    AnimatedMetricCard(
                        icon = metric.icon,
                        valor = metric.valor,
                        label = metric.label
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                metrics.forEach { metric ->
                    AnimatedMetricCard(
                        modifier = Modifier.weight(1f),
                        icon = metric.icon,
                        valor = metric.valor,
                        label = metric.label
                    )
                }
            }
        }
    }
}

@Composable
private fun OrientacaoCard(
    titulo: String,
    categoria: String
) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = categoria.replace("_", " "),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.bounceClick(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
private fun IdeiaResumoCard(
    titulo: String,
    status: com.gtnix.aguiabranca.domain.model.StatusIdeia,
    area: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Ideia",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = area.replace("_", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            IdeiaStatusBadge(status = status)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    AguiaBrancaTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.GESTOR,
                totalIdeias = 15,
                ideiasAprovadas = 8,
                totalProjetos = 3
            ),
            onNavigateToIdeias = {},
            onNavigateToProjetos = {},
            onNavigateToOrientacoes = {},
            onNavigateToPerfil = {},
            onNavigateToNovaIdeia = {},
            onNavigateToRadar = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenLiderPreview() {
    AguiaBrancaTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.LIDER,
                totalIdeias = 42,
                ideiasAprovadas = 18,
                ideiasEmProjeto = 7,
                totalProjetos = 7,
                investimentoTotal = 350000.0,
                retornoTotal = 48000.0,
                roiConsolidado = 64.6
            ),
            onNavigateToIdeias = {},
            onNavigateToProjetos = {},
            onNavigateToOrientacoes = {},
            onNavigateToPerfil = {},
            onNavigateToNovaIdeia = {},
            onNavigateToRadar = {}
        )
    }
}
