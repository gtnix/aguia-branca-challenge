package com.gtnix.aguiabranca.presentation.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaTheme

/**
 * HomeScreen - Tela Principal / Dashboard
 *
 * ## Conceito FIAP - Material 04A (Scaffold)
 *
 * O Scaffold é a estrutura padrão de uma tela Material Design:
 *
 * ```
 * ┌────────────────────────────────────┐
 * │           TopAppBar               │ ← topBar
 * ├────────────────────────────────────┤
 * │                                    │
 * │                                    │
 * │           Content                  │ ← content (recebe padding)
 * │                                    │
 * │                                    │
 * │                              [FAB] │ ← floatingActionButton
 * ├────────────────────────────────────┤
 * │        Bottom Navigation          │ ← bottomBar
 * └────────────────────────────────────┘
 * ```
 *
 * ### paddingValues
 *
 * O Scaffold passa `paddingValues` para o conteúdo para que
 * ele não fique escondido atrás do topBar ou bottomBar.
 *
 * ```kotlin
 * Scaffold { paddingValues ->
 *     LazyColumn(
 *         modifier = Modifier.padding(paddingValues)
 *     ) {
 *         // Conteúdo
 *     }
 * }
 * ```
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    perfil: String,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToOrientacoes: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToNovaIdeia: () -> Unit
) {
    val uiState = viewModel.uiState

    // Carrega dados quando a tela é exibida
    LaunchedEffect(perfil) {
        viewModel.carregarDados(perfil)
    }

    HomeScreenContent(
        uiState = uiState,
        onNavigateToIdeias = onNavigateToIdeias,
        onNavigateToProjetos = onNavigateToProjetos,
        onNavigateToPerfil = onNavigateToPerfil,
        onNavigateToNovaIdeia = onNavigateToNovaIdeia
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToIdeias: () -> Unit,
    onNavigateToProjetos: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToNovaIdeia: () -> Unit
) {
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        // =====================================================================
        // TOP APP BAR
        // =====================================================================
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Olá, ${uiState.nomeUsuario}!",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Bem-vindo à Plataforma de Inovação",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Notificações */ }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificações"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },

        // =====================================================================
        // BOTTOM NAVIGATION BAR
        // =====================================================================
        bottomBar = {
            NavigationBar {
                val items = listOf(
                    BottomNavItemData("Home", Icons.Filled.Home, Icons.Outlined.Home),
                    BottomNavItemData("Ideias", Icons.Filled.Lightbulb, Icons.Outlined.Lightbulb),
                    BottomNavItemData("Projetos", Icons.Filled.Folder, Icons.Outlined.Folder),
                    BottomNavItemData("Perfil", Icons.Filled.Person, Icons.Outlined.Person)
                )

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selectedNavIndex == index) 
                                    item.selectedIcon 
                                else 
                                    item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = selectedNavIndex == index,
                        onClick = {
                            selectedNavIndex = index
                            when (index) {
                                0 -> { /* Já está no Home */ }
                                1 -> onNavigateToIdeias()
                                2 -> onNavigateToProjetos()
                                3 -> onNavigateToPerfil()
                            }
                        }
                    )
                }
            }
        },

        // =====================================================================
        // FLOATING ACTION BUTTON
        // =====================================================================
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToNovaIdeia,
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nova Ideia"
                )
            }
        }
    ) { paddingValues ->
        // =====================================================================
        // CONTENT
        // =====================================================================
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Seção: Métricas Rápidas
                item {
                    MetricasSection(
                        totalIdeias = uiState.totalIdeias,
                        ideiasAprovadas = uiState.ideiasAprovadas,
                        totalProjetos = uiState.totalProjetos
                    )
                }

                // Seção: Orientações Estratégicas
                if (uiState.orientacoes.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Orientações Estratégicas")
                    }
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.orientacoes) { orientacao ->
                                OrientacaoCard(
                                    titulo = orientacao.titulo,
                                    categoria = orientacao.categoria.name
                                )
                            }
                        }
                    }
                }

                // Seção: Ações Rápidas
                item {
                    SectionHeader(title = "Ações Rápidas")
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Lightbulb,
                            title = "Nova Ideia",
                            onClick = onNavigateToNovaIdeia
                        )
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Folder,
                            title = "Ver Projetos",
                            onClick = onNavigateToProjetos
                        )
                    }
                }

                // Seção: Minhas Ideias Recentes
                if (uiState.minhasIdeias.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Minhas Ideias Recentes")
                    }
                    items(uiState.minhasIdeias) { ideia ->
                        IdeiaResumoCard(
                            titulo = ideia.titulo,
                            status = ideia.status.name,
                            area = ideia.area.name
                        )
                    }
                }
            }
        }
    }
}

/**
 * Header de seção com título estilizado.
 */
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

/**
 * Seção de métricas rápidas do dashboard.
 */
@Composable
private fun MetricasSection(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    totalProjetos: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MetricaCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Lightbulb,
            valor = totalIdeias.toString(),
            label = "Ideias"
        )
        MetricaCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Star,
            valor = ideiasAprovadas.toString(),
            label = "Aprovadas"
        )
        MetricaCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.TrendingUp,
            valor = totalProjetos.toString(),
            label = "Projetos"
        )
    }
}

/**
 * Card de métrica individual.
 */
@Composable
private fun MetricaCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    valor: String,
    label: String
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Card de orientação estratégica.
 */
@Composable
private fun OrientacaoCard(
    titulo: String,
    categoria: String
) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = categoria.replace("_", " "),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )
        }
    }
}

/**
 * Card de ação rápida.
 */
@Composable
private fun ActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

/**
 * Card de resumo de ideia.
 */
@Composable
private fun IdeiaResumoCard(
    titulo: String,
    status: String,
    area: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = area.replace("_", " "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            StatusChip(status = status)
        }
    }
}

/**
 * Chip de status colorido.
 */
@Composable
private fun StatusChip(status: String) {
    val (color, text) = when (status) {
        "PENDENTE" -> MaterialTheme.colorScheme.outline to "Pendente"
        "EM_ANALISE" -> MaterialTheme.colorScheme.tertiary to "Em Análise"
        "APROVADA" -> MaterialTheme.colorScheme.primary to "Aprovada"
        "REPROVADA" -> MaterialTheme.colorScheme.error to "Reprovada"
        else -> MaterialTheme.colorScheme.outline to status
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
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

/**
 * Data class para itens do Bottom Navigation.
 */
private data class BottomNavItemData(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    AguiaBrancaTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                perfil = PerfilUsuario.GESTOR,
                totalIdeias = 15,
                ideiasAprovadas = 8,
                totalProjetos = 3
            ),
            onNavigateToIdeias = {},
            onNavigateToProjetos = {},
            onNavigateToPerfil = {},
            onNavigateToNovaIdeia = {}
        )
    }
}
