package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.EmptyState
import com.gtnix.aguiabranca.presentation.components.IdeiaCard
import com.gtnix.aguiabranca.presentation.components.ShimmerListPlaceholder
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.util.UiState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IdeiasScreen(
    viewModel: IdeiasViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNovaIdeia: () -> Unit,
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
                title = "Ideias",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible,
                enter = scaleIn(),
                exit = scaleOut()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToNovaIdeia,
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.cd_add_idea)
                    )
                }
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is UiState.Initial, is UiState.Loading -> {
                ShimmerListPlaceholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is UiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Erro ao carregar ideias",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            is UiState.Success -> {
                val ideias = state.data

                AnimatedVisibility(
                    visible = ideias.isEmpty(),
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
                            icon = Icons.Default.Lightbulb,
                            title = stringResource(R.string.empty_ideas_title),
                            subtitle = stringResource(R.string.empty_ideas_desc),
                            actionLabel = stringResource(R.string.action_create_first_idea),
                            onActionClick = onNavigateToNovaIdeia
                        )
                    }
                }

                AnimatedVisibility(
                    visible = ideias.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val showQuickActions = viewModel.currentUser?.perfil == PerfilUsuario.GESTOR
                    
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = ideias,
                            key = { it.id }
                        ) { ideia ->
                            IdeiaCard(
                                ideia = ideia,
                                onClick = { onNavigateToDetalhe(ideia.id) },
                                showQuickActions = showQuickActions,
                                onAprovar = { viewModel.aprovarIdeia(ideia) },
                                onReprovar = { viewModel.reprovarIdeia(ideia) },
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
@Composable
private fun IdeiasScreenPreview() {
    InovagabTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Preview - IdeiasScreen")
        }
    }
}
