package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.util.bounceClick
import com.gtnix.aguiabranca.presentation.util.formatLongDate

private val ApproveGreen = Color(0xFF00875A)

@Composable
fun IdeiaCard(
    ideia: Ideia,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showQuickActions: Boolean = false,
    showAuthorName: Boolean = showQuickActions,
    onAprovar: (() -> Unit)? = null,
    onReprovar: (() -> Unit)? = null
) {
    val formattedDate = remember(ideia.dataCriacao) {
        formatLongDate(ideia.dataCriacao)
    }
    val statusColor = ideiaStatusColor(ideia.status)
    val statusLabel = ideiaStatusLabel(ideia.status)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(onClick = onClick)
            .semantics(mergeDescendants = true) {},
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.semantics {
                            contentDescription = statusLabel
                        }
                    ) {
                        StatusDot(
                            color = statusColor,
                            contentDescription = statusLabel
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = ideia.titulo,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .semantics { heading() }
                        )
                    }
                }

                if (ideia.temSimilaridade) {
                    Spacer(modifier = Modifier.width(8.dp))
                    AIChip(
                        text = stringResource(R.string.ai_similar)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ideia.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 20.dp)
            )

            if (showAuthorName) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.padding(start = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.ideia_card_autor, ideia.autorNome),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = stringResource(R.string.cd_idea_date),
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.cd_upvote),
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = ideia.upvotes.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (
                showQuickActions &&
                (ideia.status == StatusIdeia.PENDENTE || ideia.status == StatusIdeia.EM_ANALISE)
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onReprovar?.invoke() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text(stringResource(R.string.action_reject_short))
                    }
                    Button(
                        onClick = { onAprovar?.invoke() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ApproveGreen,
                            contentColor = Color.White
                        )
                    ) {
                        Text(stringResource(R.string.action_approve_short))
                    }
                }
            }
        }
    }
}

@Composable
fun IdeiaStatusDot(
    status: StatusIdeia,
    modifier: Modifier = Modifier
) {
    StatusDot(
        color = ideiaStatusColor(status),
        contentDescription = ideiaStatusLabel(status),
        modifier = modifier
    )
}

@Composable
private fun StatusDot(
    color: Color,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(10.dp)
            .background(color, CircleShape)
            .semantics { this.contentDescription = contentDescription }
    )
}

@Composable
private fun ideiaStatusLabel(status: StatusIdeia): String = when (status) {
    StatusIdeia.PENDENTE -> stringResource(R.string.status_pendente)
    StatusIdeia.EM_ANALISE -> stringResource(R.string.status_em_analise)
    StatusIdeia.APROVADA -> stringResource(R.string.status_aprovado)
    StatusIdeia.REPROVADA -> stringResource(R.string.status_reprovado)
    StatusIdeia.CONVERTIDA_PROJETO -> stringResource(R.string.status_em_projeto)
}

@Composable
fun AreaChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun IdeiaCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IdeiaCard(
                    ideia = Ideia(
                        id = "1",
                        titulo = "Plataforma de IA para análise de feedback de clientes",
                        descricao = "Usar IA para analisar feedbacks e identificar padrões de melhoria nos nossos produtos.",
                        tipo = TipoIdeia.IDEIA,
                        area = AreaAtuacao.OPERACOES,
                        status = StatusIdeia.APROVADA,
                        autorId = "user1",
                        autorNome = "João Silva",
                        dataCriacao = System.currentTimeMillis(),
                        upvotes = 128,
                        temSimilaridade = true
                    ),
                    onClick = {},
                    showAuthorName = true
                )

                IdeiaCard(
                    ideia = Ideia(
                        id = "2",
                        titulo = "Gamificação para engajamento em treinamentos",
                        descricao = "Aplicar mecânicas de jogos para aumentar o engajamento e a retenção nos treinamentos corporativos.",
                        tipo = TipoIdeia.IDEIA,
                        area = AreaAtuacao.RH,
                        status = StatusIdeia.PENDENTE,
                        autorId = "user2",
                        autorNome = "Maria Santos",
                        dataCriacao = System.currentTimeMillis() - 86400000 * 6,
                        upvotes = 86,
                        temSimilaridade = false
                    ),
                    onClick = {},
                    showQuickActions = true,
                    showAuthorName = true,
                    onAprovar = {},
                    onReprovar = {}
                )

                IdeiaCard(
                    ideia = Ideia(
                        id = "3",
                        titulo = "Dashboard de sustentabilidade em tempo real",
                        descricao = "Criar um painel com indicadores de impacto ambiental atualizados em tempo real.",
                        tipo = TipoIdeia.IDEIA,
                        area = AreaAtuacao.QUALIDADE,
                        status = StatusIdeia.CONVERTIDA_PROJETO,
                        autorId = "user3",
                        autorNome = "Carlos Lima",
                        dataCriacao = System.currentTimeMillis() - 86400000 * 15,
                        upvotes = 53,
                        temSimilaridade = false
                    ),
                    onClick = {},
                    showAuthorName = true
                )
            }
        }
    }
}
