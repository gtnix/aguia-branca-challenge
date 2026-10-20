package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CampanhaPatrocinada
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.model.VencedorCampanha
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.GradientProgressBar
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.InovagabFlatCard
import com.gtnix.aguiabranca.presentation.components.ideiaStatusColor
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.presentation.theme.CompletedGreen
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.NeutralGray
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.theme.WarningAmber
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.util.SeloIa
import com.gtnix.aguiabranca.presentation.util.formatCurrencyCompact
import com.gtnix.aguiabranca.presentation.util.labelRes
import com.gtnix.aguiabranca.presentation.util.seloIa
import com.gtnix.aguiabranca.presentation.util.formatPercent
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaColor
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaIcon
import com.gtnix.aguiabranca.presentation.util.orientacaoCategoriaLabel
import com.gtnix.aguiabranca.presentation.util.orientacaoDivisaoLabel
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

private const val ListaPreviewCount = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrientacaoDetalheScreen(
    viewModel: OrientacaoDetalheViewModel,
    onNavigateBack: () -> Unit,
    onEditar: (String) -> Unit = {},
    onIdeiaClick: (String) -> Unit = {},
    onProjetoClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var menuAberto by remember { mutableStateOf(false) }
    var confirmarDesativar by remember { mutableStateOf(false) }
    val erroAcao = uiState.errorAcaoRes?.let { stringResource(it) }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.recarregar()
    }

    LaunchedEffect(erroAcao) {
        if (erroAcao != null) {
            snackbarHostState.showSnackbar(erroAcao)
            viewModel.consumirErroAcao()
        }
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.orientacao_detalhe_title),
                onBackClick = onNavigateBack,
                actions = {
                    val orientacao = uiState.orientacao
                    if (uiState.isLider && orientacao != null) {
                        IconButton(onClick = { onEditar(orientacao.id) }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = stringResource(R.string.orientacao_detalhe_editar)
                            )
                        }
                        Box {
                            IconButton(
                                onClick = { menuAberto = true },
                                enabled = !uiState.alterandoAtivacao
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = stringResource(R.string.orientacao_detalhe_mais_acoes)
                                )
                            }
                            DropdownMenu(
                                expanded = menuAberto,
                                onDismissRequest = { menuAberto = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            stringResource(
                                                if (orientacao.ativa) {
                                                    R.string.orientacao_detalhe_desativar
                                                } else {
                                                    R.string.orientacao_detalhe_reativar
                                                }
                                            )
                                        )
                                    },
                                    onClick = {
                                        menuAberto = false
                                        if (orientacao.ativa) {
                                            confirmarDesativar = true
                                        } else {
                                            viewModel.alternarAtivacao()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.errorMessageRes != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(uiState.errorMessageRes!!),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            uiState.orientacao != null -> {
                OrientacaoDetalheContent(
                    uiState = uiState,
                    onIdeiaClick = onIdeiaClick,
                    onProjetoClick = onProjetoClick,
                    campanhaCard = {
                        uiState.orientacao?.campanhaPatrocinada?.let { campanha ->
                            CampanhaStatusCard(
                                campanha = campanha,
                                podeEncerrar = uiState.podeEncerrarCampanha,
                                encerrando = uiState.encerrandoCampanha,
                                vencedores = uiState.vencedoresCampanha,
                                erroRes = uiState.errorEncerramentoRes,
                                onEncerrar = viewModel::encerrarCampanha
                            )
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }

    if (confirmarDesativar) {
        AlertDialog(
            onDismissRequest = { confirmarDesativar = false },
            title = { Text(stringResource(R.string.orientacao_detalhe_desativar)) },
            text = { Text(stringResource(R.string.orientacao_detalhe_desativar_confirmacao)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmarDesativar = false
                    viewModel.alternarAtivacao()
                }) { Text(stringResource(R.string.orientacao_detalhe_desativar)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarDesativar = false }) {
                    Text(stringResource(R.string.loja_cancelar))
                }
            }
        )
    }
}

@Composable
private fun OrientacaoDetalheContent(
    uiState: OrientacaoDetalheUiState,
    onIdeiaClick: (String) -> Unit,
    onProjetoClick: (String) -> Unit,
    campanhaCard: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val orientacao = uiState.orientacao ?: return
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OrientacaoHeroCard(orientacao = orientacao)
        ImpactoCard(uiState = uiState)
        ProjetosSection(
            projetos = uiState.projetosOrdenados,
            onProjetoClick = onProjetoClick
        )
        IdeiasSection(
            ideias = uiState.topIdeias,
            perfil = uiState.perfil,
            onIdeiaClick = onIdeiaClick
        )
        campanhaCard()
        DetalhesCard(orientacao = orientacao)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun OrientacaoHeroCard(orientacao: OrientacaoEstrategica) {
    val isDarkTheme = LocalDarkTheme.current
    val categoriaAccent = orientacaoCategoriaColor(orientacao.categoria)
    var expandida by rememberSaveable { mutableStateOf(false) }
    var temMais by remember { mutableStateOf(false) }

    InovagabFlatCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(categoriaAccent.copy(alpha = if (isDarkTheme) 0.18f else 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = orientacaoCategoriaIcon(orientacao.categoria),
                        contentDescription = null,
                        tint = categoriaAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = orientacaoCategoriaLabel(orientacao.categoria),
                    style = MaterialTheme.typography.labelMedium,
                    color = categoriaAccent,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                OrientacaoStatusBadge(ativa = orientacao.ativa)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = orientacao.titulo,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.semantics { heading() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrioridadeBadge(prioridade = orientacao.prioridade)
                Text(
                    text = orientacaoDivisaoLabel(orientacao.divisao),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (orientacao.descricao.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = orientacao.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp,
                    maxLines = if (expandida) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { result ->
                        if (!expandida) temMais = result.hasVisualOverflow
                    },
                    modifier = Modifier.animateContentSize()
                )
                if (temMais || expandida) {
                    Text(
                        text = stringResource(
                            if (expandida) R.string.orientacao_detalhe_ler_menos else R.string.orientacao_detalhe_ler_mais
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { expandida = !expandida }
                            .padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ImpactoCard(uiState: OrientacaoDetalheUiState) {
    val impacto = uiState.impacto
    val atrasados = uiState.projetosAtrasados
    InovagabFlatCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SectionTitle(text = stringResource(R.string.orientacao_detalhe_impacto))
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiCell(
                    value = uiState.ideiasAlinhadas.size.toString(),
                    label = stringResource(R.string.orientacao_detalhe_kpi_ideias),
                    modifier = Modifier.weight(1f)
                )
                KpiCell(
                    value = uiState.projetosEmAndamento.toString(),
                    label = stringResource(R.string.orientacao_detalhe_kpi_projetos),
                    modifier = Modifier.weight(1f)
                )
                KpiCell(
                    value = atrasados.toString(),
                    label = stringResource(R.string.orientacao_detalhe_kpi_atrasados),
                    valueColor = if (atrasados > 0) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.weight(1f)
                )
                KpiCell(
                    value = impacto?.takeIf { it.investimentoTotal > 0 }?.let { formatPercent(it.roiMedio) } ?: "—",
                    label = stringResource(R.string.orientacao_detalhe_kpi_roi),
                    valueColor = when {
                        impacto == null || impacto.investimentoTotal <= 0 -> MaterialTheme.colorScheme.onSurfaceVariant
                        impacto.roiMedio >= 0 -> SuccessGreen
                        else -> MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            impacto?.takeIf { it.investimentoTotal > 0 }?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.orientacao_detalhe_financeiro,
                        formatCurrencyCompact(it.investimentoTotal),
                        formatCurrencyCompact(it.lucroTotal)
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (uiState.ideiasAlinhadas.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                )
                SectionTitle(text = stringResource(R.string.orientacao_detalhe_funil))
                Spacer(modifier = Modifier.height(8.dp))
                FunilIdeias(
                    emAvaliacao = uiState.ideiasEmAvaliacao,
                    aprovadas = uiState.ideiasAprovadas,
                    emProjeto = uiState.ideiasEmProjeto
                )
            }
        }
    }
}

@Composable
private fun FunilIdeias(emAvaliacao: Int, aprovadas: Int, emProjeto: Int) {
    val etapas = listOf(
        emAvaliacao to ideiaStatusColor(StatusIdeia.PENDENTE),
        aprovadas to ideiaStatusColor(StatusIdeia.APROVADA),
        emProjeto to ideiaStatusColor(StatusIdeia.CONVERTIDA_PROJETO)
    )
    val total = etapas.sumOf { it.first }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            if (total > 0) {
                etapas.filter { it.first > 0 }.forEach { (count, color) ->
                    Box(
                        modifier = Modifier
                            .weight(count.toFloat())
                            .fillMaxSize()
                            .background(color)
                    )
                }
            }
        }
        Text(
            text = stringResource(
                R.string.orientacao_detalhe_funil_legenda,
                emAvaliacao,
                aprovadas,
                emProjeto
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProjetosSection(
    projetos: List<Projeto>,
    onProjetoClick: (String) -> Unit
) {
    var expandida by rememberSaveable { mutableStateOf(false) }
    ListaSection(
        titulo = stringResource(R.string.orientacao_detalhe_projetos_titulo),
        total = projetos.size,
        vazio = stringResource(R.string.orientacao_detalhe_sem_projetos),
        expandida = expandida,
        onToggle = { expandida = !expandida }
    ) {
        val visiveis = if (expandida) projetos else projetos.take(ListaPreviewCount)
        visiveis.forEachIndexed { index, projeto ->
            if (index > 0) ListaDivider()
            ProjetoLinha(projeto = projeto, onClick = { onProjetoClick(projeto.id) })
        }
    }
}

@Composable
private fun IdeiasSection(
    ideias: List<Ideia>,
    perfil: PerfilUsuario,
    onIdeiaClick: (String) -> Unit
) {
    var expandida by rememberSaveable { mutableStateOf(false) }
    ListaSection(
        titulo = stringResource(R.string.orientacao_detalhe_ideias_titulo),
        total = ideias.size,
        vazio = stringResource(R.string.orientacao_detalhe_sem_ideias),
        expandida = expandida,
        onToggle = { expandida = !expandida }
    ) {
        val visiveis = if (expandida) ideias else ideias.take(ListaPreviewCount)
        visiveis.forEachIndexed { index, ideia ->
            if (index > 0) ListaDivider()
            IdeiaLinha(ideia = ideia, selo = seloIa(ideia, perfil), onClick = { onIdeiaClick(ideia.id) })
        }
    }
}

@Composable
private fun ListaSection(
    titulo: String,
    total: Int,
    vazio: String,
    expandida: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    InovagabFlatCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionTitle(
                    text = titulo,
                    modifier = Modifier.weight(1f)
                )
                if (total > 0) {
                    Text(
                        text = total.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (total == 0) {
                    Text(
                        text = vazio,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                    )
            } else {
                content()
                if (total > ListaPreviewCount) {
                    ListaDivider()
                    Text(
                        text = if (expandida) {
                            stringResource(R.string.orientacao_detalhe_ver_menos)
                        } else {
                            stringResource(R.string.orientacao_detalhe_ver_todos, total)
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onToggle)
                            .semantics { role = Role.Button }
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjetoLinha(projeto: Projeto, onClick: () -> Unit) {
    val atrasado = projeto.estaAtrasado()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) { role = Role.Button }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = projeto.nome,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProjetoLinhaChip(projeto = projeto, atrasado = atrasado)
                Text(
                    text = projeto.responsavelNome,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (projeto.status != StatusProjeto.CONCLUIDO) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GradientProgressBar(
                        progress = projeto.progresso / 100f,
                        height = 4.dp,
                        gradientColors = if (atrasado) {
                            listOf(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error)
                        } else {
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${projeto.progresso}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
    }
}

@Composable
private fun IdeiaLinha(ideia: Ideia, selo: SeloIa?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) { role = Role.Button }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ideia.titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IdeiaLinhaChip(status = ideia.status)
                Text(
                    text = ideia.autorNome,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        selo?.let {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(it.labelRes()),
                style = MaterialTheme.typography.labelSmall.copy(fontFeatureSettings = "tnum"),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
    }
}

@Composable
private fun DetalhesCard(orientacao: OrientacaoEstrategica) {
    var aberto by rememberSaveable { mutableStateOf(false) }
    val rotacao by animateFloatAsState(targetValue = if (aberto) 180f else 0f, label = "detalhesChevron")
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")) }

    InovagabFlatCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 48.dp)
                    .clickable { aberto = !aberto }
                    .semantics(mergeDescendants = true) { role = Role.Button },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.orientacao_detalhe_detalhes),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.rotate(rotacao)
                )
            }
            AnimatedVisibility(visible = aberto) {
                Column(
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoRow(
                        label = stringResource(R.string.orientacao_detalhe_divisao),
                        value = orientacaoDivisaoLabel(orientacao.divisao)
                    )
                    if (orientacao.campanha.isNotBlank()) {
                        InfoRow(
                            label = stringResource(R.string.orientacao_detalhe_campanha),
                            value = orientacao.campanha
                        )
                    }
                    InfoRow(
                        label = stringResource(R.string.orientacao_detalhe_criado_em),
                        value = dateFormat.format(Date(orientacao.dataCriacao))
                    )
                    orientacao.dataExpiracao?.let { expiracao ->
                        InfoRow(
                            label = stringResource(R.string.orientacao_detalhe_expira_em),
                            value = dateFormat.format(Date(expiracao)),
                            valueColor = if (expiracao < System.currentTimeMillis()) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCell(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 22.sp,
            letterSpacing = (-0.3).sp,
            color = valueColor,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.semantics { heading() }
    )
}

@Composable
private fun ListaDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.38f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = valueColor,
            textAlign = TextAlign.Start,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.62f)
        )
    }
}

@Composable
private fun PrioridadeBadge(prioridade: Int) {
    val color = prioridadeColor(prioridade)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
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
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun OrientacaoStatusBadge(ativa: Boolean) {
    val color = if (ativa) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
    val label = if (ativa) {
        stringResource(R.string.orientacao_ativa)
    } else {
        stringResource(R.string.orientacao_inativa)
    }

    Box(
        modifier = Modifier
            .widthIn(min = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
internal fun prioridadeColor(prioridade: Int): Color = when {
    prioridade <= 1 -> MaterialTheme.colorScheme.error
    prioridade == 2 -> WarningAmber
    else -> SuccessGreen
}

internal fun prioridadeLabel(prioridade: Int): String = when {
    prioridade <= 1 -> "Crítica"
    prioridade == 2 -> "Alta"
    prioridade == 3 -> "Média"
    else -> "Baixa"
}

@Composable
private fun LinhaStatusChip(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = color,
        maxLines = 1,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}

@Composable
private fun ProjetoLinhaChip(projeto: Projeto, atrasado: Boolean) {
    if (atrasado) {
        LinhaStatusChip(
            text = stringResource(R.string.orientacao_detalhe_atrasado),
            color = MaterialTheme.colorScheme.error
        )
        return
    }
    val (texto, cor) = when (projeto.status) {
        StatusProjeto.PLANEJADO -> stringResource(R.string.orientacao_detalhe_chip_planejado) to NeutralGray
        StatusProjeto.EM_ANDAMENTO -> stringResource(R.string.orientacao_detalhe_chip_andamento) to WarningAmber
        StatusProjeto.PAUSADO -> stringResource(R.string.orientacao_detalhe_chip_pausado) to MaterialTheme.colorScheme.error
        StatusProjeto.CONCLUIDO -> stringResource(R.string.orientacao_detalhe_chip_concluido) to CompletedGreen
        StatusProjeto.CANCELADO -> stringResource(R.string.orientacao_detalhe_chip_cancelado) to MaterialTheme.colorScheme.error
    }
    LinhaStatusChip(text = texto, color = cor)
}

@Composable
private fun IdeiaLinhaChip(status: StatusIdeia) {
    val texto = stringResource(
        when (status) {
            StatusIdeia.PENDENTE -> R.string.orientacao_detalhe_chip_avaliacao
            StatusIdeia.EM_ANALISE -> R.string.orientacao_detalhe_chip_analise
            StatusIdeia.APROVADA -> R.string.orientacao_detalhe_chip_aprovada
            StatusIdeia.REPROVADA -> R.string.orientacao_detalhe_chip_recusada
            StatusIdeia.CONVERTIDA_PROJETO -> R.string.orientacao_detalhe_chip_projeto
        }
    )
    LinhaStatusChip(text = texto, color = ideiaStatusColor(status))
}

@Composable
private fun CampanhaStatusCard(
    campanha: CampanhaPatrocinada,
    podeEncerrar: Boolean,
    encerrando: Boolean,
    vencedores: List<VencedorCampanha>?,
    erroRes: Int?,
    onEncerrar: () -> Unit
) {
    var confirmar by remember { mutableStateOf(false) }
    val dataFim = campanha.fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.campanha_titulo),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (campanha.encerrada) {
                    stringResource(R.string.campanha_encerrada)
                } else {
                    stringResource(R.string.campanha_ativa_ate, dataFim)
                },
                style = MaterialTheme.typography.labelLarge,
                color = if (campanha.encerrada) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.campanha_regra, campanha.topN, campanha.bonusAsas),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            vencedores?.let { lista ->
                Text(
                    text = if (lista.isEmpty()) {
                        stringResource(R.string.campanha_encerrada_sem_vencedores)
                    } else {
                        stringResource(R.string.campanha_encerrada_vencedores, lista.joinToString { it.nome })
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            erroRes?.let {
                Text(text = stringResource(it), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            if (podeEncerrar) {
                InovagabButton(
                    text = stringResource(R.string.campanha_encerrar),
                    onClick = { confirmar = true },
                    enabled = !encerrando,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text(stringResource(R.string.campanha_encerrar)) },
            text = { Text(stringResource(R.string.campanha_encerrar_confirmacao, campanha.topN, campanha.bonusAsas)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    onEncerrar()
                }) { Text(stringResource(R.string.campanha_encerrar)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmar = false }) { Text(stringResource(R.string.loja_cancelar)) }
            }
        )
    }
}
