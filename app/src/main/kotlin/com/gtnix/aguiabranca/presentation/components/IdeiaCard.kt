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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.presentation.theme.ErrorRed
import com.gtnix.aguiabranca.presentation.theme.InfoBlue
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.theme.WarningAmber
import com.gtnix.aguiabranca.presentation.util.bounceClick
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * IdeiaCard - Card premium para exibir uma ideia.
 *
 * Design inspirado em apps fintech (Revolut/Linear) com:
 * - StatusDot colorido à esquerda do título
 * - AIChip para detecção de similaridade
 * - Data formatada + contagem de upvotes
 * - Borda sutil ao invés de elevation
 */
@Composable
fun IdeiaCard(
    ideia: Ideia,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showQuickActions: Boolean = false,
    onAprovar: (() -> Unit)? = null,
    onReprovar: (() -> Unit)? = null
) {
    val formattedDate = remember(ideia.dataCriacao) {
        val dateFormat = SimpleDateFormat("dd 'de' MMM. 'de' yyyy", Locale("pt", "BR"))
        dateFormat.format(Date(ideia.dataCriacao))
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(onClick = onClick),
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusDot(status = ideia.status)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = ideia.titulo,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
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
                        contentDescription = null,
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
                        contentDescription = null,
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
            
            if (showQuickActions && ideia.status == StatusIdeia.PENDENTE) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { onReprovar?.invoke() },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.action_reject_short))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onAprovar?.invoke() }
                    ) {
                        Text(stringResource(R.string.action_approve_short))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusDot(
    status: StatusIdeia,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        StatusIdeia.APROVADA -> SuccessGreen
        StatusIdeia.PENDENTE -> WarningAmber
        StatusIdeia.EM_ANALISE -> InfoBlue
        StatusIdeia.REPROVADA -> ErrorRed
        StatusIdeia.CONVERTIDA_PROJETO -> MaterialTheme.colorScheme.primary
    }
    
    Box(
        modifier = modifier
            .size(10.dp)
            .background(color, CircleShape)
    )
}

/**
 * AreaChip - Chip para exibir a área de atuação.
 */
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
                    onClick = {}
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
                    onClick = {}
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
                    onClick = {}
                )
            }
        }
    }
}
