package com.gtnix.aguiabranca.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
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
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.large
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun IdeiaStatusBadge(status: StatusIdeia, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        StatusIdeia.PENDENTE -> StatusPendente to stringResource(R.string.status_awaiting_evaluation)
        StatusIdeia.EM_ANALISE -> StatusEmAnalise to stringResource(R.string.status_under_analysis)
        StatusIdeia.APROVADA -> StatusAprovado to stringResource(R.string.status_idea_approved)
        StatusIdeia.REPROVADA -> StatusReprovado to stringResource(R.string.status_not_prioritized)
        StatusIdeia.CONVERTIDA_PROJETO -> StatusEmAndamento to stringResource(R.string.status_converted_project)
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
