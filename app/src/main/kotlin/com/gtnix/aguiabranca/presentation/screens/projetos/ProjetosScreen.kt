package com.gtnix.aguiabranca.presentation.screens.projetos

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.EmptyState
import com.gtnix.aguiabranca.presentation.components.PremiumFilterChip
import com.gtnix.aguiabranca.presentation.components.ProjetoCard
import com.gtnix.aguiabranca.presentation.components.SkeletonListPlaceholder
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.NavBarDimensions
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProjetosScreen(
    viewModel: ProjetosViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onNavigateToNovoProjeto: () -> Unit,
    onNavigateToDetalhe: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filtroSelecionado by viewModel.filtroSelecionado.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val isTabScreen = onNavigateBack == null

    val filters = remember {
        listOf(
            FiltroProjeto.TODOS to R.string.filter_projects_all,
            FiltroProjeto.PLANEJADO to R.string.filter_projects_planned,
            FiltroProjeto.EM_ANDAMENTO to R.string.filter_projects_in_progress,
            FiltroProjeto.PAUSADO to R.string.filter_projects_paused,
            FiltroProjeto.CONCLUIDO to R.string.filter_projects_completed
        )
    }

    if (isTabScreen) {
        ProjetosTabContent(
            uiState = uiState,
            filters = filters,
            filtroSelecionado = filtroSelecionado,
            listState = listState,
            onFilterSelected = viewModel::selecionarFiltro,
            onNavigateToDetalhe = onNavigateToDetalhe
        )
    } else {
        Scaffold(
            topBar = {
                AguiaTopBar(
                    title = stringResource(R.string.projetos_title),
                    onBackClick = onNavigateBack
                )
            }
        ) { paddingValues ->
            ProjetosBody(
                uiState = uiState,
                filters = filters,
                filtroSelecionado = filtroSelecionado,
                listState = listState,
                onFilterSelected = viewModel::selecionarFiltro,
                onNavigateToDetalhe = onNavigateToDetalhe,
                modifier = Modifier.padding(paddingValues),
                bottomContentPadding = 16.dp
            )
        }
    }
}

@Composable
private fun ProjetosTabContent(
    uiState: ProjetosUiState,
    filters: List<Pair<FiltroProjeto, Int>>,
    filtroSelecionado: FiltroProjeto,
    listState: LazyListState,
    onFilterSelected: (FiltroProjeto) -> Unit,
    onNavigateToDetalhe: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AguiaTopBar(
                title = stringResource(R.string.projetos_title),
                onBackClick = null
            )
            ProjetosBody(
                uiState = uiState,
                filters = filters,
                filtroSelecionado = filtroSelecionado,
                listState = listState,
                onFilterSelected = onFilterSelected,
                onNavigateToDetalhe = onNavigateToDetalhe,
                bottomContentPadding = NavBarDimensions.ContentBottomPaddingWithFab
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProjetosBody(
    uiState: ProjetosUiState,
    filters: List<Pair<FiltroProjeto, Int>>,
    filtroSelecionado: FiltroProjeto,
    listState: LazyListState,
    onFilterSelected: (FiltroProjeto) -> Unit,
    onNavigateToDetalhe: (String) -> Unit,
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ScreenPadding.Horizontal, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { (filtro, labelRes) ->
                PremiumFilterChip(
                    label = stringResource(labelRes),
                    selected = filtro == filtroSelecionado,
                    onClick = { onFilterSelected(filtro) }
                )
            }
        }

        when {
            uiState.isLoading -> {
                SkeletonListPlaceholder(
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                AnimatedVisibility(
                    visible = uiState.projetos.isEmpty() && !uiState.isLoading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
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
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = ScreenPadding.Horizontal,
                            end = ScreenPadding.Horizontal,
                            top = 8.dp,
                            bottom = bottomContentPadding
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
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize()) {
                AguiaTopBar(
                    title = stringResource(R.string.projetos_title),
                    onBackClick = null
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
}
