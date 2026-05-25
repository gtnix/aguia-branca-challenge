package com.gtnix.aguiabranca.presentation.screens.inovacao

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Rocket
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.StartupPartner
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.components.ShimmerListPlaceholder
import com.gtnix.aguiabranca.presentation.components.StatusBadge
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

@Composable
fun RadarScreen(
    viewModel: RadarViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RadarScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RadarScreenContent(
    uiState: RadarUiState,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AguiaTopBar(
                title = "Radar de Inovação",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                ShimmerListPlaceholder(
                    itemCount = 4,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "Erro no radar de inovação",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.errorMessage,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SectionHeader(title = "Startups Recomendadas")
                    }

                    item {
                        Text(
                            text = "Parceiros de inovação aberta com maior aderência ao Grupo Águia Branca",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }

                    items(
                        items = uiState.startups,
                        key = { it.id }
                    ) { startup ->
                        StartupCard(
                            startup = startup,
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StartupCard(
    startup: StartupPartner,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.Rocket,
                    contentDescription = "Startup parceira",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = startup.nome,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = startup.setor,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                MatchScoreChip(score = startup.matchScore)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = startup.descricao,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 3
            )
        }
    }
}

@Composable
private fun MatchScoreChip(score: Int) {
    val color = when {
        score >= 90 -> MaterialTheme.colorScheme.primary
        score >= 80 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.outline
    }

    StatusBadge(
        text = "${score}% Match",
        color = color
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RadarScreenPreview() {
    InovagabTheme {
        RadarScreenContent(
            uiState = RadarUiState(
                startups = listOf(
                    StartupPartner(
                        id = "1",
                        nome = "LogTech Solutions",
                        setor = "Logística Inteligente",
                        descricao = "Plataforma de otimização de rotas com IA",
                        matchScore = 92
                    ),
                    StartupPartner(
                        id = "2",
                        nome = "GreenRoute",
                        setor = "Mobilidade Sustentável",
                        descricao = "Sistema de monitoramento de pegada de carbono",
                        matchScore = 87
                    )
                )
            ),
            onNavigateBack = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RadarScreenLoadingPreview() {
    InovagabTheme {
        RadarScreenContent(
            uiState = RadarUiState(isLoading = true),
            onNavigateBack = {}
        )
    }
}
