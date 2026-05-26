package com.gtnix.aguiabranca.presentation.screens.leader

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.components.AIChip
import com.gtnix.aguiabranca.presentation.components.AIInsightCard
import com.gtnix.aguiabranca.presentation.components.BarChartData
import com.gtnix.aguiabranca.presentation.components.BentoMetricCard
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.HorizontalBarChart
import com.gtnix.aguiabranca.presentation.components.TypewriterText
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import kotlinx.coroutines.delay

@Composable
fun LeaderDashboardScreen(
    viewModel: LeaderDashboardViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LeaderDashboardContent(
        uiState = uiState,
        onRefresh = viewModel::refresh,
        onNavigateToHome = onNavigateToHome,
        onNavigateToIdeias = onNavigateToIdeias,
        onNavigateToProjetos = onNavigateToProjetos,
        onNavigateToPerfil = onNavigateToPerfil
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeaderDashboardContent(
    uiState: LeaderDashboardUiState,
    onRefresh: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToPerfil: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val pullRefreshState = rememberPullToRefreshState()
    
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        state = pullRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
        if (uiState.isLoading && !uiState.isRefreshing) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LeaderDashboardBody(
                uiState = uiState,
                isDarkTheme = isDarkTheme
            )
        }
    }
}

@Composable
private fun LeaderDashboardBody(
    uiState: LeaderDashboardUiState,
    isDarkTheme: Boolean
) {
    var contentVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        contentVisible = true
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            PremiumLeaderTopBar()
        }
        
        item {
            AnimatedSection(visible = contentVisible, delayMillis = 0) {
                TitleWithAIChip(modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
        
        item {
            AnimatedSection(visible = contentVisible, delayMillis = 100) {
                AIInsightSection(
                    narrative = uiState.aiNarrative,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        item {
            AnimatedSection(visible = contentVisible, delayMillis = 200) {
                DesempenhoAreaSection(
                    data = uiState.desempenhoAreas,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        item {
            AnimatedSection(visible = contentVisible, delayMillis = 300) {
                KPICardsSection(
                    roiMedio = uiState.roiMedio,
                    tempoMedio = uiState.tempoMedio,
                    npsInterno = uiState.npsInterno,
                    isDarkTheme = isDarkTheme,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }
        }
    }
}

@Composable
private fun AnimatedSection(
    visible: Boolean,
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    var sectionVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(visible) {
        if (visible) {
            delay(delayMillis.toLong())
            sectionVisible = true
        }
    }
    
    AnimatedVisibility(
        visible = sectionVisible,
        enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
            initialOffsetY = { 40 },
            animationSpec = tween(300)
        )
    ) {
        content()
    }
}

@Composable
private fun PremiumLeaderTopBar(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = stringResource(R.string.home_logo),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun TitleWithAIChip(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.leader_resumo_executivo),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        AIChip(text = "IA")
    }
}

@Composable
private fun AIInsightSection(
    narrative: String,
    modifier: Modifier = Modifier
) {
    AIInsightCard(modifier = modifier.fillMaxWidth()) {
        if (narrative.isNotEmpty()) {
            TypewriterText(
                text = narrative,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                charDelay = 20
            )
        } else {
            Text(
                text = "Carregando insights...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DesempenhoAreaSection(
    data: List<BarChartData>,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.leader_desempenho_area),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = stringResource(R.string.leader_desempenho_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            HorizontalBarChart(
                data = data,
                maxValue = 50f,
                showAxisLabels = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun KPICardsSection(
    roiMedio: Double,
    tempoMedio: Int,
    npsInterno: Int,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.06f)
    } else {
        Color(0xFFE5E7EB)
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PremiumKPICard(
            title = stringResource(R.string.leader_roi_medio),
            value = "${roiMedio.toInt()}%",
            subtitle = "+5% vs mês anterior",
            accentColor = MaterialTheme.colorScheme.primary,
            icon = Icons.Default.TrendingUp,
            isDarkTheme = isDarkTheme,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        
        PremiumKPICard(
            title = stringResource(R.string.leader_tempo_medio),
            value = "$tempoMedio",
            subtitle = "dias para aprovação",
            accentColor = MaterialTheme.colorScheme.tertiary,
            icon = Icons.Default.Schedule,
            isDarkTheme = isDarkTheme,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        
        PremiumKPICard(
            title = stringResource(R.string.leader_nps_interno),
            value = "$npsInterno",
            subtitle = "Excelente",
            accentColor = MaterialTheme.colorScheme.secondary,
            icon = Icons.Default.Groups,
            isDarkTheme = isDarkTheme,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun PremiumKPICard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    icon: ImageVector,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.06f)
    } else {
        Color(0xFFE5E7EB)
    }
    
    val cardModifier = if (isDarkTheme) {
        modifier
            .clip(cardShape)
            .border(1.dp, borderColor, cardShape)
            .background(Color(0xFF1C1C1E))
    } else {
        modifier
            .shadow(
                elevation = 2.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.04f),
                spotColor = Color.Black.copy(alpha = 0.04f)
            )
            .clip(cardShape)
            .border(1.dp, borderColor, cardShape)
            .background(Color.White)
    }
    
    Box(
        modifier = cardModifier.padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = accentColor.copy(alpha = if (isDarkTheme) 0.15f else 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LeaderDashboardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LeaderDashboardBody(
                uiState = LeaderDashboardUiState(
                    aiNarrative = "Esta semana, a divisão de Logística registrou um aumento de 40% em ideias voltadas para redução de custos.",
                    desempenhoAreas = listOf(
                        BarChartData("Logística", 40f, androidx.compose.ui.graphics.Color(0xFF00D4B2)),
                        BarChartData("Qualidade", 32f, androidx.compose.ui.graphics.Color(0xFF00D4B2)),
                        BarChartData("RH", 18f, androidx.compose.ui.graphics.Color(0xFFFF7A00)),
                        BarChartData("TI", 12f, androidx.compose.ui.graphics.Color(0xFFFF7A00))
                    ),
                    roiMedio = 22.0,
                    tempoMedio = 45,
                    npsInterno = 87
                ),
                isDarkTheme = false
            )
        }
    }
}
