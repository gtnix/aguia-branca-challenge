package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen

@Composable
fun StatsGridGestor(
    ideiasAvaliadas: Int,
    ideiasAprovadas: Int,
    projetosArea: Int,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val taxaAprovacaoLabel = if (ideiasAvaliadas > 0) {
        val pct = (ideiasAprovadas.toFloat() / ideiasAvaliadas * 100).toInt().coerceIn(0, 100)
        "$pct%"
    } else {
        stringResource(R.string.perfil_gestor_taxa_aprovacao_indisponivel)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatGestorCard(
            label = stringResource(R.string.perfil_gestor_stat_ideias_avaliadas),
            value = ideiasAvaliadas.toString(),
            icon = Icons.Default.RateReview,
            iconAccent = MaterialTheme.colorScheme.primary,
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )

        StatGestorCard(
            label = stringResource(R.string.perfil_gestor_stat_taxa_aprovacao),
            value = taxaAprovacaoLabel,
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            iconAccent = SuccessGreen,
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )

        StatGestorCard(
            label = stringResource(R.string.perfil_gestor_stat_projetos_area),
            value = projetosArea.toString(),
            icon = Icons.Default.Folder,
            iconAccent = MaterialTheme.colorScheme.tertiary,
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatGestorCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconAccent: Color,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)
    val gradientColors = if (isDarkTheme) {
        listOf(
            iconAccent.copy(alpha = 0.14f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        )
    } else {
        listOf(
            iconAccent.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.surface
        )
    }

    Surface(
        modifier = modifier
            .height(100.dp)
            .clip(shape),
        shape = shape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    iconAccent.copy(alpha = 0.35f),
                                    iconAccent.copy(alpha = 0.15f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StatsGridGestorPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            StatsGridGestor(
                ideiasAvaliadas = 10,
                ideiasAprovadas = 4,
                projetosArea = 5,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Sem avaliações", showBackground = true)
@Composable
private fun StatsGridGestorEmptyPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            StatsGridGestor(
                ideiasAvaliadas = 0,
                ideiasAprovadas = 0,
                projetosArea = 2,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
