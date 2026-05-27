package com.gtnix.aguiabranca.presentation.screens.ranking

import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import com.gtnix.aguiabranca.presentation.components.GradientProgressBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.RankingEntrada
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.CelebrationOverlay
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.InspirationCard
import com.gtnix.aguiabranca.presentation.components.PremiumFilterChip
import com.gtnix.aguiabranca.presentation.components.PremiumAvatar
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.components.SkeletonListPlaceholder
import com.gtnix.aguiabranca.presentation.util.nivelIconAndColor
import com.gtnix.aguiabranca.presentation.theme.LevelEmAscensao
import com.gtnix.aguiabranca.presentation.theme.LevelEngajado
import com.gtnix.aguiabranca.presentation.theme.LevelIniciante
import com.gtnix.aguiabranca.presentation.theme.LevelTransformador
import com.gtnix.aguiabranca.presentation.theme.LevelVisionario
import com.gtnix.aguiabranca.presentation.theme.levelGradientColors
import com.gtnix.aguiabranca.presentation.theme.MedalBronze
import com.gtnix.aguiabranca.presentation.theme.MedalBronzeGlow
import com.gtnix.aguiabranca.presentation.theme.MedalGold
import com.gtnix.aguiabranca.presentation.theme.MedalGoldGlow
import com.gtnix.aguiabranca.presentation.theme.MedalSilver
import com.gtnix.aguiabranca.presentation.theme.MedalSilverGlow
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding

@Composable
fun RankingScreen(
    viewModel: RankingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RankingScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onTabSelected = viewModel::selecionarTab,
        onDivisaoFilterSelected = viewModel::selecionarDivisaoFilter,
        onRefresh = viewModel::refresh,
        onDismissPositionCelebration = viewModel::dismissPositionCelebration,
        onClearHighlightUserCard = viewModel::clearHighlightUserCard
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RankingScreenContent(
    uiState: RankingUiState,
    onNavigateBack: () -> Unit,
    onTabSelected: (PerfilUsuario) -> Unit = {},
    onDivisaoFilterSelected: (DivisaoFilter) -> Unit = {},
    onRefresh: () -> Unit = {},
    onDismissPositionCelebration: () -> Unit = {},
    onClearHighlightUserCard: () -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    val pullRefreshState = rememberPullToRefreshState()

    val handleRefresh = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onRefresh()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AguiaTopBar(
                    title = stringResource(R.string.ranking_titulo),
                    onBackClick = onNavigateBack
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                DivisaoFilterRow(
                    selectedFilter = uiState.selectedDivisaoFilter,
                    onFilterSelected = onDivisaoFilterSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ScreenPadding.Horizontal, vertical = 8.dp)
                )

                if (uiState.tabsDisponiveis.size > 1) {
                    RankingTabSelector(
                        tabs = uiState.tabsDisponiveis,
                        selectedTab = uiState.tabSelecionada,
                        onTabSelected = onTabSelected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ScreenPadding.Horizontal, vertical = 12.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = handleRefresh,
                    state = pullRefreshState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when {
                        uiState.isLoading && !uiState.isRefreshing -> {
                            SkeletonListPlaceholder(
                                itemCount = 6,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        uiState.errorMessageRes != null -> {
                            RankingErrorState(messageRes = uiState.errorMessageRes)
                        }

                        uiState.topRanking.isEmpty() -> {
                            RankingEmptyState()
                        }

                        else -> {
                            RankingListContent(
                                uiState = uiState,
                                onClearHighlightUserCard = onClearHighlightUserCard
                            )
                        }
                    }
                }
            }
        }

        CelebrationOverlay(
            message = uiState.positionCelebrationMessage.orEmpty(),
            visible = uiState.showPositionCelebration &&
                !uiState.positionCelebrationMessage.isNullOrBlank(),
            onDismiss = onDismissPositionCelebration
        )
    }
}

@Composable
private fun RankingListContent(
    uiState: RankingUiState,
    onClearHighlightUserCard: () -> Unit = {}
) {
    val topThree = uiState.topRanking.take(3)
    val restOfList = uiState.topRanking.drop(3)
    val isDarkTheme = isSystemInDarkTheme()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = ScreenPadding.Horizontal,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = tabSubtitle(uiState.tabSelecionada),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (uiState.isAnalysisMode && uiState.teamInsight != null) {
            item {
                TeamInsightCard(
                    tabSelecionada = uiState.tabSelecionada,
                    insight = uiState.teamInsight,
                    analysisHeaderText = uiState.analysisHeaderText
                )
            }
        }

        if (topThree.isNotEmpty()) {
            item {
                RankingPodium(
                    topThree = topThree,
                    usuarioLogadoId = uiState.usuarioLogadoId,
                    isDarkTheme = isDarkTheme
                )
            }
        }

        if (!uiState.isAnalysisMode &&
            uiState.posicaoUsuarioLogado != null &&
            uiState.entradaUsuarioLogado != null
        ) {
            item {
                CurrentUserHighlightCard(
                    posicao = uiState.posicaoUsuarioLogado,
                    entrada = uiState.entradaUsuarioLogado,
                    ranking = uiState.topRanking,
                    isDarkTheme = isDarkTheme,
                    highlightPulse = uiState.highlightUserCard,
                    onHighlightPulseComplete = onClearHighlightUserCard
                )
            }

            if (uiState.conquistasUsuario.any { it.desbloqueada }) {
                item {
                    VerConquistasNoPerfilLink()
                }
            }

            item {
                WeeklyChallengeCard(
                    perfil = uiState.perfilUsuarioLogado,
                    stats = uiState.gamificationStats,
                    isDarkTheme = isDarkTheme
                )
            }
        }

        if (restOfList.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.ranking_demais_participantes),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            itemsIndexed(
                items = restOfList,
                key = { _, entry -> entry.usuarioId }
            ) { index, entrada ->
                val posicao = index + 4
                RankingListItemCard(
                    posicao = posicao,
                    entrada = entrada,
                    isUsuarioLogado = entrada.usuarioId == uiState.usuarioLogadoId,
                    isDarkTheme = isDarkTheme,
                    tabSelecionada = uiState.tabSelecionada
                )
            }
        }

        if (!uiState.isAnalysisMode &&
            uiState.posicaoUsuarioLogado != null &&
            uiState.posicaoUsuarioLogado > GetRankingUseCase.TOP_LIMIT &&
            uiState.entradaUsuarioLogado != null
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.ranking_fora_top10,
                        uiState.posicaoUsuarioLogado
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            item {
                RankingListItemCard(
                    posicao = uiState.posicaoUsuarioLogado,
                    entrada = uiState.entradaUsuarioLogado,
                    isUsuarioLogado = true,
                    isDarkTheme = isDarkTheme,
                    showFullProgress = true,
                    tabSelecionada = uiState.tabSelecionada
                )
            }
        }

        item {
            InspirationCard(perfil = uiState.perfilUsuarioLogado)
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun VerConquistasNoPerfilLink() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = stringResource(R.string.ranking_ver_conquistas_no_perfil),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun WeeklyChallengeCard(
    perfil: PerfilUsuario,
    stats: GamificationStats,
    isDarkTheme: Boolean
) {
    val (goalText, progressLabel) = weeklyChallengeContent(perfil, stats)
    val progress = if (stats.desafioSemanalMeta > 0) {
        stats.desafioSemanalProgresso.toFloat() / stats.desafioSemanalMeta
    } else {
        0f
    }
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = if (isDarkTheme) 0.6f else 1f))
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                    )
                ),
                shape = shape
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(R.string.ranking_desafio_semanal),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = goalText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            GradientProgressBar(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
                height = 6.dp,
                gradientColors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                )
            )
            Text(
                text = progressLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RankingPositionVariationIndicator(variacao: VariacaoPosicao) {
    val (icon, color) = when (variacao) {
        VariacaoPosicao.UP -> Icons.Default.ArrowUpward to Color(0xFF2E7D32)
        VariacaoPosicao.DOWN -> Icons.Default.ArrowDownward to Color(0xFFC62828)
        VariacaoPosicao.NEUTRAL -> Icons.Default.Remove to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(16.dp)
    )
}

@Composable
private fun CurrentUserHighlightCard(
    posicao: Int,
    entrada: RankingEntrada,
    ranking: List<RankingEntrada>,
    isDarkTheme: Boolean,
    highlightPulse: Boolean = false,
    onHighlightPulseComplete: () -> Unit = {}
) {
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(highlightPulse) {
        if (highlightPulse) {
            pulseScale.animateTo(
                targetValue = 1.03f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
            pulseScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
            onHighlightPulseComplete()
        }
    }

    val levelColor = nivelColor(entrada.nivel)
    val progress = calcularProgressoNivel(entrada.pontos, entrada.nivel)
    val pontosProximoNivel = calcularPontosParaProximoNivel(entrada.pontos, entrada.nivel)
    val pontosProximaPosicao = calcularPontosParaProximaPosicao(posicao, entrada, ranking)
    val proximoNivel = NivelUsuario.proximoNivel(entrada.nivel)

    val shape = RoundedCornerShape(20.dp)
    val borderBrush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = pulseScale.value
                scaleY = pulseScale.value
            }
            .clip(shape)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDarkTheme) 0.35f else 0.5f))
            .border(2.dp, borderBrush, shape)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = stringResource(R.string.ranking_voce_em_lugar, posicao),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    text = stringResource(R.string.ranking_pontos, entrada.pontos),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumAvatar(
                    nome = entrada.nome,
                    nivel = entrada.nivel,
                    size = 40.dp,
                    isInRanking = true
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entrada.nome,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    NivelBadge(nivel = entrada.nivel, compact = true)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            GradientProgressBar(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                height = 8.dp,
                gradientColors = levelGradientColors(entrada.nivel)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val motivacaoText = when {
                pontosProximaPosicao != null && posicao > 1 ->
                    stringResource(R.string.ranking_pts_proxima_posicao, pontosProximaPosicao)
                proximoNivel != null ->
                    stringResource(
                        R.string.ranking_pts_proximo_nivel,
                        pontosProximoNivel,
                        proximoNivel.label
                    )
                else -> stringResource(R.string.perfil_nivel_maximo)
            }

            Text(
                text = motivacaoText,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RankingPodium(
    topThree: List<RankingEntrada>,
    usuarioLogadoId: String?,
    isDarkTheme: Boolean
) {
    val first = topThree.getOrNull(0)
    val second = topThree.getOrNull(1)
    val third = topThree.getOrNull(2)

    val podiumShape = RoundedCornerShape(24.dp)
    val gradientColors = if (isDarkTheme) {
        listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            MaterialTheme.colorScheme.surface
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
            MaterialTheme.colorScheme.surface
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(podiumShape)
            .background(Brush.verticalGradient(gradientColors))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = podiumShape
            )
            .padding(horizontal = 8.dp, vertical = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            if (second != null) {
                PodiumPlace(
                    entrada = second,
                    posicao = 2,
                    isUsuarioLogado = second.usuarioId == usuarioLogadoId,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (first != null) {
                PodiumPlace(
                    entrada = first,
                    posicao = 1,
                    isUsuarioLogado = first.usuarioId == usuarioLogadoId,
                    modifier = Modifier.weight(1.15f)
                )
            }

            if (third != null) {
                PodiumPlace(
                    entrada = third,
                    posicao = 3,
                    isUsuarioLogado = third.usuarioId == usuarioLogadoId,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun PodiumPlace(
    entrada: RankingEntrada,
    posicao: Int,
    isUsuarioLogado: Boolean,
    modifier: Modifier = Modifier
) {
    val medalColor = posicaoMedalColor(posicao)
    val glowColor = posicaoMedalGlow(posicao)
    val avatarSize = when (posicao) {
        1 -> 64.dp
        2 -> 52.dp
        else -> 52.dp
    }
    val pedestalHeight = when (posicao) {
        1 -> 88.dp
        2 -> 64.dp
        else -> 52.dp
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            if (posicao == 1) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = MedalGold,
                    modifier = Modifier
                        .size(28.dp)
                        .offset(y = (-8).dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(if (posicao == 1) 18.dp else 0.dp))

                Box(contentAlignment = Alignment.Center) {
                    PremiumAvatar(
                        nome = entrada.nome,
                        nivel = entrada.nivel,
                        size = avatarSize,
                        isInRanking = true,
                        podiumPosition = posicao
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = entrada.nome.split(" ").firstOrNull() ?: entrada.nome,
            style = when (posicao) {
                1 -> MaterialTheme.typography.titleSmall
                else -> MaterialTheme.typography.labelLarge
            },
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Text(
            text = stringResource(R.string.ranking_pontos, entrada.pontos),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = medalColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        NivelBadge(nivel = entrada.nivel, compact = true)

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .height(pedestalHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            medalColor.copy(alpha = 0.85f),
                            glowColor
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = when (posicao) {
                    1 -> "1º"
                    2 -> "2º"
                    else -> "3º"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun RankingListItemCard(
    posicao: Int,
    entrada: RankingEntrada,
    isUsuarioLogado: Boolean,
    isDarkTheme: Boolean,
    tabSelecionada: PerfilUsuario,
    showFullProgress: Boolean = isUsuarioLogado
) {
    val levelColor = nivelColor(entrada.nivel)
    val progress = calcularProgressoNivel(entrada.pontos, entrada.nivel)
    val acoesCount = simularAcoesCount(entrada, tabSelecionada)
    val variacao = simularVariacaoPosicao(entrada.usuarioId, posicao)
    val acoesLabel = acoesCountLabel(tabSelecionada)

    val shape = RoundedCornerShape(ScreenPadding.CardCornerRadius)
    val elevation = if (isUsuarioLogado) 6.dp else if (isDarkTheme) 0.dp else 2.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isUsuarioLogado) {
                    Modifier.border(
                        2.dp,
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                            )
                        ),
                        shape
                    )
                } else Modifier
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (isUsuarioLogado) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (isDarkTheme) 0.4f else 0.55f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RankingPositionBadge(posicao = posicao)

            Spacer(modifier = Modifier.width(8.dp))

            RankingPositionVariationIndicator(variacao = variacao)

            Spacer(modifier = Modifier.width(8.dp))

            PremiumAvatar(
                nome = entrada.nome,
                nivel = entrada.nivel,
                size = 44.dp,
                isInRanking = true
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entrada.nome,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isUsuarioLogado) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                NivelBadge(nivel = entrada.nivel, compact = true)
                if (showFullProgress) {
                    Spacer(modifier = Modifier.height(8.dp))
                    GradientProgressBar(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth(),
                        height = 6.dp,
                        gradientColors = levelGradientColors(entrada.nivel)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.ranking_pontos, entrada.pontos),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isUsuarioLogado) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.secondary
                    }
                )
                Text(
                    text = stringResource(R.string.ranking_acoes_count, acoesCount, acoesLabel),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                if (isUsuarioLogado) {
                    Text(
                        text = "#$posicao",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingPositionBadge(posicao: Int) {
    val medalColor = posicaoMedalColor(posicao)

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (posicao <= 3) medalColor.copy(alpha = 0.18f)
                else MaterialTheme.colorScheme.surfaceVariant
            ),
        contentAlignment = Alignment.Center
    ) {
        if (posicao <= 3) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = medalColor,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = posicao.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NivelBadge(nivel: NivelUsuario, compact: Boolean = false) {
    val (icon, color) = nivelIconAndColor(nivel)
    val shape = RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .clip(shape)
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = if (compact) 6.dp else 8.dp, vertical = if (compact) 2.dp else 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(if (compact) 12.dp else 14.dp)
        )
        Text(
            text = nivel.label,
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun RankingTabSelector(
    tabs: List<PerfilUsuario>,
    selectedTab: PerfilUsuario,
    onTabSelected: (PerfilUsuario) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabLabels = mapOf(
        PerfilUsuario.OPERADOR to TabItem(PerfilUsuario.OPERADOR, stringResource(R.string.ranking_tab_inovadores), Icons.Default.Lightbulb),
        PerfilUsuario.GESTOR to TabItem(PerfilUsuario.GESTOR, stringResource(R.string.ranking_tab_curadores), Icons.AutoMirrored.Filled.ManageSearch),
        PerfilUsuario.LIDER to TabItem(PerfilUsuario.LIDER, stringResource(R.string.ranking_tab_estrategistas), Icons.Default.Insights)
    )
    val visibleTabs = tabs.mapNotNull { tabLabels[it] }
    val isDarkTheme = isSystemInDarkTheme()
    val containerShape = RoundedCornerShape(16.dp)

    Surface(
        modifier = modifier,
        shape = containerShape,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDarkTheme) 0.5f else 0.7f),
        tonalElevation = if (isDarkTheme) 0.dp else 1.dp
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            visibleTabs.forEach { tab ->
                val isSelected = tab.perfil == selectedTab
                val backgroundColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                    else Color.Transparent,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "tabBg"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = spring(stiffness = Spring.StiffnessMedium),
                    label = "tabContent"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(backgroundColor)
                        .clickable { onTabSelected(tab.perfil) }
                        .then(
                            if (isSelected) {
                                Modifier.shadow(4.dp, RoundedCornerShape(12.dp))
                            } else Modifier
                        )
                        .padding(vertical = 10.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = isSelected,
                        transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
                        label = "tabContentAnim"
                    ) { selected ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = contentColor,
                                modifier = Modifier.size(if (selected) 22.dp else 20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tab.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = contentColor,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DivisaoFilterRow(
    selectedFilter: DivisaoFilter,
    onFilterSelected: (DivisaoFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PremiumFilterChip(
                label = stringResource(R.string.ranking_filtro_minha_divisao),
                selected = selectedFilter == DivisaoFilter.MINHA_DIVISAO,
                onClick = { onFilterSelected(DivisaoFilter.MINHA_DIVISAO) },
                modifier = Modifier.height(48.dp)
            )
            PremiumFilterChip(
                label = stringResource(R.string.ranking_filtro_grupo_completo),
                selected = selectedFilter == DivisaoFilter.GRUPO_COMPLETO,
                onClick = { onFilterSelected(DivisaoFilter.GRUPO_COMPLETO) },
                modifier = Modifier.height(48.dp)
            )
        }
        if (selectedFilter == DivisaoFilter.MINHA_DIVISAO) {
            // Decisão #3 da seção 12 — desambiguar "Minha Divisão" como divisão
            // de negócio do usuário (LOGISTICA / PASSAGEIROS / COMERCIO).
            Text(
                text = stringResource(R.string.ranking_filtro_minha_divisao_subtitulo),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, start = 4.dp)
            )
        }
    }
}

@Composable
private fun TeamInsightCard(
    tabSelecionada: PerfilUsuario,
    insight: TeamInsight,
    analysisHeaderText: String?
) {
    val tituloRes = when (tabSelecionada) {
        PerfilUsuario.OPERADOR -> R.string.ranking_team_insight_titulo_operadores
        PerfilUsuario.GESTOR -> R.string.ranking_team_insight_titulo_gestores
        PerfilUsuario.LIDER -> R.string.ranking_team_insight_titulo_estrategistas
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Insights,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(tituloRes),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (analysisHeaderText != null) {
                Text(
                    text = analysisHeaderText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TeamInsightKpiChip(
                    label = stringResource(R.string.ranking_team_insight_media, insight.mediaPontos),
                    modifier = Modifier.weight(1f)
                )
                TeamInsightKpiChip(
                    label = stringResource(R.string.ranking_team_insight_top, insight.topContribuidorNome),
                    modifier = Modifier.weight(1f)
                )
            }

            TeamInsightKpiChip(
                label = stringResource(R.string.ranking_team_insight_total_ideias, insight.totalIdeias),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TeamInsightKpiChip(
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RankingEmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.ranking_empty),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun RankingErrorState(messageRes: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Leaderboard,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(messageRes),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun tabSubtitle(perfil: PerfilUsuario): String = when (perfil) {
    PerfilUsuario.OPERADOR -> stringResource(R.string.ranking_subtitulo_operador)
    PerfilUsuario.GESTOR -> stringResource(R.string.ranking_subtitulo_gestor)
    PerfilUsuario.LIDER -> stringResource(R.string.ranking_subtitulo_lider)
}

private data class TabItem(
    val perfil: PerfilUsuario,
    val label: String,
    val icon: ImageVector
)

private enum class VariacaoPosicao {
    UP, DOWN, NEUTRAL
}

@Composable
private fun weeklyChallengeContent(
    perfil: PerfilUsuario,
    stats: GamificationStats
): Pair<String, String> = when (perfil) {
    PerfilUsuario.OPERADOR -> stringResource(R.string.ranking_desafio_operador) to
        stringResource(
            R.string.ranking_desafio_progresso_ideias,
            stats.desafioSemanalProgresso,
            stats.desafioSemanalMeta
        )
    PerfilUsuario.GESTOR -> stringResource(R.string.ranking_desafio_gestor) to
        stringResource(
            R.string.ranking_desafio_progresso_avaliacoes,
            stats.desafioSemanalProgresso,
            stats.desafioSemanalMeta
        )
    PerfilUsuario.LIDER -> stringResource(R.string.ranking_desafio_lider) to
        stringResource(
            R.string.ranking_desafio_progresso_orientacoes,
            stats.desafioSemanalProgresso,
            stats.desafioSemanalMeta
        )
}

private fun simularAcoesCount(entrada: RankingEntrada, tab: PerfilUsuario): Int = when (tab) {
    PerfilUsuario.OPERADOR -> (entrada.pontos / 15).coerceAtLeast(1)
    PerfilUsuario.GESTOR -> (entrada.pontos / 20).coerceAtLeast(1)
    PerfilUsuario.LIDER -> (entrada.pontos / 25).coerceAtLeast(1)
}

private fun acoesCountLabel(tab: PerfilUsuario): String = when (tab) {
    PerfilUsuario.OPERADOR -> "ideias"
    PerfilUsuario.GESTOR -> "avaliações"
    PerfilUsuario.LIDER -> "orientações"
}

private fun simularVariacaoPosicao(usuarioId: String, posicao: Int): VariacaoPosicao {
    val hash = (usuarioId.hashCode() + posicao) % 3
    return when (hash) {
        0 -> VariacaoPosicao.UP
        1 -> VariacaoPosicao.DOWN
        else -> VariacaoPosicao.NEUTRAL
    }
}

private fun posicaoMedalColor(posicao: Int): Color = when (posicao) {
    1 -> MedalGold
    2 -> MedalSilver
    3 -> MedalBronze
    else -> Color.Unspecified
}

private fun posicaoMedalGlow(posicao: Int): Color = when (posicao) {
    1 -> MedalGoldGlow
    2 -> MedalSilverGlow
    3 -> MedalBronzeGlow
    else -> Color.Transparent
}

private fun nivelColor(nivel: NivelUsuario): Color = when (nivel) {
    NivelUsuario.INICIANTE -> LevelIniciante
    NivelUsuario.EM_ASCENSAO -> LevelEmAscensao
    NivelUsuario.ENGAJADO -> LevelEngajado
    NivelUsuario.VISIONARIO -> LevelVisionario
    NivelUsuario.TRANSFORMADOR -> LevelTransformador
}

private fun calcularProgressoNivel(total: Int, nivel: NivelUsuario): Float {
    if (nivel == NivelUsuario.TRANSFORMADOR) return 1f
    val range = nivel.pontoMaximo - nivel.pontoMinimo + 1
    val within = total - nivel.pontoMinimo
    return (within.toFloat() / range).coerceIn(0f, 1f)
}

private fun calcularPontosParaProximoNivel(total: Int, nivel: NivelUsuario): Int {
    if (nivel == NivelUsuario.TRANSFORMADOR) return 0
    return nivel.pontoMaximo - total + 1
}

private fun calcularPontosParaProximaPosicao(
    posicao: Int,
    entrada: RankingEntrada,
    ranking: List<RankingEntrada>
): Int? {
    if (posicao <= 1) return null
    val acima = ranking.getOrNull(posicao - 2) ?: return null
    val diff = acima.pontos - entrada.pontos
    return if (diff <= 0) null else diff + 1
}

@Preview(showBackground = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun RankingScreenPreview() {
    InovagabTheme {
        RankingScreenContent(
            uiState = RankingUiState(
                isLoading = false,
                tabSelecionada = PerfilUsuario.OPERADOR,
                topRanking = listOf(
                    RankingEntrada("1", "Maria Santos", 350, NivelUsuario.VISIONARIO, 'M', PerfilUsuario.OPERADOR, "Operador", DivisaoNegocio.LOGISTICA),
                    RankingEntrada("2", "João Silva", 120, NivelUsuario.EM_ASCENSAO, 'J', PerfilUsuario.OPERADOR, "Operador", DivisaoNegocio.LOGISTICA),
                    RankingEntrada("3", "Ana Costa", 80, NivelUsuario.EM_ASCENSAO, 'A', PerfilUsuario.OPERADOR, "Operador", DivisaoNegocio.PASSAGEIROS),
                    RankingEntrada("4", "Pedro Lima", 45, NivelUsuario.EM_ASCENSAO, 'P', PerfilUsuario.OPERADOR, "Operador", DivisaoNegocio.LOGISTICA),
                    RankingEntrada("5", "Carla Souza", 30, NivelUsuario.INICIANTE, 'C', PerfilUsuario.OPERADOR, "Operador", DivisaoNegocio.COMERCIO)
                ),
                posicaoUsuarioLogado = 2,
                entradaUsuarioLogado = RankingEntrada("2", "João Silva", 120, NivelUsuario.EM_ASCENSAO, 'J', PerfilUsuario.OPERADOR, "Operador", DivisaoNegocio.LOGISTICA),
                usuarioLogadoId = "2",
                gamificationStats = GamificationStats(
                    ideiasEnviadas = 8,
                    streakDias = 3,
                    desafioSemanalProgresso = 1,
                    desafioSemanalMeta = 2,
                    isTopThree = true
                )
            ),
            onNavigateBack = {}
        )
    }
}
