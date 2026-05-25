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
import com.gtnix.aguiabranca.presentation.theme.CompletedGreen
import com.gtnix.aguiabranca.presentation.theme.ErrorRed
import com.gtnix.aguiabranca.presentation.theme.InfoBlue
import com.gtnix.aguiabranca.presentation.theme.NeutralGray
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.theme.WarningAmber

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
        StatusIdeia.PENDENTE -> NeutralGray to stringResource(R.string.status_awaiting_evaluation)
        StatusIdeia.EM_ANALISE -> InfoBlue to stringResource(R.string.status_under_analysis)
        StatusIdeia.APROVADA -> SuccessGreen to stringResource(R.string.status_idea_approved)
        StatusIdeia.REPROVADA -> ErrorRed to stringResource(R.string.status_not_prioritized)
        StatusIdeia.CONVERTIDA_PROJETO -> WarningAmber to stringResource(R.string.status_converted_project)
    }
    StatusBadge(text = text, color = color, modifier = modifier)
}

@Composable
fun ProjetoStatusBadge(status: StatusProjeto, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        StatusProjeto.PLANEJADO -> NeutralGray to "Planejado"
        StatusProjeto.EM_ANDAMENTO -> WarningAmber to "Em Andamento"
        StatusProjeto.PAUSADO -> ErrorRed to "Pausado"
        StatusProjeto.CONCLUIDO -> CompletedGreen to "Concluído"
        StatusProjeto.CANCELADO -> ErrorRed to "Cancelado"
    }
    StatusBadge(text = text, color = color, modifier = modifier)
}
