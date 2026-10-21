package com.gtnix.aguiabranca.presentation.screens.orientacoes

import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.rememberAguiaTopBarScrollBehavior
import com.gtnix.aguiabranca.presentation.components.EmptyState
import com.gtnix.aguiabranca.presentation.components.InovagabFlatCard
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.components.SkeletonListPlaceholder
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.spacing
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaColor
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaIconForList
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaShortLabel
import com.gtnix.aguiabranca.presentation.util.orientacaoDivisaoLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrientacoesScreen(
    viewModel: OrientacoesViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToNova: () -> Unit,
    onNavigateToEditar: (String) -> Unit = {},
    onNavigateToDetalhe: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var orientacaoParaExcluir by remember { mutableStateOf<OrientacaoEstrategica?>(null) }

    LaunchedEffect(uiState.errorMessageRes) {
        uiState.errorMessageRes?.let { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
            viewModel.clearError()
        }
    }

    val scrollBehavior = rememberAguiaTopBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.orientacoes_title),
                onBackClick = onNavigateBack,
                scrollBehavior = scrollBehavior,
                actions = {
                    if (uiState.isLider) {
                        IconButton(onClick = onNavigateToNova) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.orientacoes_nova)
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                SkeletonListPlaceholder(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            uiState.orientacoes.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyState(
                        icon = Icons.Default.Flag,
                        title = stringResource(R.string.orientacoes_empty_title),
                        subtitle = if (uiState.isLider) {
                            stringResource(R.string.orientacoes_empty_subtitle_lider)
                        } else {
                            stringResource(R.string.orientacoes_empty_subtitle_readonly)
                        },
                        actionLabel = if (uiState.isLider) {
                            stringResource(R.string.orientacoes_nova)
                        } else {
                            null
                        },
                        onActionClick = if (uiState.isLider) onNavigateToNova else null
                    )
                }
            }

            else -> {
                val ativas = uiState.orientacoes.filter { it.ativa }
                val inativas = uiState.orientacoes.filter { !it.ativa }
                val spacing = MaterialTheme.spacing
                AnimatedVisibility(
                    visible = uiState.orientacoes.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(
                            start = spacing.medium,
                            top = spacing.medium,
                            end = spacing.medium,
                            bottom = spacing.extraLarge * 3
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (ativas.isNotEmpty()) {
                            item(key = "header-ativas") {
                                SectionHeader(title = stringResource(R.string.orientacoes_secao_ativas))
                            }
                            items(
                                items = ativas,
                                key = { it.id }
                            ) { orientacao ->
                                OrientacaoCard(
                                    orientacao = orientacao,
                                    showActions = uiState.isLider,
                                    onClick = { onNavigateToDetalhe(orientacao.id) },
                                    onEditar = { onNavigateToEditar(orientacao.id) },
                                    onToggleAtivacao = {
                                        viewModel.toggleAtivacao(orientacao.id, orientacao.ativa)
                                    },
                                    onExcluir = { orientacaoParaExcluir = orientacao }
                                )
                            }
                        }
                        if (inativas.isNotEmpty()) {
                            item(key = "header-inativas") {
                                SectionHeader(title = stringResource(R.string.orientacoes_secao_inativas))
                            }
                            item(key = "inativas-list") {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    inativas.forEach { orientacao ->
                                        OrientacaoInativaRow(
                                            orientacao = orientacao,
                                            showActions = uiState.isLider,
                                            onClick = { onNavigateToDetalhe(orientacao.id) },
                                            onEditar = { onNavigateToEditar(orientacao.id) },
                                            onToggleAtivacao = {
                                                viewModel.toggleAtivacao(orientacao.id, orientacao.ativa)
                                            },
                                            onExcluir = { orientacaoParaExcluir = orientacao }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        orientacaoParaExcluir?.let { orientacao ->
            AlertDialog(
                onDismissRequest = { orientacaoParaExcluir = null },
                title = { Text(stringResource(R.string.orientacao_excluir_titulo)) },
                text = {
                    Text(
                        stringResource(
                            R.string.orientacao_excluir_mensagem,
                            orientacao.titulo
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.excluirOrientacao(orientacao.id)
                            orientacaoParaExcluir = null
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.orientacao_excluir_confirmar),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { orientacaoParaExcluir = null }) {
                        Text(stringResource(R.string.btn_cancelar))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OrientacaoCard(
    orientacao: OrientacaoEstrategica,
    showActions: Boolean,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onToggleAtivacao: () -> Unit,
    onExcluir: () -> Unit
) {
    val isDarkTheme = LocalDarkTheme.current
    val categoriaAccent = orientacaoCategoriaColor(orientacao.categoria)
    val containerColor = if (orientacao.ativa) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    }

    InovagabFlatCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        containerColor = containerColor,
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = if (showActions) 4.dp else 16.dp,
            bottom = 16.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            categoriaAccent.copy(alpha = if (isDarkTheme) 0.18f else 0.12f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = orientacaoCategoriaIconForList(orientacao.categoria),
                        contentDescription = null,
                        tint = categoriaAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = orientacaoDivisaoLabel(orientacao.divisao),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = orientacao.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = orientacao.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (showActions) {
                    OrientacaoAcoesMenu(
                        ativa = orientacao.ativa,
                        onEditar = onEditar,
                        onToggleAtivacao = onToggleAtivacao,
                        onExcluir = onExcluir,
                        modifier = Modifier.offset(y = (-8).dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 52.dp, end = if (showActions) 12.dp else 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OrientacaoMetaChip(
                    text = orientacaoCategoriaShortLabel(orientacao.categoria),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
                PrioridadeMetaChip(prioridade = orientacao.prioridade)
                if (!orientacao.ativa) {
                    OrientacaoInativaBadge()
                }
                if (orientacao.campanha.isNotBlank()) {
                    OrientacaoMetaChip(
                        text = orientacao.campanha,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        leadingIcon = Icons.Default.Flag
                    )
                }
            }
        }
    }
}

@Composable
private fun OrientacaoInativaRow(
    orientacao: OrientacaoEstrategica,
    showActions: Boolean,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onToggleAtivacao: () -> Unit,
    onExcluir: () -> Unit
) {
    val categoriaAccent = orientacaoCategoriaColor(orientacao.categoria)
    val campanha = orientacao.campanha.trim()
    val caption = buildString {
        append(orientacaoDivisaoLabel(orientacao.divisao))
        append(" · ")
        append(
            if (campanha.isNotEmpty()) campanha
            else orientacaoCategoriaShortLabel(orientacao.categoria)
        )
    }

    InovagabFlatCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 10.dp,
            end = if (showActions) 2.dp else 12.dp,
            bottom = 10.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(categoriaAccent.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = orientacaoCategoriaIconForList(orientacao.categoria),
                    contentDescription = null,
                    tint = categoriaAccent.copy(alpha = 0.45f),
                    modifier = Modifier.size(16.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = orientacao.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = caption,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (showActions) {
                OrientacaoAcoesMenu(
                    ativa = false,
                    onEditar = onEditar,
                    onToggleAtivacao = onToggleAtivacao,
                    onExcluir = onExcluir
                )
            }
        }
    }
}

@Composable
private fun OrientacaoAcoesMenu(
    ativa: Boolean,
    onEditar: () -> Unit,
    onToggleAtivacao: () -> Unit,
    onExcluir: () -> Unit,
    modifier: Modifier = Modifier
) {
    var aberto by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        IconButton(onClick = { aberto = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.orientacao_detalhe_mais_acoes),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.orientacao_menu_editar)) },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                onClick = {
                    aberto = false
                    onEditar()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        stringResource(
                            if (ativa) R.string.orientacao_cd_desativar else R.string.orientacao_cd_ativar
                        )
                    )
                },
                leadingIcon = { Icon(Icons.Default.PowerSettingsNew, contentDescription = null) },
                onClick = {
                    aberto = false
                    onToggleAtivacao()
                }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.cd_delete_orientacao),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    aberto = false
                    onExcluir()
                }
            )
        }
    }
}

@Composable
private fun OrientacaoInativaBadge() {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .widthIn(min = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = stringResource(R.string.orientacao_inativa),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun OrientacaoMetaChip(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PrioridadeMetaChip(prioridade: Int) {
    val color = prioridadeColor(prioridade)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = prioridadeLabel(prioridade),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview(showBackground = true, name = "Card LIDER")
@Composable
private fun OrientacaoCardLiderPreview() {
    InovagabTheme {
        OrientacaoCard(
            orientacao = OrientacaoEstrategica(
                id = "1",
                titulo = "Eficiência operacional e experiência do passageiro",
                descricao = "Priorizar iniciativas que reduzam custos logísticos e melhorem NPS.",
                categoria = CategoriaOrientacao.EFICIENCIA_OPERACIONAL,
                prioridade = 3,
                ativa = true,
                criadoPor = "demo-lider",
                campanha = "Águia 2026 — Inovação em Campo"
            ),
            showActions = true,
            onClick = {},
            onEditar = {},
            onToggleAtivacao = {},
            onExcluir = {}
        )
    }
}

@Preview(showBackground = true, name = "Card inativa compacta")
@Composable
private fun OrientacaoCardInativaPreview() {
    InovagabTheme {
        OrientacaoInativaRow(
            orientacao = OrientacaoEstrategica(
                id = "2",
                titulo = "Eficiência operacional e experiência do passageiro",
                descricao = "Priorizar iniciativas que reduzam custos logísticos e melhorem NPS.",
                categoria = CategoriaOrientacao.EFICIENCIA_OPERACIONAL,
                prioridade = 3,
                ativa = false,
                criadoPor = "demo-lider",
                campanha = "Águia 2026 — Inovação em Campo"
            ),
            showActions = true,
            onClick = {},
            onEditar = {},
            onToggleAtivacao = {},
            onExcluir = {}
        )
    }
}

@Preview(showBackground = true, name = "Card Read-Only")
@Composable
private fun OrientacaoCardReadOnlyPreview() {
    InovagabTheme {
        OrientacaoCard(
            orientacao = OrientacaoEstrategica(
                id = "1",
                titulo = "Reduzir custos operacionais",
                descricao = "Meta de redução de 15% nos custos de operação até dezembro",
                categoria = CategoriaOrientacao.REDUCAO_CUSTOS,
                prioridade = 1,
                ativa = true,
                criadoPor = "demo-lider"
            ),
            showActions = false,
            onClick = {},
            onEditar = {},
            onToggleAtivacao = {},
            onExcluir = {}
        )
    }
}
