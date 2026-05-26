package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.EmptyState
import com.gtnix.aguiabranca.presentation.components.SkeletonListPlaceholder
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import androidx.compose.ui.res.stringResource

@Composable
fun OrientacoesScreen(
    viewModel: OrientacoesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNova: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    val isFabVisible by remember {
        derivedStateOf {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val scrollOffset = listState.firstVisibleItemScrollOffset
            firstVisibleIndex == 0 || scrollOffset <= 0
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Orientações Estratégicas",
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToNova,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nova orientação"
                    )
                }
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                SkeletonListPlaceholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            uiState.orientacoes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.Flag,
                        title = "Nenhuma orientação",
                        subtitle = "Toque no + para criar a primeira orientação estratégica"
                    )
                }
            }

            else -> {
                AnimatedVisibility(
                    visible = uiState.orientacoes.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.orientacoes,
                            key = { it.id }
                        ) { orientacao ->
                            OrientacaoCard(
                                orientacao = orientacao,
                                onDesativar = { viewModel.desativarOrientacao(orientacao.id) },
                                onExcluir = { viewModel.excluirOrientacao(orientacao.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrientacaoCard(
    orientacao: OrientacaoEstrategica,
    onDesativar: () -> Unit,
    onExcluir: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (orientacao.ativa) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = if (orientacao.ativa) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = orientacao.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (orientacao.ativa) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = orientacao.descricao,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (orientacao.ativa) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f) 
                                   else MaterialTheme.colorScheme.outline,
                            maxLines = 2
                        )
                    }
                }
                
                Row {
                    IconButton(onClick = onDesativar) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = if (orientacao.ativa) "Desativar" else "Ativar",
                            tint = if (orientacao.ativa) MaterialTheme.colorScheme.secondary 
                                   else MaterialTheme.colorScheme.outline
                        )
                    }
                    IconButton(onClick = onExcluir) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoriaChip(categoria = orientacao.categoria)
                PrioridadeChip(prioridade = orientacao.prioridade)
                StatusChip(ativa = orientacao.ativa)
            }
        }
    }
}

@Composable
private fun CategoriaChip(categoria: CategoriaOrientacao) {
    val label = when (categoria) {
        CategoriaOrientacao.REDUCAO_CUSTOS -> "Custos"
        CategoriaOrientacao.QUALIDADE_SERVICO -> "Qualidade"
        CategoriaOrientacao.INOVACAO_TECNOLOGICA -> "Inovação"
        CategoriaOrientacao.SUSTENTABILIDADE -> "ESG"
        CategoriaOrientacao.SEGURANCA -> "Segurança"
        CategoriaOrientacao.EXPERIENCIA_CLIENTE -> "Cliente"
        CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> "Eficiência"
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun PrioridadeChip(prioridade: Int) {
    val color = when (prioridade) {
        1 -> MaterialTheme.colorScheme.error
        2 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.tertiary
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.15f)
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "P$prioridade",
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }
    }
}

@Composable
private fun StatusChip(ativa: Boolean) {
    val backgroundColor = if (ativa) MaterialTheme.colorScheme.tertiaryContainer
                         else MaterialTheme.colorScheme.errorContainer
    val contentColor = if (ativa) MaterialTheme.colorScheme.onTertiaryContainer
                       else MaterialTheme.colorScheme.error
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = if (ativa) "Ativa" else "Inativa",
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OrientacaoCardPreview() {
    InovagabTheme {
        OrientacaoCard(
            orientacao = OrientacaoEstrategica(
                id = "1",
                titulo = "Reduzir custos operacionais",
                descricao = "Meta de redução de 15% nos custos de operação até dezembro",
                categoria = CategoriaOrientacao.REDUCAO_CUSTOS,
                prioridade = 1,
                ativa = true,
                criadoPor = "demo-lider"
            ),
            onDesativar = {},
            onExcluir = {}
        )
    }
}
