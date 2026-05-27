package com.gtnix.aguiabranca.presentation.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.gtnix.aguiabranca.presentation.components.BackdropGlassCard
import com.gtnix.aguiabranca.presentation.components.CelebrationOverlay
import com.gtnix.aguiabranca.presentation.components.GradientProgressBar
import com.gtnix.aguiabranca.presentation.components.PremiumAvatar
import com.gtnix.aguiabranca.presentation.components.RankingHeroCard
import com.gtnix.aguiabranca.presentation.components.StatsGridGestor
import com.gtnix.aguiabranca.presentation.components.StatsGridLider
import com.gtnix.aguiabranca.presentation.components.badges.BadgeGrid
import com.gtnix.aguiabranca.presentation.components.badges.ConquistaDetailSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Conquista
import com.gtnix.aguiabranca.domain.model.Estatisticas
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.TipoConquista
import com.gtnix.aguiabranca.domain.model.defaultTier
import com.gtnix.aguiabranca.presentation.util.nivelIcon
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.levelBrush
import com.gtnix.aguiabranca.presentation.theme.levelGradientColors
import com.gtnix.aguiabranca.presentation.util.titulo
import com.gtnix.aguiabranca.presentation.theme.NavBarDimensions
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onNavigateToRanking: () -> Unit = {},
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) {
            onLogout()
        }
    }

    PerfilScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToRanking = onNavigateToRanking,
        onLogout = { viewModel.onLogout() },
        onCelebrationDismiss = { viewModel.clearNewlyUnlocked() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PerfilScreenContent(
    uiState: PerfilUiState,
    onNavigateBack: (() -> Unit)?,
    onNavigateToRanking: () -> Unit,
    onLogout: () -> Unit,
    onCelebrationDismiss: () -> Unit = {}
) {
    val isDarkTheme = isSystemInDarkTheme()
    var selectedConquista by remember { mutableStateOf<Conquista?>(null) }
    var showConquistaSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.newlyUnlockedIds) {
        if (uiState.newlyUnlockedIds.isNotEmpty()) {
            delay(1500)
            onCelebrationDismiss()
        }
    }

    val celebrationConquista = uiState.pontuacao?.conquistas?.firstOrNull {
        it.id in uiState.newlyUnlockedIds
    }
    val celebrationMessage = celebrationConquista?.let { conquista ->
        "${conquista.tipo.titulo()}\n${stringResource(R.string.ranking_badge_desbloqueada)}"
    }.orEmpty()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenPadding.Horizontal)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.perfil_titulo),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                ProfileHeader(
                    nome = uiState.nome,
                    area = formatAreaLabel(uiState.area),
                    perfil = formatPerfilLabel(uiState.perfil),
                    nivel = uiState.pontuacao?.nivel ?: NivelUsuario.INICIANTE
                )

                Spacer(modifier = Modifier.height(20.dp))

                uiState.pontuacao?.let { pontuacao ->
                    val perfil = parsePerfil(uiState.perfil)

                    NivelCard(
                        pontuacao = pontuacao,
                        perfil = perfil,
                        isDarkTheme = isDarkTheme
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.posicaoRanking > 0 && uiState.totalParticipantesRanking >= 3) {
                        RankingHeroCard(
                            posicao = uiState.posicaoRanking,
                            totalParticipantes = uiState.totalParticipantesRanking,
                            perfilLabel = uiState.perfilRankingLabel,
                            divisaoLabel = uiState.divisaoRankingLabel,
                            deltaSemanaPosicao = uiState.deltaSemanaPosicao,
                            onClick = onNavigateToRanking
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    ConquistasSection(
                        conquistas = pontuacao.conquistas,
                        newlyUnlockedIds = uiState.newlyUnlockedIds,
                        onBadgeClick = { conquista ->
                            selectedConquista = conquista
                            showConquistaSheet = true
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    when (perfil) {
                        PerfilUsuario.GESTOR -> StatsGridGestor(
                            ideiasAvaliadas = pontuacao.estatisticas.totalIdeias,
                            ideiasAprovadas = pontuacao.estatisticas.ideiasAprovadas,
                            projetosArea = pontuacao.estatisticas.projetosParticipando
                        )
                        PerfilUsuario.LIDER -> StatsGridLider(
                            orientacoesAtivas = pontuacao.estatisticas.totalIdeias,
                            ideiasAlinhadas = pontuacao.estatisticas.ideiasAprovadas,
                            projetosDirecionados = pontuacao.estatisticas.projetosParticipando
                        )
                        PerfilUsuario.OPERADOR -> StatsGridOperador(
                            totalIdeias = pontuacao.estatisticas.totalIdeias,
                            ideiasAprovadas = pontuacao.estatisticas.ideiasAprovadas,
                            posicaoRanking = if (uiState.totalParticipantesRanking >= 3) uiState.posicaoRanking else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onLogout)
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.perfil_logout),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(NavBarDimensions.ContentBottomPaddingNoFab))
                }
            }
        }

        CelebrationOverlay(
            message = celebrationMessage,
            visible = uiState.showCelebration && celebrationMessage.isNotBlank(),
            onDismiss = onCelebrationDismiss
        )

        if (showConquistaSheet && selectedConquista != null) {
            ConquistaDetailSheet(
                conquista = selectedConquista!!,
                onDismiss = {
                    showConquistaSheet = false
                    selectedConquista = null
                },
                estatisticas = uiState.pontuacao?.estatisticas
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    nome: String,
    area: String,
    perfil: String,
    nivel: NivelUsuario
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PremiumAvatar(
            nome = nome,
            nivel = nivel,
            size = 96.dp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = nome,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "$area  •  $perfil",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NivelCard(
    pontuacao: Pontuacao,
    perfil: PerfilUsuario,
    isDarkTheme: Boolean
) {
    val levelColor = levelGradientColors(pontuacao.nivel).first()
    val proximoNivel = NivelUsuario.proximoNivel(pontuacao.nivel)
    val mensagemMotivacional = pontuacao.nivel.mensagemMotivacionalPara(perfil)

    BackdropGlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(levelBrush(pontuacao.nivel)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = pontuacao.nivel.nivelIcon(),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = pontuacao.nivel.label,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = levelColor
                        )
                        Text(
                            text = mensagemMotivacional,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = pontuacao.total.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.perfil_pontos),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GradientProgressBar(
                progress = pontuacao.progressoProximoNivel,
                modifier = Modifier.fillMaxWidth(),
                height = 10.dp,
                gradientColors = levelGradientColors(pontuacao.nivel)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (proximoNivel != null) {
                Text(
                    text = stringResource(R.string.perfil_pontos_para_nivel, pontuacao.pontosParaProximoNivel, proximoNivel.label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = levelColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = stringResource(R.string.perfil_nivel_maximo),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = levelColor
                    )
                }
            }
        }
    }
}

@Composable
private fun ConquistasSection(
    conquistas: List<Conquista>,
    newlyUnlockedIds: Set<String> = emptySet(),
    onBadgeClick: ((Conquista) -> Unit)? = null
) {
    var showAll by remember { mutableStateOf(false) }
    val desbloqueadas = conquistas.count { it.desbloqueada }
    val visibleConquistas = if (showAll) conquistas else conquistas.take(6)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.perfil_conquistas_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (conquistas.size > 6) {
                    Text(
                        text = stringResource(R.string.home_gestor_ver_todas),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable { showAll = !showAll }
                    )
                }
                Text(
                    text = "$desbloqueadas/${conquistas.size}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BadgeGrid(
            conquistas = visibleConquistas,
            newlyUnlockedIds = newlyUnlockedIds,
            badgeSize = 72.dp,
            onBadgeClick = onBadgeClick
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PerfilScreenPreview() {
    InovagabTheme {
        PerfilScreenContent(
            uiState = PerfilUiState(
                isLoading = false,
                nome = "João Silva",
                area = "OPERACOES",
                perfil = "OPERADOR",
                inicialNome = 'J',
                posicaoRanking = 2,
                totalParticipantesRanking = 12,
                perfilRankingLabel = "Inovadores",
                divisaoRankingLabel = "Logística",
                deltaSemanaPosicao = 2,
                pontuacao = Pontuacao(
                    total = 70,
                    nivel = NivelUsuario.EM_ASCENSAO,
                    progressoProximoNivel = 0.36f,
                    pontosParaProximoNivel = 51,
                    conquistas = listOf(
                        previewConquista("0", TipoConquista.BEM_VINDO, true),
                        previewConquista("1", TipoConquista.PRIMEIRA_IDEIA, true),
                        previewConquista("2", TipoConquista.INOVADOR, true),
                        previewConquista("3", TipoConquista.VISIONARIO_IDEIAS, false),
                        previewConquista("4", TipoConquista.PRIMEIRA_APROVACAO, true),
                        previewConquista("5", TipoConquista.INFLUENCIADOR, false),
                        previewConquista("6", TipoConquista.EM_PROJETO, true),
                        previewConquista("7", TipoConquista.TRANSFORMADOR, false),
                        previewConquista("8", TipoConquista.LIDER_INOVACAO, false)
                    ),
                    estatisticas = Estatisticas(
                        totalIdeias = 7,
                        ideiasAprovadas = 4,
                        projetosParticipando = 1
                    )
                )
            ),
            onNavigateBack = {},
            onNavigateToRanking = {},
            onLogout = {}
        )
    }
}

private fun formatAreaLabel(area: String): String = when (area) {
    "OPERACOES" -> "Operações"
    "LOGISTICA" -> "Logística"
    "COMERCIAL" -> "Comercial"
    "FINANCEIRO" -> "Financeiro"
    "RH" -> "RH"
    "TI" -> "TI"
    "MARKETING" -> "Marketing"
    "QUALIDADE" -> "Qualidade"
    else -> area
}

private fun formatPerfilLabel(perfil: String): String = when (perfil) {
    "OPERADOR" -> "Operador"
    "GESTOR" -> "Gestor"
    "LIDER" -> "Líder"
    else -> perfil
}

private fun parsePerfil(perfil: String): PerfilUsuario =
    runCatching { PerfilUsuario.valueOf(perfil) }.getOrDefault(PerfilUsuario.OPERADOR)

@Composable
private fun NivelUsuario.mensagemMotivacionalPara(perfil: PerfilUsuario): String =
    when (perfil) {
        PerfilUsuario.GESTOR -> stringResource(R.string.nivel_motivacional_curadoria)
        PerfilUsuario.LIDER -> stringResource(R.string.nivel_motivacional_lider)
        PerfilUsuario.OPERADOR -> mensagemMotivacional
    }

private fun previewConquista(
    id: String,
    tipo: TipoConquista,
    desbloqueada: Boolean
): Conquista = Conquista(
    id = id,
    tipo = tipo,
    desbloqueada = desbloqueada,
    tier = tipo.defaultTier()
)

@Composable
private fun StatsGridOperador(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    posicaoRanking: Int?,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatOperadorCard(
                label = stringResource(R.string.perfil_stat_ideias_enviadas),
                value = totalIdeias.toString(),
                icon = Icons.Default.Lightbulb,
                iconAccent = MaterialTheme.colorScheme.primary,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            
            StatOperadorCard(
                label = stringResource(R.string.perfil_stat_aprovadas),
                value = ideiasAprovadas.toString(),
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconAccent = com.gtnix.aguiabranca.presentation.theme.SuccessGreen,
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f)
            )
            
            if (posicaoRanking != null) {
                StatOperadorCard(
                    label = stringResource(R.string.perfil_stat_ranking),
                    value = "#$posicaoRanking",
                    icon = Icons.Default.Leaderboard,
                    iconAccent = MaterialTheme.colorScheme.tertiary,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatOperadorCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconAccent: Color,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val gradientColors = if (isDarkTheme) {
        listOf(
            iconAccent.copy(alpha = 0.14f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        )
    } else {
        listOf(
            iconAccent.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.surface
        )
    }

    Surface(
        modifier = modifier
            .height(100.dp)
            .clip(shape),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    iconAccent.copy(alpha = 0.35f),
                                    iconAccent.copy(alpha = 0.15f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PerfilScreenVisionarioPreview() {
    InovagabTheme {
        PerfilScreenContent(
            uiState = PerfilUiState(
                isLoading = false,
                nome = "Maria Santos",
                area = "TI",
                perfil = "LIDER",
                inicialNome = 'M',
                posicaoRanking = 1,
                totalParticipantesRanking = 8,
                perfilRankingLabel = "Estrategistas",
                divisaoRankingLabel = "Logística",
                deltaSemanaPosicao = 3,
                pontuacao = Pontuacao(
                    total = 350,
                    nivel = NivelUsuario.VISIONARIO,
                    progressoProximoNivel = 0.29f,
                    pontosParaProximoNivel = 151,
                    conquistas = listOf(
                        previewConquista("0", TipoConquista.BEM_VINDO, true),
                        previewConquista("1", TipoConquista.PRIMEIRA_IDEIA, true),
                        previewConquista("2", TipoConquista.INOVADOR, true),
                        previewConquista("3", TipoConquista.VISIONARIO_IDEIAS, true),
                        previewConquista("4", TipoConquista.PRIMEIRA_APROVACAO, true),
                        previewConquista("5", TipoConquista.INFLUENCIADOR, true),
                        previewConquista("6", TipoConquista.EM_PROJETO, true),
                        previewConquista("7", TipoConquista.TRANSFORMADOR, true),
                        previewConquista("8", TipoConquista.LIDER_INOVACAO, true)
                    ),
                    estatisticas = Estatisticas(
                        totalIdeias = 18,
                        ideiasAprovadas = 12,
                        projetosParticipando = 5
                    )
                )
            ),
            onNavigateBack = {},
            onNavigateToRanking = {},
            onLogout = {}
        )
    }
}
