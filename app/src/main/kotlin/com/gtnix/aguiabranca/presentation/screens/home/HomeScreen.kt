package com.gtnix.aguiabranca.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaTheme
import com.gtnix.aguiabranca.presentation.components.FunilInovacao
import com.gtnix.aguiabranca.presentation.util.formatCurrency
import com.gtnix.aguiabranca.presentation.util.formatPercent

@OptIn(ExperimentalMaterial3Api::class)
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
        onNavigateToPerfil = onNavigateToPerfil,
        onNavigateToNovaIdeia = onNavigateToNovaIdeia,
        onNavigateToRadar = onNavigateToRadar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToNovaIdeia: () -> Unit,
    onNavigateToRadar: () -> Unit
) {
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    val navItems = remember(uiState.perfil) {
        buildList {
            add(BottomNavItemData("Home", Icons.Filled.Home, Icons.Outlined.Home))
            add(BottomNavItemData("Ideias", Icons.Filled.Lightbulb, Icons.Outlined.Lightbulb))
            if (uiState.perfil != PerfilUsuario.OPERADOR) {
                add(BottomNavItemData("Projetos", Icons.Filled.Folder, Icons.Outlined.Folder))
            }
            add(BottomNavItemData("Perfil", Icons.Filled.Person, Icons.Outlined.Person))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.home_welcome, uiState.nomeUsuario),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Bem-vindo à Plataforma de Inovação",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = stringResource(R.string.cd_notifications)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },

        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selectedNavIndex == index)
                                    item.selectedIcon
                                else
                                    item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = selectedNavIndex == index,
                        onClick = {
                            selectedNavIndex = index
                            when (item.label) {
                                "Home" -> { }
                                "Ideias" -> onNavigateToIdeias()
                                "Projetos" -> onNavigateToProjetos()
                                "Perfil" -> onNavigateToPerfil()
                            }
                        }
                    )
                }
            }
        },

        floatingActionButton = {
            if (uiState.perfil != PerfilUsuario.LIDER) {
                FloatingActionButton(
                    onClick = onNavigateToNovaIdeia,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.ideias_nova)
                    )
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
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

                // Dashboard Executivo for LIDER
                if (uiState.perfil == PerfilUsuario.LIDER) {
                    item {
                        SectionHeader(title = stringResource(R.string.dashboard_titulo))
                    }
                    item {
                        FunilInovacao(
                            totalIdeias = uiState.totalIdeias,
                            ideiasAprovadas = uiState.ideiasAprovadas,
                            projetosAtivos = uiState.ideiasEmProjeto
                        )
                    }
                    item {
                        FinanceiroCard(
                            investimentoTotal = uiState.investimentoTotal,
                            retornoTotal = uiState.retornoTotal
                        )
                    }
                    item {
                        RoiCard(roi = uiState.roiConsolidado)
                    }
                }

                // Quick actions (differentiated by profile)
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

                // "Minhas Ideias" only for non-LIDER
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
                            status = ideia.status.name,
                            area = ideia.area.name
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// Dashboard Executivo Components
// =========================================================================


@Composable
private fun FinanceiroCard(
    investimentoTotal: Double,
    retornoTotal: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                    Text(
                        text = formatCurrency(investimentoTotal),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.dashboard_retorno),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = formatCurrency(retornoTotal),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun RoiCard(roi: Double) {
    val roiColor = if (roi >= 0)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.error

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatPercent(roi),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = roiColor
            )
        }
    }
}

// =========================================================================
// Shared Components
// =========================================================================

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun MetricasSection(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    totalProjetos: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricaCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Lightbulb,
            valor = totalIdeias.toString(),
            label = stringResource(R.string.nav_ideias)
        )
        MetricaCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Star,
            valor = ideiasAprovadas.toString(),
            label = stringResource(R.string.dashboard_aprovadas)
        )
        MetricaCard(
            modifier = Modifier.weight(1f),
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            valor = totalProjetos.toString(),
            label = stringResource(R.string.nav_projetos)
        )
    }
}

@Composable
private fun MetricaCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    valor: String,
    label: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
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
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
    status: String,
    area: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
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
                contentDescription = null,
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
            StatusChip(status = status)
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (color, text) = when (status) {
        "PENDENTE" -> MaterialTheme.colorScheme.outline to stringResource(R.string.status_pendente)
        "EM_ANALISE" -> MaterialTheme.colorScheme.tertiary to stringResource(R.string.status_em_analise)
        "APROVADA" -> MaterialTheme.colorScheme.primary to stringResource(R.string.status_aprovado)
        "REPROVADA" -> MaterialTheme.colorScheme.error to stringResource(R.string.status_reprovado)
        else -> MaterialTheme.colorScheme.outline to status
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private data class BottomNavItemData(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

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
            onNavigateToPerfil = {},
            onNavigateToNovaIdeia = {},
            onNavigateToRadar = {}
        )
    }
}
