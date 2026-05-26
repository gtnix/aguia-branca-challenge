package com.gtnix.aguiabranca.presentation.screens.ideias

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.presentation.components.AIChip
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.InovagabButtonVariant
import com.gtnix.aguiabranca.presentation.components.InovagabTextField
import com.gtnix.aguiabranca.presentation.components.VoiceRecordButton
import com.gtnix.aguiabranca.presentation.theme.AISpark
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
        onToggleRecording = viewModel::toggleRecording,
        onToggleManualForm = viewModel::toggleManualForm,
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
    onToggleRecording: () -> Unit,
    onToggleManualForm: () -> Unit,
    onSalvar: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isFormValid = uiState.titulo.isNotBlank() && uiState.descricao.isNotBlank()
    val hasTranscription = uiState.transcribedText.isNotBlank()

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.nova_ideia_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VoiceSection(
                isRecording = uiState.isRecording,
                transcribedText = uiState.transcribedText,
                isCompact = uiState.isManualFormExpanded,
                onToggleRecording = onToggleRecording,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = if (uiState.isManualFormExpanded) 16.dp else 32.dp)
            )

            AnimatedVisibility(
                visible = hasTranscription || uiState.aiSuggestedArea != null || uiState.aiSuggestedImpact != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                AISuggestionsSection(
                    suggestedArea = uiState.aiSuggestedArea,
                    suggestedImpact = uiState.aiSuggestedImpact,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 32.dp)
                )
            }

            ManualFormSection(
                isExpanded = uiState.isManualFormExpanded,
                titulo = uiState.titulo,
                descricao = uiState.descricao,
                area = uiState.area,
                onToggle = onToggleManualForm,
                onTituloChange = onTituloChange,
                onDescricaoChange = onDescricaoChange,
                onAreaChange = onAreaChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp)
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            ActionButtons(
                isFormValid = isFormValid || hasTranscription,
                isLoading = uiState.isLoading,
                onCancel = onNavigateBack,
                onSubmit = onSalvar,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            )
        }
    }
}

@Composable
private fun VoiceSection(
    isRecording: Boolean,
    transcribedText: String,
    isCompact: Boolean,
    onToggleRecording: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerSize by animateDpAsState(
        targetValue = if (isCompact) 100.dp else 200.dp,
        animationSpec = spring(stiffness = 300f),
        label = "containerSize"
    )
    
    Column(
        modifier = modifier.animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(containerSize),
            contentAlignment = Alignment.Center
        ) {
            VoiceRecordButton(
                isRecording = isRecording,
                onClick = onToggleRecording,
                isCompact = isCompact
            )
        }

        Spacer(modifier = Modifier.height(if (isCompact) 8.dp else 16.dp))

        Text(
            text = if (isRecording) {
                stringResource(R.string.nova_ideia_recording)
            } else {
                stringResource(R.string.nova_ideia_tap_to_speak)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = if (isRecording) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        AnimatedVisibility(
            visible = transcribedText.isNotBlank(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            TranscriptionCard(
                text = transcribedText,
                isRecording = isRecording,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            )
        }
    }
}

@Composable
private fun TranscriptionCard(
    text: String,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (isRecording) {
                BlinkingCursorInline(
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun BlinkingCursorInline(
    color: Color,
    style: androidx.compose.ui.text.TextStyle,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cursorBlink")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 530),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )
    
    Text(
        text = "|",
        style = style,
        color = color.copy(alpha = alpha),
        modifier = modifier
    )
}

@Composable
private fun AISuggestionsSection(
    suggestedArea: AreaAtuacao?,
    suggestedImpact: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = AISpark
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.nova_ideia_ai_suggestions),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            suggestedArea?.let { area ->
                SuggestionChip(
                    label = stringResource(R.string.nova_ideia_area_prefix),
                    value = area.name.replace("_", " ").lowercase()
                        .replaceFirstChar { it.uppercase() }
                )
            }

            suggestedImpact?.let { impact ->
                SuggestionChip(
                    label = stringResource(R.string.nova_ideia_impact_prefix),
                    value = impact
                )
            }
        }
    }
}

@Composable
private fun SuggestionChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    AIChip(
        text = "$label: $value",
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManualFormSection(
    isExpanded: Boolean,
    titulo: String,
    descricao: String,
    area: AreaAtuacao,
    onToggle: () -> Unit,
    onTituloChange: (String) -> Unit,
    onDescricaoChange: (String) -> Unit,
    onAreaChange: (AreaAtuacao) -> Unit,
    modifier: Modifier = Modifier
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "chevronRotation"
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggle
                )
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.nova_ideia_manual_form),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = stringResource(
                    if (isExpanded) R.string.cd_collapse_manual_form 
                    else R.string.cd_expand_manual_form
                ),
                modifier = Modifier
                    .size(24.dp)
                    .rotate(rotationAngle),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                InovagabTextField(
                    value = titulo,
                    onValueChange = onTituloChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(R.string.new_idea_title_label),
                    placeholder = stringResource(R.string.new_idea_title_placeholder),
                    singleLine = true
                )

                InovagabTextField(
                    value = descricao,
                    onValueChange = onDescricaoChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    label = stringResource(R.string.new_idea_desc_label),
                    placeholder = stringResource(R.string.new_idea_desc_placeholder),
                    singleLine = false
                )

                AreaDropdown(
                    selectedArea = area,
                    onAreaSelected = onAreaChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AreaDropdown(
    selectedArea: AreaAtuacao,
    onAreaSelected: (AreaAtuacao) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "dropdownRotation"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.nova_ideia_area_label),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedArea.name.replace("_", " "),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(rotationAngle),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            AreaAtuacao.entries.forEach { area ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = area.name.replace("_", " "),
                            style = MaterialTheme.typography.bodyMedium
                        ) 
                    },
                    onClick = {
                        onAreaSelected(area)
                        expanded = false
                    },
                    modifier = Modifier.background(
                        if (area == selectedArea) 
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        else 
                            Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    isFormValid: Boolean,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        InovagabButton(
            text = stringResource(R.string.nova_ideia_cancel),
            onClick = onCancel,
            variant = InovagabButtonVariant.Neutral,
            modifier = Modifier.weight(1f)
        )

        InovagabButton(
            text = if (isLoading) {
                stringResource(R.string.loading)
            } else {
                stringResource(R.string.nova_ideia_submit)
            },
            onClick = onSubmit,
            variant = InovagabButtonVariant.Primary,
            enabled = isFormValid && !isLoading,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NovaIdeiaScreenPreview() {
    InovagabTheme {
        NovaIdeiaScreenContent(
            uiState = NovaIdeiaUiState(),
            onTituloChange = {},
            onDescricaoChange = {},
            onAreaChange = {},
            onToggleRecording = {},
            onToggleManualForm = {},
            onSalvar = {},
            onNavigateBack = {}
        )
    }
}

@Preview(
    name = "Recording State - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NovaIdeiaScreenRecordingPreview() {
    InovagabTheme {
        NovaIdeiaScreenContent(
            uiState = NovaIdeiaUiState(
                isRecording = true,
                transcribedText = "Sugiro implementar um sistema de rastreamento por GPS nos veículos da frota para...",
                aiSuggestedArea = AreaAtuacao.LOGISTICA,
                aiSuggestedImpact = "Alto"
            ),
            onTituloChange = {},
            onDescricaoChange = {},
            onAreaChange = {},
            onToggleRecording = {},
            onToggleManualForm = {},
            onSalvar = {},
            onNavigateBack = {}
        )
    }
}

@Preview(
    name = "Manual Form Expanded - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NovaIdeiaScreenFormExpandedPreview() {
    InovagabTheme {
        NovaIdeiaScreenContent(
            uiState = NovaIdeiaUiState(
                isManualFormExpanded = true,
                titulo = "Sistema de rastreamento GPS",
                descricao = "Implementar GPS na frota para otimização de rotas"
            ),
            onTituloChange = {},
            onDescricaoChange = {},
            onAreaChange = {},
            onToggleRecording = {},
            onToggleManualForm = {},
            onSalvar = {},
            onNavigateBack = {}
        )
    }
}
