package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

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

    LaunchedEffect(uiState.actionSuccess) {
        if (uiState.actionSuccess) {
            snackbarHostState.showSnackbar("Ação realizada com sucesso!")
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhe da Ideia") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                    CircularProgressIndicator()
                }
            }

            uiState.ideia == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ideia não encontrada",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                val ideia = uiState.ideia
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = ideia.titulo,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = ideia.descricao,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Autor",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = ideia.autorNome,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Área",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = ideia.area.name.replace("_", " "),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            StatusChip(status = ideia.status)
                        }
                    }

                    if (uiState.perfil == PerfilUsuario.OPERADOR && ideia.feedback != null) {
                        FeedbackCard(feedback = ideia.feedback)
                    }

                    if (uiState.perfil == PerfilUsuario.GESTOR || uiState.perfil == PerfilUsuario.LIDER) {
                        when (ideia.status) {
                            StatusIdeia.PENDENTE -> {
                                Button(
                                    onClick = onIniciarAnalise,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Iniciar Análise")
                                }
                            }

                            StatusIdeia.EM_ANALISE -> {
                                AvaliacaoSection(
                                    impacto = uiState.impacto,
                                    esforco = uiState.esforco,
                                    score = uiState.scorePriorizacao,
                                    feedback = uiState.feedback,
                                    errorMessage = uiState.errorMessage,
                                    onImpactoChange = onImpactoChange,
                                    onEsforcoChange = onEsforcoChange,
                                    onFeedbackChange = onFeedbackChange,
                                    onAprovar = onAprovar,
                                    onReprovar = onReprovar
                                )
                            }

                            StatusIdeia.APROVADA -> {
                                if (ideia.impactoEstimado > 0 || ideia.esforcoEstimado > 0) {
                                    ScoreResumoCard(
                                        impacto = ideia.impactoEstimado,
                                        esforco = ideia.esforcoEstimado,
                                        score = ideia.scorePriorizacao
                                    )
                                }
                                if (ideia.feedback != null) {
                                    FeedbackCard(feedback = ideia.feedback)
                                }
                                if (uiState.perfil == PerfilUsuario.GESTOR || uiState.perfil == PerfilUsuario.LIDER) {
                                    Button(
                                        onClick = { onCriarProjeto(ideia.id) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Folder,
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(R.string.ideia_criar_projeto))
                                    }
                                }
                            }

                            else -> {
                                if (ideia.impactoEstimado > 0 || ideia.esforcoEstimado > 0) {
                                    ScoreResumoCard(
                                        impacto = ideia.impactoEstimado,
                                        esforco = ideia.esforcoEstimado,
                                        score = ideia.scorePriorizacao
                                    )
                                }
                                if (ideia.feedback != null) {
                                    FeedbackCard(feedback = ideia.feedback)
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState.actionSuccess,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Ação realizada com sucesso!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AvaliacaoSection(
    impacto: Float,
    esforco: Float,
    score: Int,
    feedback: String,
    errorMessage: String?,
    onImpactoChange: (Float) -> Unit,
    onEsforcoChange: (Float) -> Unit,
    onFeedbackChange: (String) -> Unit,
    onAprovar: () -> Unit,
    onReprovar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Avaliação",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Impacto Estimado: ${impacto.toInt()}",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = impacto,
                onValueChange = onImpactoChange,
                valueRange = 1f..5f,
                steps = 3
            )

            Text(
                text = "Esforço Estimado: ${esforco.toInt()}",
                style = MaterialTheme.typography.bodyMedium
            )
            Slider(
                value = esforco,
                onValueChange = onEsforcoChange,
                valueRange = 1f..5f,
                steps = 3
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Score de Priorização",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "$score",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            OutlinedTextField(
                value = feedback,
                onValueChange = onFeedbackChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text("Feedback") },
                placeholder = { Text("Deixe seu feedback para o autor da ideia") },
                shape = RoundedCornerShape(8.dp)
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
                Button(
                    onClick = onReprovar,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.action_reject))
                }
                Button(
                    onClick = onAprovar,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.action_approve))
                }
            }
        }
    }
}

@Composable
private fun FeedbackCard(feedback: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Feedback do Gestor",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = feedback,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun ScoreResumoCard(impacto: Int, esforco: Int, score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Priorização",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Impacto", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "$impacto",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Esforço", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "$esforco",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Score", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "$score",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: StatusIdeia) {
    val (color, text) = when (status) {
        StatusIdeia.PENDENTE -> MaterialTheme.colorScheme.outline to stringResource(R.string.status_awaiting_evaluation)
        StatusIdeia.EM_ANALISE -> MaterialTheme.colorScheme.tertiary to stringResource(R.string.status_under_analysis)
        StatusIdeia.APROVADA -> MaterialTheme.colorScheme.primary to stringResource(R.string.status_idea_approved)
        StatusIdeia.REPROVADA -> MaterialTheme.colorScheme.error to stringResource(R.string.status_not_prioritized)
        StatusIdeia.CONVERTIDA_PROJETO -> MaterialTheme.colorScheme.secondary to stringResource(R.string.status_converted_project)
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

@Preview(showBackground = true, showSystemUi = true)
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
