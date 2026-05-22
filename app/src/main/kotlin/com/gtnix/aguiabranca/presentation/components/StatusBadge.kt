package com.gtnix.aguiabranca.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.presentation.theme.StatusAprovado
import com.gtnix.aguiabranca.presentation.theme.StatusConcluido
import com.gtnix.aguiabranca.presentation.theme.StatusEmAnalise
import com.gtnix.aguiabranca.presentation.theme.StatusEmAndamento
import com.gtnix.aguiabranca.presentation.theme.StatusPendente
import com.gtnix.aguiabranca.presentation.theme.StatusReprovado

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun IdeiaStatusBadge(status: StatusIdeia, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        StatusIdeia.PENDENTE -> StatusPendente to "Pendente"
        StatusIdeia.EM_ANALISE -> StatusEmAnalise to "Em Análise"
        StatusIdeia.APROVADA -> StatusAprovado to "Aprovada"
        StatusIdeia.REPROVADA -> StatusReprovado to "Reprovada"
        StatusIdeia.CONVERTIDA_PROJETO -> StatusEmAndamento to "Projeto"
    }
    StatusBadge(text = text, color = color, modifier = modifier)
}

@Composable
fun ProjetoStatusBadge(status: StatusProjeto, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        StatusProjeto.PLANEJADO -> StatusPendente to "Planejado"
        StatusProjeto.EM_ANDAMENTO -> StatusEmAndamento to "Em Andamento"
        StatusProjeto.PAUSADO -> StatusReprovado to "Pausado"
        StatusProjeto.CONCLUIDO -> StatusConcluido to "Concluído"
        StatusProjeto.CANCELADO -> StatusReprovado to "Cancelado"
    }
    StatusBadge(text = text, color = color, modifier = modifier)
}
