package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import kotlin.math.roundToInt

@Composable
fun NovaOrientacaoScreen(
    viewModel: NovaOrientacaoViewModel,
    onNavigateBack: () -> Unit,
    onOrientacaoCreated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NovaOrientacaoScreenContent(
        uiState = uiState,
        onTituloChange = viewModel::onTituloChange,
        onDescricaoChange = viewModel::onDescricaoChange,
        onCategoriaChange = viewModel::onCategoriaChange,
        onPrioridadeChange = viewModel::onPrioridadeChange,
        onSalvar = {
            viewModel.salvar {
                onOrientacaoCreated()
            }
        },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NovaOrientacaoScreenContent(
    uiState: NovaOrientacaoUiState,
    onTituloChange: (String) -> Unit,
    onDescricaoChange: (String) -> Unit,
    onCategoriaChange: (CategoriaOrientacao) -> Unit,
    onPrioridadeChange: (Int) -> Unit,
    onSalvar: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var categoriaExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val isFormValid = uiState.titulo.isNotBlank() && uiState.descricao.isNotBlank()

    LaunchedEffect(uiState.errorMessageRes) {
        uiState.errorMessageRes?.let { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Nova Orientação",
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
            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = onTituloChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Título da Orientação *") },
                placeholder = { Text("Ex: Reduzir custos operacionais em 15%") },
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                isError = uiState.errorMessageRes != null && uiState.titulo.isBlank()
            )

            OutlinedTextField(
                value = uiState.descricao,
                onValueChange = onDescricaoChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text("Descrição *") },
                placeholder = { Text("Descreva a orientação estratégica e seus objetivos") },
                shape = MaterialTheme.shapes.small,
                isError = uiState.errorMessageRes != null && uiState.descricao.isBlank()
            )

            ExposedDropdownMenuBox(
                expanded = categoriaExpanded,
                onExpandedChange = { categoriaExpanded = !categoriaExpanded }
            ) {
                OutlinedTextField(
                    value = getCategoriaLabel(uiState.categoria),
                    onValueChange = {},
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded)
                    },
                    shape = MaterialTheme.shapes.small
                )
                ExposedDropdownMenu(
                    expanded = categoriaExpanded,
                    onDismissRequest = { categoriaExpanded = false }
                ) {
                    CategoriaOrientacao.entries.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(getCategoriaLabel(categoria)) },
                            onClick = {
                                onCategoriaChange(categoria)
                                categoriaExpanded = false
                            }
                        )
                    }
                }
            }

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Prioridade",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = getPrioridadeLabel(uiState.prioridade),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = getPrioridadeColor(uiState.prioridade)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = uiState.prioridade.toFloat(),
                    onValueChange = { onPrioridadeChange(it.roundToInt()) },
                    valueRange = 1f..5f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = getPrioridadeColor(uiState.prioridade),
                        activeTrackColor = getPrioridadeColor(uiState.prioridade)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Crítica",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Baixa",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            uiState.errorMessageRes?.let { messageRes ->
                Text(
                    text = stringResource(messageRes),
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
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    disabledContentColor = MaterialTheme.colorScheme.outline
                )
            ) {
                Text(
                    text = if (uiState.isLoading) "Salvando..." else "Criar Orientação",
                    color = if (!uiState.isLoading && isFormValid) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                )
            }

            if (!isFormValid && !uiState.isLoading) {
                Text(
                    text = "* Campos obrigatórios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

private fun getCategoriaLabel(categoria: CategoriaOrientacao): String {
    return when (categoria) {
        CategoriaOrientacao.REDUCAO_CUSTOS -> "Redução de Custos"
        CategoriaOrientacao.QUALIDADE_SERVICO -> "Qualidade de Serviço"
        CategoriaOrientacao.INOVACAO_TECNOLOGICA -> "Inovação Tecnológica"
        CategoriaOrientacao.SUSTENTABILIDADE -> "Sustentabilidade"
        CategoriaOrientacao.SEGURANCA -> "Segurança do Trabalho"
        CategoriaOrientacao.EXPERIENCIA_CLIENTE -> "Experiência do Cliente"
        CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> "Eficiência Operacional"
    }
}

private fun getPrioridadeLabel(prioridade: Int): String {
    return when (prioridade) {
        1 -> "P1 - Crítica"
        2 -> "P2 - Alta"
        3 -> "P3 - Média"
        4 -> "P4 - Normal"
        5 -> "P5 - Baixa"
        else -> "P$prioridade"
    }
}

@Composable
private fun getPrioridadeColor(prioridade: Int): Color {
    return when (prioridade) {
        1 -> MaterialTheme.colorScheme.error
        2 -> MaterialTheme.colorScheme.secondary
        3 -> MaterialTheme.colorScheme.tertiary
        4 -> MaterialTheme.colorScheme.tertiary
        5 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primary
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NovaOrientacaoScreenPreview() {
    InovagabTheme {
        NovaOrientacaoScreenContent(
            uiState = NovaOrientacaoUiState(),
            onTituloChange = {},
            onDescricaoChange = {},
            onCategoriaChange = {},
            onPrioridadeChange = {},
            onSalvar = {},
            onNavigateBack = {}
        )
    }
}
