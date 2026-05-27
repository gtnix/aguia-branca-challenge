package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.presentation.theme.CardBorderLight
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.util.bounceClick

/**
 * ProjetoCard - Card premium reutilizável para exibir um projeto.
 *
 * Usado em ProjetosScreen e HomeScreen.
 * Segue o design system Inovagab com:
 * - Glass effect no dark mode
 * - Bordas sutis no light mode
 * - GradientProgressBar animada
 * - Ícone em círculo com fundo alpha
 *
 * @param projeto Dados do projeto
 * @param onClick Callback ao clicar no card
 * @param modifier Modificador opcional
 */
@Composable
fun ProjetoCard(
    projeto: Projeto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    
    val cardContent = @Composable {
        ProjetoCardContent(projeto = projeto)
    }
    
    if (isDarkTheme) {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .bounceClick(onClick = onClick)
        ) {
            cardContent()
        }
    } else {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .bounceClick(onClick = onClick)
                .semantics(mergeDescendants = true) {},
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = shape,
            border = BorderStroke(1.dp, CardBorderLight),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                cardContent()
            }
        }
    }
}

@Composable
private fun ProjetoCardContent(projeto: Projeto) {
    val isDarkTheme = isSystemInDarkTheme()
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.15f else 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = stringResource(R.string.cd_project_icon),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = projeto.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = projeto.objetivo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            ProjetoStatusBadge(status = projeto.status)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.label_progresso),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${projeto.progresso}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            GradientProgressBar(
                progress = projeto.progresso / 100f,
                modifier = Modifier.fillMaxWidth(),
                height = 6.dp,
                animationDuration = 600
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProjetoCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProjetoCard(
                    projeto = Projeto(
                        id = "1",
                        nome = "Monitoramento de Emissões",
                        objetivo = "Reduzir emissões de CO2 da frota em 30% com monitoramento em tempo real",
                        descricao = "Sistema de monitoramento",
                        area = AreaAtuacao.LOGISTICA,
                        status = StatusProjeto.EM_ANDAMENTO,
                        responsavelId = "user1",
                        responsavelNome = "Carlos Manager",
                        progresso = 45
                    ),
                    onClick = {}
                )
                
                ProjetoCard(
                    projeto = Projeto(
                        id = "2",
                        nome = "Sistema de Gestão ESG",
                        objetivo = "Implementar dashboard de métricas ESG integrado",
                        descricao = "Dashboard ESG",
                        area = AreaAtuacao.TI,
                        status = StatusProjeto.PLANEJADO,
                        responsavelId = "user2",
                        responsavelNome = "Ana Tech",
                        progresso = 15
                    ),
                    onClick = {}
                )
            }
        }
    }
}
