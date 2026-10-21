package com.gtnix.aguiabranca.presentation.screens.ideias

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.FrequenciaProblema
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.rememberAguiaTopBarScrollBehavior
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.InovagabButtonVariant
import com.gtnix.aguiabranca.presentation.components.InovagabTextField
import com.gtnix.aguiabranca.presentation.components.PremiumFilterChip
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import com.gtnix.aguiabranca.presentation.util.labelRes
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaColor
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaIcon
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaShortLabel
import com.gtnix.aguiabranca.presentation.util.orientacaoDivisaoLabel
import kotlinx.coroutines.launch

private const val TITULO_MAX = 80
private const val DESCRICAO_MAX = 1000

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
        onOrientacaoChange = viewModel::onOrientacaoChange,
        onPublicoAfetadoChange = viewModel::onPublicoAfetadoChange,
        onFrequenciaChange = viewModel::onFrequenciaChange,
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
    onOrientacaoChange: (String?) -> Unit,
    onPublicoAfetadoChange: (String) -> Unit = {},
    onFrequenciaChange: (FrequenciaProblema) -> Unit = {},
    onSalvar: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val isFormValid = uiState.titulo.isNotBlank() &&
        uiState.descricao.isNotBlank() &&
        uiState.orientacaoId != null

    LaunchedEffect(uiState.errorMessageRes) {
        uiState.errorMessageRes?.let { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    val scrollBehavior = rememberAguiaTopBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.nova_ideia_title),
                subtitle = stringResource(R.string.nova_ideia_subtitle),
                onBackClick = onNavigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            SubmitBar(
                pendencias = listOfNotNull(
                    R.string.nova_ideia_falta_titulo.takeIf { uiState.titulo.isBlank() },
                    R.string.nova_ideia_falta_descricao.takeIf { uiState.descricao.isBlank() },
                    R.string.nova_ideia_falta_foco.takeIf { uiState.orientacaoId == null }
                ),
                isFormValid = isFormValid,
                isLoading = uiState.isLoading,
                onSubmit = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSalvar()
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding.Horizontal)
                .padding(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FormBlock(
                label = stringResource(R.string.nova_ideia_label_titulo),
                value = uiState.titulo,
                maxLength = TITULO_MAX
            ) {
                InovagabTextField(
                    value = uiState.titulo,
                    onValueChange = { onTituloChange(it.take(TITULO_MAX)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = stringResource(R.string.new_idea_title_placeholder),
                    singleLine = true
                )
            }

            FormBlock(
                label = stringResource(R.string.nova_ideia_label_descricao),
                value = uiState.descricao,
                maxLength = DESCRICAO_MAX
            ) {
                InovagabTextField(
                    value = uiState.descricao,
                    onValueChange = { onDescricaoChange(it.take(DESCRICAO_MAX)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    placeholder = stringResource(R.string.new_idea_desc_placeholder),
                    singleLine = false
                )
            }

            FormBlock(label = stringResource(R.string.nova_ideia_secao_area)) {
                AreaChips(
                    selectedArea = uiState.area,
                    onAreaSelected = onAreaChange
                )
            }

            FormBlock(label = stringResource(R.string.nova_ideia_secao_foco)) {
                if (uiState.orientacoes.isNotEmpty()) {
                    FocoEstrategicoPicker(
                        orientacoes = uiState.orientacoes,
                        selectedId = uiState.orientacaoId,
                        onOrientacaoSelected = onOrientacaoChange
                    )
                } else {
                    Text(
                        text = stringResource(R.string.nova_ideia_orientacao_vazia),
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalInovagabColors.current.textSecondary
                    )
                }
            }

            ContextoSection(
                uiState = uiState,
                onPublicoAfetadoChange = onPublicoAfetadoChange,
                onFrequenciaChange = onFrequenciaChange
            )
        }
    }
}

/**
 * Duas perguntas rápidas e opcionais que dão escala ao gestor e à IA sem alongar o formulário;
 * problema e ganho já cabem na descrição.
 */
@Composable
private fun ContextoSection(
    uiState: NovaIdeiaUiState,
    onPublicoAfetadoChange: (String) -> Unit,
    onFrequenciaChange: (FrequenciaProblema) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        HorizontalDivider(color = LocalInovagabColors.current.cardBorder)
        Text(
            text = stringResource(R.string.nova_ideia_contexto_titulo),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        FormBlock(
            label = stringResource(R.string.nova_ideia_label_publico),
            value = uiState.publicoAfetado,
            maxLength = NovaIdeiaViewModel.PUBLICO_MAX
        ) {
            InovagabTextField(
                value = uiState.publicoAfetado,
                onValueChange = onPublicoAfetadoChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.nova_ideia_placeholder_publico),
                singleLine = true
            )
        }

        FormBlock(label = stringResource(R.string.nova_ideia_label_frequencia)) {
            FrequenciaChips(
                selected = uiState.frequencia,
                onSelected = onFrequenciaChange
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FrequenciaChips(
    selected: FrequenciaProblema?,
    onSelected: (FrequenciaProblema) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FrequenciaProblema.entries.forEach { frequencia ->
            PremiumFilterChip(
                label = stringResource(frequencia.labelRes()),
                selected = frequencia == selected,
                onClick = { onSelected(frequencia) },
                compact = true
            )
        }
    }
}

@Composable
private fun FormBlock(
    label: String,
    value: String = "",
    maxLength: Int? = null,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            if (maxLength != null && value.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.nova_ideia_contador, value.length, maxLength),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (value.length >= maxLength * 9 / 10) {
                        LocalInovagabColors.current.warning
                    } else {
                        LocalInovagabColors.current.textSecondary
                    }
                )
            }
        }
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AreaChips(
    selectedArea: AreaAtuacao,
    onAreaSelected: (AreaAtuacao) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AreaAtuacao.entries.forEach { area ->
            PremiumFilterChip(
                label = stringResource(area.labelRes()),
                selected = area == selectedArea,
                onClick = { onAreaSelected(area) },
                compact = true
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FocoEstrategicoPicker(
    orientacoes: List<OrientacaoEstrategica>,
    selectedId: String?,
    onOrientacaoSelected: (String?) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val selected = orientacoes.find { it.id == selectedId }

    FocoEstrategicoCard(
        selected = selected,
        totalAtivas = orientacoes.size,
        onClick = { showSheet = true }
    )

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            OrientacaoSheetContent(
                orientacoes = orientacoes,
                selectedId = selectedId,
                onSelect = { id ->
                    onOrientacaoSelected(id)
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) showSheet = false
                    }
                }
            )
        }
    }
}

@Composable
private fun FocoEstrategicoCard(
    selected: OrientacaoEstrategica?,
    totalAtivas: Int,
    onClick: () -> Unit
) {
    val colors = LocalInovagabColors.current
    val shape = RoundedCornerShape(16.dp)
    val accent = selected?.let { orientacaoCategoriaColor(it.categoria) }
        ?: MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.surfaceElevated)
            .border(1.dp, colors.cardBorder, shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoriaBadge(
            color = accent,
            icon = selected?.let { orientacaoCategoriaIcon(it.categoria) } ?: Icons.Default.TrackChanges,
            size = 44
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = selected?.titulo ?: stringResource(R.string.nova_ideia_foco_placeholder),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = if (selected != null) 2 else 1,
                overflow = TextOverflow.Ellipsis
            )
            if (selected != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = orientacaoMeta(selected),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            }
        }
        if (selected == null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.nova_ideia_ativas_selo, totalAtivas),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary,
                modifier = Modifier
                    .border(1.dp, colors.cardBorder, RoundedCornerShape(100))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(R.string.cd_foco_estrategico),
            tint = colors.textSecondary
        )
    }
}

@Composable
private fun OrientacaoSheetContent(
    orientacoes: List<OrientacaoEstrategica>,
    selectedId: String?,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenPadding.Horizontal)
            .padding(bottom = 24.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.nova_ideia_secao_foco),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            orientacoes.forEach { orientacao ->
                OrientacaoRow(
                    orientacao = orientacao,
                    selected = orientacao.id == selectedId,
                    onClick = { onSelect(orientacao.id) }
                )
            }
        }
    }
}

@Composable
private fun OrientacaoRow(
    orientacao: OrientacaoEstrategica,
    selected: Boolean,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CategoriaBadge(
            color = orientacaoCategoriaColor(orientacao.categoria),
            icon = orientacaoCategoriaIcon(orientacao.categoria),
            size = 40
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = orientacao.titulo,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = orientacaoMeta(orientacao),
                style = MaterialTheme.typography.bodySmall,
                color = LocalInovagabColors.current.textSecondary
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        if (selected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = primary,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
        }
    }
}

@Composable
private fun CategoriaBadge(
    color: Color,
    icon: ImageVector,
    size: Int
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size((size / 2).dp)
        )
    }
}

private fun orientacaoMeta(orientacao: OrientacaoEstrategica): String =
    "${orientacaoDivisaoLabel(orientacao.divisao)} · ${orientacaoCategoriaShortLabel(orientacao.categoria)}"

@Composable
private fun SubmitBar(
    pendencias: List<Int>,
    isFormValid: Boolean,
    isLoading: Boolean,
    onSubmit: () -> Unit
) {
    val itens = pendencias.map { stringResource(it) }
    val conectivo = stringResource(R.string.nova_ideia_falta_e)
    val faltando = when (itens.size) {
        0 -> null
        1 -> itens.first()
        else -> itens.dropLast(1).joinToString(", ") + " $conectivo " + itens.last()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = ScreenPadding.Horizontal, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InovagabButton(
                text = if (isLoading) {
                    stringResource(R.string.loading)
                } else {
                    stringResource(R.string.nova_ideia_submit)
                },
                onClick = onSubmit,
                variant = InovagabButtonVariant.Primary,
                enabled = isFormValid && !isLoading,
                leadingIcon = Icons.AutoMirrored.Filled.Send,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )
            if (faltando != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.nova_ideia_falta, faltando),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalInovagabColors.current.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private val previewOrientacoes = listOf(
    OrientacaoEstrategica(
        id = "1",
        titulo = "Experiência digital do passageiro",
        descricao = "",
        categoria = CategoriaOrientacao.EXPERIENCIA_CLIENTE,
        criadoPor = "lider"
    ),
    OrientacaoEstrategica(
        id = "2",
        titulo = "Redução de emissões da frota",
        descricao = "",
        categoria = CategoriaOrientacao.SUSTENTABILIDADE,
        criadoPor = "lider"
    )
)

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
            uiState = NovaIdeiaUiState(orientacoes = previewOrientacoes),
            onTituloChange = {},
            onDescricaoChange = {},
            onAreaChange = {},
            onOrientacaoChange = {},
            onSalvar = {},
            onNavigateBack = {}
        )
    }
}

@Preview(
    name = "Formulário preenchido - Dark",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NovaIdeiaScreenFilledPreview() {
    InovagabTheme {
        NovaIdeiaScreenContent(
            uiState = NovaIdeiaUiState(
                titulo = "Sistema de rastreamento GPS",
                descricao = "Implementar GPS na frota para otimização de rotas",
                area = AreaAtuacao.LOGISTICA,
                orientacaoId = "1",
                orientacoes = previewOrientacoes
            ),
            onTituloChange = {},
            onDescricaoChange = {},
            onAreaChange = {},
            onOrientacaoChange = {},
            onSalvar = {},
            onNavigateBack = {}
        )
    }
}
