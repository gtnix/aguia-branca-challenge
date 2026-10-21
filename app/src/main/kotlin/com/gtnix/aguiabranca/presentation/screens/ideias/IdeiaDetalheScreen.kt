package com.gtnix.aguiabranca.presentation.screens.ideias

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AnaliseIa
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.CriterioIa
import com.gtnix.aguiabranca.domain.model.CriterioRubrica
import com.gtnix.aguiabranca.domain.model.FaixaIa
import com.gtnix.aguiabranca.domain.model.FrequenciaProblema
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusAnaliseIa
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.presentation.components.AIChip
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.rememberAguiaTopBarScrollBehavior
import com.gtnix.aguiabranca.presentation.components.CelebrationOverlay
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.InovagabButtonVariant
import com.gtnix.aguiabranca.presentation.components.InovagabFlatCard
import com.gtnix.aguiabranca.presentation.components.InovagabInsetSurface
import com.gtnix.aguiabranca.presentation.components.PremiumFilterChip
import com.gtnix.aguiabranca.presentation.components.ideiaStatusColor
import com.gtnix.aguiabranca.presentation.theme.CardDimensions
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.util.faixaDoScore
import com.gtnix.aguiabranca.presentation.util.formatShortDate
import com.gtnix.aguiabranca.presentation.util.labelRes

@Composable
fun IdeiaDetalheScreen(
    viewModel: IdeiaDetalheViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNovoProjeto: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    IdeiaDetalheScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onIniciarAnalise = viewModel::iniciarAnalise,
        onImpactoChange = viewModel::onImpactoChange,
        onEsforcoChange = viewModel::onEsforcoChange,
        onFeedbackChange = viewModel::onFeedbackChange,
        onConcordaIaChange = viewModel::onConcordaIaChange,
        onMotivoDiscordanciaChange = viewModel::onMotivoDiscordanciaChange,
        onAprovar = viewModel::aprovar,
        onReprovar = viewModel::reprovar,
        onUpvote = viewModel::upvote,
        onFeedbackUtil = viewModel::marcarFeedbackUtil,
        onCriarProjeto = onNavigateToNovoProjeto,
        onExcluir = viewModel::excluir
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdeiaDetalheScreenContent(
    uiState: IdeiaDetalheUiState,
    onNavigateBack: () -> Unit,
    onIniciarAnalise: () -> Unit,
    onImpactoChange: (Float) -> Unit,
    onEsforcoChange: (Float) -> Unit,
    onFeedbackChange: (String) -> Unit,
    onConcordaIaChange: (Boolean) -> Unit = {},
    onMotivoDiscordanciaChange: (String) -> Unit = {},
    onAprovar: () -> Unit,
    onReprovar: () -> Unit,
    onUpvote: () -> Unit,
    onFeedbackUtil: (Boolean) -> Unit = {},
    onCriarProjeto: (String) -> Unit = {},
    onExcluir: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isDarkTheme = LocalDarkTheme.current
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var celebrationMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val actionSuccessMessage = stringResource(R.string.ideia_detalhe_acao_sucesso)
    val ideiaAprovadaMessage = "Ideia Aprovada!"

    LaunchedEffect(uiState.deleted) {
        if (uiState.deleted) {
            onNavigateBack()
        }
    }

    LaunchedEffect(uiState.actionSuccess, uiState.ideia?.status) {
        if (uiState.actionSuccess) {
            when (uiState.ideia?.status) {
                StatusIdeia.APROVADA -> celebrationMessage = ideiaAprovadaMessage
                else -> snackbarHostState.showSnackbar(actionSuccessMessage)
            }
        }
    }

    LaunchedEffect(uiState.errorMessageRes) {
        uiState.errorMessageRes?.let { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    val scrollBehavior = rememberAguiaTopBarScrollBehavior()
    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.ideia_detalhe_titulo),
                onBackClick = onNavigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (uiState.ideia != null && uiState.canEvaluate && uiState.isPendingEvaluation) {
                DecisaoBar(
                    onAprovar = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onAprovar()
                    },
                    onReprovar = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onReprovar()
                    }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            uiState.ideia == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = stringResource(R.string.cd_idea_icon),
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = stringResource(
                                uiState.errorMessageRes ?: R.string.ideia_detalhe_nao_encontrada
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            else -> {
                val ideia = uiState.ideia
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IdeiaResumoCard(
                        ideia = ideia,
                        orientacao = uiState.orientacao
                    )

                    val podeAnalisar = uiState.isPendingEvaluation && uiState.canEvaluate
                    val temAnalise = ideia.analiseIa?.status != null || ideia.scoreIa != null
                    if (uiState.canViewIa && (temAnalise || podeAnalisar)) {
                        LeituraIaCard(
                            ideia = ideia,
                            fallback = uiState.scoreFallback || ideia.scoreIaFallback,
                            podeAnalisar = podeAnalisar,
                            analisando = uiState.scoringInProgress,
                            onAnalisar = onIniciarAnalise
                        )
                    }

                    if (podeAnalisar) {
                        SuaAvaliacaoCard(
                            uiState = uiState,
                            onImpactoChange = onImpactoChange,
                            onEsforcoChange = onEsforcoChange,
                            onFeedbackChange = onFeedbackChange,
                            onConcordaIaChange = onConcordaIaChange,
                            onMotivoDiscordanciaChange = onMotivoDiscordanciaChange
                        )
                    } else if (ideia.impactoEstimado > 0 || ideia.esforcoEstimado > 0) {
                        PremiumScoreCard(
                            impacto = ideia.impactoEstimado,
                            esforco = ideia.esforcoEstimado,
                            score = ideia.scorePriorizacao,
                            upvotes = ideia.upvotes
                        )
                    }

                    if (ideia.feedback != null) {
                        PremiumFeedbackCard(feedback = ideia.feedback, isDarkTheme = isDarkTheme)
                    }

                    if (uiState.isAuthor && !ideia.feedback.isNullOrBlank()) {
                        FeedbackUtilCard(
                            feedbackUtil = ideia.feedbackUtil,
                            acabouDeResponder = uiState.feedbackUtilEnviado,
                            onResponder = onFeedbackUtil
                        )
                    }

                    if (uiState.canSupport && !podeAnalisar) {
                        InovagabButton(
                            text = if (uiState.jaVotou) {
                                stringResource(R.string.ideia_apoiada, ideia.upvotes)
                            } else {
                                stringResource(R.string.ideia_apoiar, ideia.upvotes)
                            },
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onUpvote()
                            },
                            enabled = !uiState.jaVotou,
                            variant = InovagabButtonVariant.Secondary,
                            leadingIcon = Icons.Default.ThumbUp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (uiState.perfil == PerfilUsuario.GESTOR && ideia.status == StatusIdeia.APROVADA) {
                        InovagabButton(
                            text = stringResource(R.string.ideia_converter_projeto),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onCriarProjeto(ideia.id)
                            },
                            leadingIcon = Icons.Default.Folder,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (uiState.canDelete) {
                        InovagabButton(
                            text = stringResource(R.string.ideia_excluir_botao),
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showDeleteDialog = true
                            },
                            variant = InovagabButtonVariant.Neutral,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    AnimatedVisibility(
                        visible = uiState.actionSuccess,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        SuccessCard()
                    }

                    Spacer(modifier = Modifier.height(if (podeAnalisar) 16.dp else 80.dp))
                }
            }
        }
    }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(R.string.ideia_excluir_titulo)) },
                text = {
                    Text(
                        stringResource(
                            R.string.ideia_excluir_mensagem,
                            uiState.ideia?.titulo.orEmpty()
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onExcluir()
                            showDeleteDialog = false
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.ideia_excluir_confirmar),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(R.string.btn_cancelar))
                    }
                }
            )
        }

        celebrationMessage?.let { message ->
            CelebrationOverlay(
                message = message,
                visible = true,
                onDismiss = { celebrationMessage = null }
            )
        }
    }
}

@Composable
private fun ideiaStatusLabel(status: StatusIdeia): String = when (status) {
    StatusIdeia.PENDENTE -> stringResource(R.string.status_awaiting_evaluation)
    StatusIdeia.EM_ANALISE -> stringResource(R.string.status_under_analysis)
    StatusIdeia.APROVADA -> stringResource(R.string.status_idea_approved)
    StatusIdeia.REPROVADA -> stringResource(R.string.status_not_prioritized)
    StatusIdeia.CONVERTIDA_PROJETO -> stringResource(R.string.status_converted_project)
}

@Composable
private fun DetalheCard(
    content: @Composable ColumnScope.() -> Unit
) {
    InovagabFlatCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IdeiaResumoCard(
    ideia: Ideia,
    orientacao: OrientacaoEstrategica?
) {
    val statusColor = ideiaStatusColor(ideia.status)
    val data = remember(ideia.dataCriacao) { formatShortDate(ideia.dataCriacao) }
    val textSecondary = LocalInovagabColors.current.textSecondary

    DetalheCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = ideiaStatusLabel(ideia.status),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = ideia.titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )
            Text(
                text = ideia.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = stringResource(
                R.string.ideia_meta,
                ideia.autorNome,
                stringResource(ideia.area.labelRes()),
                data
            ),
            style = MaterialTheme.typography.labelMedium,
            color = textSecondary
        )

        if (orientacao != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Explore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.ideia_meta_orientacao, orientacao.titulo),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        val publico = ideia.publicoAfetado?.takeIf { it.isNotBlank() }
        val frequencia = ideia.frequencia
        if (publico != null || frequencia != null) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                publico?.let { ContextoChip(stringResource(R.string.ideia_contexto_chip_publico, it)) }
                frequencia?.let {
                    ContextoChip(
                        stringResource(R.string.ideia_contexto_chip_frequencia, stringResource(it.labelRes()))
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextoChip(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .border(1.dp, LocalInovagabColors.current.cardBorder, RoundedCornerShape(100))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

/**
 * Leitura curta da IA: número, faixa e motivo primeiro; critérios e riscos só sob demanda,
 * para que a recomendação apoie a decisão sem virar mais um formulário para o gestor.
 */
@Composable
private fun LeituraIaCard(
    ideia: Ideia,
    fallback: Boolean,
    podeAnalisar: Boolean,
    analisando: Boolean,
    onAnalisar: () -> Unit
) {
    val analise = ideia.analiseIa
    val score = ideia.scoreIa
    val temAnalise = analise?.status != null || score != null
    var expandido by remember { mutableStateOf(false) }
    var textoCortado by remember { mutableStateOf(false) }

    DetalheCard {
        if (!temAnalise) {
            Text(
                text = stringResource(R.string.ideia_score_ia_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            InovagabButton(
                text = stringResource(
                    if (analisando) R.string.ia_analisando else R.string.ia_analisar
                ),
                onClick = onAnalisar,
                variant = InovagabButtonVariant.Secondary,
                leadingIcon = Icons.Outlined.AutoAwesome,
                enabled = !analisando,
                modifier = Modifier.fillMaxWidth()
            )
            return@DetalheCard
        }

        val status = analise?.status
            ?: if (fallback) StatusAnaliseIa.ESTIMATIVA else StatusAnaliseIa.IA
        val faixa = analise?.faixa ?: score?.let(::faixaDoScore)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = stringResource(R.string.cd_ai_suggestion),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.ideia_score_ia_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (score != null && faixa != null) {
                        stringResource(R.string.ia_score_faixa, score, stringResource(faixa.labelRes()))
                    } else {
                        stringResource(R.string.ia_selo_dados_insuficientes)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            AIChip(
                text = stringResource(
                    when (status) {
                        StatusAnaliseIa.IA -> R.string.ia_origem_ia
                        StatusAnaliseIa.ESTIMATIVA -> R.string.ia_origem_estimativa
                        StatusAnaliseIa.DADOS_INSUFICIENTES -> R.string.ia_origem_sem_dados
                    }
                )
            )
            if (podeAnalisar) {
                IconButton(onClick = onAnalisar, enabled = !analisando) {
                    if (analisando) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.ideia_detalhe_reanalisar_ia),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        Text(
            text = when (status) {
                StatusAnaliseIa.IA -> ideia.justificativaIa?.takeIf { it.isNotBlank() }
                    ?: stringResource(R.string.ideia_score_ia_fallback)
                StatusAnaliseIa.ESTIMATIVA -> stringResource(R.string.ia_explicacao_estimativa)
                StatusAnaliseIa.DADOS_INSUFICIENTES -> stringResource(R.string.ia_explicacao_sem_dados)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = if (expandido) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { if (!expandido) textoCortado = it.hasVisualOverflow }
        )

        val perguntas = analise?.perguntas.orEmpty()
        if (perguntas.isNotEmpty()) {
            ListaIa(
                titulo = stringResource(R.string.ia_perguntas_titulo),
                itens = if (expandido) perguntas else perguntas.take(2)
            )
        }

        val temDetalhes = textoCortado || expandido || (analise != null &&
            (analise.criterios.isNotEmpty() || analise.riscos.isNotEmpty() || perguntas.size > 2))
        AnimatedVisibility(visible = expandido && analise != null) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (analise != null && analise.criterios.isNotEmpty()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    analise.criterios.forEach { criterio -> CriterioLinha(criterio) }
                }
                if (analise != null && analise.riscos.isNotEmpty()) {
                    ListaIa(titulo = stringResource(R.string.ia_riscos_titulo), itens = analise.riscos)
                }
            }
        }

        if (temDetalhes) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { expandido = !expandido }) {
                    Text(
                        text = stringResource(
                            if (expandido) R.string.ia_ver_menos else R.string.ia_ver_analise_completa
                        ),
                        style = MaterialTheme.typography.labelMedium
                    )
                    Icon(
                        imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CriterioLinha(criterio: CriterioIa) {
    val label = stringResource(criterio.criterio.labelRes())
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.ia_criterio_peso, label, criterio.peso),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(R.string.ia_criterio_nota, criterio.nota),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (criterio.evidencia.isNotBlank()) {
            Text(
                text = criterio.evidencia,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ListaIa(titulo: String, itens: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        itens.forEach { item ->
            Text(
                text = "• $item",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SuaAvaliacaoCard(
    uiState: IdeiaDetalheUiState,
    onImpactoChange: (Float) -> Unit,
    onEsforcoChange: (Float) -> Unit,
    onFeedbackChange: (String) -> Unit,
    onConcordaIaChange: (Boolean) -> Unit,
    onMotivoDiscordanciaChange: (String) -> Unit
) {
    DetalheCard {
        Text(
            text = stringResource(R.string.ideia_sua_avaliacao),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() }
        )

        EscalaSelector(
            label = stringResource(R.string.ideia_detalhe_impacto),
            value = uiState.impacto.toInt(),
            sugestaoIa = uiState.impactoSugeridoIa,
            onSelect = { onImpactoChange(it.toFloat()) }
        )
        EscalaSelector(
            label = stringResource(R.string.ideia_detalhe_esforco),
            value = uiState.esforco.toInt(),
            sugestaoIa = uiState.esforcoSugeridoIa,
            onSelect = { onEsforcoChange(it.toFloat()) }
        )

        if (uiState.temRecomendacaoIa) {
            ConcordanciaIaSection(
                concordaIa = uiState.concordaIa,
                motivo = uiState.motivoDiscordancia,
                onConcordaIaChange = onConcordaIaChange,
                onMotivoChange = onMotivoDiscordanciaChange
            )
        }

        AvaliacaoTextField(
            value = uiState.feedback,
            onValueChange = onFeedbackChange,
            label = stringResource(R.string.ideia_feedback_autor),
            placeholder = stringResource(R.string.ideia_detalhe_feedback_placeholder),
            minLines = 2,
            maxLines = 6
        )
        Text(
            text = stringResource(R.string.ideia_feedback_obrigatorio_reprovar),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EscalaSelector(
    label: String,
    value: Int,
    sugestaoIa: Int?,
    onSelect: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            sugestaoIa?.let {
                Text(
                    text = stringResource(R.string.ia_sugere_valor, it),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalInovagabColors.current.textSecondary
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..5).forEach { nota ->
                PremiumFilterChip(
                    label = nota.toString(),
                    selected = nota == value,
                    onClick = { onSelect(nota) },
                    compact = true,
                    showCheck = false,
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "$label $nota" }
                )
            }
        }
    }
}

@Composable
private fun DecisaoBar(
    onAprovar: () -> Unit,
    onReprovar: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InovagabButton(
                text = stringResource(R.string.action_reject),
                onClick = onReprovar,
                variant = InovagabButtonVariant.Neutral,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )
            InovagabButton(
                text = stringResource(R.string.action_approve),
                onClick = onAprovar,
                variant = InovagabButtonVariant.Success,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )
        }
    }
}

@Composable
private fun FeedbackUtilCard(
    feedbackUtil: Boolean?,
    acabouDeResponder: Boolean,
    onResponder: (Boolean) -> Unit
) {
    InovagabFlatCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            when {
                acabouDeResponder -> Text(
                    text = stringResource(R.string.feedback_util_obrigado),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                feedbackUtil == true -> Text(
                    text = stringResource(R.string.feedback_util_respondido_sim),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                feedbackUtil == false -> Text(
                    text = stringResource(R.string.feedback_util_respondido_nao),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                else -> {
                    Text(
                        text = stringResource(R.string.feedback_util_pergunta),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InovagabButton(
                            text = stringResource(R.string.feedback_util_sim),
                            onClick = { onResponder(true) },
                            leadingIcon = Icons.Default.ThumbUp,
                            modifier = Modifier.weight(1f)
                        )
                        InovagabButton(
                            text = stringResource(R.string.feedback_util_nao),
                            onClick = { onResponder(false) },
                            variant = InovagabButtonVariant.Neutral,
                            leadingIcon = Icons.Default.ThumbDown,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PremiumFeedbackCard(
    feedback: String,
    isDarkTheme: Boolean
) {
    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
    }
    
    val backgroundColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardDimensions.CornerRadiusSmall))
            .border(1.dp, borderColor, RoundedCornerShape(CardDimensions.CornerRadiusSmall))
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = stringResource(R.string.ideia_detalhe_feedback_gestor),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ConcordanciaIaSection(
    concordaIa: Boolean?,
    motivo: String,
    onConcordaIaChange: (Boolean) -> Unit,
    onMotivoChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.ia_concorda_pergunta),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = concordaIa == true,
                onClick = { onConcordaIaChange(true) },
                label = { Text(stringResource(R.string.ia_concordo)) },
                leadingIcon = { Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            FilterChip(
                selected = concordaIa == false,
                onClick = { onConcordaIaChange(false) },
                label = { Text(stringResource(R.string.ia_discordo)) },
                leadingIcon = { Icon(Icons.Default.ThumbDown, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        if (concordaIa == false) {
            AvaliacaoTextField(
                value = motivo,
                onValueChange = onMotivoChange,
                label = stringResource(R.string.ia_motivo_discordancia_label)
            )
        }
    }
}

@Composable
private fun AvaliacaoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String? = null,
    minLines: Int = 1,
    maxLines: Int = 3
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        shape = RoundedCornerShape(12.dp),
        minLines = minLines,
        maxLines = maxLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun ScoreDisplay(
    score: Int,
    apoio: String? = null
) {
    InovagabInsetSurface(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.ideia_detalhe_score_priorizacao),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (!apoio.isNullOrBlank()) {
            Text(
                text = apoio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PremiumScoreCard(
    impacto: Int,
    esforco: Int,
    score: Int,
    upvotes: Int
) {
    InovagabFlatCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.ideia_detalhe_priorizacao),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            ScoreDotsIndicator(
                label = stringResource(R.string.ideia_detalhe_impacto),
                value = impacto,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )

            ScoreDotsIndicator(
                label = stringResource(R.string.ideia_detalhe_esforco),
                value = esforco,
                color = MaterialTheme.colorScheme.tertiary
            )

            ScoreDisplay(
                score = score,
                apoio = if (upvotes > 0) {
                    stringResource(R.string.ideia_detalhe_score_apoios, upvotes)
                } else {
                    null
                }
            )
        }
    }
}

@Composable
private fun ScoreDotsIndicator(
    label: String,
    value: Int,
    color: Color,
    maxValue: Int = 5
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(maxValue) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(100))
                            .background(
                                if (index < value) color else color.copy(alpha = 0.18f)
                            )
                    )
                }
            }
        }
        Text(
            text = "$value/$maxValue",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun SuccessCard() {
    val isDarkTheme = LocalDarkTheme.current
    val backgroundColor = if (isDarkTheme) {
        SuccessGreen.copy(alpha = 0.15f)
    } else {
        SuccessGreen.copy(alpha = 0.1f)
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CardDimensions.CornerRadiusInset))
            .background(backgroundColor)
            .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(CardDimensions.CornerRadiusInset))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.ideia_detalhe_acao_sucesso),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = SuccessGreen
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun IdeiaDetalheGestorPendentePreview() {
    InovagabTheme {
        IdeiaDetalheScreenContent(
            uiState = IdeiaDetalheUiState(
                ideia = Ideia(
                    id = "1",
                    titulo = "Rastreamento de frota em tempo real",
                    descricao = "Usar telemetria para reduzir combustível e melhorar pontualidade das linhas.",
                    tipo = TipoIdeia.IDEIA,
                    area = AreaAtuacao.OPERACOES,
                    status = StatusIdeia.REPROVADA,
                    autorId = "user1",
                    autorNome = "Pedro Santos",
                    upvotes = 0,
                    dataAvaliacao = System.currentTimeMillis()
                ),
                orientacao = OrientacaoEstrategica(
                    id = "o1",
                    titulo = "Eficiência operacional e experiência do passageiro",
                    descricao = "Priorizar iniciativas que reduzam custos logísticos e melhorem NPS nas linhas rodoviárias.",
                    categoria = CategoriaOrientacao.EFICIENCIA_OPERACIONAL,
                    criadoPor = "lider"
                ),
                perfil = PerfilUsuario.OPERADOR
            ),
            onNavigateBack = {},
            onIniciarAnalise = {},
            onImpactoChange = {},
            onEsforcoChange = {},
            onFeedbackChange = {},
            onAprovar = {},
            onReprovar = {},
            onUpvote = {},
            onCriarProjeto = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun IdeiaDetalheGestorEmAnalisePreview() {
    InovagabTheme {
        IdeiaDetalheScreenContent(
            uiState = IdeiaDetalheUiState(
                ideia = Ideia(
                    id = "1",
                    titulo = "Redução de Papel",
                    descricao = "Digitalizar documentos para reduzir uso de papel em 80%.",
                    tipo = TipoIdeia.IDEIA,
                    area = AreaAtuacao.OPERACOES,
                    status = StatusIdeia.EM_ANALISE,
                    autorId = "user2",
                    autorNome = "Maria Santos",
                    problema = "Cada viagem gera 6 vias impressas de manifesto.",
                    publicoAfetado = "Despachantes das garagens",
                    frequencia = FrequenciaProblema.DIARIA,
                    beneficio = "Menos papel e conferência mais rápida.",
                    scoreIa = 72,
                    justificativaIa = "Alinhada à eficiência operacional, com ganho recorrente e baixo esforço.",
                    analiseIa = AnaliseIa(
                        status = StatusAnaliseIa.IA,
                        faixa = FaixaIa.ALTA,
                        criterios = listOf(
                            CriterioIa(CriterioRubrica.ALINHAMENTO, 4, 30, "Cita redução de custo logístico."),
                            CriterioIa(CriterioRubrica.IMPACTO, 4, 30, "Problema diário em todas as garagens."),
                            CriterioIa(CriterioRubrica.VIABILIDADE, 4, 20, "Usa sistemas já existentes."),
                            CriterioIa(CriterioRubrica.CLAREZA, 3, 10, "Falta meta de prazo."),
                            CriterioIa(CriterioRubrica.ORIGINALIDADE, 2, 10, "Prática comum no setor.")
                        ),
                        riscos = listOf("Exigência legal de via impressa em fiscalização."),
                        perguntas = listOf("Quantas folhas por dia cada garagem usa hoje?"),
                        impactoSugerido = 4,
                        esforcoSugerido = 2
                    )
                ),
                perfil = PerfilUsuario.GESTOR,
                impacto = 4f,
                esforco = 3f,
                concordaIa = false
            ),
            onNavigateBack = {},
            onIniciarAnalise = {},
            onImpactoChange = {},
            onEsforcoChange = {},
            onFeedbackChange = {},
            onAprovar = {},
            onReprovar = {},
            onUpvote = {},
            onCriarProjeto = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun IdeiaDetalheOperadorComFeedbackPreview() {
    InovagabTheme {
        IdeiaDetalheScreenContent(
            uiState = IdeiaDetalheUiState(
                ideia = Ideia(
                    id = "1",
                    titulo = "Melhoria no Atendimento",
                    descricao = "Criar canal digital para feedback dos passageiros.",
                    tipo = TipoIdeia.IDEIA,
                    area = AreaAtuacao.COMERCIAL,
                    status = StatusIdeia.APROVADA,
                    autorId = "user3",
                    autorNome = "Carlos Oliveira",
                    feedback = "Excelente ideia! Vamos implementar no próximo trimestre.",
                    impactoEstimado = 5,
                    esforcoEstimado = 3,
                    upvotes = 8
                ),
                perfil = PerfilUsuario.OPERADOR,
                jaVotou = true
            ),
            onNavigateBack = {},
            onIniciarAnalise = {},
            onImpactoChange = {},
            onEsforcoChange = {},
            onFeedbackChange = {},
            onAprovar = {},
            onReprovar = {},
            onUpvote = {},
            onCriarProjeto = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun IdeiaDetalheOperadorApoioPreview() {
    InovagabTheme {
        IdeiaDetalheScreenContent(
            uiState = IdeiaDetalheUiState(
                ideia = Ideia(
                    id = "2",
                    titulo = "Rastreamento de frota em tempo real",
                    descricao = "Usar telemetria para reduzir combustível e melhorar pontualidade das linhas.",
                    tipo = TipoIdeia.IDEIA,
                    area = AreaAtuacao.OPERACOES,
                    status = StatusIdeia.PENDENTE,
                    autorId = "user1",
                    autorNome = "Pedro Santos",
                    upvotes = 12
                ),
                jaVotou = false,
                perfil = PerfilUsuario.OPERADOR
            ),
            onNavigateBack = {},
            onIniciarAnalise = {},
            onImpactoChange = {},
            onEsforcoChange = {},
            onFeedbackChange = {},
            onAprovar = {},
            onReprovar = {},
            onUpvote = {},
            onCriarProjeto = {}
        )
    }
}
