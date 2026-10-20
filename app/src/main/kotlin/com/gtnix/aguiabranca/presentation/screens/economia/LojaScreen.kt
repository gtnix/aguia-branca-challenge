package com.gtnix.aguiabranca.presentation.screens.economia

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Toll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.model.Resgate
import com.gtnix.aguiabranca.domain.model.StatusResgate
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.EmptyState
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.InovagabFlatCard
import com.gtnix.aguiabranca.presentation.components.ResgateStatusChip
import com.gtnix.aguiabranca.presentation.components.SaldoCard
import com.gtnix.aguiabranca.presentation.components.asasCor
import com.gtnix.aguiabranca.presentation.components.categoriaRecompensaLabel
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding

@Composable
fun LojaScreen(
    viewModel: LojaViewModel,
    onNavigateBack: () -> Unit,
    onAbrirExtrato: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sucesso = stringResource(R.string.loja_resgate_sucesso)

    LaunchedEffect(uiState.mensagem, uiState.resgateConcluido) {
        val texto = uiState.mensagem ?: if (uiState.resgateConcluido) sucesso else null
        if (texto != null) {
            snackbarHostState.showSnackbar(texto)
            viewModel.consumirMensagem()
        }
    }

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.loja_titulo),
                onBackClick = onNavigateBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoading && uiState.recompensas.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = ScreenPadding.Horizontal, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                SaldoCard(
                    asas = uiState.asas,
                    xp = uiState.xp,
                    acaoLabel = stringResource(R.string.economia_ver_carteira),
                    onAcao = onAbrirExtrato
                )
            }

            uiState.erro?.let { erro ->
                item {
                    Text(text = erro, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (uiState.meusResgates.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        LojaSectionTitle(stringResource(R.string.loja_em_andamento))
                        ResgatesCard(uiState.meusResgates)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LojaSectionTitle(stringResource(R.string.loja_recompensas))
                    if (uiState.recompensas.isEmpty() && uiState.erro == null) {
                        EmptyState(
                            icon = Icons.Default.Storefront,
                            title = stringResource(R.string.loja_vazia),
                            subtitle = stringResource(R.string.gamification_explanation)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            uiState.recompensasOrdenadas.forEach { recompensa ->
                                key(recompensa.id) {
                                    RecompensaCard(
                                        recompensa = recompensa,
                                        saldoAsas = uiState.asas,
                                        enabled = !uiState.isResgatando,
                                        onResgatar = { viewModel.pedirConfirmacao(recompensa) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    uiState.confirmando?.let { recompensa ->
        AlertDialog(
            onDismissRequest = viewModel::cancelarConfirmacao,
            title = { Text(stringResource(R.string.loja_confirmar_titulo)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = recompensa.titulo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(stringResource(R.string.loja_confirmar_mensagem, recompensa.custoAsas))
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmarResgate) {
                    Text(stringResource(R.string.loja_confirmar))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::cancelarConfirmacao) {
                    Text(stringResource(R.string.loja_cancelar))
                }
            }
        )
    }
}

@Composable
private fun RecompensaCard(
    recompensa: Recompensa,
    saldoAsas: Int,
    enabled: Boolean,
    onResgatar: () -> Unit
) {
    val motivo = recompensa.motivoBloqueio.orEmpty()
    val nivelExigido = recompensa.nivelMinimo != NivelUsuario.INICIANTE
    val mostrarNivel = nivelExigido && !motivo.contains(recompensa.nivelMinimo.label, ignoreCase = true)
    val faltaSaldo = !recompensa.podeResgatar && saldoAsas < recompensa.custoAsas &&
        !(nivelExigido && motivo.contains(recompensa.nivelMinimo.label, ignoreCase = true))

    InovagabFlatCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recompensa.titulo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val origem = listOf(categoriaRecompensaLabel(recompensa.categoria), recompensa.parceiro)
                        .filter { it.isNotBlank() }
                        .joinToString(" · ")
                    if (origem.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = origem,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                PrecoAsas(recompensa.custoAsas)
            }
            if (recompensa.descricao.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = recompensa.descricao,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = buildList {
                    add(stringResource(R.string.loja_estoque, recompensa.estoque))
                    if (mostrarNivel) add(stringResource(R.string.loja_nivel_minimo, recompensa.nivelMinimo.label))
                }.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            when {
                recompensa.podeResgatar -> InovagabButton(
                    text = stringResource(R.string.loja_resgatar),
                    onClick = onResgatar,
                    enabled = enabled,
                    leadingIcon = Icons.Default.Redeem,
                    modifier = Modifier.fillMaxWidth()
                )
                else -> Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (faltaSaldo && recompensa.custoAsas > 0) {
                        SaldoProgresso(progresso = saldoAsas.toFloat() / recompensa.custoAsas)
                    }
                    BloqueioLinha(motivo)
                }
            }
        }
    }
}

@Composable
private fun PrecoAsas(custo: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Toll,
            contentDescription = null,
            tint = asasCor(),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.economia_asas, custo),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SaldoProgresso(progresso: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progresso.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(CircleShape)
                .background(asasCor())
        )
    }
}

@Composable
private fun BloqueioLinha(motivo: String) {
    if (motivo.isBlank()) return
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = motivo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ResgatesCard(resgates: List<Resgate>) {
    val cardBorder = LocalInovagabColors.current.cardBorder
    InovagabFlatCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column {
            resgates.forEachIndexed { index, resgate ->
                if (index > 0) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = cardBorder)
                }
                MeuResgateRow(resgate)
            }
        }
    }
}

@Composable
private fun MeuResgateRow(resgate: Resgate) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = resgate.recompensaTitulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            ResgateStatusChip(resgate.status)
        }
        Text(
            text = textoSlaResgate(resgate),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        resgate.observacao?.takeIf { it.isNotBlank() }?.let { obs ->
            Text(
                text = obs,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LojaSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        letterSpacing = (-0.5).sp,
        modifier = Modifier.semantics { heading() }
    )
}

@Composable
private fun textoSlaResgate(resgate: Resgate): String {
    return when (resgate.status) {
        StatusResgate.SOLICITADO, StatusResgate.APROVADO ->
            stringResource(R.string.loja_sla_aprovado)
        StatusResgate.ENTREGUE -> stringResource(R.string.loja_sla_entregue)
        StatusResgate.NEGADO -> stringResource(R.string.loja_sla_negado)
    }
}

@Preview(name = "Loja clara")
@Composable
private fun LojaCardLightPreview() {
    InovagabTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            RecompensaCard(
                recompensa = previewRecompensa(),
                saldoAsas = 30,
                enabled = true,
                onResgatar = {}
            )
        }
    }
}

@Preview(name = "Loja escura")
@Composable
private fun LojaCardDarkPreview() {
    InovagabTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            RecompensaCard(
                recompensa = previewRecompensa(),
                saldoAsas = 30,
                enabled = true,
                onResgatar = {}
            )
        }
    }
}

private fun previewRecompensa() = Recompensa(
    id = "1",
    titulo = "Dia de folga",
    descricao = "Uma folga extra para quem move a operação.",
    categoria = "bem-estar",
    parceiro = "Gente e Gestão",
    custoAsas = 80,
    estoque = 4,
    nivelMinimo = NivelUsuario.EM_ASCENSAO,
    ativo = true,
    podeResgatar = true,
    motivoBloqueio = null
)
