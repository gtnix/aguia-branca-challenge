package com.gtnix.aguiabranca.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import com.gtnix.aguiabranca.domain.model.ConquistaTier
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.theme.tierAccent
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Impacto
import com.gtnix.aguiabranca.domain.model.Missao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.StatusResgate
import com.gtnix.aguiabranca.presentation.theme.MedalGold
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.util.formatCurrency

private val CATEGORIAS_RECOMPENSA = mapOf(
    "VIAGEM" to "Viagem",
    "VEICULO" to "Veículo",
    "TEMPO" to "Tempo livre",
    "VALE" to "Vale-compras",
    "CARREIRA" to "Carreira"
)

/** Códigos sugeridos ao Líder no cadastro; qualquer outro texto continua aceito. */
val categoriasRecompensaSugeridas: List<Pair<String, String>> = CATEGORIAS_RECOMPENSA.toList()

/** Categoria é texto livre do Líder; os códigos do catálogo inicial ganham rótulo legível. */
fun categoriaRecompensaLabel(categoria: String): String =
    CATEGORIAS_RECOMPENSA[categoria.trim().uppercase()] ?: categoria

@Composable
fun AsasBadge(asas: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MedalGold.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = AsasIcons.Wings,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = stringResource(R.string.economia_asas, asas),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/** Dourado das Asas no tema atual; mesma cor do ícone de moeda na Home. */
@Composable
fun asasCor(): Color = tierAccent(ConquistaTier.OURO, LocalDarkTheme.current)

@Composable
fun ResgateStatusChip(status: StatusResgate, modifier: Modifier = Modifier) {
    val color = when (status) {
        StatusResgate.SOLICITADO -> MaterialTheme.colorScheme.tertiary
        StatusResgate.APROVADO -> MaterialTheme.colorScheme.primary
        StatusResgate.ENTREGUE -> SuccessGreen
        StatusResgate.NEGADO -> MaterialTheme.colorScheme.error
    }
    InovagabPill(text = status.label, color = color, modifier = modifier)
}

/**
 * Saldo do colaborador na anatomia dos cards da Home. Sem [asas] (Líder),
 * mostra só o XP e nenhuma ação.
 */
@Composable
fun SaldoCard(
    asas: Int?,
    xp: Int,
    modifier: Modifier = Modifier,
    acaoLabel: String? = null,
    onAcao: (() -> Unit)? = null
) {
    val ouro = asasCor()
    InovagabFlatCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, bottom = 16.dp, end = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ouro.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (asas != null) AsasIcons.Wings else Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = ouro,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (asas != null) {
                        stringResource(R.string.economia_asas, asas)
                    } else {
                        stringResource(R.string.economia_xp, xp)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (asas != null) {
                    Text(
                        text = stringResource(R.string.saldo_xp_acumulado, xp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (acaoLabel != null && onAcao != null) {
                TextButton(
                    onClick = onAcao,
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Text(text = acaoLabel, fontWeight = FontWeight.SemiBold)
                }
            } else {
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

/** Números que o colaborador pode levar para a avaliação de desempenho. */
@Composable
fun ImpactoCard(
    impacto: Impacto,
    perfil: PerfilUsuario,
    modifier: Modifier = Modifier
) {
    val metricas = buildList {
        when (perfil) {
            PerfilUsuario.OPERADOR -> {
                add(R.string.impacto_ideias to impacto.ideiasEnviadas)
                add(R.string.impacto_aprovadas to impacto.ideiasAprovadas)
            }
            PerfilUsuario.GESTOR -> add(R.string.impacto_avaliadas to impacto.ideiasAvaliadas)
            PerfilUsuario.LIDER -> add(R.string.impacto_alinhadas to impacto.ideiasAlinhadas)
        }
        add(R.string.impacto_projetos to impacto.projetos)
        add(R.string.impacto_concluidos to impacto.projetosConcluidos)
    }
    val isLider = perfil == PerfilUsuario.LIDER

    InovagabFlatCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(if (isLider) R.string.impacto_titulo_lider else R.string.impacto_titulo),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(if (isLider) R.string.impacto_subtitulo_lider else R.string.impacto_subtitulo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    metricas.forEach { (labelRes, valor) ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = valor.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(labelRes),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
            if (impacto.retornoMensal > 0.0) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = LocalInovagabColors.current.cardBorder
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.impacto_retorno),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatCurrency(impacto.retornoMensal),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
            }
        }
    }
}
