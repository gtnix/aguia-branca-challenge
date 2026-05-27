package com.gtnix.aguiabranca.presentation.screens.inovacao

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.StartupPartner
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.components.SkeletonListPlaceholder
import com.gtnix.aguiabranca.presentation.theme.CardBorderLight
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding

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
                title = stringResource(R.string.home_innovation_radar),
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                SkeletonListPlaceholder(
                    itemCount = 4,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            uiState.errorMessageRes != null -> {
                val errorMessage = stringResource(uiState.errorMessageRes!!)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = stringResource(R.string.cd_radar_error),
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage,
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
                    contentPadding = PaddingValues(horizontal = ScreenPadding.Horizontal, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        SectionHeader(title = "Startups Recomendadas")
                    }

                    item {
                        Text(
                            text = "Parceiros de inovação aberta com maior aderência ao Grupo Águia Branca",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
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
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    
    val cardContent = @Composable {
        StartupCardContent(startup = startup)
    }
    
    if (isDarkTheme) {
        GlassCard(
            modifier = modifier.fillMaxWidth()
        ) {
            cardContent()
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
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
private fun StartupCardContent(startup: StartupPartner) {
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
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = startup.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = startup.setor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            PremiumMatchScoreChip(score = startup.matchScore)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = startup.descricao,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight
        )
    }
}

@Composable
private fun PremiumMatchScoreChip(score: Int) {
    val isDarkTheme = isSystemInDarkTheme()
    
    val (backgroundColor, textColor, borderColor) = when {
        score >= 90 -> Triple(
            MaterialTheme.colorScheme.primary,
            Color.White,
            MaterialTheme.colorScheme.primary
        )
        score >= 80 -> Triple(
            MaterialTheme.colorScheme.tertiary,
            Color.White,
            MaterialTheme.colorScheme.tertiary
        )
        else -> Triple(
            if (isDarkTheme) MaterialTheme.colorScheme.surfaceVariant 
            else MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    }
    
    val isHighScore = score >= 80
    val shape = RoundedCornerShape(100)
    
    Surface(
        color = backgroundColor,
        shape = shape,
        border = if (!isHighScore) BorderStroke(1.dp, borderColor) else null,
        shadowElevation = if (isHighScore && !isDarkTheme) 2.dp else 0.dp
    ) {
        Text(
            text = "${score}% MATCH",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
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
                        descricao = "Plataforma de otimização de rotas com IA que reduz emissões de CO₂ em até 30% através de algoritmos de roteirização sustentável para frotas de transporte rodoviário.",
                        matchScore = 92
                    ),
                    StartupPartner(
                        id = "2",
                        nome = "GreenRoute",
                        setor = "Mobilidade Sustentável",
                        descricao = "Sistema de monitoramento em tempo real de pegada de carbono por viagem, com dashboards ESG integrados e relatórios automáticos para compliance ambiental.",
                        matchScore = 87
                    ),
                    StartupPartner(
                        id = "3",
                        nome = "FleetAI",
                        setor = "Gestão de Frotas",
                        descricao = "Solução de manutenção preditiva para frotas utilizando sensores IoT e machine learning, reduzindo custos operacionais e aumentando a vida útil dos veículos.",
                        matchScore = 78
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
