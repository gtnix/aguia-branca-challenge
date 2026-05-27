package com.gtnix.aguiabranca.presentation.screens.ideias

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.CelebrationOverlay
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.IdeiaStatusBadge
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.InovagabButtonVariant
import com.gtnix.aguiabranca.presentation.components.StatusBadge
import com.gtnix.aguiabranca.presentation.components.ideiaStatusColor
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.util.formatLongDate
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
        onAprovar = viewModel::aprovar,
        onReprovar = viewModel::reprovar,
        onUpvote = viewModel::upvote,
        onCriarProjeto = onNavigateToNovoProjeto
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
    onAprovar: () -> Unit,
    onReprovar: () -> Unit,
    onUpvote: () -> Unit,
    onCriarProjeto: (String) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    var celebrationMessage by remember { mutableStateOf<String?>(null) }

    val actionSuccessMessage = stringResource(R.string.ideia_detalhe_acao_sucesso)
    val ideiaAprovadaMessage = "Ideia Aprovada!"

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

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.ideia_detalhe_titulo),
                onBackClick = onNavigateBack
            )
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
                            text = stringResource(R.string.ideia_detalhe_nao_encontrada),
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
                    PremiumStatusHeaderCard(ideia = ideia, isDarkTheme = isDarkTheme)

                    PremiumIdeiaInfoCard(
                        ideia = ideia,
                        isDarkTheme = isDarkTheme,
                        jaVotou = uiState.jaVotou,
                        onUpvote = onUpvote
                    )

                    if (uiState.orientacao != null) {
                        PremiumOrientacaoCard(
                            orientacao = uiState.orientacao,
                            isDarkTheme = isDarkTheme
                        )
                    }

                    if (uiState.isPendingEvaluation && uiState.canEvaluate) {
                        PremiumAvaliacaoSection(
                            impacto = uiState.impacto,
                            esforco = uiState.esforco,
                            score = uiState.scorePriorizacao,
                            feedback = uiState.feedback,
                            errorMessageRes = uiState.errorMessageRes,
                            isDarkTheme = isDarkTheme,
                            onImpactoChange = onImpactoChange,
                            onEsforcoChange = onEsforcoChange,
                            onFeedbackChange = onFeedbackChange,
                            onAprovar = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onAprovar()
                            },
                            onReprovar = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                onReprovar()
                            }
                        )

                        if (ideia.status == StatusIdeia.PENDENTE) {
                            InovagabButton(
                                text = stringResource(R.string.ideia_detalhe_iniciar_analise),
                                onClick = onIniciarAnalise,
                                variant = InovagabButtonVariant.Neutral,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else if (ideia.impactoEstimado > 0 || ideia.esforcoEstimado > 0) {
                        PremiumScoreCard(
                            impacto = ideia.impactoEstimado,
                            esforco = ideia.esforcoEstimado,
                            score = ideia.scorePriorizacao,
                            isDarkTheme = isDarkTheme
                        )
                    }

                    if (ideia.feedback != null) {
                        PremiumFeedbackCard(feedback = ideia.feedback, isDarkTheme = isDarkTheme)
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

                    AnimatedVisibility(
                        visible = uiState.actionSuccess,
                        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(),
                        exit = fadeOut(animationSpec = tween(300))
                    ) {
                        SuccessCard()
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
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
private fun PremiumStatusHeaderCard(
    ideia: Ideia,
    isDarkTheme: Boolean
) {
    val statusColor = ideiaStatusColor(ideia.status)
    val tipoLabel = when (ideia.tipo) {
        TipoIdeia.IDEIA -> stringResource(R.string.nova_ideia_tipo_ideia)
        TipoIdeia.PROBLEMA -> stringResource(R.string.nova_ideia_tipo_problema)
    }
    val tipoIcon = when (ideia.tipo) {
        TipoIdeia.IDEIA -> Icons.Default.Lightbulb
        TipoIdeia.PROBLEMA -> Icons.Default.ReportProblem
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IdeiaStatusBadge(status = ideia.status)
                StatusBadge(
                    text = tipoLabel,
                    color = MaterialTheme.colorScheme.primary,
                    useWhiteText = false
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(100))
                    .background(statusColor.copy(alpha = if (isDarkTheme) 0.6f else 0.8f))
            )

            Text(
                text = ideia.titulo,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() }
            )

            Text(
                text = ideia.descricao,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = tipoIcon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.ideia_detalhe_tipo) + ": $tipoLabel",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PremiumIdeiaInfoCard(
    ideia: Ideia,
    isDarkTheme: Boolean,
    jaVotou: Boolean,
    onUpvote: () -> Unit
) {
    val dataCriacao = remember(ideia.dataCriacao) {
        formatLongDate(ideia.dataCriacao)
    }
    val dataAvaliacao = remember(ideia.dataAvaliacao) {
        ideia.dataAvaliacao?.let { formatLongDate(it) }
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoRow(
                icon = Icons.Default.Person,
                label = stringResource(R.string.ideia_detalhe_autor),
                value = ideia.autorNome,
                isDarkTheme = isDarkTheme
            )

            InfoRow(
                icon = Icons.Outlined.Category,
                label = stringResource(R.string.ideia_detalhe_area),
                value = stringResource(ideia.area.labelRes()),
                isDarkTheme = isDarkTheme
            )

            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = stringResource(R.string.ideia_detalhe_data_criacao),
                value = dataCriacao,
                isDarkTheme = isDarkTheme
            )

            dataAvaliacao?.let { avaliacao ->
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = stringResource(R.string.ideia_detalhe_data_avaliacao),
                    value = avaliacao,
                    isDarkTheme = isDarkTheme
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.15f else 0.1f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = stringResource(R.string.cd_upvote),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(R.string.ideia_detalhe_upvotes),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = ideia.upvotes.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (!jaVotou) {
                    InovagabButton(
                        text = stringResource(R.string.ideia_detalhe_apoiar),
                        onClick = onUpvote,
                        variant = InovagabButtonVariant.Secondary,
                        leadingIcon = Icons.Default.KeyboardArrowUp
                    )
                }
            }
        }
    }
}

@Composable
private fun PremiumOrientacaoCard(
    orientacao: OrientacaoEstrategica,
    isDarkTheme: Boolean
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.tertiary.copy(alpha = if (isDarkTheme) 0.15f else 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Explore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(R.string.ideia_detalhe_orientacao),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = orientacao.titulo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (orientacao.descricao.isNotBlank()) {
                    Text(
                        text = orientacao.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    isDarkTheme: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.15f else 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
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
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
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
private fun PremiumAvaliacaoSection(
    impacto: Float,
    esforco: Float,
    score: Int,
    feedback: String,
    errorMessageRes: Int?,
    isDarkTheme: Boolean,
    onImpactoChange: (Float) -> Unit,
    onEsforcoChange: (Float) -> Unit,
    onFeedbackChange: (String) -> Unit,
    onAprovar: () -> Unit,
    onReprovar: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {}
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.ideia_detalhe_avaliacao),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )

            SliderSection(
                label = stringResource(R.string.ideia_detalhe_impacto),
                value = impacto,
                onValueChange = onImpactoChange
            )
            
            SliderSection(
                label = stringResource(R.string.ideia_detalhe_esforco),
                value = esforco,
                onValueChange = onEsforcoChange
            )

            ScoreDisplay(score = score, isDarkTheme = isDarkTheme)

            OutlinedTextField(
                value = feedback,
                onValueChange = onFeedbackChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text(stringResource(R.string.ideia_detalhe_feedback)) },
                placeholder = { Text(stringResource(R.string.ideia_detalhe_feedback_placeholder)) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            errorMessageRes?.let { messageRes ->
                Text(
                    text = stringResource(messageRes),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InovagabButton(
                    text = stringResource(R.string.action_reject),
                    onClick = onReprovar,
                    variant = InovagabButtonVariant.Neutral,
                    modifier = Modifier.weight(1f)
                )
                InovagabButton(
                    text = stringResource(R.string.action_approve),
                    onClick = onAprovar,
                    variant = InovagabButtonVariant.Success,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SliderSection(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    val sliderValueDesc = stringResource(R.string.cd_slider_value, label, value.toInt())

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${value.toInt()}/5",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 1f..5f,
            steps = 3,
            modifier = Modifier.semantics {
                contentDescription = label
                stateDescription = sliderValueDesc
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun ScoreDisplay(
    score: Int,
    isDarkTheme: Boolean
) {
    val backgroundColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.ideia_detalhe_score_priorizacao),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PremiumScoreCard(
    impacto: Int,
    esforco: Int,
    score: Int,
    isDarkTheme: Boolean
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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

            ScoreDotsIndicator(
                label = stringResource(R.string.ideia_detalhe_esforco),
                value = esforco,
                color = MaterialTheme.colorScheme.tertiary
            )

            ScoreDisplay(score = score, isDarkTheme = isDarkTheme)
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
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$value/$maxValue",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(maxValue) { index ->
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(
                            if (index < value) color else color.copy(alpha = 0.2f)
                        )
                )
            }
        }
    }
}

@Composable
private fun SuccessCard() {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) {
        SuccessGreen.copy(alpha = 0.15f)
    } else {
        SuccessGreen.copy(alpha = 0.1f)
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, SuccessGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
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
                    titulo = "Sistema de Rastreamento GPS",
                    descricao = "Implementar GPS em toda a frota para melhor controle de rotas e redução de custos operacionais.",
                    tipo = TipoIdeia.IDEIA,
                    area = AreaAtuacao.LOGISTICA,
                    status = StatusIdeia.PENDENTE,
                    autorId = "user1",
                    autorNome = "João Silva"
                ),
                perfil = PerfilUsuario.GESTOR
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
                    autorNome = "Maria Santos"
                ),
                perfil = PerfilUsuario.GESTOR,
                impacto = 4f,
                esforco = 2f
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
                    esforcoEstimado = 3
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
