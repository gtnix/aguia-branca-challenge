package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.presentation.theme.CompletedGreen
import com.gtnix.aguiabranca.presentation.theme.ErrorRed
import com.gtnix.aguiabranca.presentation.theme.InfoBlue
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.NeutralGray
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.theme.WarningAmber

@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    useWhiteText: Boolean = true
) {
    val pillShape = RoundedCornerShape(100)
    
    Surface(
        modifier = modifier,
        color = if (useWhiteText) color else color.copy(alpha = 0.15f),
        shape = pillShape
    ) {
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = if (useWhiteText) Color.White else color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

fun ideiaStatusColor(status: StatusIdeia): Color = when (status) {
    StatusIdeia.PENDENTE -> WarningAmber
    StatusIdeia.EM_ANALISE -> InfoBlue
    StatusIdeia.APROVADA -> SuccessGreen
    StatusIdeia.REPROVADA -> ErrorRed
    StatusIdeia.CONVERTIDA_PROJETO -> Color(0xFF7C3AED)
}

@Composable
fun IdeiaStatusBadge(status: StatusIdeia, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        StatusIdeia.PENDENTE -> ideiaStatusColor(status) to stringResource(R.string.status_awaiting_evaluation)
        StatusIdeia.EM_ANALISE -> ideiaStatusColor(status) to stringResource(R.string.status_under_analysis)
        StatusIdeia.APROVADA -> ideiaStatusColor(status) to stringResource(R.string.status_idea_approved)
        StatusIdeia.REPROVADA -> ideiaStatusColor(status) to stringResource(R.string.status_not_prioritized)
        StatusIdeia.CONVERTIDA_PROJETO -> ideiaStatusColor(status) to stringResource(R.string.status_converted_project)
    }
    StatusBadge(text = text, color = color, modifier = modifier)
}

@Composable
fun ProjetoStatusBadge(status: StatusProjeto, modifier: Modifier = Modifier) {
    val (color, text) = when (status) {
        StatusProjeto.PLANEJADO -> NeutralGray to stringResource(R.string.status_planejado)
        StatusProjeto.EM_ANDAMENTO -> WarningAmber to stringResource(R.string.status_em_andamento)
        StatusProjeto.PAUSADO -> ErrorRed to stringResource(R.string.status_pausado)
        StatusProjeto.CONCLUIDO -> CompletedGreen to stringResource(R.string.status_concluido)
        StatusProjeto.CANCELADO -> ErrorRed to stringResource(R.string.status_cancelado)
    }
    StatusBadge(text = text, color = color, modifier = modifier)
}

enum class BadgeStatus {
    Pendente,
    Aprovada,
    EmProjeto,
    Concluida
}

@Composable
fun PremiumStatusBadge(
    status: BadgeStatus,
    modifier: Modifier = Modifier
) {
    val (color, text) = when (status) {
        BadgeStatus.Pendente -> WarningAmber to stringResource(R.string.status_pendente)
        BadgeStatus.Aprovada -> SuccessGreen to stringResource(R.string.status_aprovado)
        BadgeStatus.EmProjeto -> MaterialTheme.colorScheme.primary to stringResource(R.string.status_em_projeto)
        BadgeStatus.Concluida -> MaterialTheme.colorScheme.tertiary to stringResource(R.string.status_concluido)
    }
    
    StatusBadge(
        text = text,
        color = color,
        modifier = modifier,
        useWhiteText = true
    )
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatusBadgePreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumStatusBadge(status = BadgeStatus.Pendente)
                PremiumStatusBadge(status = BadgeStatus.Aprovada)
                PremiumStatusBadge(status = BadgeStatus.EmProjeto)
                PremiumStatusBadge(status = BadgeStatus.Concluida)
                
                StatusBadge(
                    text = "Custom Badge",
                    color = InfoBlue,
                    useWhiteText = true
                )
                
                StatusBadge(
                    text = "Light Badge",
                    color = SuccessGreen,
                    useWhiteText = false
                )
            }
        }
    }
}
