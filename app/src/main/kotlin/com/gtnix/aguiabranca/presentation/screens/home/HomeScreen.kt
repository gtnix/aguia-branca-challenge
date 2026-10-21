package com.gtnix.aguiabranca.presentation.screens.home

import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.TemporadaInfo
import com.gtnix.aguiabranca.domain.usecase.dashboard.IdeiaConvertidaRecente
import com.gtnix.aguiabranca.domain.usecase.dashboard.WeeklyRecap
import com.gtnix.aguiabranca.presentation.components.AsasIcons
import com.gtnix.aguiabranca.presentation.components.InovagabBrandLogo
import com.gtnix.aguiabranca.presentation.components.InspirationHeroCard
import com.gtnix.aguiabranca.presentation.components.InovagabFlatCard
import com.gtnix.aguiabranca.presentation.components.InovagabPill
import com.gtnix.aguiabranca.presentation.components.LiderPortfolioStage
import com.gtnix.aguiabranca.presentation.components.LiderPortfolioUi
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.components.SkeletonAvatar
import com.gtnix.aguiabranca.presentation.components.SkeletonCard
import com.gtnix.aguiabranca.presentation.components.SkeletonLoader
import com.gtnix.aguiabranca.presentation.components.SkeletonMetricCard
import com.gtnix.aguiabranca.presentation.components.SkeletonText
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.theme.NavBarDimensions
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.theme.WarningAmber
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import com.gtnix.aguiabranca.presentation.theme.CardDimensions
import com.gtnix.aguiabranca.presentation.theme.spacing
import com.gtnix.aguiabranca.presentation.util.bounceClick
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaColor
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaIcon
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaLabel
import com.gtnix.aguiabranca.presentation.util.orientacaoDivisaoLabel
import com.gtnix.aguiabranca.presentation.util.formatPercent
import com.gtnix.aguiabranca.presentation.util.nivelIcon
import com.gtnix.aguiabranca.presentation.theme.levelBrush
import com.gtnix.aguiabranca.presentation.theme.levelGradientColors
import com.gtnix.aguiabranca.presentation.theme.tierAccent
import com.gtnix.aguiabranca.presentation.components.GradientProgressBar
import com.gtnix.aguiabranca.presentation.components.ideiaStatusColor
import com.gtnix.aguiabranca.presentation.components.ideiaStatusTexto
import com.gtnix.aguiabranca.presentation.screens.ranking.RankingViewModel
import com.gtnix.aguiabranca.domain.model.ConquistaTier
import com.gtnix.aguiabranca.domain.model.Ideia
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.pluralStringResource
import java.util.Calendar

@Composable
fun HomeContent(
    viewModel: HomeViewModel,
    perfil: String,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToOrientacaoDetalhe: (String) -> Unit = {},
    onNavigateToLeaderDashboard: () -> Unit = {},
    onNavigateToNovaIdeia: () -> Unit = {},
    onNavigateToIdeias: () -> Unit = {},
    onNavigateToIdeiaDetalhe: (String) -> Unit = {},
    onNavigateToProjetos: () -> Unit = {},
    onNavigateToRanking: () -> Unit = {},
    onNavigateToNovaOrientacao: () -> Unit = {},
    onNavigateToNotificacoes: () -> Unit = {},
    onNavigateToPerfilTab: () -> Unit = {},
    onNavigateToLoja: () -> Unit = {},
    onNavigateToCarteira: () -> Unit = {},
    onNavigateToCatalogoRecompensas: () -> Unit = {},
    onNavigateToNovoProjeto: (String?) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(perfil) {
        viewModel.carregarDados(perfil)
    }

    // A Home fica no back stack enquanto a Loja ou a Carteira estão abertas.
    // ON_RESUME recarrega XP e Asas ao voltar, inclusive depois de um resgate.
    LifecycleResumeEffect(Unit) {
        viewModel.atualizarGamificacao()
        onPauseOrDispose { }
    }

    HomeBodyContent(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onNavigateToOrientacoes = onNavigateToOrientacoes,
        onNavigateToOrientacaoDetalhe = onNavigateToOrientacaoDetalhe,
        onNavigateToLeaderDashboard = onNavigateToLeaderDashboard,
        onNavigateToNovaIdeia = onNavigateToNovaIdeia,
        onNavigateToIdeias = onNavigateToIdeias,
        onNavigateToIdeiaDetalhe = onNavigateToIdeiaDetalhe,
        onNavigateToProjetos = onNavigateToProjetos,
        onNavigateToRanking = onNavigateToRanking,
        onNavigateToNovaOrientacao = onNavigateToNovaOrientacao,
        onNavigateToNotificacoes = onNavigateToNotificacoes,
        onNavigateToPerfilTab = onNavigateToPerfilTab,
        onNavigateToLoja = onNavigateToLoja,
        onNavigateToCarteira = onNavigateToCarteira,
        onNavigateToCatalogoRecompensas = onNavigateToCatalogoRecompensas,
        onNavigateToNovoProjeto = onNavigateToNovoProjeto
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeBodyContent(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToOrientacaoDetalhe: (String) -> Unit = {},
    onNavigateToLeaderDashboard: () -> Unit = {},
    onNavigateToNovaIdeia: () -> Unit = {},
    onNavigateToIdeias: () -> Unit = {},
    onNavigateToIdeiaDetalhe: (String) -> Unit = {},
    onNavigateToProjetos: () -> Unit = {},
    onNavigateToRanking: () -> Unit = {},
    onNavigateToNovaOrientacao: () -> Unit = {},
    onNavigateToNotificacoes: () -> Unit = {},
    onNavigateToPerfilTab: () -> Unit = {},
    onNavigateToLoja: () -> Unit = {},
    onNavigateToCarteira: () -> Unit = {},
    onNavigateToCatalogoRecompensas: () -> Unit = {},
    onNavigateToNovoProjeto: (String?) -> Unit = {}
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
                    onNavigateToProjetos = onNavigateToProjetos,
                    onNavigateToIdeias = onNavigateToIdeias,
                    onNavigateToIdeiaDetalhe = onNavigateToIdeiaDetalhe,
                    onNavigateToLeaderDashboard = onNavigateToLeaderDashboard,
                    onNavigateToNovaIdeia = onNavigateToNovaIdeia,
                    onNavigateToRanking = onNavigateToRanking,
                    onNavigateToNovaOrientacao = onNavigateToNovaOrientacao,
                    onNavigateToNotificacoes = onNavigateToNotificacoes,
                    onNavigateToPerfilTab = onNavigateToPerfilTab,
                    onNavigateToLoja = onNavigateToLoja,
                    onNavigateToCarteira = onNavigateToCarteira,
                    onNavigateToCatalogoRecompensas = onNavigateToCatalogoRecompensas,
                    onNavigateToNovoProjeto = onNavigateToNovoProjeto
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
    onNavigateToProjetos: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onNavigateToIdeiaDetalhe: (String) -> Unit,
    onNavigateToLeaderDashboard: () -> Unit = {},
    onNavigateToNovaIdeia: () -> Unit = {},
    onNavigateToRanking: () -> Unit = {},
    onNavigateToNovaOrientacao: () -> Unit = {},
    onNavigateToNotificacoes: () -> Unit = {},
    onNavigateToPerfilTab: () -> Unit = {},
    onNavigateToLoja: () -> Unit = {},
    onNavigateToCarteira: () -> Unit = {},
    onNavigateToCatalogoRecompensas: () -> Unit = {},
    onNavigateToNovoProjeto: (String?) -> Unit = {}
) {
    val spacing = MaterialTheme.spacing
    val navigationBarBottom = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = NavBarDimensions.ContentBottomPadding + navigationBarBottom
        )
    ) {
        item {
            PremiumTopBar(
                nomeUsuario = uiState.nomeUsuario,
                onNotificationsClick = onNavigateToNotificacoes,
                onProfileClick = onNavigateToPerfilTab,
                mostrarAvatar = uiState.perfil != PerfilUsuario.OPERADOR,
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
            GreetingSection(
                nome = uiState.nomeUsuario,
                modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal, vertical = 8.dp)
            )
        }

        uiState.errorMessageRes?.let { errorRes ->
            item {
                Text(
                    text = stringResource(errorRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        if (uiState.perfil == PerfilUsuario.GESTOR) {
            item(key = "fila") {
                FilaAvaliacaoCard(
                    pendentes = uiState.ideiasPendentes,
                    fila = uiState.filaPendente,
                    diasMaisAntiga = uiState.diasMaisAntigaPendente,
                    onIdeiaClick = onNavigateToIdeiaDetalhe,
                    onAvaliarClick = onNavigateToIdeias,
                    modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                )
            }
            item {
                Spacer(modifier = Modifier.height(HomeSectionGap))
            }

            if (uiState.aprovadasSemProjeto.isNotEmpty() || uiState.projetosAtrasados > 0) {
                item(key = "proximos-passos") {
                    ProximosPassosSection(
                        aprovadasSemProjeto = uiState.aprovadasSemProjeto,
                        projetosAtrasados = uiState.projetosAtrasados,
                        onCriarProjeto = { ideia ->
                            if (uiState.aprovadasSemProjeto.size == 1) {
                                onNavigateToNovoProjeto(ideia.id)
                            } else {
                                onNavigateToProjetos()
                            }
                        },
                        onAbrirProjetos = onNavigateToProjetos,
                        modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(HomeSectionGap))
                }
            }

            item(key = "pipeline") {
                PipelineSection(
                    totalIdeias = uiState.totalIdeias,
                    ideiasAprovadas = uiState.aprovadasSemProjeto.size,
                    ideiasEmProjeto = uiState.ideiasEmProjeto,
                    taxaConversao = uiState.taxaConversao,
                    onAbrirProjetos = onNavigateToProjetos,
                    modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                )
            }
            item {
                Spacer(modifier = Modifier.height(HomeSectionGap))
            }

            uiState.pontuacao?.let { pontuacao ->
                item(key = "temporada") {
                    TemporadaCard(
                        pontuacao = pontuacao,
                        posicao = uiState.posicaoRanking,
                        divisao = uiState.divisaoRanking,
                        onAbrirRanking = onNavigateToRanking,
                        onAbrirLoja = onNavigateToLoja,
                        onAbrirCarteira = onNavigateToCarteira,
                        modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(HomeSectionGap))
                }
            }

            if (uiState.orientacoes.isNotEmpty()) {
                item(key = "orientacoes") {
                    HomeOrientacoesSection(
                        orientacoes = uiState.orientacoes,
                        onSeeAllClick = onNavigateToOrientacoes,
                        onOrientacaoClick = onNavigateToOrientacaoDetalhe,
                        sectionActionLabel = stringResource(R.string.orientacao_ver_todas),
                        ideiasPorOrientacao = uiState.ideiasPorOrientacao
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(HomeSectionGap))
                }
            }
        }

        if (uiState.perfil == PerfilUsuario.OPERADOR) {
            item(key = "hero-operador") {
                InspirationHeroCard(
                    kicker = stringResource(R.string.hero_kicker_operador),
                    headline = stringResource(R.string.hero_headline_operador),
                    subheadline = stringResource(R.string.hero_subheadline_operador),
                    ctaText = stringResource(R.string.hero_cta_operador),
                    comingSoonLabel = stringResource(R.string.hero_cta_voz_em_breve),
                    ctaContentDescription = stringResource(R.string.cd_hero_cta_operador),
                    onCtaClick = onNavigateToNovaIdeia,
                    modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                )
            }
            item {
                Spacer(modifier = Modifier.height(HomeSectionGap))
            }

            if (uiState.orientacoes.isNotEmpty()) {
                item(key = "orientacoes") {
                    HomeOrientacoesSection(
                        orientacoes = uiState.orientacoes,
                        onSeeAllClick = onNavigateToOrientacoes,
                        onOrientacaoClick = onNavigateToOrientacaoDetalhe,
                        sectionActionLabel = stringResource(R.string.orientacao_ver_todas),
                        mostrarContagemIdeias = false
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(HomeSectionGap))
                }
            }

            if (uiState.minhasIdeias.isNotEmpty()) {
                item(key = "minhas-ideias") {
                    MinhasIdeiasSection(
                        ideias = uiState.minhasIdeias.sortedByDescending { it.dataCriacao }.take(3),
                        enviadas = uiState.totalIdeias,
                        aprovadas = uiState.ideiasAprovadas,
                        emAvaliacao = uiState.ideiasPendentesAvaliacao,
                        onSeeAllClick = onNavigateToIdeias,
                        onIdeiaClick = onNavigateToIdeiaDetalhe,
                        modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(HomeSectionGap))
                }
            }

            uiState.ideiasConvertidasRecentes.firstOrNull()?.let { convertida ->
                item(key = "convertida") {
                    IdeiaConvertidaCard(
                        convertida = convertida,
                        onClick = { onNavigateToIdeiaDetalhe(convertida.ideia.id) },
                        modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            uiState.pontuacao?.let { pontuacao ->
                item(key = "temporada") {
                    TemporadaCard(
                        pontuacao = pontuacao,
                        posicao = uiState.posicaoRanking,
                        divisao = uiState.divisaoRanking,
                        onAbrirRanking = onNavigateToRanking,
                        onAbrirLoja = onNavigateToLoja,
                        onAbrirCarteira = onNavigateToCarteira,
                        modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(HomeSectionGap))
                }
            }
        }

        if (uiState.perfil == PerfilUsuario.LIDER) {
            item {
                LiderPortfolioStage(
                    snapshot = LiderPortfolioUi(
                        roi = uiState.roiConsolidado,
                        lucroAnualizado = uiState.lucroAnualizado,
                        investimentoTotal = uiState.investimentoTotal,
                        retornoMensal = uiState.retornoTotal,
                        totalIdeias = uiState.totalIdeias,
                        ideiasAprovadas = uiState.ideiasAprovadas,
                        ideiasEmProjeto = uiState.ideiasEmProjeto,
                        projetosAtrasados = uiState.projetosAtrasados
                    ),
                    onVerIdeiasClick = onNavigateToIdeias,
                    onVerProjetosClick = onNavigateToProjetos,
                    onVerAnaliseClick = onNavigateToLeaderDashboard,
                    modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                )
            }

            item {
                Spacer(modifier = Modifier.height(HomeSectionGap))
            }

            item {
                HomeOrientacoesSection(
                    orientacoes = uiState.orientacoes,
                    onSeeAllClick = onNavigateToOrientacoes,
                    onOrientacaoClick = onNavigateToOrientacaoDetalhe,
                    sectionActionLabel = stringResource(R.string.orientacao_ver_todas),
                    sectionTitle = stringResource(R.string.home_section_orientacoes),
                    ideiasPorOrientacao = uiState.ideiasPorOrientacao,
                    atrasadosPorOrientacao = uiState.atrasadosPorOrientacao
                )
            }

            item {
                Spacer(modifier = Modifier.height(HomeSectionGap))
            }

            item {
                LiderLojaRow(
                    catalogo = uiState.catalogoLoja,
                    onClick = onNavigateToCatalogoRecompensas,
                    modifier = Modifier.padding(horizontal = ScreenPadding.Horizontal)
                )
            }

            item {
                Spacer(modifier = Modifier.height(HomeSectionGap))
            }
        }
    }
}

private data class LiderNivelChipState(
    val nivelLabel: String,
    val posicao: Int?
)

@Composable
private fun PremiumTopBar(
    nomeUsuario: String,
    onNotificationsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    mostrarAvatar: Boolean = true,
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
            .padding(start = ScreenPadding.Horizontal, top = 8.dp, end = 12.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        InovagabBrandLogo(modifier = Modifier.width(120.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (liderNivelChip != null) {
                Spacer(modifier = Modifier.width(12.dp))
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

            if (mostrarAvatar) {
                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
                        .clickable(onClick = onProfileClick)
                        .semantics { contentDescription = cdAvatar },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (LocalDarkTheme.current) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initial,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = if (LocalDarkTheme.current) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onPrimary
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LiderNivelChip(
    state: LiderNivelChipState,
    onClick: () -> Unit
) {
    val isDarkTheme = LocalDarkTheme.current
    val background = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceContainerHighest
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    }
    val color = if (isDarkTheme) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.primary
    }
    val contentDescription = if (state.posicao != null) {
        stringResource(
            R.string.home_lider_chip_cd_com_ranking,
            state.nivelLabel,
            state.posicao
        )
    } else {
        stringResource(R.string.home_lider_chip_cd_sem_ranking, state.nivelLabel)
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = contentDescription }
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = if (state.posicao != null) 8.dp else 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        state.posicao?.let { posicao ->
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(R.string.home_lider_chip_ranking, posicao),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = color,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun GreetingSection(
    nome: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "${getGreetingByTime()}, $nome",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        letterSpacing = (-0.5).sp,
        modifier = modifier
    )
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
private fun IdeiaConvertidaCard(
    convertida: IdeiaConvertidaRecente,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val roiColor = if (convertida.roi >= 0) SuccessGreen else MaterialTheme.colorScheme.error

    InovagabFlatCard(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { role = Role.Button },
        onClick = onClick,
        contentPadding = PaddingValues(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SuccessGreen.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_ideia_convertida_titulo),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        R.string.home_ideia_convertida_subtitulo,
                        convertida.ideia.titulo,
                        convertida.projeto.nome
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.home_ideia_convertida_roi,
                        formatPercent(convertida.roi)
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = roiColor
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
}

@Composable
private fun TemporadaCard(
    pontuacao: Pontuacao,
    posicao: Int?,
    divisao: String?,
    onAbrirRanking: () -> Unit,
    onAbrirLoja: () -> Unit,
    onAbrirCarteira: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = LocalDarkTheme.current
    val gab = LocalInovagabColors.current
    val ouro = tierAccent(ConquistaTier.OURO, isDarkTheme)
    val proximoNivel = NivelUsuario.proximoNivel(pontuacao.nivel)
    val naZona = posicao != null && posicao <= RankingViewModel.ZONA_PREMIO

    InovagabFlatCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAbrirRanking)
                    .padding(16.dp)
            ) {
                pontuacao.temporada?.let { temporada ->
                    Text(
                        text = stringResource(
                            R.string.ranking_temporada,
                            temporada.label,
                            temporada.diasRestantes.toInt()
                        ).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(levelBrush(pontuacao.nivel)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = pontuacao.nivel.nivelIcon(),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (posicao != null && divisao != null) {
                                stringResource(R.string.ranking_status_posicao, posicao, divisao)
                            } else {
                                pontuacao.nivel.label
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(
                                R.string.home_temporada_nivel_xp,
                                pontuacao.nivel.label,
                                pontuacao.total
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (naZona) {
                        InovagabPill(
                            text = stringResource(R.string.home_temporada_zona),
                            color = ouro
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(modifier = Modifier.padding(start = TemporadaTextoInicio)) {
                    GradientProgressBar(
                        progress = pontuacao.progressoProximoNivel,
                        modifier = Modifier.fillMaxWidth(),
                        height = 6.dp,
                        gradientColors = levelGradientColors(pontuacao.nivel)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (proximoNivel != null) {
                            stringResource(
                                R.string.ranking_pts_proximo_nivel,
                                pontuacao.pontosParaProximoNivel,
                                proximoNivel.label
                            )
                        } else {
                            stringResource(R.string.perfil_nivel_maximo)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = gab.cardBorder
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.width(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AsasIcons.Wings,
                        contentDescription = null,
                        tint = ouro,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.economia_asas, pontuacao.asas),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = onAbrirLoja,
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.economia_ver_loja),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                TextButton(
                    onClick = onAbrirCarteira,
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.economia_ver_carteira),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun MinhasIdeiasSection(
    ideias: List<Ideia>,
    enviadas: Int,
    aprovadas: Int,
    emAvaliacao: Int,
    onSeeAllClick: () -> Unit,
    onIdeiaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val gab = LocalInovagabColors.current
    val resumo = listOfNotNull(
        pluralStringResource(R.plurals.home_ideias_resumo_enviadas, enviadas, enviadas),
        aprovadas.takeIf { it > 0 }?.let {
            pluralStringResource(R.plurals.home_ideias_resumo_aprovadas, it, it)
        },
        emAvaliacao.takeIf { it > 0 }?.let {
            stringResource(R.string.home_ideias_resumo_em_avaliacao, it)
        }
    ).joinToString("  ·  ")

    Column(modifier = modifier) {
        SectionHeader(
            title = stringResource(R.string.home_section_minhas_ideias),
            actionLabel = stringResource(R.string.home_activity_see_all),
            onActionClick = onSeeAllClick
        )
        Text(
            text = resumo,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        InovagabFlatCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(0.dp)
        ) {
            Column {
                ideias.forEachIndexed { index, ideia ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onIdeiaClick(ideia.id) }
                            .padding(start = 16.dp, end = 12.dp, top = 14.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = ideia.titulo,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(ideiaStatusColor(ideia.status))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = ideiaStatusTexto(ideia.status),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (index < ideias.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = gab.cardBorder
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeSkeletonScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(ScreenPadding.Horizontal),
        verticalArrangement = Arrangement.spacedBy(ScreenPadding.SectionSpacing)
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
private fun HomeOrientacoesSection(
    orientacoes: List<OrientacaoEstrategica>,
    onSeeAllClick: () -> Unit,
    onOrientacaoClick: (String) -> Unit,
    sectionActionLabel: String,
    sectionTitle: String? = null,
    ideiasPorOrientacao: Map<String, Int> = emptyMap(),
    atrasadosPorOrientacao: Map<String, Int> = emptyMap(),
    mostrarContagemIdeias: Boolean = true,
    modifier: Modifier = Modifier
) {
    OrientacoesCarousel(
        title = sectionTitle ?: stringResource(R.string.home_section_orientacoes),
        actionLabel = sectionActionLabel,
        orientacoes = orientacoes,
        ideiasPorOrientacao = ideiasPorOrientacao,
        atrasadosPorOrientacao = atrasadosPorOrientacao,
        mostrarContagemIdeias = mostrarContagemIdeias,
        onSeeAllClick = onSeeAllClick,
        onOrientacaoClick = onOrientacaoClick,
        modifier = modifier
    )
}

/**
 * Carrossel de orientações do líder. Ocupa a largura da tela (sem padding do pai) para que o
 * próximo card apareça cortado na borda física, e não na margem de 20dp.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OrientacoesCarousel(
    title: String,
    actionLabel: String,
    orientacoes: List<OrientacaoEstrategica>,
    ideiasPorOrientacao: Map<String, Int>,
    atrasadosPorOrientacao: Map<String, Int>,
    mostrarContagemIdeias: Boolean,
    onSeeAllClick: () -> Unit,
    onOrientacaoClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val inset = ScreenPadding.Horizontal
    val gap = 12.dp
    val peek = 28.dp
    val listState = rememberLazyListState()
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val cardWidth = if (orientacoes.size <= 1) {
            maxWidth - inset * 2
        } else {
            maxWidth - inset - gap - peek
        }
        Column {
            SectionHeader(
                title = title,
                actionLabel = actionLabel,
                onActionClick = onSeeAllClick,
                modifier = Modifier.padding(horizontal = inset)
            )
            if (orientacoes.isEmpty()) {
                Text(
                    text = stringResource(R.string.home_orientacoes_vazio),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = inset)
                )
            } else {
                LazyRow(
                    state = listState,
                    flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
                    horizontalArrangement = Arrangement.spacedBy(gap),
                    contentPadding = PaddingValues(horizontal = inset)
                ) {
                    items(
                        items = orientacoes,
                        key = { it.id }
                    ) { orientacao ->
                        OrientacaoCarouselCard(
                            orientacao = orientacao,
                            ideias = ideiasPorOrientacao[orientacao.id] ?: 0,
                            atrasados = atrasadosPorOrientacao[orientacao.id] ?: 0,
                            mostrarContagemIdeias = mostrarContagemIdeias,
                            onClick = { onOrientacaoClick(orientacao.id) },
                            modifier = Modifier.width(cardWidth)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrientacaoCarouselCard(
    orientacao: OrientacaoEstrategica,
    ideias: Int,
    atrasados: Int,
    mostrarContagemIdeias: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rodape = orientacaoRodape(
        orientacao = orientacao,
        ideias = ideias.takeIf { mostrarContagemIdeias },
        incluirPrazo = atrasados == 0
    ).ifBlank { orientacaoCategoriaLabel(orientacao.categoria) }
    InovagabFlatCard(
        modifier = modifier
            .height(OrientacaoCarouselHeight)
            .semantics(mergeDescendants = true) { role = Role.Button },
        onClick = onClick,
        contentPadding = PaddingValues(CardDimensions.ContentPadding)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = orientacaoDivisaoLabel(orientacao.divisao),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (LocalDarkTheme.current) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                OrientacaoCarouselPrioridade(prioridade = orientacao.prioridade)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = orientacao.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = rodape,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (atrasados > 0) {
                    Text(
                        text = "  ·  ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (atrasados == 1) {
                            stringResource(R.string.home_orientacao_atrasado_single)
                        } else {
                            stringResource(R.string.home_orientacao_atrasados_plural, atrasados)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

private val OrientacaoCarouselHeight = 128.dp
private val HomeSectionGap = 20.dp

/** Início do texto ao lado do círculo de 40dp (40 + 12 de espaço). */
private val TemporadaTextoInicio = 52.dp

/** Só P1 e P2 aparecem: prioridades menores não pedem atenção do líder. */
@Composable
private fun OrientacaoCarouselPrioridade(prioridade: Int) {
    val (label, color) = when {
        prioridade <= 1 -> stringResource(R.string.home_orientacao_prioridade_critica) to MaterialTheme.colorScheme.error
        prioridade == 2 -> stringResource(R.string.home_orientacao_prioridade_alta) to WarningAmber
        else -> return
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun orientacaoRodape(
    orientacao: OrientacaoEstrategica,
    ideias: Int?,
    incluirPrazo: Boolean
): String {
    val ideiasTexto = when (ideias) {
        null -> null
        0 -> stringResource(R.string.home_orientacao_ideias_zero)
        1 -> stringResource(R.string.home_orientacao_ideias_single)
        else -> stringResource(R.string.home_orientacao_ideias_plural, ideias)
    }
    if (!incluirPrazo) return ideiasTexto.orEmpty()
    val campanha = orientacao.campanhaPatrocinada?.takeIf { it.ativa && !it.encerrada }
    val prazo = when {
        campanha != null -> stringResource(
            R.string.home_orientacao_campanha_ate,
            orientacaoDataCurta(campanha.fim)
        )
        orientacao.dataExpiracao != null -> {
            val expiracao = orientacao.dataExpiracao
            if (expiracao < System.currentTimeMillis()) {
                stringResource(R.string.home_orientacao_expirada)
            } else {
                stringResource(
                    R.string.home_orientacao_ate,
                    orientacaoDataCurta(
                        java.time.Instant.ofEpochMilli(expiracao)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                    )
                )
            }
        }
        else -> null
    }
    return listOfNotNull(ideiasTexto, prazo).joinToString("  ·  ")
}

private fun orientacaoDataCurta(data: java.time.LocalDate): String =
    data.format(java.time.format.DateTimeFormatter.ofPattern("d MMM", java.util.Locale("pt", "BR")))
        .replace(".", "")

@Composable
private fun LiderLojaRow(
    catalogo: CatalogoHomeUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val vazio = catalogo.premiosAtivos == 0
    val atencao = catalogo.semEstoque > 0
    val unidade = if (catalogo.premiosAtivos == 1) {
        stringResource(R.string.home_lider_loja_unidade_single)
    } else {
        stringResource(R.string.home_lider_loja_unidade_plural)
    }

    Column(modifier = modifier) {
        SectionHeader(title = stringResource(R.string.home_lider_loja_titulo))
        InovagabFlatCard(
            modifier = Modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) { role = Role.Button },
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (vazio) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = stringResource(R.string.home_lider_loja_vazio),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = catalogo.premiosAtivos.toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = unidade,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (atencao) {
                            Text(
                                text = stringResource(
                                    R.string.home_lider_loja_sem_estoque,
                                    catalogo.semEstoque
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                                maxLines = 1
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = stringResource(R.string.home_lider_loja_cta),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenOperadorPreview() {
    InovagabTheme {
        HomeBodyContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.OPERADOR,
                nomeUsuario = "Pedro"
            ),
            onRefresh = {},
            onNavigateToOrientacoes = {},
            onNavigateToNovaIdeia = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenGestorPreview() {
    val agora = System.currentTimeMillis()
    val dia = 24L * 60 * 60 * 1000
    InovagabTheme {
        HomeBodyContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.GESTOR,
                nomeUsuario = "Ana",
                totalIdeias = 7,
                ideiasAprovadas = 3,
                ideiasEmProjeto = 2,
                totalProjetos = 4,
                ideiasPendentesAvaliacao = 4,
                diasMaisAntigaPendente = 9,
                filaPendente = listOf(
                    previewIdeia("1", "Check-in digital no embarque", "Pedro Santos", agora - 9 * dia),
                    previewIdeia("2", "Rastreamento de frota em tempo real", "Joana Lima", agora - 3 * dia),
                    previewIdeia("3", "Roteirização sustentável", "Rafael Souza", agora - dia)
                ),
                aprovadasSemProjeto = listOf(
                    previewIdeia("4", "Manutenção preditiva", "Carlos Silva", agora - 12 * dia)
                ),
                projetosAtrasados = 1
            ),
            onRefresh = {},
            onNavigateToOrientacoes = {}
        )
    }
}

private fun previewIdeia(id: String, titulo: String, autor: String, criacao: Long) = Ideia(
    id = id,
    titulo = titulo,
    descricao = "",
    tipo = com.gtnix.aguiabranca.domain.model.TipoIdeia.IDEIA,
    area = com.gtnix.aguiabranca.domain.model.AreaAtuacao.OPERACOES,
    autorId = id,
    autorNome = autor,
    dataCriacao = criacao
)

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenLiderPreview() {
    InovagabTheme {
        HomeBodyContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.LIDER,
                nomeUsuario = "Marcos",
                totalIdeias = 7,
                ideiasAprovadas = 0,
                ideiasEmProjeto = 3,
                totalProjetos = 4,
                roiConsolidado = 25.6,
                lucroAnualizado = 82_000.0,
                investimentoTotal = 320_000.0,
                retornoTotal = 33_500.0,
                projetosAtrasados = 0,
                tempoMedioAprovacaoDias = 2,
                ideiasPendentesAvaliacao = 4,
                weeklyRecap = WeeklyRecap(
                    novasIdeias = 4,
                    avaliacoes = 0,
                    projetosAtualizados = 2
                ),
                orientacoes = listOf(
                    OrientacaoEstrategica(
                        id = "1",
                        titulo = "Eficiência operacional e experiência do passageiro",
                        descricao = "Priorizar iniciativas que reduzam custos e melhorem o NPS.",
                        categoria = CategoriaOrientacao.EFICIENCIA_OPERACIONAL,
                        prioridade = 3,
                        ativa = true,
                        criadoPor = "demo-lider"
                    )
                ),
                pontuacao = Pontuacao(
                    total = 15,
                    nivel = NivelUsuario.INICIANTE,
                    progressoProximoNivel = 0.3f,
                    pontosParaProximoNivel = 35,
                    temporada = TemporadaInfo(
                        id = "t4",
                        label = "4º trimestre",
                        diasRestantes = 73
                    )
                ),
                catalogoLoja = CatalogoHomeUi(
                    carregado = true,
                    premiosAtivos = 8,
                    semEstoque = 0
                )
            ),
            onRefresh = {},
            onNavigateToIdeias = {},
            onNavigateToProjetos = {},
            onNavigateToOrientacoes = {},
            onNavigateToNovaIdeia = {}
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
