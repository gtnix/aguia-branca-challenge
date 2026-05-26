package com.gtnix.aguiabranca.presentation.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.components.AnimatedCurrencyCounter
import com.gtnix.aguiabranca.presentation.components.FloatingNavBar
import com.gtnix.aguiabranca.presentation.components.FloatingNavItem
import com.gtnix.aguiabranca.presentation.components.AddNewIdeiaCard
import com.gtnix.aguiabranca.presentation.components.CompactIdeiaCard
import com.gtnix.aguiabranca.presentation.components.GradientMetricCard
import com.gtnix.aguiabranca.presentation.components.GradientMetricCardDefaults
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.IdeiaStatusBadge
import com.gtnix.aguiabranca.presentation.components.InspirationHeroCard
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.components.SkeletonAvatar
import com.gtnix.aguiabranca.presentation.components.SkeletonCard
import com.gtnix.aguiabranca.presentation.components.SkeletonLoader
import com.gtnix.aguiabranca.presentation.components.SkeletonMetricCard
import com.gtnix.aguiabranca.presentation.components.SkeletonText
import com.gtnix.aguiabranca.presentation.components.charts.DonutChart
import com.gtnix.aguiabranca.presentation.components.charts.FunnelStep
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.NavBarDimensions
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.util.bounceClick
import com.gtnix.aguiabranca.presentation.util.formatPercent
import java.util.Calendar

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
        onRefresh = viewModel::refresh,
        onNavigateToIdeias = onNavigateToIdeias,
        onNavigateToProjetos = onNavigateToProjetos,
        onNavigateToOrientacoes = onNavigateToOrientacoes,
        onNavigateToPerfil = onNavigateToPerfil,
        onNavigateToNovaIdeia = onNavigateToNovaIdeia,
        onNavigateToRadar = onNavigateToRadar
    )
}

@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    perfil: String,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToRadar: () -> Unit,
    onNavigateToLeaderDashboard: () -> Unit = {},
    onNavigateToNovaIdeia: () -> Unit = {},
    onNavigateToIdeias: () -> Unit = {},
    onNavigateToProjetos: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(perfil) {
        viewModel.carregarDados(perfil)
    }

    HomeBodyContent(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onNavigateToOrientacoes = onNavigateToOrientacoes,
        onNavigateToRadar = onNavigateToRadar,
        onNavigateToLeaderDashboard = onNavigateToLeaderDashboard,
        onNavigateToNovaIdeia = onNavigateToNovaIdeia,
        onNavigateToIdeias = onNavigateToIdeias,
        onNavigateToProjetos = onNavigateToProjetos
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToNovaIdeia: () -> Unit,
    onNavigateToRadar: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            val homeLabel = stringResource(R.string.nav_home)
            val ideiasLabel = stringResource(R.string.nav_ideias)
            val projetosLabel = stringResource(R.string.nav_projetos)
            val perfilLabel = stringResource(R.string.nav_perfil)
            
            val navItems = buildList {
                add(FloatingNavItem("home", homeLabel, Icons.Outlined.Home, Icons.Filled.Home))
                add(FloatingNavItem("ideias", ideiasLabel, Icons.Outlined.Lightbulb, Icons.Filled.Lightbulb))
                if (uiState.perfil != PerfilUsuario.OPERADOR) {
                    add(FloatingNavItem("projetos", projetosLabel, Icons.Outlined.Folder, Icons.Filled.Folder))
                }
                add(FloatingNavItem("perfil", perfilLabel, Icons.Outlined.Person, Icons.Filled.Person))
            }
            
            FloatingNavBar(
                items = navItems,
                selectedRoute = "home",
                onItemSelected = { route ->
                    when (route) {
                        "ideias" -> onNavigateToIdeias()
                        "projetos" -> onNavigateToProjetos()
                        "perfil" -> onNavigateToPerfil()
                    }
                },
                centerAction = if (uiState.perfil != PerfilUsuario.LIDER) onNavigateToNovaIdeia else null
            )
        }
    ) { paddingValues ->
        val pullRefreshState = rememberPullToRefreshState()
        
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && !uiState.isRefreshing) {
                HomeSkeletonScreen()
            } else {
                HomePremiumContent(
                    uiState = uiState,
                    onNavigateToOrientacoes = onNavigateToOrientacoes,
                    onNavigateToRadar = onNavigateToRadar,
                    onNavigateToNovaIdeia = onNavigateToNovaIdeia,
                    onNavigateToProjetos = onNavigateToProjetos,
                    onNavigateToIdeias = onNavigateToIdeias,
                    onActivityClick = {}
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeBodyContent(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToRadar: () -> Unit,
    onNavigateToLeaderDashboard: () -> Unit = {},
    onNavigateToNovaIdeia: () -> Unit = {},
    onNavigateToIdeias: () -> Unit = {},
    onNavigateToProjetos: () -> Unit = {}
) {
    val pullRefreshState = rememberPullToRefreshState()
    
    Column(modifier = Modifier.fillMaxSize()) {
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefresh,
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.isLoading && !uiState.isRefreshing) {
                HomeSkeletonScreen()
            } else {
                HomePremiumContent(
                    uiState = uiState,
                    onNavigateToOrientacoes = onNavigateToOrientacoes,
                    onNavigateToRadar = onNavigateToRadar,
                    onNavigateToNovaIdeia = onNavigateToNovaIdeia,
                    onNavigateToProjetos = onNavigateToProjetos,
                    onNavigateToIdeias = onNavigateToIdeias,
                    onActivityClick = {},
                    onNavigateToLeaderDashboard = onNavigateToLeaderDashboard
                )
            }
        }
    }
}

// =============================================================================
// PREMIUM HOME CONTENT
// =============================================================================

@Composable
private fun HomePremiumContent(
    uiState: HomeUiState,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToRadar: () -> Unit,
    onNavigateToNovaIdeia: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onActivityClick: (String) -> Unit,
    onNavigateToLeaderDashboard: () -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            PremiumTopBar(onNotificationClick = {})
        }
        
        item {
            GreetingSection(
                nome = uiState.nomeUsuario,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        // =====================================================================
        // GESTOR-SPECIFIC FLOW
        // =====================================================================
        if (uiState.perfil == PerfilUsuario.GESTOR) {
            item {
                GestorHeroCard(
                    ideiasPendentes = uiState.ideiasPendentes,
                    onAvaliarClick = onNavigateToIdeias,
                    onCriarProjetoClick = onNavigateToProjetos,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                GestorMetricsSection(
                    ideiasPendentes = uiState.ideiasPendentes,
                    totalProjetos = uiState.totalProjetos,
                    taxaConversao = uiState.taxaConversao,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                GestorFunilSection(
                    totalIdeias = uiState.totalIdeias,
                    ideiasAprovadas = uiState.ideiasAprovadas,
                    ideiasEmProjeto = uiState.ideiasEmProjeto,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (uiState.atividadesRecentes.isNotEmpty()) {
                item {
                    AtividadeRecenteSection(
                        atividades = uiState.atividadesRecentes,
                        onSeeAllClick = onNavigateToIdeias,
                        onActivityClick = onActivityClick,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                GestorQuickActionsSection(
                    ideiasPendentes = uiState.ideiasPendentes,
                    totalProjetos = uiState.totalProjetos,
                    onAvaliarClick = onNavigateToIdeias,
                    onProjetosClick = onNavigateToProjetos,
                    onRadarClick = onNavigateToRadar,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            if (uiState.orientacoes.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.home_section_orientacoes),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
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
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (uiState.minhasIdeias.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.home_gestor_ideias_recentes),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onNavigateToIdeias)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.home_gestor_ver_todas),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                items(
                    items = uiState.minhasIdeias.take(5),
                    key = { it.id }
                ) { ideia ->
                    IdeiaResumoCard(
                        titulo = ideia.titulo,
                        status = ideia.status,
                        area = ideia.area.name,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // =====================================================================
        // OPERADOR-SPECIFIC FLOW
        // =====================================================================
        if (uiState.perfil == PerfilUsuario.OPERADOR) {
            item {
                OperadorMetricsSection(
                    totalIdeias = uiState.totalIdeias,
                    ideiasAprovadas = uiState.ideiasAprovadas,
                    engajamento = uiState.engajamentoPercentual,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            item {
                InspirationHeroCard(
                    headline = stringResource(R.string.hero_headline_operador),
                    subheadline = stringResource(R.string.hero_subheadline_operador),
                    ctaText = stringResource(R.string.hero_cta_operador),
                    onCtaClick = onNavigateToNovaIdeia,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            if (uiState.minhasIdeias.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.home_section_minhas_ideias),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        items(
                            items = uiState.minhasIdeias.take(5),
                            key = { it.id }
                        ) { ideia ->
                            CompactIdeiaCard(
                                titulo = ideia.titulo,
                                status = ideia.status.name.replace("_", " "),
                                onClick = { }
                            )
                        }
                        item {
                            AddNewIdeiaCard(onClick = onNavigateToNovaIdeia)
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (uiState.atividadesRecentes.isNotEmpty()) {
                item {
                    AtividadeRecenteSection(
                        atividades = uiState.atividadesRecentes,
                        onSeeAllClick = onNavigateToIdeias,
                        onActivityClick = onActivityClick,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (uiState.orientacoes.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.home_section_orientacoes),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
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
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.home_quick_actions),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Lightbulb,
                        title = stringResource(R.string.ideias_nova),
                        onClick = onNavigateToNovaIdeia
                    )
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Lightbulb,
                        title = stringResource(R.string.nav_ideias),
                        onClick = onNavigateToIdeias
                    )
                }
            }
        }

        // =====================================================================
        // LIDER-SPECIFIC FLOW
        // =====================================================================
        if (uiState.perfil == PerfilUsuario.LIDER) {
            item {
                PremiumMetricsSection(
                    totalIdeias = uiState.totalIdeias,
                    totalProjetos = uiState.totalProjetos,
                    engajamento = uiState.engajamentoPercentual,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.atividadesRecentes.isNotEmpty()) {
                item {
                    AtividadeRecenteSection(
                        atividades = uiState.atividadesRecentes,
                        onSeeAllClick = onNavigateToIdeias,
                        onActivityClick = onActivityClick,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (uiState.orientacoes.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.home_section_orientacoes),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
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
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                SectionHeader(
                    title = stringResource(R.string.dashboard_titulo),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                DashboardExecutivo(
                    totalIdeias = uiState.totalIdeias,
                    ideiasAprovadas = uiState.ideiasAprovadas,
                    projetosAtivos = uiState.ideiasEmProjeto,
                    investimentoTotal = uiState.investimentoTotal,
                    retornoTotal = uiState.retornoTotal,
                    roi = uiState.roiConsolidado,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                ExecutiveActionsCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    icon = Icons.Default.Insights,
                    title = stringResource(R.string.leader_resumo_executivo),
                    subtitle = "Visão consolidada de métricas e insights",
                    onClick = onNavigateToLeaderDashboard,
                    isHighlighted = true
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Settings,
                        title = stringResource(R.string.home_manage_strategy),
                        onClick = onNavigateToOrientacoes
                    )
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Radar,
                        title = stringResource(R.string.home_innovation_radar),
                        onClick = onNavigateToRadar
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(NavBarDimensions.ContentBottomPaddingWithFab))
        }
    }
}

// =============================================================================
// PREMIUM TOP BAR
// =============================================================================

@Composable
private fun PremiumTopBar(
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cdNotifications = stringResource(R.string.cd_notifications)
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.home_logo),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier.semantics { contentDescription = cdNotifications }
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// =============================================================================
// GREETING SECTION
// =============================================================================

@Composable
private fun GreetingSection(
    nome: String,
    modifier: Modifier = Modifier
) {
    val greeting = getGreetingByTime()
    val cdOnline = stringResource(R.string.cd_status_online)
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$greeting, $nome",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(SuccessGreen, CircleShape)
                .semantics { contentDescription = cdOnline }
        )
    }
}

@Composable
private fun getGreetingByTime(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> stringResource(R.string.home_greeting_morning)
        hour < 18 -> stringResource(R.string.home_greeting_afternoon)
        else -> stringResource(R.string.home_greeting_evening)
    }
}

// =============================================================================
// PREMIUM METRICS SECTION
// =============================================================================

@Composable
private fun PremiumMetricsSection(
    totalIdeias: Int,
    totalProjetos: Int,
    engajamento: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val ideiasLabel = stringResource(R.string.home_metric_ideias_ativas)
    val projetosLabel = stringResource(R.string.home_metric_projetos)
    val engajamentoLabel = stringResource(R.string.home_metric_engajamento)
    val thisWeek = stringResource(R.string.home_trend_this_week)
    val thisMonth = stringResource(R.string.home_trend_this_month)
    
    val unifiedGradient = if (isDarkTheme) {
        GradientMetricCardDefaults.UnifiedGradientDark
    } else {
        GradientMetricCardDefaults.UnifiedGradientLight
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = ideiasLabel,
            value = totalIdeias.toString(),
            trend = "+12 $thisWeek",
            trendPositive = true,
            icon = Icons.Default.Lightbulb,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.primary
        )
        
        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = projetosLabel,
            value = totalProjetos.toString(),
            trend = "+3 $thisMonth",
            trendPositive = true,
            icon = Icons.Default.Folder,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.tertiary
        )
        
        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = engajamentoLabel,
            value = "$engajamento%",
            trend = "+8% $thisMonth",
            trendPositive = true,
            icon = Icons.Default.Groups,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun OperadorMetricsSection(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    engajamento: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val minhasIdeiasLabel = stringResource(R.string.home_metric_minhas_ideias)
    val aprovadasLabel = stringResource(R.string.home_metric_aprovadas)
    val engajamentoLabel = stringResource(R.string.home_metric_engajamento)
    val thisWeek = stringResource(R.string.home_trend_this_week)
    val thisMonth = stringResource(R.string.home_trend_this_month)
    
    val unifiedGradient = if (isDarkTheme) {
        GradientMetricCardDefaults.UnifiedGradientDark
    } else {
        GradientMetricCardDefaults.UnifiedGradientLight
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = minhasIdeiasLabel,
            value = totalIdeias.toString(),
            trend = "+2 $thisWeek",
            trendPositive = true,
            icon = Icons.Default.Lightbulb,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.primary
        )
        
        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = aprovadasLabel,
            value = ideiasAprovadas.toString(),
            trend = "+1 $thisMonth",
            trendPositive = true,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            gradientColors = unifiedGradient,
            accentColor = SuccessGreen
        )
        
        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = engajamentoLabel,
            value = "$engajamento%",
            trend = "+5% $thisMonth",
            trendPositive = true,
            icon = Icons.Default.Groups,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.secondary
        )
    }
}

// =============================================================================
// ATIVIDADE RECENTE SECTION
// =============================================================================

@Composable
private fun AtividadeRecenteSection(
    atividades: List<AtividadeRecente>,
    onSeeAllClick: () -> Unit,
    onActivityClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_activity_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onSeeAllClick)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_activity_see_all),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                atividades.forEachIndexed { index, atividade ->
                    ActivityListItem(
                        atividade = atividade,
                        onClick = { onActivityClick(atividade.id) }
                    )
                    if (index < atividades.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityListItem(
    atividade: AtividadeRecente,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timestamp = formatTimestamp(atividade.timestamp)
    val cdActivity = stringResource(R.string.cd_activity_item)
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .semantics { contentDescription = "$cdActivity: ${atividade.titulo}" },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = atividade.autorNome.take(2).uppercase(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            if (!atividade.isRead) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .align(Alignment.TopStart)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = atividade.titulo,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(2.dp))
            
            Text(
                text = atividade.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val minutes = diff / (60 * 1000)
    val hours = diff / (60 * 60 * 1000)
    val days = diff / (24 * 60 * 60 * 1000)
    
    return when {
        minutes < 1 -> stringResource(R.string.home_activity_time_just_now)
        minutes < 60 -> stringResource(R.string.home_activity_time_minutes, minutes.toInt())
        hours < 24 -> stringResource(R.string.home_activity_time_hours, hours.toInt())
        else -> stringResource(R.string.home_activity_time_days, days.toInt())
    }
}

// =============================================================================
// SKELETON LOADING SCREEN
// =============================================================================

@Composable
private fun HomeSkeletonScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonLoader(
                modifier = Modifier
                    .width(120.dp)
                    .height(28.dp)
            )
            SkeletonAvatar(size = 40.dp)
        }
        
        SkeletonLoader(
            modifier = Modifier
                .width(200.dp)
                .height(32.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(3) {
                SkeletonMetricCard(modifier = Modifier.weight(1f))
            }
        }
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SkeletonText(modifier = Modifier.width(150.dp))
                SkeletonText(modifier = Modifier.width(80.dp))
            }
            
            repeat(3) {
                SkeletonCard()
            }
        }
    }
}

// =============================================================================
// DASHBOARD EXECUTIVO (Líder only) - Premium Bento Grid Layout
// =============================================================================

@Composable
private fun DashboardExecutivo(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    projetosAtivos: Int,
    investimentoTotal: Double,
    retornoTotal: Double,
    roi: Double,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val cardShape = RoundedCornerShape(24.dp)
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )
    val cardElevation = CardDefaults.cardElevation(
        defaultElevation = if (isDarkTheme) 0.dp else 4.dp
    )
    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ROI Hero Card - Full Width
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isDarkTheme) Modifier.border(1.dp, borderColor, cardShape)
                    else Modifier
                ),
            colors = cardColors,
            shape = cardShape,
            elevation = cardElevation
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_roi_anual),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val roiColor = if (roi >= 0) SuccessGreen else MaterialTheme.colorScheme.error
                    Text(
                        text = formatPercent(roi),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = roiColor
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (roi >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = roiColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (roi >= 0) "+2.3% vs mês anterior" else "-1.2% vs mês anterior",
                            style = MaterialTheme.typography.labelSmall,
                            color = roiColor
                        )
                    }
                }
                
                val roiColor = if (roi >= 0) SuccessGreen else MaterialTheme.colorScheme.error
                DonutChart(
                    percentage = (roi.toFloat() / 100f).coerceIn(0f, 1f),
                    centerText = "",
                    color = roiColor,
                    size = 80.dp,
                    strokeWidth = 10.dp
                )
            }
        }

        // Funil Card - Full Width
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isDarkTheme) Modifier.border(1.dp, borderColor, cardShape)
                    else Modifier
                ),
            colors = cardColors,
            shape = cardShape,
            elevation = cardElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = stringResource(R.string.funil_title),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                val ideiasLabel = stringResource(R.string.funil_ideias)
                val aprovadasLabel = stringResource(R.string.funil_aprovadas)
                val projetosLabel = stringResource(R.string.funil_em_projeto)
                
                FunnelChartHorizontal(
                    steps = listOf(
                        FunnelStep(ideiasLabel, totalIdeias, MaterialTheme.colorScheme.primary),
                        FunnelStep(aprovadasLabel, ideiasAprovadas, SuccessGreen),
                        FunnelStep(projetosLabel, projetosAtivos, MaterialTheme.colorScheme.tertiary)
                    )
                )
            }
        }

        // Financeiro - Split Cards
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
    }
}

@Composable
private fun FunnelChartHorizontal(
    steps: List<FunnelStep>,
    modifier: Modifier = Modifier
) {
    val maxValue = steps.maxOfOrNull { it.value }?.toFloat()?.coerceAtLeast(1f) ?: 1f
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        steps.forEach { step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = step.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(90.dp)
                )
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(step.color.copy(alpha = 0.12f))
                ) {
                    val fraction = (step.value.toFloat() / maxValue).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction)
                            .clip(RoundedCornerShape(12.dp))
                            .background(step.color)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = step.value.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = step.color,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun FinanceiroMiniCard(
    label: String,
    value: Double,
    isNegative: Boolean,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.06f)
    } else {
        Color(0xFFE5E7EB)
    }
    
    val accentColor = if (isNegative) MaterialTheme.colorScheme.error else SuccessGreen
    val backgroundColor = if (isDarkTheme) {
        Color(0xFF1C1C1E)
    } else {
        Color.White
    }
    
    val cardModifier = if (isDarkTheme) {
        modifier
            .clip(cardShape)
            .border(1.dp, borderColor, cardShape)
            .background(backgroundColor)
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
            .background(backgroundColor)
    }
    
    Box(modifier = cardModifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            color = accentColor.copy(alpha = if (isDarkTheme) 0.15f else 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isNegative) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            AnimatedCurrencyCounter(
                targetValue = value,
                style = MaterialTheme.typography.titleLarge,
                color = accentColor
            )
        }
    }
}

// =============================================================================
// SHARED COMPONENTS
// =============================================================================

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
    val isDarkTheme = isSystemInDarkTheme()
    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    }
    
    Surface(
        modifier = modifier
            .bounceClick(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isDarkTheme) 0.dp else 1.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ExecutiveActionsCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isHighlighted: Boolean = false
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(20.dp)
    val primaryColor = MaterialTheme.colorScheme.primary
    
    val gradientBrush = if (isHighlighted) {
        androidx.compose.ui.graphics.Brush.horizontalGradient(
            colors = listOf(
                primaryColor,
                primaryColor.copy(alpha = 0.85f)
            )
        )
    } else {
        androidx.compose.ui.graphics.Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surface
            )
        )
    }
    
    val contentColor = if (isHighlighted) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    
    val subtitleColor = if (isHighlighted) {
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    val borderColor = if (!isHighlighted && isDarkTheme) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    } else {
        Color.Transparent
    }
    
    Box(
        modifier = modifier
            .bounceClick(onClick = onClick)
            .clip(shape)
            .then(
                if (!isHighlighted && isDarkTheme) {
                    Modifier.border(1.dp, borderColor, shape)
                } else if (isHighlighted) {
                    Modifier.shadow(
                        elevation = if (isDarkTheme) 8.dp else 12.dp,
                        shape = shape,
                        ambientColor = primaryColor.copy(alpha = 0.25f),
                        spotColor = primaryColor.copy(alpha = 0.25f)
                    )
                } else {
                    Modifier.shadow(
                        elevation = 4.dp,
                        shape = shape
                    )
                }
            )
            .background(gradientBrush, shape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        if (isHighlighted) Color.White.copy(alpha = 0.2f)
                        else primaryColor.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isHighlighted) Color.White else primaryColor,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = subtitleColor
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        if (isHighlighted) Color.White.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = if (isHighlighted) Color.White else primaryColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun IdeiaResumoCard(
    titulo: String,
    status: com.gtnix.aguiabranca.domain.model.StatusIdeia,
    area: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
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
                contentDescription = stringResource(R.string.cd_idea_icon),
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

// =============================================================================
// GESTOR-SPECIFIC COMPONENTS
// =============================================================================

@Composable
private fun GestorHeroCard(
    ideiasPendentes: Int,
    onAvaliarClick: () -> Unit,
    onCriarProjetoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(24.dp)
    val hasPending = ideiasPendentes > 0

    val gradientColors = if (isDarkTheme) {
        listOf(Color(0xFF0D1B2A), Color(0xFF0A1628), Color(0xFF061220))
    } else {
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
            MaterialTheme.colorScheme.surface
        )
    }

    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(brush = Brush.verticalGradient(colors = gradientColors))
            .border(1.dp, borderColor, shape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (hasPending) Icons.Default.Assessment else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            ideiasPendentes > 1 -> stringResource(R.string.home_gestor_hero_headline, ideiasPendentes)
                            ideiasPendentes == 1 -> stringResource(R.string.home_gestor_hero_headline_single)
                            else -> stringResource(R.string.home_gestor_hero_headline_empty)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (hasPending) {
                            stringResource(R.string.home_gestor_hero_subheadline)
                        } else {
                            stringResource(R.string.home_gestor_hero_subheadline_empty)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .bounceClick(
                        onClick = if (hasPending) onAvaliarClick else onCriarProjetoClick
                    ),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (hasPending) Icons.Default.Assessment else Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasPending) {
                            stringResource(R.string.home_gestor_hero_cta)
                        } else {
                            stringResource(R.string.home_gestor_hero_cta_empty)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun GestorMetricsSection(
    ideiasPendentes: Int,
    totalProjetos: Int,
    taxaConversao: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val pendentesLabel = stringResource(R.string.home_gestor_metric_pendentes)
    val projetosLabel = stringResource(R.string.home_metric_projetos)
    val conversaoLabel = stringResource(R.string.home_gestor_metric_taxa_conversao)
    val thisWeek = stringResource(R.string.home_trend_this_week)
    val thisMonth = stringResource(R.string.home_trend_this_month)

    val unifiedGradient = if (isDarkTheme) {
        GradientMetricCardDefaults.UnifiedGradientDark
    } else {
        GradientMetricCardDefaults.UnifiedGradientLight
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = pendentesLabel,
            value = ideiasPendentes.toString(),
            trend = if (ideiasPendentes > 0) "+$ideiasPendentes $thisWeek" else "0 $thisWeek",
            trendPositive = ideiasPendentes > 0,
            icon = Icons.Default.Lightbulb,
            gradientColors = unifiedGradient,
            accentColor = if (ideiasPendentes > 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
        )

        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = projetosLabel,
            value = totalProjetos.toString(),
            trend = if (totalProjetos > 0) "+1 $thisMonth" else "0 $thisMonth",
            trendPositive = totalProjetos > 0,
            icon = Icons.Default.Folder,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.tertiary
        )

        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = conversaoLabel,
            value = "$taxaConversao%",
            trend = if (taxaConversao > 0) "+$taxaConversao% $thisMonth" else "0% $thisMonth",
            trendPositive = taxaConversao > 0,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            gradientColors = unifiedGradient,
            accentColor = SuccessGreen
        )
    }
}

@Composable
private fun GestorFunilSection(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    ideiasEmProjeto: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(R.string.funil_title),
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(12.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
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
}

@Composable
private fun GestorQuickActionsSection(
    ideiasPendentes: Int,
    totalProjetos: Int,
    onAvaliarClick: () -> Unit,
    onProjetosClick: () -> Unit,
    onRadarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(R.string.home_quick_actions),
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ActionCardWithBadge(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Assessment,
                title = stringResource(R.string.home_gestor_avaliar_ideias),
                badgeCount = ideiasPendentes,
                onClick = onAvaliarClick
            )
            ActionCardWithBadge(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Folder,
                title = stringResource(R.string.home_gestor_meus_projetos),
                badgeCount = totalProjetos,
                onClick = onProjetosClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ActionCard(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Radar,
            title = stringResource(R.string.home_innovation_radar),
            onClick = onRadarClick
        )
    }
}

@Composable
private fun ActionCardWithBadge(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    badgeCount: Int,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    }

    Surface(
        modifier = modifier
            .bounceClick(onClick = onClick)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isDarkTheme) 0.dp else 1.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (badgeCount > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    InovagabTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.GESTOR,
                totalIdeias = 47,
                ideiasAprovadas = 18,
                ideiasEmProjeto = 7,
                totalProjetos = 12,
                atividadesRecentes = listOf(
                    AtividadeRecente(
                        id = "1",
                        tipo = TipoAtividade.NOVA_IDEIA,
                        titulo = "Nova ideia: Plataforma de Mentoria Interna",
                        descricao = "Uma plataforma para conectar mentores e mentorados dentro da empresa.",
                        autorNome = "Carlos Silva",
                        timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000)
                    ),
                    AtividadeRecente(
                        id = "2",
                        tipo = TipoAtividade.NOVA_IDEIA,
                        titulo = "Nova ideia: Dashboard de Impacto",
                        descricao = "Dashboard interativo para acompanhar métricas de impacto dos projetos.",
                        autorNome = "Ana Costa",
                        timestamp = System.currentTimeMillis() - (5 * 60 * 60 * 1000),
                        isRead = true
                    ),
                    AtividadeRecente(
                        id = "3",
                        tipo = TipoAtividade.NOVA_IDEIA,
                        titulo = "Nova ideia: Gamificação Interna",
                        descricao = "Programa de gamificação para aumentar o engajamento da equipe.",
                        autorNome = "Pedro Santos",
                        timestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000),
                        isRead = true
                    )
                )
            ),
            onRefresh = {},
            onNavigateToIdeias = {},
            onNavigateToProjetos = {},
            onNavigateToOrientacoes = {},
            onNavigateToPerfil = {},
            onNavigateToNovaIdeia = {},
            onNavigateToRadar = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenDarkPreview() {
    InovagabTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.GESTOR,
                totalIdeias = 47,
                ideiasAprovadas = 18,
                ideiasEmProjeto = 7,
                totalProjetos = 12,
                atividadesRecentes = listOf(
                    AtividadeRecente(
                        id = "1",
                        tipo = TipoAtividade.NOVA_IDEIA,
                        titulo = "Nova ideia: Plataforma de Mentoria Interna",
                        descricao = "Uma plataforma para conectar mentores e mentorados dentro da empresa.",
                        autorNome = "Carlos Silva",
                        timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000)
                    ),
                    AtividadeRecente(
                        id = "2",
                        tipo = TipoAtividade.NOVA_IDEIA,
                        titulo = "Nova ideia: Dashboard de Impacto",
                        descricao = "Dashboard interativo para acompanhar métricas de impacto dos projetos.",
                        autorNome = "Ana Costa",
                        timestamp = System.currentTimeMillis() - (5 * 60 * 60 * 1000),
                        isRead = true
                    )
                )
            ),
            onRefresh = {},
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
    InovagabTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.LIDER,
                totalIdeias = 42,
                ideiasAprovadas = 18,
                ideiasEmProjeto = 7,
                totalProjetos = 7,
                investimentoTotal = 350000.0,
                retornoTotal = 48000.0,
                roiConsolidado = 64.6,
                atividadesRecentes = listOf(
                    AtividadeRecente(
                        id = "1",
                        tipo = TipoAtividade.NOVA_IDEIA,
                        titulo = "Nova ideia: Plataforma de Mentoria",
                        descricao = "Uma plataforma para conectar mentores e mentorados.",
                        autorNome = "Carlos Silva",
                        timestamp = System.currentTimeMillis() - (2 * 60 * 60 * 1000)
                    )
                )
            ),
            onRefresh = {},
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
private fun HomeSkeletonPreview() {
    InovagabTheme {
        HomeSkeletonScreen()
    }
}
