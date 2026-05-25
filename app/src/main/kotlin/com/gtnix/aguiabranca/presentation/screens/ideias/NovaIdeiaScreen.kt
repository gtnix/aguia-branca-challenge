package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

@Composable
fun NovaIdeiaScreen(
    viewModel: NovaIdeiaViewModel,
    onNavigateBack: () -> Unit,
    onIdeiaCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NovaIdeiaScreenContent(
        uiState = uiState,
        onTituloChange = viewModel::onTituloChange,
        onDescricaoChange = viewModel::onDescricaoChange,
        onAreaChange = viewModel::onAreaChange,
        onTipoChange = viewModel::onTipoChange,
        onSalvar = {
            viewModel.salvar {
                onIdeiaCreated()
            }
        },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NovaIdeiaScreenContent(
    uiState: NovaIdeiaUiState,
    onTituloChange: (String) -> Unit,
    onDescricaoChange: (String) -> Unit,
    onAreaChange: (AreaAtuacao) -> Unit,
    onTipoChange: (TipoIdeia) -> Unit,
    onSalvar: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var areaExpanded by remember { mutableStateOf(false) }
    var tipoExpanded by remember { mutableStateOf(false) }
    var orientacoesExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val isFormValid = uiState.titulo.isNotBlank() && uiState.descricao.isNotBlank()

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.action_suggest_innovation),
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.orientacoes.isNotEmpty()) {
                OrientacoesCard(
                    orientacoes = uiState.orientacoes,
                    expanded = orientacoesExpanded,
                    onToggle = { orientacoesExpanded = !orientacoesExpanded }
                )
            } else {
                Text(
                    text = stringResource(R.string.new_idea_helper_text),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = onTituloChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.new_idea_title_label)) },
                placeholder = { Text(stringResource(R.string.new_idea_title_placeholder)) },
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                isError = uiState.errorMessage != null && uiState.titulo.isBlank()
            )

            OutlinedTextField(
                value = uiState.descricao,
                onValueChange = onDescricaoChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text(stringResource(R.string.new_idea_desc_label)) },
                placeholder = { Text(stringResource(R.string.new_idea_desc_placeholder)) },
                shape = MaterialTheme.shapes.small,
                isError = uiState.errorMessage != null && uiState.descricao.isBlank()
            )

            ExposedDropdownMenuBox(
                expanded = tipoExpanded,
                onExpandedChange = { tipoExpanded = !tipoExpanded }
            ) {
                OutlinedTextField(
                    value = when (uiState.tipo) {
                        TipoIdeia.IDEIA -> "Ideia de Melhoria"
                        TipoIdeia.PROBLEMA -> "Problema Identificado"
                    },
                    onValueChange = {},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    label = { Text("Tipo") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded)
                    },
                    shape = MaterialTheme.shapes.small
                )
                ExposedDropdownMenu(
                    expanded = tipoExpanded,
                    onDismissRequest = { tipoExpanded = false }
                ) {
                    TipoIdeia.entries.forEach { tipo ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    when (tipo) {
                                        TipoIdeia.IDEIA -> "Ideia de Melhoria"
                                        TipoIdeia.PROBLEMA -> "Problema Identificado"
                                    }
                                )
                            },
                            onClick = {
                                onTipoChange(tipo)
                                tipoExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = areaExpanded,
                onExpandedChange = { areaExpanded = !areaExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.area.name.replace("_", " "),
                    onValueChange = {},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    label = { Text("Área de Atuação") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = areaExpanded)
                    },
                    shape = MaterialTheme.shapes.small
                )
                ExposedDropdownMenu(
                    expanded = areaExpanded,
                    onDismissRequest = { areaExpanded = false }
                ) {
                    AreaAtuacao.entries.forEach { area ->
                        DropdownMenuItem(
                            text = { Text(area.name.replace("_", " ")) },
                            onClick = {
                                onAreaChange(area)
                                areaExpanded = false
                            }
                        )
                    }
                }
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSalvar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading && isFormValid,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    disabledContentColor = MaterialTheme.colorScheme.outline
                )
            ) {
                Text(if (uiState.isLoading) stringResource(R.string.loading) else stringResource(R.string.action_send_evaluation))
            }

            if (!isFormValid && !uiState.isLoading) {
                Text(
                    text = "Preencha título e descrição para habilitar o envio",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun OrientacoesCard(
    orientacoes: List<OrientacaoEstrategica>,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    val accentColor = MaterialTheme.colorScheme.primary
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle,
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Foco estratégico",
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Column {
                        Text(
                            text = "Foco Estratégico Atual",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                        if (!expanded) {
                            Text(
                                text = "${orientacoes.size} orientações ativas",
                                style = MaterialTheme.typography.bodySmall,
                                color = accentColor
                            )
                        }
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Recolher orientações" else "Expandir orientações",
                    tint = contentColor
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    orientacoes.forEach { orientacao ->
                        Text(
                            text = "- ${orientacao.titulo}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = accentColor,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NovaIdeiaScreenPreview() {
    InovagabTheme {
        NovaIdeiaScreenContent(
            uiState = NovaIdeiaUiState(),
            onTituloChange = {},
            onDescricaoChange = {},
            onAreaChange = {},
            onTipoChange = {},
            onSalvar = {},
            onNavigateBack = {}
        )
    }
}
