package com.gtnix.aguiabranca.presentation.screens.projetos

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.session.UserSession
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.InovagabButtonVariant
import com.gtnix.aguiabranca.presentation.components.InovagabTextField
import com.gtnix.aguiabranca.presentation.navigation.Destination
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    private val _uiState = MutableStateFlow(NovoProjetoUiState())
    val uiState: StateFlow<NovoProjetoUiState> = _uiState.asStateFlow()

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
                    _uiState.update { it.copy(nome = ideia.titulo, area = ideia.area, ideiaOrigemId = id) }
                }
            } catch (_: Exception) { }
        }
    }

    fun onNomeChange(value: String) {
        _uiState.update { it.copy(nome = value) }
    }

    fun onObjetivoChange(value: String) {
        _uiState.update { it.copy(objetivo = value) }
    }

    fun onAreaChange(value: AreaAtuacao) {
        _uiState.update { it.copy(area = value) }
    }

    fun onInvestimentoChange(value: String) {
        _uiState.update { it.copy(investimentoEstimado = value) }
    }

    fun onRetornoChange(value: String) {
        _uiState.update { it.copy(retornoEstimadoMensal = value) }
    }

    fun salvar(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (current.nome.isBlank() || current.objetivo.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos obrigatórios") }
            return
        }

        val investimento = current.investimentoEstimado.toDoubleOrNull() ?: 0.0
        val retorno = current.retornoEstimadoMensal.toDoubleOrNull() ?: 0.0

        _uiState.update { it.copy(isSaving = true) }
        val projetoId = UUID.randomUUID().toString()

        viewModelScope.launch {
            try {
                val projeto = Projeto(
                    id = projetoId,
                    nome = current.nome,
                    objetivo = current.objetivo,
                    descricao = "",
                    area = current.area,
                    status = StatusProjeto.PLANEJADO,
                    ideiaOrigemId = current.ideiaOrigemId,
                    responsavelId = userSession.userId,
                    responsavelNome = userSession.userName,
                    investimentoEstimado = investimento,
                    retornoEstimadoMensal = retorno
                )
                projetoRepository.salvar(projeto)

                val origem = current.ideiaOrigemId
                if (!origem.isNullOrBlank()) {
                    ideiaRepository.atualizarStatus(origem, StatusIdeia.CONVERTIDA_PROJETO)
                    ideiaRepository.vincularProjeto(origem, projetoId)
                }

                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, errorMessage = "Erro ao salvar projeto") }
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
    val isDarkTheme = isSystemInDarkTheme()

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.novo_projeto_titulo),
                onBackClick = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding.Horizontal, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (uiState.ideiaOrigemId != null) {
                IdeiaOrigemCard(ideiaId = uiState.ideiaOrigemId)
            }

            InovagabTextField(
                value = uiState.nome,
                onValueChange = onNomeChange,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.novo_projeto_nome),
                placeholder = stringResource(R.string.novo_projeto_nome_placeholder),
                leadingIcon = Icons.Default.Lightbulb,
                singleLine = true
            )

            PremiumAreaDropdown(
                selectedArea = uiState.area,
                expanded = areaExpanded,
                onExpandedChange = { areaExpanded = it },
                onAreaSelected = { area ->
                    onAreaChange(area)
                    areaExpanded = false
                }
            )

            InovagabTextField(
                value = uiState.objetivo,
                onValueChange = onObjetivoChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                label = stringResource(R.string.novo_projeto_objetivo),
                placeholder = stringResource(R.string.novo_projeto_objetivo_placeholder),
                leadingIcon = Icons.Default.Description,
                singleLine = false
            )

            InovagabTextField(
                value = uiState.investimentoEstimado,
                onValueChange = onInvestimentoChange,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.novo_projeto_investimento),
                placeholder = stringResource(R.string.novo_projeto_investimento_placeholder),
                leadingIcon = Icons.Default.AttachMoney,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            InovagabTextField(
                value = uiState.retornoEstimadoMensal,
                onValueChange = onRetornoChange,
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.novo_projeto_retorno),
                placeholder = stringResource(R.string.novo_projeto_retorno_placeholder),
                leadingIcon = Icons.Default.TrendingUp,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            InovagabButton(
                text = if (uiState.isSaving) stringResource(R.string.loading) 
                       else stringResource(R.string.novo_projeto_salvar),
                onClick = onSalvar,
                modifier = Modifier.fillMaxWidth(),
                variant = InovagabButtonVariant.Primary,
                enabled = !uiState.isSaving
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun IdeiaOrigemCard(ideiaId: String) {
    val isDarkTheme = isSystemInDarkTheme()
    
    val cardContent = @Composable {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.15f else 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(
                    text = "Projeto baseado em ideia",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "ID: ${ideiaId.take(8)}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    
    if (isDarkTheme) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            cardContent()
        }
    } else {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                cardContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PremiumAreaDropdown(
    selectedArea: AreaAtuacao,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onAreaSelected: (AreaAtuacao) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = MaterialTheme.shapes.small
    
    val borderColor by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "dropdownBorder"
    )
    
    val borderWidth by animateFloatAsState(
        targetValue = if (expanded) 2f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "dropdownBorderWidth"
    )
    
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "chevronRotation"
    )
    
    Column {
        Text(
            text = stringResource(R.string.novo_projeto_area),
            style = MaterialTheme.typography.labelSmall,
            color = if (expanded) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = borderWidth.dp,
                        color = borderColor,
                        shape = shape
                    )
                    .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                    .clickable { onExpandedChange(!expanded) }
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = if (expanded) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = selectedArea.name.replace("_", " "),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotation)
                    )
                }
            }
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                shape = RoundedCornerShape(12.dp)
            ) {
                AreaAtuacao.entries.forEach { area ->
                    val isSelected = area == selectedArea
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = area.name.replace("_", " "),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = { onAreaSelected(area) },
                        modifier = Modifier.background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            else Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun NovoProjetoScreenPreview() {
    InovagabTheme {
        NovoProjetoContent(
            uiState = NovoProjetoUiState(
                nome = "",
                objetivo = "",
                area = AreaAtuacao.OPERACOES,
                investimentoEstimado = "",
                retornoEstimadoMensal = ""
            ),
            onNomeChange = {},
            onObjetivoChange = {},
            onAreaChange = {},
            onInvestimentoChange = {},
            onRetornoChange = {},
            onSalvar = {},
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun NovoProjetoFromIdeiaPreview() {
    InovagabTheme {
        NovoProjetoContent(
            uiState = NovoProjetoUiState(
                nome = "Monitoramento de Emissões",
                objetivo = "",
                area = AreaAtuacao.LOGISTICA,
                ideiaOrigemId = "abc123def456"
            ),
            onNomeChange = {},
            onObjetivoChange = {},
            onAreaChange = {},
            onInvestimentoChange = {},
            onRetornoChange = {},
            onSalvar = {},
            onNavigateBack = {}
        )
    }
}
