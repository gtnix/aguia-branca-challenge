package com.gtnix.aguiabranca.presentation.screens.ideias

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.EmptyState
import com.gtnix.aguiabranca.presentation.components.IdeiaCard
import com.gtnix.aguiabranca.presentation.components.PremiumFilterChip
import com.gtnix.aguiabranca.presentation.components.SkeletonListPlaceholder
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.NavBarDimensions
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import com.gtnix.aguiabranca.presentation.util.UiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IdeiasScreen(
    viewModel: IdeiasViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onNavigateToNovaIdeia: () -> Unit,
    onNavigateToDetalhe: (String) -> Unit
) {
    val uiState by viewModel.ideiasFiltradas.collectAsStateWithLifecycle()
    val filtroSelecionado by viewModel.filtroSelecionado.collectAsStateWithLifecycle()
    val actionMessageRes by viewModel.actionMessageRes.collectAsStateWithLifecycle()
    val scoringInProgress by viewModel.scoringInProgress.collectAsStateWithLifecycle()
    val ordenacao by viewModel.ordenacao.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }

    val actionMessage = actionMessageRes?.let { stringResource(it) }

    LaunchedEffect(actionMessage) {
        actionMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearActionMessage()
        }
    }

    val filters = remember {
        listOf(
            FiltroIdeia.TODAS to R.string.filter_all,
            FiltroIdeia.PENDENTES to R.string.filter_pending,
            FiltroIdeia.APROVADAS to R.string.filter_approved,
            FiltroIdeia.EM_PROJETO to R.string.filter_in_project
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AguiaTopBar(
                title = stringResource(viewModel.listTitleRes),
                onBackClick = onNavigateBack,
                actions = {
                    if (viewModel.canEvaluateIdeias) {
                        if (scoringInProgress) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(onClick = viewModel::pontuarComIa) {
                                Icon(
                                    imageVector = Icons.Outlined.AutoAwesome,
                                    contentDescription = stringResource(R.string.cd_score_ia)
                                )
                            }
                        }
                    }
                }
            )
            IdeiasFilterRow(
                filters = filters,
                selectedFilter = filtroSelecionado,
                onFilterSelected = { viewModel.selecionarFiltro(it) }
            )

            if (viewModel.canEvaluateIdeias) {
                OrdenacaoRow(
                    ordenacao = ordenacao,
                    onAlternar = viewModel::alternarOrdenacao
                )
            }
            
            when (val state = uiState) {
                is UiState.Initial, is UiState.Loading -> {
                    SkeletonListPlaceholder(
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is UiState.Error -> {
                    val errorDescription = stringResource(R.string.error_load_ideias)
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = errorDescription,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = state.message.ifBlank { stringResource(R.string.error_unknown) },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                is UiState.Success -> {
                    val ideias = state.data

                    if (ideias.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyState(
                                icon = Icons.Default.Lightbulb,
                                title = stringResource(R.string.empty_ideas_title),
                                subtitle = stringResource(R.string.empty_ideas_desc),
                                actionLabel = if (viewModel.canCreateIdeias) {
                                    stringResource(R.string.action_create_first_idea)
                                } else {
                                    null
                                },
                                onActionClick = if (viewModel.canCreateIdeias) onNavigateToNovaIdeia else null
                            )
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = ScreenPadding.Horizontal,
                                end = ScreenPadding.Horizontal,
                                top = 8.dp,
                                bottom = NavBarDimensions.ContentBottomPadding
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = ideias,
                                key = { it.id }
                            ) { ideia ->
                                IdeiaCard(
                                    ideia = ideia,
                                    onClick = { onNavigateToDetalhe(ideia.id) },
                                    showAuthorName = viewModel.canEvaluateIdeias,
                                    selo = viewModel.seloDe(ideia),
                                    modifier = Modifier.animateItemPlacement()
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
private fun IdeiasFilterRow(
    filters: List<Pair<FiltroIdeia, Int>>,
    selectedFilter: FiltroIdeia,
    onFilterSelected: (FiltroIdeia) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPadding.Horizontal, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (filtro, labelRes) ->
            PremiumFilterChip(
                label = stringResource(labelRes),
                selected = filtro == selectedFilter,
                onClick = { onFilterSelected(filtro) }
            )
        }
    }
}

@Composable
private fun OrdenacaoRow(
    ordenacao: OrdenacaoIdeias,
    onAlternar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPadding.Horizontal)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PremiumFilterChip(
            label = stringResource(R.string.ideias_ordenar_ia),
            selected = ordenacao == OrdenacaoIdeias.RECOMENDACAO_IA,
            onClick = onAlternar
        )
        Text(
            text = stringResource(
                if (ordenacao == OrdenacaoIdeias.FILA) {
                    R.string.ideias_ordem_fila_hint
                } else {
                    R.string.ideias_ordem_ia_hint
                }
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(name = "Light Mode", showBackground = true, showSystemUi = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true, showSystemUi = true)
@Composable
private fun IdeiasScreenPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize()) {
                AguiaTopBar(
                    title = stringResource(R.string.my_ideas_title),
                    onBackClick = null
                )
                IdeiasFilterRow(
                    filters = listOf(
                        FiltroIdeia.TODAS to R.string.filter_all,
                        FiltroIdeia.PENDENTES to R.string.filter_pending,
                        FiltroIdeia.APROVADAS to R.string.filter_approved,
                        FiltroIdeia.EM_PROJETO to R.string.filter_in_project
                    ),
                    selectedFilter = FiltroIdeia.TODAS,
                    onFilterSelected = {}
                )
            }
        }
    }
}
