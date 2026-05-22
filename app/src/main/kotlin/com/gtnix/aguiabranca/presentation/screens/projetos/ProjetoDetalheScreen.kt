package com.gtnix.aguiabranca.presentation.screens.projetos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.session.UserSession
import com.gtnix.aguiabranca.presentation.navigation.Destination
import com.gtnix.aguiabranca.presentation.util.formatCurrency
import com.gtnix.aguiabranca.presentation.util.formatPercent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// =========================================================================
// ViewModel
// =========================================================================

@HiltViewModel
class ProjetoDetalheViewModel @Inject constructor(
    private val projetoRepository: ProjetoRepository,
    private val userSession: UserSession,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjetoDetalheUiState())
    val uiState: StateFlow<ProjetoDetalheUiState> = _uiState.asStateFlow()

    private val projetoId: String = savedStateHandle[Destination.ProjetoDetalhe.ARG_PROJETO_ID] ?: ""

    init {
        carregarProjeto()
    }

    private fun carregarProjeto() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val projeto = projetoRepository.buscarPorId(projetoId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        projeto = projeto,
                        perfil = userSession.perfil,
                        progressoSlider = projeto?.progresso?.toFloat() ?: 0f
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar projeto") }
            }
        }
    }

    fun onProgressoChange(value: Float) {
        _uiState.update { it.copy(progressoSlider = value) }
    }

    fun salvarProgresso() {
        val projeto = _uiState.value.projeto ?: return
        viewModelScope.launch {
            try {
                val novoProgresso = _uiState.value.progressoSlider.toInt()
                projetoRepository.atualizarProgresso(projeto.id, novoProgresso)
                val atualizado = projeto.copy(progresso = novoProgresso)
                _uiState.update { it.copy(projeto = atualizado, actionSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao salvar progresso") }
            }
        }
    }

    fun concluirProjeto() {
        val projeto = _uiState.value.projeto ?: return
        viewModelScope.launch {
            try {
                val concluido = projeto.copy(
                    status = StatusProjeto.CONCLUIDO,
                    progresso = 100,
                    dataConclusao = System.currentTimeMillis()
                )
                projetoRepository.salvar(concluido)
                _uiState.update { it.copy(projeto = concluido, progressoSlider = 100f, actionSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao concluir projeto") }
            }
        }
    }
}

data class ProjetoDetalheUiState(
    val isLoading: Boolean = false,
    val projeto: Projeto? = null,
    val perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
    val progressoSlider: Float = 0f,
    val errorMessage: String? = null,
    val actionSuccess: Boolean = false
)

// =========================================================================
// Screen
// =========================================================================

@Composable
fun ProjetoDetalheScreen(
    viewModel: ProjetoDetalheViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProjetoDetalheContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onProgressoChange = viewModel::onProgressoChange,
        onSalvarProgresso = viewModel::salvarProgresso,
        onConcluir = viewModel::concluirProjeto
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjetoDetalheContent(
    uiState: ProjetoDetalheUiState,
    onNavigateBack: () -> Unit,
    onProgressoChange: (Float) -> Unit,
    onSalvarProgresso: () -> Unit,
    onConcluir: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.projeto_detalhe_titulo)) },
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
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.projeto == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.projeto_detalhe_nao_encontrado),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                val projeto = uiState.projeto
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Project info card
                    InfoCard(projeto = projeto)

                    // Financial card
                    FinanceiroCard(projeto = projeto)

                    // Progress section (GESTOR / LIDER only)
                    if (uiState.perfil == PerfilUsuario.GESTOR || uiState.perfil == PerfilUsuario.LIDER) {
                        ProgressoCard(
                            progresso = uiState.progressoSlider,
                            status = projeto.status,
                            onProgressoChange = onProgressoChange,
                            onSalvar = onSalvarProgresso,
                            onConcluir = onConcluir
                        )
                    }

                    if (uiState.actionSuccess) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.projeto_detalhe_sucesso),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
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
private fun InfoCard(projeto: Projeto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = projeto.nome,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = projeto.objetivo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            if (projeto.descricao.isNotBlank()) {
                Text(
                    text = projeto.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LabelValue(
                    label = stringResource(R.string.projeto_detalhe_area),
                    value = projeto.area.name.replace("_", " ")
                )
                LabelValue(
                    label = stringResource(R.string.projeto_detalhe_responsavel),
                    value = projeto.responsavelNome,
                    alignEnd = true
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LabelValue(
                    label = stringResource(R.string.projeto_detalhe_status),
                    value = formatStatus(projeto.status)
                )
                LabelValue(
                    label = stringResource(R.string.projeto_detalhe_progresso),
                    value = "${projeto.progresso}%",
                    alignEnd = true
                )
            }
        }
    }
}

@Composable
private fun FinanceiroCard(projeto: Projeto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.projeto_detalhe_financeiro),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LabelValue(
                    label = stringResource(R.string.projeto_detalhe_investimento_estimado),
                    value = formatCurrency(projeto.investimentoEstimado)
                )
                LabelValue(
                    label = stringResource(R.string.projeto_detalhe_investimento_realizado),
                    value = formatCurrency(projeto.investimentoRealizado),
                    alignEnd = true
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LabelValue(
                    label = stringResource(R.string.projeto_detalhe_retorno_estimado),
                    value = formatCurrency(projeto.retornoEstimadoMensal)
                )
                LabelValue(
                    label = stringResource(R.string.projeto_detalhe_retorno_realizado),
                    value = formatCurrency(projeto.retornoRealizadoMensal),
                    alignEnd = true
                )
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = null,
                    tint = if (projeto.roi >= 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${stringResource(R.string.projeto_detalhe_roi)}: ${formatPercent(projeto.roi)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (projeto.roi >= 0) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun ProgressoCard(
    progresso: Float,
    status: StatusProjeto,
    onProgressoChange: (Float) -> Unit,
    onSalvar: () -> Unit,
    onConcluir: () -> Unit
) {
    val canEdit = status == StatusProjeto.EM_ANDAMENTO || status == StatusProjeto.PLANEJADO

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.projeto_detalhe_progresso),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${progresso.toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (canEdit) {
                Slider(
                    value = progresso,
                    onValueChange = onProgressoChange,
                    valueRange = 0f..100f,
                    steps = 19
                )

                OutlinedButton(
                    onClick = onSalvar,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(R.string.projeto_detalhe_salvar_progresso))
                }

                if (progresso.toInt() >= 100 && status == StatusProjeto.EM_ANDAMENTO) {
                    Button(
                        onClick = onConcluir,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.projeto_detalhe_concluir),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelValue(
    label: String,
    value: String,
    alignEnd: Boolean = false
) {
    Column(
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatStatus(status: StatusProjeto): String = when (status) {
    StatusProjeto.PLANEJADO -> "Planejado"
    StatusProjeto.EM_ANDAMENTO -> "Em Andamento"
    StatusProjeto.PAUSADO -> "Pausado"
    StatusProjeto.CONCLUIDO -> "Concluído"
    StatusProjeto.CANCELADO -> "Cancelado"
}
