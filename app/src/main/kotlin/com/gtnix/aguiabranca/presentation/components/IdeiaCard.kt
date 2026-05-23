package com.gtnix.aguiabranca.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.presentation.util.bounceClick

/**
 * IdeiaCard - Card reutilizável para exibir uma ideia.
 *
 * Usado em IdeiasScreen e HomeScreen (seção "Minhas Ideias").
 *
 * @param ideia Dados da ideia
 * @param onClick Callback ao clicar no card
 * @param modifier Modificador opcional
 * @param showQuickActions Se true, exibe botões de ação rápida (para GESTOR)
 * @param onAprovar Callback ao aprovar a ideia
 * @param onReprovar Callback ao reprovar a ideia
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ideia.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ideia.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AreaChip(text = ideia.area.name.replace("_", " "))
                        IdeiaStatusBadge(status = ideia.status)
                    }
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
                        Text("Reprovar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onAprovar?.invoke() }
                    ) {
                        Text("Aprovar")
                    }
                }
            }
        }
    }
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
