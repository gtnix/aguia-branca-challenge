package com.gtnix.aguiabranca.presentation.screens.projetos

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.CelebrationOverlay
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.InovagabButtonVariant
import com.gtnix.aguiabranca.presentation.components.InovagabTextField
import com.gtnix.aguiabranca.presentation.components.PremiumFilterChip
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import com.gtnix.aguiabranca.presentation.util.labelRes
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        onDescricaoChange = viewModel::onDescricaoChange,
        onAreaChange = viewModel::onAreaChange,
        onOrientacaoChange = viewModel::onOrientacaoChange,
        onToggleMembro = viewModel::toggleMembro,
        onDataPrevistaChange = viewModel::onDataPrevistaChange,
        onInvestimentoChange = viewModel::onInvestimentoChange,
        onRetornoChange = viewModel::onRetornoChange,
        onSalvar = {
            viewModel.salvar { }
        },
        onProjetoCreated = onProjetoCreated,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun NovoProjetoContent(
    uiState: NovoProjetoUiState,
    onNomeChange: (String) -> Unit,
    onObjetivoChange: (String) -> Unit,
    onDescricaoChange: (String) -> Unit,
    onAreaChange: (AreaAtuacao) -> Unit,
    onOrientacaoChange: (String?) -> Unit,
    onToggleMembro: (String) -> Unit,
    onDataPrevistaChange: (Long?) -> Unit,
    onInvestimentoChange: (String) -> Unit,
    onRetornoChange: (String) -> Unit,
    onSalvar: () -> Unit,
    onProjetoCreated: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var areaExpanded by remember { mutableStateOf(false) }
    var orientacaoExpanded by remember { mutableStateOf(false) }
    var showCelebration by remember { mutableStateOf(false) }
    var saveRequested by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val step1Valid = uiState.nome.isNotBlank() && uiState.objetivo.isNotBlank()

    LaunchedEffect(saveRequested, uiState.isSaving, uiState.errorMessageRes) {
        if (saveRequested && !uiState.isSaving) {
            if (uiState.errorMessageRes == null) {
                showCelebration = true
            }
            saveRequested = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            StepIndicator(currentStep = currentStep)

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 3 } + fadeOut())
                    } else {
                        (slideInHorizontally { -it / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { it / 3 } + fadeOut())
                    }
                },
                label = "novoProjetoStep"
            ) { step ->
                when (step) {
                    0 -> StepBasicInfo(
                        uiState = uiState,
                        areaExpanded = areaExpanded,
                        onAreaExpandedChange = { areaExpanded = it },
                        onNomeChange = onNomeChange,
                        onObjetivoChange = onObjetivoChange,
                        onDescricaoChange = onDescricaoChange,
                        onAreaChange = onAreaChange
                    )
                    else -> StepDetails(
                        uiState = uiState,
                        orientacaoExpanded = orientacaoExpanded,
                        onOrientacaoExpandedChange = { orientacaoExpanded = it },
                        onOrientacaoChange = onOrientacaoChange,
                        onToggleMembro = onToggleMembro,
                        onDataPrevistaChange = onDataPrevistaChange,
                        onInvestimentoChange = onInvestimentoChange,
                        onRetornoChange = onRetornoChange
                    )
                }
            }

            uiState.errorMessageRes?.let { messageRes ->
                Text(
                    text = stringResource(messageRes),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (currentStep == 0) {
                InovagabButton(
                    text = "Próximo",
                    onClick = { currentStep = 1 },
                    modifier = Modifier.fillMaxWidth(),
                    variant = InovagabButtonVariant.Primary,
                    enabled = step1Valid
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InovagabButton(
                        text = "Voltar",
                        onClick = { currentStep = 0 },
                        modifier = Modifier.weight(1f),
                        variant = InovagabButtonVariant.Neutral
                    )
                    InovagabButton(
                        text = if (uiState.isSaving) stringResource(R.string.loading)
                               else stringResource(R.string.novo_projeto_salvar),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            saveRequested = true
                            onSalvar()
                        },
                        modifier = Modifier.weight(1f),
                        variant = InovagabButtonVariant.Primary,
                        enabled = !uiState.isSaving && step1Valid
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

        if (showCelebration) {
            CelebrationOverlay(
                message = "Projeto Criado!",
                visible = true,
                onDismiss = {
                    showCelebration = false
                    onProjetoCreated()
                }
            )
        }
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Passo ${currentStep + 1} de 2",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(2) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentStep) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= currentStep) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            }
                        )
                )
            }
        }
    }
}

@Composable
private fun StepBasicInfo(
    uiState: NovoProjetoUiState,
    areaExpanded: Boolean,
    onAreaExpandedChange: (Boolean) -> Unit,
    onNomeChange: (String) -> Unit,
    onObjetivoChange: (String) -> Unit,
    onDescricaoChange: (String) -> Unit,
    onAreaChange: (AreaAtuacao) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
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
            onExpandedChange = onAreaExpandedChange,
            onAreaSelected = { area ->
                onAreaChange(area)
                onAreaExpandedChange(false)
            }
        )

        InovagabTextField(
            value = uiState.objetivo,
            onValueChange = onObjetivoChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            label = stringResource(R.string.novo_projeto_objetivo),
            placeholder = stringResource(R.string.novo_projeto_objetivo_placeholder),
            leadingIcon = Icons.Default.Description,
            singleLine = false
        )

        InovagabTextField(
            value = uiState.descricao,
            onValueChange = onDescricaoChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            label = stringResource(R.string.novo_projeto_descricao),
            placeholder = stringResource(R.string.novo_projeto_descricao_placeholder),
            leadingIcon = Icons.Default.Description,
            singleLine = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun StepDetails(
    uiState: NovoProjetoUiState,
    orientacaoExpanded: Boolean,
    onOrientacaoExpandedChange: (Boolean) -> Unit,
    onOrientacaoChange: (String?) -> Unit,
    onToggleMembro: (String) -> Unit,
    onDataPrevistaChange: (Long?) -> Unit,
    onInvestimentoChange: (String) -> Unit,
    onRetornoChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        OrientacaoDropdown(
            orientacoes = uiState.orientacoesDisponiveis,
            selectedId = uiState.orientacaoId,
            expanded = orientacaoExpanded,
            onExpandedChange = onOrientacaoExpandedChange,
            onOrientacaoSelected = { id ->
                onOrientacaoChange(id)
                onOrientacaoExpandedChange(false)
            }
        )

        EquipeSelector(
            usuarios = uiState.usuariosDisponiveis,
            membrosSelecionados = uiState.membrosIds,
            onToggleMembro = onToggleMembro
        )

        PrazoDatePickerField(
            selectedDateMillis = uiState.dataPrevistaConclusao,
            onDateSelected = onDataPrevistaChange
        )

        Text(
            text = "Campos financeiros (opcional)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp)
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
                    text = stringResource(R.string.novo_projeto_ideia_banner_title),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.novo_projeto_ideia_id, ideiaId.take(8)),
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
                            text = stringResource(selectedArea.labelRes()),
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
                                text = stringResource(area.labelRes()),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrientacaoDropdown(
    orientacoes: List<OrientacaoEstrategica>,
    selectedId: String?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOrientacaoSelected: (String?) -> Unit
) {
    val selectedOrientacao = orientacoes.find { it.id == selectedId }
    val shape = MaterialTheme.shapes.small

    val borderColor by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "orientacaoBorder"
    )

    val borderWidth by animateFloatAsState(
        targetValue = if (expanded) 2f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "orientacaoBorderWidth"
    )

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "orientacaoChevron"
    )

    Column {
        Text(
            text = stringResource(R.string.novo_projeto_orientacao),
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrackChanges,
                            contentDescription = null,
                            tint = if (expanded) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = selectedOrientacao?.titulo
                                ?: stringResource(R.string.novo_projeto_orientacao_nenhuma),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 12.dp),
                            maxLines = 2
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
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.novo_projeto_orientacao_nenhuma),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (selectedId == null) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selectedId == null) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = { onOrientacaoSelected(null) },
                    modifier = Modifier.background(
                        if (selectedId == null) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                        else Color.Transparent
                    )
                )
                orientacoes.forEach { orientacao ->
                    val isSelected = orientacao.id == selectedId
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = orientacao.titulo,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = { onOrientacaoSelected(orientacao.id) },
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EquipeSelector(
    usuarios: List<Usuario>,
    membrosSelecionados: List<String>,
    onToggleMembro: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.novo_projeto_equipe),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Groups,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(R.string.novo_projeto_equipe_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (usuarios.isEmpty()) {
            Text(
                text = stringResource(R.string.loading),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp)
            )
        } else {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                usuarios.forEach { usuario ->
                    PremiumFilterChip(
                        label = usuario.nome,
                        selected = usuario.id in membrosSelecionados,
                        onClick = { onToggleMembro(usuario.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrazoDatePickerField(
    selectedDateMillis: Long?,
    onDateSelected: (Long?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis ?: System.currentTimeMillis()
    )
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }
    val displayText = selectedDateMillis?.let { dateFormat.format(Date(it)) }
        ?: stringResource(R.string.novo_projeto_prazo_placeholder)

    Column {
        Text(
            text = stringResource(R.string.novo_projeto_prazo),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { showDatePicker = true }
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = stringResource(R.string.cd_select_date),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedDateMillis != null) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                if (selectedDateMillis != null) {
                    IconButton(onClick = { onDateSelected(null) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.novo_projeto_prazo_limpar),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.btn_salvar))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.btn_cancelar))
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
                descricao = "",
                area = AreaAtuacao.OPERACOES,
                investimentoEstimado = "",
                retornoEstimadoMensal = ""
            ),
            onNomeChange = {},
            onObjetivoChange = {},
            onDescricaoChange = {},
            onAreaChange = {},
            onOrientacaoChange = {},
            onToggleMembro = {},
            onDataPrevistaChange = {},
            onInvestimentoChange = {},
            onRetornoChange = {},
            onSalvar = {},
            onProjetoCreated = {},
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
                descricao = "Projeto derivado de ideia aprovada",
                area = AreaAtuacao.LOGISTICA,
                ideiaOrigemId = "abc123def456"
            ),
            onNomeChange = {},
            onObjetivoChange = {},
            onDescricaoChange = {},
            onAreaChange = {},
            onOrientacaoChange = {},
            onToggleMembro = {},
            onDataPrevistaChange = {},
            onInvestimentoChange = {},
            onRetornoChange = {},
            onSalvar = {},
            onProjetoCreated = {},
            onNavigateBack = {}
        )
    }
}
