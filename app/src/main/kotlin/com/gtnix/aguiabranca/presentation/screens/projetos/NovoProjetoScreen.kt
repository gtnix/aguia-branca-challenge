package com.gtnix.aguiabranca.presentation.screens.projetos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.session.UserSession
import com.gtnix.aguiabranca.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// =========================================================================
// ViewModel
// =========================================================================

@HiltViewModel
class NovoProjetoViewModel @Inject constructor(
    private val projetoRepository: ProjetoRepository,
    private val ideiaRepository: IdeiaRepository,
    private val userSession: UserSession,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(NovoProjetoUiState())
        private set

    private val ideiaId: String? = savedStateHandle[Destination.NovoProjeto.ARG_IDEIA_ID]

    init {
        if (!ideiaId.isNullOrBlank()) {
            carregarIdeia(ideiaId)
        }
    }

    private fun carregarIdeia(id: String) {
        viewModelScope.launch {
            try {
                val ideia = ideiaRepository.buscarPorId(id)
                if (ideia != null) {
                    uiState = uiState.copy(
                        nome = ideia.titulo,
                        area = ideia.area,
                        ideiaOrigemId = id
                    )
                }
            } catch (_: Exception) { }
        }
    }

    fun onNomeChange(value: String) {
        uiState = uiState.copy(nome = value)
    }

    fun onObjetivoChange(value: String) {
        uiState = uiState.copy(objetivo = value)
    }

    fun onAreaChange(value: AreaAtuacao) {
        uiState = uiState.copy(area = value)
    }

    fun onInvestimentoChange(value: String) {
        uiState = uiState.copy(investimentoEstimado = value)
    }

    fun onRetornoChange(value: String) {
        uiState = uiState.copy(retornoEstimadoMensal = value)
    }

    fun salvar(onSuccess: () -> Unit) {
        if (uiState.nome.isBlank() || uiState.objetivo.isBlank()) {
            uiState = uiState.copy(errorMessage = "Preencha todos os campos obrigatórios")
            return
        }

        val investimento = uiState.investimentoEstimado.toDoubleOrNull() ?: 0.0
        val retorno = uiState.retornoEstimadoMensal.toDoubleOrNull() ?: 0.0

        uiState = uiState.copy(isSaving = true)
        val projetoId = UUID.randomUUID().toString()

        viewModelScope.launch {
            try {
                val projeto = Projeto(
                    id = projetoId,
                    nome = uiState.nome,
                    objetivo = uiState.objetivo,
                    descricao = "",
                    area = uiState.area,
                    status = StatusProjeto.PLANEJADO,
                    ideiaOrigemId = uiState.ideiaOrigemId,
                    responsavelId = userSession.userId,
                    responsavelNome = userSession.userName,
                    investimentoEstimado = investimento,
                    retornoEstimadoMensal = retorno
                )
                projetoRepository.salvar(projeto)

                val origem = uiState.ideiaOrigemId
                if (!origem.isNullOrBlank()) {
                    ideiaRepository.atualizarStatus(origem, StatusIdeia.CONVERTIDA_PROJETO)
                    ideiaRepository.vincularProjeto(origem, projetoId)
                }

                uiState = uiState.copy(isSaving = false)
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isSaving = false,
                    errorMessage = "Erro ao salvar projeto"
                )
            }
        }
    }
}

data class NovoProjetoUiState(
    val nome: String = "",
    val objetivo: String = "",
    val area: AreaAtuacao = AreaAtuacao.OPERACOES,
    val investimentoEstimado: String = "",
    val retornoEstimadoMensal: String = "",
    val ideiaOrigemId: String? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

// =========================================================================
// Screen
// =========================================================================

@Composable
fun NovoProjetoScreen(
    viewModel: NovoProjetoViewModel,
    onNavigateBack: () -> Unit,
    onProjetoCreated: () -> Unit
) {
    val uiState = viewModel.uiState

    NovoProjetoContent(
        uiState = uiState,
        onNomeChange = viewModel::onNomeChange,
        onObjetivoChange = viewModel::onObjetivoChange,
        onAreaChange = viewModel::onAreaChange,
        onInvestimentoChange = viewModel::onInvestimentoChange,
        onRetornoChange = viewModel::onRetornoChange,
        onSalvar = { viewModel.salvar(onProjetoCreated) },
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NovoProjetoContent(
    uiState: NovoProjetoUiState,
    onNomeChange: (String) -> Unit,
    onObjetivoChange: (String) -> Unit,
    onAreaChange: (AreaAtuacao) -> Unit,
    onInvestimentoChange: (String) -> Unit,
    onRetornoChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var areaExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.novo_projeto_titulo)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_voltar)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.ideiaOrigemId != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.novo_projeto_origem_ideia, uiState.ideiaOrigemId.take(8)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = uiState.nome,
                onValueChange = onNomeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.novo_projeto_nome)) },
                placeholder = { Text(stringResource(R.string.novo_projeto_nome_placeholder)) },
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            // Area dropdown
            ExposedDropdownMenuBox(
                expanded = areaExpanded,
                onExpandedChange = { areaExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.area.name.replace("_", " "),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text(stringResource(R.string.novo_projeto_area)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = areaExpanded) },
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

            OutlinedTextField(
                value = uiState.objetivo,
                onValueChange = onObjetivoChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                label = { Text(stringResource(R.string.novo_projeto_objetivo)) },
                placeholder = { Text(stringResource(R.string.novo_projeto_objetivo_placeholder)) },
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = uiState.investimentoEstimado,
                onValueChange = onInvestimentoChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.novo_projeto_investimento)) },
                placeholder = { Text(stringResource(R.string.novo_projeto_investimento_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.retornoEstimadoMensal,
                onValueChange = onRetornoChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.novo_projeto_retorno)) },
                placeholder = { Text(stringResource(R.string.novo_projeto_retorno_placeholder)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

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
                enabled = !uiState.isSaving,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (uiState.isSaving) stringResource(R.string.loading)
                    else stringResource(R.string.novo_projeto_salvar)
                )
            }
        }
    }
}
