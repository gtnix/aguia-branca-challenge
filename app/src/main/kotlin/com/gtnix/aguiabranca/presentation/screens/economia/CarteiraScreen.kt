package com.gtnix.aguiabranca.presentation.screens.economia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.EventoPontos
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.rememberAguiaTopBarScrollBehavior
import com.gtnix.aguiabranca.presentation.components.EmptyState
import com.gtnix.aguiabranca.presentation.components.InovagabFlatCard
import com.gtnix.aguiabranca.presentation.components.SaldoCard
import com.gtnix.aguiabranca.presentation.components.asasCor
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.components.ImpactoCard
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dataExtratoFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("pt", "BR"))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarteiraScreen(
    viewModel: CarteiraViewModel,
    onNavigateBack: () -> Unit,
    onAbrirLoja: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehavior = rememberAguiaTopBarScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AguiaTopBar(
                title = stringResource(
                    if (viewModel.perfil == PerfilUsuario.LIDER) R.string.carteira_extrato
                    else R.string.carteira_titulo
                ),
                onBackClick = onNavigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (uiState.isLoading && uiState.eventos.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = ScreenPadding.Horizontal, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                val isLider = viewModel.perfil == PerfilUsuario.LIDER
                SaldoCard(
                    asas = if (isLider) null else uiState.asas,
                    xp = uiState.xpTotal,
                    acaoLabel = if (isLider) null else stringResource(R.string.economia_ver_loja),
                    onAcao = if (isLider) null else onAbrirLoja
                )
            }

            item { ImpactoCard(impacto = uiState.impacto, perfil = viewModel.perfil) }

            uiState.erro?.let { erro ->
                item { Text(text = erro, color = MaterialTheme.colorScheme.error) }
            }

            item { SectionHeader(title = stringResource(R.string.carteira_extrato)) }

            if (uiState.eventos.isEmpty() && uiState.erro == null) {
                item {
                    EmptyState(
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        title = stringResource(
                            if (viewModel.perfil == PerfilUsuario.LIDER) R.string.carteira_extrato
                            else R.string.carteira_vazia
                        ),
                        subtitle = stringResource(
                            if (viewModel.perfil == PerfilUsuario.LIDER) R.string.carteira_vazia_lider
                            else R.string.gamification_explanation
                        )
                    )
                }
            } else {
                item {
                    val cardBorder = LocalInovagabColors.current.cardBorder
                    InovagabFlatCard(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Column {
                            uiState.eventos.forEachIndexed { index, evento ->
                                if (index > 0) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = cardBorder
                                    )
                                }
                                EventoRow(evento, perfil = viewModel.perfil)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventoRow(evento: EventoPontos, perfil: PerfilUsuario) {
    val decoracao = if (evento.estornado) TextDecoration.LineThrough else null
    val rotulo = eventoRotulo(evento)
    val ouro = asasCor()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rotulo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textDecoration = decoracao
            )
            Text(
                text = buildString {
                    if (evento.criadoEm > 0) {
                        append(
                            dataExtratoFormatter.format(
                                Instant.ofEpochMilli(evento.criadoEm).atZone(ZoneId.systemDefault())
                            )
                        )
                    }
                    if (evento.estornado) {
                        if (isNotEmpty()) append(" · ")
                        append(stringResource(R.string.carteira_estornado))
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(horizontalAlignment = Alignment.End) {
            if (evento.xp != 0) {
                Text(
                    text = sinal(evento.xp) + stringResource(R.string.economia_xp, kotlin.math.abs(evento.xp)),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = valorColor(evento.xp, positivo = MaterialTheme.colorScheme.onSurface),
                    textDecoration = decoracao
                )
            }
            if (perfil != PerfilUsuario.LIDER && evento.asas != 0) {
                Text(
                    text = sinal(evento.asas) + stringResource(R.string.economia_asas, kotlin.math.abs(evento.asas)),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = valorColor(evento.asas, positivo = ouro),
                    textDecoration = decoracao
                )
            }
        }
    }
}

@Composable
private fun eventoRotulo(evento: EventoPontos): String {
    if (evento.tipo == "IDEIA_ALINHADA") {
        val titulo = TITULO_ENTRE_ASPAS.find(evento.descricao)?.groupValues?.getOrNull(1)
        return if (!titulo.isNullOrBlank()) {
            stringResource(R.string.evento_ideia_alinhada_titulo, titulo)
        } else {
            stringResource(R.string.evento_ideia_alinhada)
        }
    }
    return evento.descricao.ifBlank { evento.tipo }
}

private val TITULO_ENTRE_ASPAS = Regex("\"([^\"]+)\"")

private fun sinal(valor: Int): String = if (valor > 0) "+" else "−"

@Composable
private fun valorColor(valor: Int, positivo: Color): Color =
    if (valor > 0) positivo else MaterialTheme.colorScheme.error
