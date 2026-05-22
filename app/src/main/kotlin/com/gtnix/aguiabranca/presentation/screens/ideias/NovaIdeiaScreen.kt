package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaTheme

@OptIn(ExperimentalMaterial3Api::class)
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

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Ideia") },
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
            }

            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = onTituloChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Título da Ideia") },
                placeholder = { Text("Descreva sua ideia em uma frase") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            // Descrição
            OutlinedTextField(
                value = uiState.descricao,
                onValueChange = onDescricaoChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Descrição") },
                placeholder = { Text("Detalhe sua ideia, problema identificado e solução proposta") },
                shape = RoundedCornerShape(8.dp)
            )

            // Tipo (Ideia ou Problema)
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
                    shape = RoundedCornerShape(8.dp)
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

            // Área de Atuação
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
                    shape = RoundedCornerShape(8.dp)
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

            // Mensagem de erro
            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botão Salvar
            Button(
                onClick = onSalvar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading && uiState.titulo.isNotBlank() && uiState.descricao.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submeter Ideia")
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
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onToggle
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Orientações Estratégicas",
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Recolher" else "Expandir"
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    orientacoes.forEach { orientacao ->
                        Text(
                            text = "• ${orientacao.titulo}",
                            style = MaterialTheme.typography.bodyMedium,
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
    AguiaBrancaTheme {
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
