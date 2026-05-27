package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.GlassCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrientacaoDetalheScreen(
    viewModel: OrientacaoDetalheViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.orientacao_detalhe_title),
                onBackClick = onNavigateBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.errorMessageRes != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(uiState.errorMessageRes!!),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            uiState.orientacao != null -> {
                OrientacaoDetalheContent(
                    orientacao = uiState.orientacao!!,
                    ideiasAlinhadasCount = uiState.ideiasAlinhadas.size,
                    projetosEmAndamento = uiState.projetosEmAndamento,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun OrientacaoDetalheContent(
    orientacao: OrientacaoEstrategica,
    ideiasAlinhadasCount: Int,
    projetosEmAndamento: Int,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                categoriaColor(orientacao.categoria)
                                    .copy(alpha = if (isDarkTheme) 0.15f else 0.1f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoriaIcon(orientacao.categoria),
                            contentDescription = null,
                            tint = categoriaColor(orientacao.categoria),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = formatCategoria(orientacao.categoria),
                            style = MaterialTheme.typography.labelMedium,
                            color = categoriaColor(orientacao.categoria),
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        StatusBadge(ativa = orientacao.ativa)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = orientacao.titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                SectionLabel(
                    icon = Icons.Default.Info,
                    label = stringResource(R.string.orientacao_detalhe_descricao)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = orientacao.descricao,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionLabel(
                    icon = Icons.Default.Lightbulb,
                    label = stringResource(R.string.orientacao_detalhe_impacto)
                )
                ImpactoRow(
                    icon = Icons.Default.Lightbulb,
                    label = when (ideiasAlinhadasCount) {
                        1 -> stringResource(R.string.orientacao_detalhe_ideia_alinhada)
                        else -> stringResource(
                            R.string.orientacao_detalhe_ideias_alinhadas,
                            ideiasAlinhadasCount
                        )
                    }
                )
                if (projetosEmAndamento > 0) {
                    ImpactoRow(
                        icon = Icons.Default.Folder,
                        label = when (projetosEmAndamento) {
                            1 -> stringResource(R.string.orientacao_detalhe_projeto_andamento)
                            else -> stringResource(
                                R.string.orientacao_detalhe_projetos_andamento,
                                projetosEmAndamento
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoRow(
                    icon = Icons.Default.PriorityHigh,
                    label = stringResource(R.string.orientacao_detalhe_prioridade),
                    value = prioridadeLabel(orientacao.prioridade),
                    valueColor = prioridadeColor(orientacao.prioridade)
                )

                InfoRow(
                    icon = Icons.Outlined.Category,
                    label = stringResource(R.string.orientacao_detalhe_categoria),
                    value = formatCategoria(orientacao.categoria)
                )

                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = stringResource(R.string.orientacao_detalhe_criado_em),
                    value = dateFormat.format(Date(orientacao.dataCriacao))
                )

                orientacao.dataExpiracao?.let { expiracao ->
                    InfoRow(
                        icon = Icons.Default.Flag,
                        label = stringResource(R.string.orientacao_detalhe_expira_em),
                        value = dateFormat.format(Date(expiracao)),
                        valueColor = if (expiracao < System.currentTimeMillis())
                            MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ImpactoRow(
    icon: ImageVector,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SectionLabel(icon: ImageVector, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
private fun StatusBadge(ativa: Boolean) {
    val color = if (ativa) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
    val label = if (ativa) stringResource(R.string.orientacao_ativa)
    else stringResource(R.string.orientacao_inativa)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

private fun formatCategoria(categoria: CategoriaOrientacao): String = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> "Redução de Custos"
    CategoriaOrientacao.QUALIDADE_SERVICO -> "Qualidade de Serviço"
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> "Inovação Tecnológica"
    CategoriaOrientacao.SUSTENTABILIDADE -> "Sustentabilidade"
    CategoriaOrientacao.SEGURANCA -> "Segurança"
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> "Experiência do Cliente"
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> "Eficiência Operacional"
}

@Composable
private fun categoriaColor(categoria: CategoriaOrientacao): Color = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> Color(0xFF2196F3)
    CategoriaOrientacao.QUALIDADE_SERVICO -> Color(0xFF4CAF50)
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> Color(0xFF9C27B0)
    CategoriaOrientacao.SUSTENTABILIDADE -> Color(0xFF009688)
    CategoriaOrientacao.SEGURANCA -> Color(0xFFFF9800)
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> Color(0xFFE91E63)
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> Color(0xFF607D8B)
}

private fun categoriaIcon(categoria: CategoriaOrientacao): ImageVector = when (categoria) {
    CategoriaOrientacao.REDUCAO_CUSTOS -> Icons.Default.PriorityHigh
    CategoriaOrientacao.QUALIDADE_SERVICO -> Icons.Default.Flag
    CategoriaOrientacao.INOVACAO_TECNOLOGICA -> Icons.Outlined.Category
    CategoriaOrientacao.SUSTENTABILIDADE -> Icons.Default.Flag
    CategoriaOrientacao.SEGURANCA -> Icons.Default.Flag
    CategoriaOrientacao.EXPERIENCIA_CLIENTE -> Icons.Default.Flag
    CategoriaOrientacao.EFICIENCIA_OPERACIONAL -> Icons.Default.Flag
}

@Composable
private fun prioridadeColor(prioridade: Int): Color = when {
    prioridade <= 1 -> MaterialTheme.colorScheme.error
    prioridade == 2 -> Color(0xFFFF9800)
    else -> Color(0xFF4CAF50)
}

private fun prioridadeLabel(prioridade: Int): String = when {
    prioridade <= 1 -> "Crítica"
    prioridade == 2 -> "Alta"
    prioridade == 3 -> "Média"
    else -> "Baixa"
}
