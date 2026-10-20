package com.gtnix.aguiabranca.presentation.screens.ranking

import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import android.content.res.Configuration
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.gtnix.aguiabranca.presentation.components.GradientProgressBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.IndicacoesInovaGab
import com.gtnix.aguiabranca.domain.model.RankingEntrada
import com.gtnix.aguiabranca.domain.model.EquipeRanking
import com.gtnix.aguiabranca.domain.model.Conquista
import com.gtnix.aguiabranca.domain.model.Missao
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.TemporadaInfo
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.HorizontalDivider
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.PremioInovagabDialog
import com.gtnix.aguiabranca.presentation.components.badges.BadgeGrid
import com.gtnix.aguiabranca.presentation.components.badges.ConquistaDetailSheet
import com.gtnix.aguiabranca.presentation.theme.levelBrush
import com.gtnix.aguiabranca.presentation.theme.levelGradientColors
import com.gtnix.aguiabranca.presentation.util.nivelIcon
import com.gtnix.aguiabranca.presentation.components.CelebrationOverlay
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.PremiumAvatar
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.components.SkeletonListPlaceholder
import com.gtnix.aguiabranca.presentation.theme.MedalBronze
import com.gtnix.aguiabranca.presentation.theme.MedalBronzeGlow
import com.gtnix.aguiabranca.presentation.theme.MedalGold
import com.gtnix.aguiabranca.presentation.theme.MedalGoldGlow
import com.gtnix.aguiabranca.presentation.theme.MedalSilver
import com.gtnix.aguiabranca.presentation.theme.MedalSilverGlow
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import com.gtnix.aguiabranca.presentation.theme.CardDimensions

@Composable
fun RankingScreen(
    viewModel: RankingViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RankingScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
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
                Spacer(modifier = Modifier.height(8.dp))

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

                        !uiState.temConteudo -> {
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
    val divisaoLabel = uiState.entradaUsuarioLogado?.divisao?.label ?: uiState.divisaoUsuario.label
    val isDarkTheme = LocalDarkTheme.current
    val perfil = uiState.perfilUsuarioLogado
    val isLider = perfil == PerfilUsuario.LIDER
    val missoes = uiState.painel?.missoes.orEmpty()
    val tituloPodio = when (perfil) {
        PerfilUsuario.GESTOR -> stringResource(R.string.ranking_podio_gestores, divisaoLabel)
        else -> stringResource(R.string.ranking_podio_operadores, divisaoLabel)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = ScreenPadding.Horizontal,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val mostrarStatus = uiState.posicaoUsuarioLogado != null &&
            uiState.entradaUsuarioLogado != null

        if (mostrarStatus || uiState.temporada != null) {
            item {
                RankingStatusCard(
                    posicao = if (mostrarStatus) uiState.posicaoUsuarioLogado else null,
                    entrada = if (mostrarStatus) uiState.entradaUsuarioLogado else null,
                    recorteLabel = divisaoLabel,
                    perfil = perfil,
                    temporada = uiState.temporada,
                    regraPremio = uiState.regraPremio,
                    mostrarRegraPremio = perfil == PerfilUsuario.OPERADOR,
                    xpParaZonaPremio = uiState.xpParaZonaPremio,
                    proximaPosicao = if (isLider) null else uiState.proximaPosicao,
                    isDarkTheme = isDarkTheme,
                    highlightPulse = uiState.highlightUserCard,
                    onHighlightPulseComplete = onClearHighlightUserCard
                )
            }
        }

        if (isLider) {
            if (missoes.isNotEmpty()) {
                item {
                    MissoesSemanaisCard(
                        missoes = missoes,
                        sequenciaSemanas = uiState.painel?.sequenciaSemanas ?: 0,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
            val indicacoes = uiState.indicacoes
            if (indicacoes != null && indicacoes.indicados.isNotEmpty()) {
                item { IndicacoesInovagabCard(indicacoes = indicacoes) }
            }
            if (uiState.equipes.isNotEmpty()) {
                item { EquipesRankingCard(equipes = equipesVisiveis(uiState.equipes)) }
            }
        } else {
            if (topThree.isNotEmpty()) {
                item {
                    RankingPodium(
                        titulo = tituloPodio,
                        topThree = topThree,
                        usuarioLogadoId = uiState.usuarioLogadoId,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
            if (missoes.isNotEmpty()) {
                item {
                    MissoesSemanaisCard(
                        missoes = missoes,
                        sequenciaSemanas = uiState.painel?.sequenciaSemanas ?: 0,
                        isDarkTheme = isDarkTheme
                    )
                }
            }
            if (perfil == PerfilUsuario.GESTOR && uiState.podioOperadores.isNotEmpty()) {
                item { OperacaoDivisaoCard(operadores = uiState.podioOperadores) }
            }
            if (uiState.equipes.isNotEmpty()) {
                item { EquipesRankingCard(equipes = equipesVisiveis(uiState.equipes)) }
            }
        }

        val painel = uiState.painel
        if (painel != null) {
            item { JornadaSection(painel = painel) }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
private fun RankingStatusCard(
    posicao: Int?,
    entrada: RankingEntrada?,
    recorteLabel: String,
    perfil: PerfilUsuario,
    temporada: TemporadaInfo?,
    regraPremio: String,
    mostrarRegraPremio: Boolean,
    xpParaZonaPremio: Int?,
    proximaPosicao: ProximaPosicao?,
    isDarkTheme: Boolean,
    highlightPulse: Boolean = false,
    onHighlightPulseComplete: () -> Unit = {}
) {
    val pulseScale = remember { Animatable(1f) }
    var mostrarRegra by rememberSaveable { mutableStateOf(false) }

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

    val shape = RoundedCornerShape(CardDimensions.CornerRadiusMedium)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = pulseScale.value
                scaleY = pulseScale.value
            }
            .clip(shape)
            .background(
                if (isDarkTheme) MaterialTheme.colorScheme.surfaceContainerHigh
                else MaterialTheme.colorScheme.surface
            )
            .border(1.dp, LocalInovagabColors.current.cardBorder, shape)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (posicao != null && entrada != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
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
                            text = stringResource(
                                when (perfil) {
                                    PerfilUsuario.OPERADOR -> R.string.ranking_status_posicao
                                    PerfilUsuario.GESTOR -> R.string.ranking_status_gestores
                                    PerfilUsuario.LIDER -> R.string.ranking_status_lideres
                                },
                                posicao,
                                recorteLabel
                            ),
                            style = if (perfil == PerfilUsuario.LIDER) {
                                MaterialTheme.typography.titleSmall
                            } else {
                                MaterialTheme.typography.titleMedium
                            },
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = stringResource(R.string.ranking_pontos, entrada.pontos),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (perfil == PerfilUsuario.OPERADOR) {
                    when {
                        entrada.zonaPremio -> ZonaPremioChip()
                        xpParaZonaPremio != null -> Text(
                            text = stringResource(R.string.ranking_zona_premio_fora, xpParaZonaPremio),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }

                if (perfil != PerfilUsuario.LIDER) {
                    Text(
                        text = when {
                            proximaPosicao == null -> stringResource(R.string.ranking_status_lider)
                            proximaPosicao.empatado -> stringResource(
                                R.string.ranking_status_empate,
                                proximaPosicao.nome,
                                proximaPosicao.posicao
                            )
                            else -> stringResource(
                                R.string.ranking_status_passar,
                                proximaPosicao.xpFaltando,
                                proximaPosicao.nome
                            )
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (temporada != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WorkspacePremium,
                        contentDescription = null,
                        tint = MedalGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.ranking_temporada, temporada.label, temporada.diasRestantes.toInt()),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    if (mostrarRegraPremio) {
                        IconButton(
                            onClick = { mostrarRegra = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.ranking_regra_premio_titulo),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarRegra) {
        PremioInovagabDialog(
            vagasPorDivisao = RankingViewModel.ZONA_PREMIO,
            diasRestantes = temporada?.diasRestantes?.toInt(),
            naZona = entrada?.zonaPremio == true,
            xpParaZona = xpParaZonaPremio,
            regraServidor = regraPremio.takeUnless {
                it.isBlank() || it.contains("${RankingViewModel.ZONA_PREMIO} primeiros", ignoreCase = true)
            },
            onDismiss = { mostrarRegra = false }
        )
    }
}

@Composable
private fun JornadaSection(painel: Pontuacao) {
    var conquistaSelecionada by remember { mutableStateOf<Conquista?>(null) }
    var mostrarTodas by rememberSaveable { mutableStateOf(false) }
    val proximoNivel = NivelUsuario.proximoNivel(painel.nivel)
    val corNivel = levelGradientColors(painel.nivel).first()
    val conquistas = painel.conquistas
    val desbloqueadas = conquistas.count { it.desbloqueada }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.ranking_jornada_titulo),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(levelBrush(painel.nivel)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = painel.nivel.nivelIcon(),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.ranking_jornada_nivel, painel.nivel.label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = corNivel
                    )
                    Text(
                        text = if (proximoNivel != null) {
                            stringResource(
                                R.string.ranking_pts_proximo_nivel,
                                painel.pontosParaProximoNivel,
                                proximoNivel.label
                            )
                        } else {
                            stringResource(R.string.perfil_nivel_maximo)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = stringResource(R.string.ranking_jornada_xp_total, painel.total),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            GradientProgressBar(
                progress = if (proximoNivel == null) 1f else painel.progressoProximoNivel,
                modifier = Modifier.fillMaxWidth(),
                height = 6.dp,
                gradientColors = levelGradientColors(painel.nivel)
            )

            if (conquistas.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = LocalInovagabColors.current.cardBorder
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.ranking_jornada_conquistas, desbloqueadas, conquistas.size),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (conquistas.size > 6) {
                        Text(
                            text = if (mostrarTodas) {
                                stringResource(R.string.action_show_less)
                            } else {
                                stringResource(R.string.home_gestor_ver_todas)
                            },
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { mostrarTodas = !mostrarTodas }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                BadgeGrid(
                    conquistas = if (mostrarTodas) conquistas else conquistas.take(6),
                    badgeSize = 64.dp,
                    onBadgeClick = { conquistaSelecionada = it }
                )
            }
        }
    }

    conquistaSelecionada?.let { conquista ->
        ConquistaDetailSheet(
            conquista = conquista,
            onDismiss = { conquistaSelecionada = null }
        )
    }
}

@Composable
private fun MissoesSemanaisCard(
    missoes: List<Missao>,
    sequenciaSemanas: Int,
    isDarkTheme: Boolean
) {
    val shape = RoundedCornerShape(CardDimensions.CornerRadiusSmall)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (isDarkTheme) MaterialTheme.colorScheme.surfaceContainerHigh
                else MaterialTheme.colorScheme.surface
            )
            .border(1.dp, LocalInovagabColors.current.cardBorder, shape)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    text = stringResource(R.string.ranking_missoes_titulo),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                SequenciaChip(sequenciaSemanas = sequenciaSemanas)
            }
            missoes.forEach { missao -> MissaoRow(missao) }
        }
    }
}

@Composable
private fun SequenciaChip(sequenciaSemanas: Int) {
    val color = if (sequenciaSemanas > 0) MedalBronze else MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocalFireDepartment,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = if (sequenciaSemanas > 0) {
                pluralStringResource(R.plurals.ranking_sequencia_chip, sequenciaSemanas, sequenciaSemanas)
            } else {
                stringResource(R.string.ranking_sequencia_chip_zero)
            },
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun MissaoRow(missao: Missao) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = missao.descricao.ifBlank { missao.titulo },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (missao.recompensaAsas > 0) {
                    stringResource(R.string.ranking_missao_recompensa, missao.recompensaXp, missao.recompensaAsas)
                } else {
                    stringResource(R.string.ranking_missao_recompensa_xp, missao.recompensaXp)
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GradientProgressBar(
                progress = missao.fracao,
                modifier = Modifier.weight(1f),
                height = 4.dp,
                gradientColors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                )
            )
            if (missao.concluida) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.ranking_missao_concluida),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.ranking_missao_progresso, missao.progresso, missao.meta),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EquipesRankingCard(equipes: List<EquipeRanking>) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = stringResource(R.string.ranking_equipes_titulo),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = stringResource(R.string.ranking_equipes_subtitulo),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            equipes.forEach { equipe ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RankingPositionBadge(posicao = equipe.posicao)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = equipe.equipe,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (equipe.minhaEquipe) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (equipe.minhaEquipe) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                        Text(
                            text = pluralStringResource(
                                R.plurals.ranking_equipe_item,
                                equipe.membros,
                                equipe.membros,
                                equipe.xpMedio
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (equipe.minhaEquipe) {
                        Text(
                            text = stringResource(R.string.ranking_minha_equipe),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ZonaPremioChip() {
    Text(
        text = stringResource(R.string.ranking_status_zona_premio),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MedalGold,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MedalGold.copy(alpha = 0.14f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

@Composable
private fun OperacaoDivisaoCard(operadores: List<RankingEntrada>) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.ranking_operacao_titulo),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.ranking_operacao_subtitulo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            operadores.take(RankingViewModel.ZONA_PREMIO).forEachIndexed { index, entrada ->
                ParticipanteRankingRow(
                    posicao = index + 1,
                    nome = entrada.nome,
                    detalhe = stringResource(R.string.ranking_pontos, entrada.pontos),
                    destaque = entrada.zonaPremio
                )
            }
        }
    }
}

@Composable
private fun IndicacoesInovagabCard(indicacoes: IndicacoesInovaGab) {
    val porDivisao = indicacoes.indicados.groupBy { it.divisao }
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.ranking_indicacoes_titulo),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.ranking_indicacoes_subtitulo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            DivisaoNegocio.entries.forEach { divisao ->
                val grupo = porDivisao[divisao].orEmpty()
                    .sortedBy { it.posicaoNaDivisao }
                    .take(RankingViewModel.ZONA_PREMIO)
                if (grupo.isEmpty()) return@forEach
                Text(
                    text = divisao.label.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                grupo.forEach { indicado ->
                    ParticipanteRankingRow(
                        posicao = indicado.posicaoNaDivisao,
                        nome = indicado.nome,
                        detalhe = stringResource(
                            R.string.ranking_indicacao_equipe,
                            indicado.equipe?.takeIf { it.isNotBlank() } ?: divisao.label,
                            indicado.xpTemporada
                        ),
                        destaque = true
                    )
                }
            }
        }
    }
}

@Composable
private fun ParticipanteRankingRow(
    posicao: Int,
    nome: String,
    detalhe: String,
    destaque: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RankingPositionBadge(posicao = posicao)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nome,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = detalhe,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (destaque) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = stringResource(R.string.ranking_status_zona_premio),
                tint = MedalGold,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun RankingPodium(
    titulo: String,
    topThree: List<RankingEntrada>,
    usuarioLogadoId: String?,
    isDarkTheme: Boolean
) {
    val first = topThree.getOrNull(0)
    val second = topThree.getOrNull(1)
    val third = topThree.getOrNull(2)

    val podiumShape = RoundedCornerShape(CardDimensions.CornerRadiusLarge)
    val gradientColors = if (isDarkTheme) {
        listOf(
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.surfaceContainerHigh
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
            MaterialTheme.colorScheme.surface
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(podiumShape)
            .background(Brush.verticalGradient(gradientColors))
            .border(
                width = 1.dp,
                color = LocalInovagabColors.current.cardBorder,
                shape = podiumShape
            )
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Text(
            text = titulo.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

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
        Box(contentAlignment = Alignment.Center) {
            PremiumAvatar(
                nome = entrada.nome,
                nivel = entrada.nivel,
                size = avatarSize,
                isInRanking = true
            )
            if (posicao == 1) {
                // O avatar reserva 8dp de brilho em volta do anel; +4dp centraliza a fita na borda inferior.
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = MedalGold,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 4.dp)
                        .size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(if (posicao == 1) 14.dp else 10.dp))

        Text(
            text = if (isUsuarioLogado) {
                stringResource(R.string.ranking_voce)
            } else {
                entrada.nome.split(" ").firstOrNull() ?: entrada.nome
            },
            style = when (posicao) {
                1 -> MaterialTheme.typography.titleSmall
                else -> MaterialTheme.typography.labelLarge
            },
            fontWeight = FontWeight.Bold,
            color = if (isUsuarioLogado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Text(
            text = stringResource(R.string.ranking_pontos, entrada.pontos),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

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

/** Top 5 do grupo; a equipe do usuário entra no fim se estiver fora. */
private fun equipesVisiveis(equipes: List<EquipeRanking>): List<EquipeRanking> {
    val top = equipes.take(5)
    val minha = equipes.firstOrNull { it.minhaEquipe }
    return if (minha == null || minha in top) top else top + minha
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
                perfilUsuarioLogado = PerfilUsuario.OPERADOR,
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
                temporada = TemporadaInfo("2026-T4", "Temporada 4 · 2026", 42),
                regraPremio = "Os 3 primeiros de cada divisão ao fim da temporada são indicados ao Prêmio InovaGAB.",
                proximaPosicao = ProximaPosicao("Maria", 1, 231, 351, empatado = false)
            ),
            onNavigateBack = {}
        )
    }
}
