package com.gtnix.aguiabranca.presentation.screens.projetos

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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjetosViewModel @Inject constructor(
    private val projetoRepository: ProjetoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjetosUiState())
    val uiState: StateFlow<ProjetosUiState> = _uiState.asStateFlow()

    init {
        carregarProjetos()
    }

    private fun carregarProjetos() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                projetoRepository.listarTodos().collect { projetos ->
                    _uiState.update { it.copy(isLoading = false, projetos = projetos) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar projetos") }
            }
        }
    }
}

data class ProjetosUiState(
    val isLoading: Boolean = false,
    val projetos: List<Projeto> = emptyList(),
    val errorMessage: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjetosScreen(
    viewModel: ProjetosViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNovoProjeto: () -> Unit,
    onNavigateToDetalhe: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProjetosScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToNovoProjeto = onNavigateToNovoProjeto,
        onNavigateToDetalhe = onNavigateToDetalhe
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjetosScreenContent(
    uiState: ProjetosUiState,
    onNavigateBack: () -> Unit,
    onNavigateToNovoProjeto: () -> Unit,
    onNavigateToDetalhe: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Projetos") },
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
                onClick = onNavigateToNovoProjeto,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Novo Projeto"
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
        } else if (uiState.projetos.isEmpty()) {
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
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhum projeto ainda",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Projetos surgem de ideias aprovadas",
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
                items(
                    items = uiState.projetos,
                    key = { it.id }
                ) { projeto ->
                    ProjetoCard(
                        projeto = projeto,
                        onClick = { onNavigateToDetalhe(projeto.id) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjetoCard(
    projeto: Projeto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = projeto.nome,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = projeto.objetivo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2
                    )
                }
                StatusChip(status = projeto.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de progresso
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progresso",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${projeto.progresso}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { projeto.progresso / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: StatusProjeto) {
    val (color, text) = when (status) {
        StatusProjeto.PLANEJADO -> MaterialTheme.colorScheme.outline to "Planejado"
        StatusProjeto.EM_ANDAMENTO -> MaterialTheme.colorScheme.tertiary to "Em Andamento"
        StatusProjeto.PAUSADO -> MaterialTheme.colorScheme.error to "Pausado"
        StatusProjeto.CONCLUIDO -> MaterialTheme.colorScheme.primary to "Concluído"
        StatusProjeto.CANCELADO -> MaterialTheme.colorScheme.error to "Cancelado"
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
private fun ProjetosScreenPreview() {
    AguiaBrancaTheme {
        ProjetosScreenContent(
            uiState = ProjetosUiState(
                projetos = listOf(
                    Projeto(
                        id = "1",
                        nome = "Modernização da Frota",
                        objetivo = "Substituir veículos antigos por modelos mais eficientes",
                        descricao = "Projeto de modernização",
                        area = AreaAtuacao.LOGISTICA,
                        status = StatusProjeto.EM_ANDAMENTO,
                        responsavelId = "user1",
                        responsavelNome = "Carlos Manager",
                        progresso = 65
                    ),
                    Projeto(
                        id = "2",
                        nome = "Sistema de Gestão",
                        objetivo = "Implementar ERP integrado",
                        descricao = "Novo sistema de gestão",
                        area = AreaAtuacao.TI,
                        status = StatusProjeto.PLANEJADO,
                        responsavelId = "user2",
                        responsavelNome = "Ana Tech",
                        progresso = 10
                    )
                )
            ),
            onNavigateBack = {},
            onNavigateToNovoProjeto = {},
            onNavigateToDetalhe = {}
        )
    }
}
