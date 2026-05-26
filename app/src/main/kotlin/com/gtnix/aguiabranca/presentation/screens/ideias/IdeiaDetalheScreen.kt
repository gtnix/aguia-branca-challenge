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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.IdeiaStatusBadge
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.InovagabButtonVariant
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen

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
    onCriarProjeto: (String) -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isDarkTheme = isSystemInDarkTheme()

    val actionSuccessMessage = stringResource(R.string.ideia_detalhe_acao_sucesso)
    LaunchedEffect(uiState.actionSuccess) {
        if (uiState.actionSuccess) {
            snackbarHostState.showSnackbar(actionSuccessMessage)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDarkTheme) Color.White.copy(alpha = 0.1f)
                                else Color.Black.copy(alpha = 0.05f)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
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
                            contentDescription = null,
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
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    PremiumIdeiaHeader(ideia = ideia)
                    
                    PremiumIdeiaInfoCard(ideia = ideia, isDarkTheme = isDarkTheme)

                    if (uiState.perfil == PerfilUsuario.OPERADOR && ideia.feedback != null) {
                        PremiumFeedbackCard(feedback = ideia.feedback, isDarkTheme = isDarkTheme)
                    }

                    if (uiState.perfil == PerfilUsuario.GESTOR || uiState.perfil == PerfilUsuario.LIDER) {
                        when (ideia.status) {
                            StatusIdeia.PENDENTE -> {
                                InovagabButton(
                                    text = stringResource(R.string.ideia_detalhe_iniciar_analise),
                                    onClick = onIniciarAnalise,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            StatusIdeia.EM_ANALISE -> {
                                PremiumAvaliacaoSection(
                                    impacto = uiState.impacto,
                                    esforco = uiState.esforco,
                                    score = uiState.scorePriorizacao,
                                    feedback = uiState.feedback,
                                    errorMessage = uiState.errorMessage,
                                    isDarkTheme = isDarkTheme,
                                    onImpactoChange = onImpactoChange,
                                    onEsforcoChange = onEsforcoChange,
                                    onFeedbackChange = onFeedbackChange,
                                    onAprovar = onAprovar,
                                    onReprovar = onReprovar
                                )
                            }

                            StatusIdeia.APROVADA -> {
                                if (ideia.impactoEstimado > 0 || ideia.esforcoEstimado > 0) {
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
                                if (uiState.perfil == PerfilUsuario.GESTOR || uiState.perfil == PerfilUsuario.LIDER) {
                                    InovagabButton(
                                        text = stringResource(R.string.ideia_criar_projeto),
                                        onClick = { onCriarProjeto(ideia.id) },
                                        leadingIcon = Icons.Default.Folder,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            else -> {
                                if (ideia.impactoEstimado > 0 || ideia.esforcoEstimado > 0) {
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
                            }
                        }
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
}

@Composable
private fun PremiumIdeiaHeader(ideia: Ideia) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IdeiaStatusBadge(status = ideia.status)
        }
        
        Text(
            text = ideia.titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = ideia.descricao,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PremiumIdeiaInfoCard(
    ideia: Ideia,
    isDarkTheme: Boolean
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
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
                value = ideia.area.name.replace("_", " "),
                isDarkTheme = isDarkTheme
            )
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
    errorMessage: String?,
    isDarkTheme: Boolean,
    onImpactoChange: (Float) -> Unit,
    onEsforcoChange: (Float) -> Unit,
    onFeedbackChange: (String) -> Unit,
    onAprovar: () -> Unit,
    onReprovar: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.ideia_detalhe_avaliacao),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
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
                    variant = InovagabButtonVariant.Ghost,
                    modifier = Modifier.weight(1f)
                )
                InovagabButton(
                    text = stringResource(R.string.action_approve),
                    onClick = onAprovar,
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
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ScoreMetric(
                    label = stringResource(R.string.ideia_detalhe_impacto),
                    value = impacto,
                    color = MaterialTheme.colorScheme.primary,
                    isDarkTheme = isDarkTheme
                )
                ScoreMetric(
                    label = stringResource(R.string.ideia_detalhe_esforco),
                    value = esforco,
                    color = MaterialTheme.colorScheme.tertiary,
                    isDarkTheme = isDarkTheme
                )
                ScoreMetric(
                    label = stringResource(R.string.ideia_detalhe_score),
                    value = score,
                    color = SuccessGreen,
                    isDarkTheme = isDarkTheme,
                    isHighlighted = true
                )
            }
        }
    }
}

@Composable
private fun ScoreMetric(
    label: String,
    value: Int,
    color: Color,
    isDarkTheme: Boolean,
    isHighlighted: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isHighlighted) 64.dp else 56.dp)
                .then(
                    if (isHighlighted) {
                        Modifier.shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = color.copy(alpha = 0.3f),
                            spotColor = color.copy(alpha = 0.3f)
                        )
                    } else Modifier
                )
                .background(
                    color.copy(alpha = if (isDarkTheme) 0.2f else 0.15f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$value",
                style = if (isHighlighted) MaterialTheme.typography.headlineSmall 
                        else MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
            onCriarProjeto = {}
        )
    }
}
