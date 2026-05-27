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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.usecase.dashboard.IdeiaConvertidaRecente
import com.gtnix.aguiabranca.domain.usecase.dashboard.WeeklyRecap
import com.gtnix.aguiabranca.presentation.components.CompactIdeiaCard
import com.gtnix.aguiabranca.presentation.components.GamificationSnippetCard
import com.gtnix.aguiabranca.presentation.components.GradientMetricCard
import com.gtnix.aguiabranca.presentation.components.GradientMetricCardDefaults
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.InovagabBrandLogo
import com.gtnix.aguiabranca.presentation.components.InspirationHeroCard
import com.gtnix.aguiabranca.presentation.components.LiderHeroCard
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.components.SkeletonAvatar
import com.gtnix.aguiabranca.presentation.components.SkeletonCard
import com.gtnix.aguiabranca.presentation.components.SkeletonLoader
import com.gtnix.aguiabranca.presentation.components.SkeletonMetricCard
import com.gtnix.aguiabranca.presentation.components.SkeletonText
import com.gtnix.aguiabranca.presentation.components.charts.FunnelChartHorizontal
import com.gtnix.aguiabranca.presentation.components.charts.FunnelStep
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.NavBarDimensions
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import androidx.compose.ui.res.stringResource
import com.gtnix.aguiabranca.presentation.util.bounceClick
import com.gtnix.aguiabranca.presentation.util.formatPercent
import java.util.Calendar

@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    perfil: String,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToOrientacaoDetalhe: (String) -> Unit = {},
    onNavigateToRadar: () -> Unit,
    onNavigateToLeaderDashboard: () -> Unit = {},
    onNavigateToNovaIdeia: () -> Unit = {},
    onNavigateToIdeias: () -> Unit = {},
    onNavigateToIdeiaDetalhe: (String) -> Unit = {},
    onNavigateToProjetos: () -> Unit = {},
    onNavigateToRanking: () -> Unit = {},
    onNavigateToNovaOrientacao: () -> Unit = {},
    onNavigateToNotificacoes: () -> Unit = {},
    onNavigateToPerfilTab: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(perfil) {
        viewModel.carregarDados(perfil)
    }

    HomeBodyContent(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onNavigateToOrientacoes = onNavigateToOrientacoes,
        onNavigateToOrientacaoDetalhe = onNavigateToOrientacaoDetalhe,
        onNavigateToRadar = onNavigateToRadar,
        onNavigateToLeaderDashboard = onNavigateToLeaderDashboard,
        onNavigateToNovaIdeia = onNavigateToNovaIdeia,
        onNavigateToIdeias = onNavigateToIdeias,
        onNavigateToIdeiaDetalhe = onNavigateToIdeiaDetalhe,
        onNavigateToProjetos = onNavigateToProjetos,
        onNavigateToRanking = onNavigateToRanking,
        onNavigateToNovaOrientacao = onNavigateToNovaOrientacao,
        onNavigateToNotificacoes = onNavigateToNotificacoes,
        onNavigateToPerfilTab = onNavigateToPerfilTab
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeBodyContent(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToOrientacaoDetalhe: (String) -> Unit = {},
    onNavigateToRadar: () -> Unit,
    onNavigateToLeaderDashboard: () -> Unit = {},
    onNavigateToNovaIdeia: () -> Unit = {},
    onNavigateToIdeias: () -> Unit = {},
    onNavigateToIdeiaDetalhe: (String) -> Unit = {},
    onNavigateToProjetos: () -> Unit = {},
    onNavigateToRanking: () -> Unit = {},
    onNavigateToNovaOrientacao: () -> Unit = {},
    onNavigateToNotificacoes: () -> Unit = {},
    onNavigateToPerfilTab: () -> Unit = {}
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
                    onNavigateToOrientacaoDetalhe = onNavigateToOrientacaoDetalhe,
                    onNavigateToRadar = onNavigateToRadar,
                    onNavigateToProjetos = onNavigateToProjetos,
                    onNavigateToIdeias = onNavigateToIdeias,
                    onNavigateToIdeiaDetalhe = onNavigateToIdeiaDetalhe,
                    onActivityClick = onNavigateToIdeiaDetalhe,
                    onNavigateToLeaderDashboard = onNavigateToLeaderDashboard,
                    onNavigateToNovaIdeia = onNavigateToNovaIdeia,
                    onNavigateToRanking = onNavigateToRanking,
                    onNavigateToNovaOrientacao = onNavigateToNovaOrientacao,
                    onNavigateToNotificacoes = onNavigateToNotificacoes,
                    onNavigateToPerfilTab = onNavigateToPerfilTab
                )
            }
        }
    }
}

@Composable
private fun HomePremiumContent(
    uiState: HomeUiState,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToOrientacaoDetalhe: (String) -> Unit = {},
    onNavigateToRadar: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onNavigateToIdeiaDetalhe: (String) -> Unit,
    onActivityClick: (String) -> Unit,
    onNavigateToLeaderDashboard: () -> Unit = {},
    onNavigateToNovaIdeia: () -> Unit = {},
    onNavigateToRanking: () -> Unit = {},
    onNavigateToNovaOrientacao: () -> Unit = {},
    onNavigateToNotificacoes: () -> Unit = {},
    onNavigateToPerfilTab: () -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = NavBarDimensions.ContentBottomPaddingWithFab)
    ) {
        item {
            // Para o líder, aproveitamos o slot do avatar para mostrar um chip
            // compacto com nível + posição (substitui o GamificationSnippetCard
            // grande, evitando duplicação com NivelCard do Perfil — decisão da
            // seção 9 "Redundâncias" da auditoria).
            PremiumTopBar(
                nomeUsuario = uiState.nomeUsuario,
                onNotificationsClick = onNavigateToNotificacoes,
                onProfileClick = onNavigateToPerfilTab,
                liderNivelChip = if (uiState.perfil == PerfilUsuario.LIDER && uiState.pontuacao != null) {
                    LiderNivelChipState(
                        nivelLabel = uiState.pontuacao.nivel.label,
                        posicao = uiState.posicaoRanking
                    )
                } else null,
                onNivelChipClick = onNavigateToRanking
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            GreetingSection(
                nome = uiState.nomeUsuario,
                perfil = uiState.perfil,
                ideiasPendentes = uiState.ideiasPendentesAvaliacao,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

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

            if (uiState.orientacoes.isNotEmpty()) {
                item {
                    HomeOrientacoesSection(
                        orientacoes = uiState.orientacoes,
                        onSeeAllClick = onNavigateToOrientacoes,
                        onOrientacaoClick = onNavigateToOrientacaoDetalhe,
                        sectionActionLabel = stringResource(R.string.orientacao_ver_todas),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            item {
                HomeComplementActionsSection(
                    onRadarClick = onNavigateToRadar,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (uiState.perfil == PerfilUsuario.OPERADOR) {
            if (uiState.orientacoes.isNotEmpty()) {
                item {
                    HomeOrientacoesSection(
                        orientacoes = uiState.orientacoes,
                        onSeeAllClick = onNavigateToOrientacoes,
                        onOrientacaoClick = onNavigateToOrientacaoDetalhe,
                        sectionActionLabel = stringResource(R.string.orientacao_ver_todas),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (uiState.ideiasConvertidasRecentes.isNotEmpty()) {
                items(
                    items = uiState.ideiasConvertidasRecentes,
                    key = { it.ideia.id }
                ) { convertida ->
                    IdeiaConvertidaCard(
                        convertida = convertida,
                        onClick = { onNavigateToIdeiaDetalhe(convertida.ideia.id) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 12.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            if (uiState.minhasIdeias.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = stringResource(R.string.home_section_minhas_ideias),
                        actionLabel = stringResource(R.string.home_activity_see_all),
                        onActionClick = onNavigateToIdeias,
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
                                onClick = { onNavigateToIdeiaDetalhe(ideia.id) }
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
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
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            if (uiState.pontuacao != null) {
                item {
                    GamificationSnippetCard(
                        pontuacao = uiState.pontuacao,
                        posicaoRanking = uiState.posicaoRanking,
                        onClick = onNavigateToRanking,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            item {
                OperadorMetricsSimplificado(
                    totalIdeias = uiState.totalIdeias,
                    ideiasAprovadas = uiState.ideiasAprovadas,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (uiState.perfil == PerfilUsuario.LIDER) {
            item {
                LiderHeroCard(
                    orientacoesAtivas = uiState.orientacoes.size,
                    onPublicarClick = onNavigateToNovaOrientacao,
                    onVerOrientacoesClick = onNavigateToOrientacoes,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // KPI strip clicável (L-10): cada cartão navega para a tela
            // correspondente — Ideias, Projetos e Leader Dashboard.
            item {
                PremiumMetricsSection(
                    totalIdeias = uiState.totalIdeias,
                    totalProjetos = uiState.totalProjetos,
                    engajamento = uiState.engajamentoPercentual,
                    isDarkTheme = isDarkTheme,
                    onIdeiasClick = onNavigateToIdeias,
                    onProjetosClick = onNavigateToProjetos,
                    onEngajamentoClick = onNavigateToLeaderDashboard,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }

            if (uiState.orientacoes.isNotEmpty()) {
                item {
                    HomeOrientacoesSection(
                        orientacoes = uiState.orientacoes,
                        onSeeAllClick = onNavigateToOrientacoes,
                        onOrientacaoClick = onNavigateToOrientacaoDetalhe,
                        sectionActionLabel = stringResource(R.string.home_manage_strategy),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Recap semanal.
            item {
                WeeklyRecapCard(
                    recap = uiState.weeklyRecap,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Resumo executivo + funil mini inline (L-16).
            item {
                ExecutiveSummaryCTACard(
                    roi = uiState.roiConsolidado,
                    totalIdeias = uiState.totalIdeias,
                    ideiasAprovadas = uiState.ideiasAprovadas,
                    ideiasEmProjeto = uiState.ideiasEmProjeto,
                    onClick = onNavigateToLeaderDashboard,
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

            // Explorar (Radar de Inovação).
            item {
                HomeComplementActionsSection(
                    onRadarClick = onNavigateToRadar,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/**
 * Estado opcional do chip de nível compacto exibido na top bar do líder
 * (decisão da seção 9 "Redundâncias" — substitui o GamificationSnippetCard
 * grande para liberar espaço ao Hero CTA).
 */
private data class LiderNivelChipState(
    val nivelLabel: String,
    val posicao: Int?
)

@Composable
private fun PremiumTopBar(
    nomeUsuario: String,
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    liderNivelChip: LiderNivelChipState? = null,
    onNivelChipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cdNotifications = stringResource(R.string.cd_notifications)
    val cdAvatar = stringResource(R.string.cd_user_avatar)
    val initial = nomeUsuario.trim().firstOrNull()?.uppercase() ?: "?"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 10.dp, end = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InovagabBrandLogo()

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (liderNivelChip != null) {
                LiderNivelChip(
                    state = liderNivelChip,
                    onClick = onNivelChipClick
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            IconButton(onClick = onNotificationsClick) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = cdNotifications,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onProfileClick)
                    .semantics { contentDescription = cdAvatar },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun LiderNivelChip(
    state: LiderNivelChipState,
    onClick: () -> Unit
) {
    val color = MaterialTheme.colorScheme.primary
    val label = if (state.posicao != null) {
        stringResource(R.string.home_lider_chip_nivel, state.nivelLabel, state.posicao)
    } else {
        stringResource(R.string.home_lider_chip_nivel_sem_ranking, state.nivelLabel)
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun GreetingSection(
    nome: String,
    perfil: PerfilUsuario,
    ideiasPendentes: Int,
    modifier: Modifier = Modifier
) {
    val greeting = getGreetingByTime()
    val cdOnline = stringResource(R.string.cd_status_online)
    val subtitle = when (perfil) {
        PerfilUsuario.OPERADOR -> when {
            ideiasPendentes > 1 -> stringResource(R.string.home_greeting_operador_pendentes_plural, ideiasPendentes)
            ideiasPendentes == 1 -> stringResource(R.string.home_greeting_operador_pendentes_single)
            else -> stringResource(R.string.home_greeting_operador_empty)
        }
        PerfilUsuario.LIDER -> when {
            ideiasPendentes > 1 -> stringResource(
                R.string.home_greeting_lider_pendentes_plural,
                ideiasPendentes
            )
            ideiasPendentes == 1 -> stringResource(R.string.home_greeting_lider_pendentes_single)
            else -> stringResource(R.string.home_greeting_lider_empty)
        }
        PerfilUsuario.GESTOR -> null
    }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$greeting, $nome!",
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

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeeklyRecapCard(
    recap: WeeklyRecap,
    modifier: Modifier = Modifier
) {
    val resources = LocalContext.current.resources
    val summary = buildString {
        append(stringResource(R.string.home_recap_prefix))
        append(' ')
        append(resources.getQuantityString(R.plurals.home_recap_novas_ideias, recap.novasIdeias, recap.novasIdeias))
        append(", ")
        append(resources.getQuantityString(R.plurals.home_recap_avaliacoes, recap.avaliacoes, recap.avaliacoes))
        append(", ")
        append(resources.getQuantityString(R.plurals.home_projetos_atualizados, recap.projetosAtualizados, recap.projetosAtualizados))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_recap_title),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
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


@Composable
private fun PremiumMetricsSection(
    totalIdeias: Int,
    totalProjetos: Int,
    engajamento: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier,
    onIdeiasClick: (() -> Unit)? = null,
    onProjetosClick: (() -> Unit)? = null,
    onEngajamentoClick: (() -> Unit)? = null
) {
    val ideiasLabel = stringResource(R.string.home_metric_ideias_ativas)
    val projetosLabel = stringResource(R.string.home_metric_projetos)
    val engajamentoLabel = stringResource(R.string.home_metric_engajamento_short)

    val cdIdeias = stringResource(R.string.cd_lider_kpi_ideias)
    val cdProjetos = stringResource(R.string.cd_lider_kpi_projetos)
    val cdEngajamento = stringResource(R.string.cd_lider_kpi_engajamento)

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
            modifier = Modifier
                .weight(1f)
                .then(
                    if (onIdeiasClick != null) {
                        Modifier
                            .bounceClick(onClick = onIdeiasClick)
                            .semantics { contentDescription = cdIdeias }
                    } else Modifier
                ),
            title = ideiasLabel,
            value = totalIdeias.toString(),
            icon = Icons.Default.Lightbulb,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.primary
        )

        GradientMetricCard(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (onProjetosClick != null) {
                        Modifier
                            .bounceClick(onClick = onProjetosClick)
                            .semantics { contentDescription = cdProjetos }
                    } else Modifier
                ),
            title = projetosLabel,
            value = totalProjetos.toString(),
            icon = Icons.Default.Folder,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.tertiary
        )

        GradientMetricCard(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (onEngajamentoClick != null) {
                        Modifier
                            .bounceClick(onClick = onEngajamentoClick)
                            .semantics { contentDescription = cdEngajamento }
                    } else Modifier
                ),
            title = engajamentoLabel,
            value = "$engajamento%",
            icon = Icons.Default.Groups,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun IdeiaConvertidaCard(
    convertida: IdeiaConvertidaRecente,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    val primaryColor = MaterialTheme.colorScheme.primary
    val roiColor = if (convertida.roi >= 0) SuccessGreen else MaterialTheme.colorScheme.error

    Box(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(onClick = onClick)
            .clip(shape)
            .shadow(
                elevation = 12.dp,
                shape = shape,
                ambientColor = primaryColor.copy(alpha = 0.25f),
                spotColor = primaryColor.copy(alpha = 0.25f)
            )
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        primaryColor,
                        primaryColor.copy(alpha = 0.85f)
                    )
                ),
                shape
            )
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
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_ideia_convertida_titulo),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.home_ideia_convertida_subtitulo,
                        convertida.ideia.titulo,
                        convertida.projeto.nome
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (convertida.roi >= 0) {
                            Icons.AutoMirrored.Filled.TrendingUp
                        } else {
                            Icons.AutoMirrored.Filled.TrendingDown
                        },
                        contentDescription = null,
                        tint = roiColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(
                            R.string.home_ideia_convertida_roi,
                            formatPercent(convertida.roi)
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = roiColor
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun OperadorMetricsSimplificado(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val minhasIdeiasLabel = stringResource(R.string.home_metric_minhas_ideias)
    val aprovadasLabel = stringResource(R.string.home_metric_aprovadas)
    
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
            icon = Icons.Default.Lightbulb,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.primary
        )
        
        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = aprovadasLabel,
            value = ideiasAprovadas.toString(),
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            gradientColors = unifiedGradient,
            accentColor = SuccessGreen
        )
    }
}


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


@Composable
private fun ExecutiveSummaryCTACard(
    roi: Double,
    totalIdeias: Int,
    ideiasAprovadas: Int,
    ideiasEmProjeto: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(24.dp)
    val primaryColor = MaterialTheme.colorScheme.primary
    val roiColor = if (roi >= 0) SuccessGreen else MaterialTheme.colorScheme.error
    val trendIcon = if (roi >= 0) {
        Icons.AutoMirrored.Filled.TrendingUp
    } else {
        Icons.AutoMirrored.Filled.TrendingDown
    }

    val gradientColors = if (isDarkTheme) {
        listOf(
            primaryColor.copy(alpha = 0.22f),
            primaryColor.copy(alpha = 0.10f),
            MaterialTheme.colorScheme.surface
        )
    } else {
        listOf(
            primaryColor.copy(alpha = 0.14f),
            primaryColor.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.surface
        )
    }

    val borderColor = if (isDarkTheme) {
        primaryColor.copy(alpha = 0.25f)
    } else {
        primaryColor.copy(alpha = 0.15f)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(onClick = onClick),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(Brush.linearGradient(gradientColors))
                .border(1.dp, borderColor, shape)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (isDarkTheme) Color.White.copy(alpha = 0.12f)
                                else primaryColor.copy(alpha = 0.12f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Insights,
                            contentDescription = null,
                            tint = if (isDarkTheme) Color.White else primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = stringResource(R.string.leader_resumo_executivo),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.dashboard_roi),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formatPercent(roi),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = roiColor
                        )
                    }
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(roiColor.copy(alpha = if (isDarkTheme) 0.15f else 0.1f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = trendIcon,
                            contentDescription = null,
                            tint = roiColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(
                        R.string.home_lider_funil_inline,
                        totalIdeias,
                        ideiasAprovadas,
                        ideiasEmProjeto
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.home_leader_ver_analise_completa),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = primaryColor
                )
            }
        }
    }
}


@Composable
private fun HomeOrientacoesSection(
    orientacoes: List<OrientacaoEstrategica>,
    onSeeAllClick: () -> Unit,
    onOrientacaoClick: (String) -> Unit,
    sectionActionLabel: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(R.string.home_section_orientacoes),
            actionLabel = sectionActionLabel,
            onActionClick = onSeeAllClick
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 4.dp)
        ) {
            items(
                items = orientacoes,
                key = { it.id }
            ) { orientacao ->
                OrientacaoCard(
                    orientacao = orientacao,
                    onClick = { onOrientacaoClick(orientacao.id) }
                )
            }
        }
    }
}

@Composable
private fun OrientacaoCard(
    orientacao: OrientacaoEstrategica,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    val categoriaAccent = orientacaoCategoriaColor(orientacao.categoria)
    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    }

    Card(
        modifier = modifier
            .width(280.dp)
            .bounceClick(onClick = onClick)
            .then(
                if (isDarkTheme) Modifier.border(1.dp, borderColor, shape)
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = shape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 0.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            categoriaAccent.copy(alpha = if (isDarkTheme) 0.18f else 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = orientacaoCategoriaIcon(orientacao.categoria),
                        contentDescription = null,
                        tint = categoriaAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                OrientacaoPrioridadeBadge(prioridade = orientacao.prioridade)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = orientacaoCategoriaLabel(orientacao.categoria),
                style = MaterialTheme.typography.labelSmall,
                color = categoriaAccent,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = orientacao.titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = orientacao.descricao,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            orientacao.dataExpiracao?.let { expiracao ->
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = if (expiracao < System.currentTimeMillis()) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = orientacaoExpiracaoLabel(expiracao),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (expiracao < System.currentTimeMillis()) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OrientacaoPrioridadeBadge(prioridade: Int) {
    val color = when {
        prioridade <= 1 -> MaterialTheme.colorScheme.error
        prioridade == 2 -> Color(0xFFFF9800)
        prioridade == 3 -> MaterialTheme.colorScheme.tertiary
        else -> SuccessGreen
    }
    val label = when {
        prioridade <= 1 -> "P1"
        prioridade == 2 -> "P2"
        prioridade == 3 -> "P3"
        else -> "P$prioridade"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.PriorityHigh,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

private fun orientacaoExpiracaoLabel(expiracao: Long): String {
    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR"))
    val prefix = if (expiracao < System.currentTimeMillis()) "Expirou em" else "Expira em"
    return "$prefix ${formatter.format(java.util.Date(expiracao))}"
}

private fun orientacaoCategoriaLabel(categoria: CategoriaOrientacao): String = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> "Redução de Custos"
    CategoriaOrientacao.QUALIDADE_SERVICO -> "Qualidade de Serviço"
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> "Inovação Tecnológica"
    CategoriaOrientacao.SUSTENTABILIDADE -> "Sustentabilidade"
    CategoriaOrientacao.SEGURANCA -> "Segurança"
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> "Experiência do Cliente"
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> "Eficiência Operacional"
}

private fun orientacaoCategoriaColor(categoria: CategoriaOrientacao): Color = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> Color(0xFF2196F3)
    CategoriaOrientacao.QUALIDADE_SERVICO -> Color(0xFF4CAF50)
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> Color(0xFF9C27B0)
    CategoriaOrientacao.SUSTENTABILIDADE -> Color(0xFF009688)
    CategoriaOrientacao.SEGURANCA -> Color(0xFFFF9800)
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> Color(0xFFE91E63)
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> Color(0xFF607D8B)
}

private fun orientacaoCategoriaIcon(categoria: CategoriaOrientacao): ImageVector = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> Icons.AutoMirrored.Filled.TrendingDown
    CategoriaOrientacao.QUALIDADE_SERVICO -> Icons.Default.Star
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> Icons.Default.Lightbulb
    CategoriaOrientacao.SUSTENTABILIDADE -> Icons.Default.Eco
    CategoriaOrientacao.SEGURANCA -> Icons.Default.Security
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> Icons.Default.SupportAgent
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> Icons.Default.Speed
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
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.surface
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
            MaterialTheme.colorScheme.surface
        )
    }

    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
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
            icon = Icons.Default.Lightbulb,
            gradientColors = unifiedGradient,
            accentColor = if (ideiasPendentes > 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
        )

        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = projetosLabel,
            value = totalProjetos.toString(),
            icon = Icons.Default.Folder,
            gradientColors = unifiedGradient,
            accentColor = MaterialTheme.colorScheme.tertiary
        )

        GradientMetricCard(
            modifier = Modifier.weight(1f),
            title = conversaoLabel,
            value = "$taxaConversao%",
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
private fun HomeComplementActionsSection(
    onRadarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(R.string.home_quick_actions_complement),
            modifier = Modifier
        )
        ActionCard(
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Default.Radar,
            title = stringResource(R.string.home_innovation_radar),
            onClick = onRadarClick
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    InovagabTheme {
        HomeBodyContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.GESTOR,
                nomeUsuario = "Ana",
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
            onNavigateToNovaIdeia = {},
            onNavigateToRadar = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenDarkPreview() {
    InovagabTheme {
        HomeBodyContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.GESTOR,
                nomeUsuario = "Ana",
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
            onNavigateToNovaIdeia = {},
            onNavigateToRadar = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenLiderPreview() {
    InovagabTheme {
        HomeBodyContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.LIDER,
                nomeUsuario = "Carlos",
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
