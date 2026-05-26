package com.gtnix.aguiabranca.presentation.screens.projetos

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.gtnix.aguiabranca.R
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.EmptyState
import com.gtnix.aguiabranca.presentation.components.ProjetoCard
import com.gtnix.aguiabranca.presentation.components.SkeletonListPlaceholder
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
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
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}

data class ProjetosUiState(
    val isLoading: Boolean = false,
    val projetos: List<Projeto> = emptyList(),
    val errorMessage: String? = null
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjetosScreen(
    viewModel: ProjetosViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onNavigateToNovoProjeto: () -> Unit,
    onNavigateToDetalhe: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val isFabVisible by remember {
        derivedStateOf {
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val scrollOffset = listState.firstVisibleItemScrollOffset
            firstVisibleIndex == 0 || scrollOffset <= 0
        }
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.projetos_title),
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            val isDarkTheme = isSystemInDarkTheme()
            val secondaryColor = MaterialTheme.colorScheme.secondary
            
            AnimatedVisibility(
                visible = isFabVisible,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToNovoProjeto,
                    containerColor = secondaryColor,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    ),
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(
                            elevation = 16.dp,
                            shape = CircleShape,
                            ambientColor = secondaryColor.copy(alpha = if (isDarkTheme) 0.4f else 0.3f),
                            spotColor = secondaryColor.copy(alpha = if (isDarkTheme) 0.4f else 0.3f)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_project),
                        modifier = Modifier.size(24.dp)
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

            else -> {
                AnimatedVisibility(
                    visible = uiState.projetos.isEmpty() && !uiState.isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyState(
                            icon = Icons.Default.Folder,
                            title = stringResource(R.string.empty_projects_title),
                            subtitle = stringResource(R.string.empty_projects_desc)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = uiState.projetos.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(
                            horizontal = ScreenPadding.Horizontal,
                            vertical = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.projetos,
                            key = { it.id }
                        ) { projeto ->
                            ProjetoCard(
                                projeto = projeto,
                                onClick = { onNavigateToDetalhe(projeto.id) },
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun ProjetosScreenPreview() {
    InovagabTheme {
        Scaffold(
            topBar = {
                AguiaTopBar(title = stringResource(R.string.projetos_title))
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(
                    horizontal = ScreenPadding.Horizontal,
                    vertical = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    listOf(
                        Projeto(
                            id = "1",
                            nome = "Monitoramento de Emissões",
                            objetivo = "Reduzir emissões de CO2 da frota em 30% com monitoramento em tempo real",
                            descricao = "Projeto de modernização",
                            area = AreaAtuacao.LOGISTICA,
                            status = StatusProjeto.EM_ANDAMENTO,
                            responsavelId = "user1",
                            responsavelNome = "Carlos Manager",
                            progresso = 45
                        ),
                        Projeto(
                            id = "2",
                            nome = "Sistema de Gestão ESG",
                            objetivo = "Implementar dashboard de métricas ESG integrado",
                            descricao = "Novo sistema de gestão",
                            area = AreaAtuacao.TI,
                            status = StatusProjeto.PLANEJADO,
                            responsavelId = "user2",
                            responsavelNome = "Ana Tech",
                            progresso = 15
                        )
                    )
                ) { projeto ->
                    ProjetoCard(
                        projeto = projeto,
                        onClick = {}
                    )
                }
            }
        }
    }
}
