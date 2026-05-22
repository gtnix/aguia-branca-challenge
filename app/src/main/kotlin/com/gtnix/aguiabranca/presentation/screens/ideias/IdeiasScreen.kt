package com.gtnix.aguiabranca.presentation.screens.ideias

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdeiasScreen(
    viewModel: IdeiasViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNovaIdeia: () -> Unit,
    onNavigateToDetalhe: (String) -> Unit
) {
    val uiState = viewModel.uiState

    IdeiasScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToNovaIdeia = onNavigateToNovaIdeia,
        onNavigateToDetalhe = onNavigateToDetalhe
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IdeiasScreenContent(
    uiState: IdeiasUiState,
    onNavigateBack: () -> Unit,
    onNavigateToNovaIdeia: () -> Unit,
    onNavigateToDetalhe: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ideias") },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNovaIdeia,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nova Ideia"
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.ideias.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhuma ideia ainda",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Toque no + para adicionar sua primeira ideia",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.ideias) { ideia ->
                    IdeiaCard(
                        ideia = ideia,
                        onClick = { onNavigateToDetalhe(ideia.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IdeiaCard(
    ideia: Ideia,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ideia.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ideia.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Chip(
                        text = ideia.area.name.replace("_", " "),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                    StatusChip(status = ideia.status)
                }
            }
        }
    }
}

@Composable
private fun Chip(
    text: String,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun StatusChip(status: StatusIdeia) {
    val (color, text) = when (status) {
        StatusIdeia.PENDENTE -> MaterialTheme.colorScheme.outline to "Pendente"
        StatusIdeia.EM_ANALISE -> MaterialTheme.colorScheme.tertiary to "Em Análise"
        StatusIdeia.APROVADA -> MaterialTheme.colorScheme.primary to "Aprovada"
        StatusIdeia.REPROVADA -> MaterialTheme.colorScheme.error to "Reprovada"
        StatusIdeia.CONVERTIDA_PROJETO -> MaterialTheme.colorScheme.secondary to "Projeto"
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
private fun IdeiasScreenPreview() {
    AguiaBrancaTheme {
        IdeiasScreenContent(
            uiState = IdeiasUiState(
                ideias = listOf(
                    Ideia(
                        id = "1",
                        titulo = "Sistema de Rastreamento GPS",
                        descricao = "Implementar GPS em toda a frota para melhor controle",
                        tipo = TipoIdeia.IDEIA,
                        area = AreaAtuacao.LOGISTICA,
                        status = StatusIdeia.APROVADA,
                        autorId = "user1",
                        autorNome = "João Silva"
                    ),
                    Ideia(
                        id = "2",
                        titulo = "Redução de Papel",
                        descricao = "Digitalizar documentos para reduzir uso de papel",
                        tipo = TipoIdeia.IDEIA,
                        area = AreaAtuacao.OPERACOES,
                        status = StatusIdeia.PENDENTE,
                        autorId = "user2",
                        autorNome = "Maria Santos"
                    )
                )
            ),
            onNavigateBack = {},
            onNavigateToNovaIdeia = {},
            onNavigateToDetalhe = {}
        )
    }
}
