package com.gtnix.aguiabranca.presentation.screens.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.usecase.NivelUsuario
import com.gtnix.aguiabranca.domain.usecase.Pontuacao
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaBlue
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaBlueDark
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaOrange
import com.gtnix.aguiabranca.presentation.theme.AguiaBrancaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) {
            onLogout()
        }
    }

    PerfilScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onLogout = { viewModel.onLogout() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PerfilScreenContent(
    uiState: PerfilUiState,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AguiaBrancaBlueDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AguiaBrancaBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(
                    nome = uiState.nome,
                    area = formatAreaLabel(uiState.area),
                    perfil = formatPerfilLabel(uiState.perfil),
                    inicialNome = uiState.inicialNome
                )

                Spacer(modifier = Modifier.height(24.dp))

                uiState.pontuacao?.let { pontuacao ->
                    ConquistasSection(pontuacao = pontuacao)
                }

                Spacer(modifier = Modifier.weight(1f))

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sair", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    nome: String,
    area: String,
    perfil: String,
    inicialNome: Char
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(AguiaBrancaBlue, AguiaBrancaBlueDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inicialNome.toString(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = nome,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(area, style = MaterialTheme.typography.labelMedium) }
                )
                SuggestionChip(
                    onClick = {},
                    label = { Text(perfil, style = MaterialTheme.typography.labelMedium) }
                )
            }
        }
    }
}

@Composable
private fun ConquistasSection(pontuacao: Pontuacao) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Minhas Conquistas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "${pontuacao.total}",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AguiaBrancaOrange
            )
            Text(
                text = "pontos",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            NivelBadge(nivel = pontuacao.nivel)

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                progress = { pontuacao.progressoProximoNivel },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = AguiaBrancaOrange,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(8.dp))

            val proximoNivel = NivelUsuario.proximoNivel(pontuacao.nivel)
            if (proximoNivel != null) {
                Text(
                    text = "${pontuacao.pontosParaProximoNivel} pontos para ${proximoNivel.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Nível máximo alcançado!",
                    style = MaterialTheme.typography.bodySmall,
                    color = AguiaBrancaOrange,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.gamification_explanation),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NivelBadge(nivel: NivelUsuario) {
    val (icon, color) = nivelIconAndColor(nivel)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = nivel.label,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = nivel.label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

private fun nivelIconAndColor(nivel: NivelUsuario): Pair<ImageVector, Color> = when (nivel) {
    NivelUsuario.INICIANTE -> Icons.Filled.Star to Color(0xFF78909C)
    NivelUsuario.ENGAJADO -> Icons.AutoMirrored.Filled.TrendingUp to Color(0xFF1565C0)
    NivelUsuario.VISIONARIO -> Icons.Filled.EmojiEvents to Color(0xFFFF8F00)
}

private fun formatAreaLabel(area: String): String = when (area) {
    "OPERACOES" -> "Operações"
    "LOGISTICA" -> "Logística"
    "COMERCIAL" -> "Comercial"
    "FINANCEIRO" -> "Financeiro"
    "RH" -> "RH"
    "TI" -> "TI"
    "MARKETING" -> "Marketing"
    "QUALIDADE" -> "Qualidade"
    else -> area
}

private fun formatPerfilLabel(perfil: String): String = when (perfil) {
    "OPERADOR" -> "Operador"
    "GESTOR" -> "Gestor"
    "LIDER" -> "Líder"
    else -> perfil
}

@Preview(showBackground = true)
@Composable
private fun PerfilScreenPreview() {
    AguiaBrancaTheme {
        PerfilScreenContent(
            uiState = PerfilUiState(
                isLoading = false,
                nome = "João Silva",
                area = "OPERACOES",
                perfil = "OPERADOR",
                inicialNome = 'J',
                pontuacao = Pontuacao(
                    total = 70,
                    nivel = NivelUsuario.ENGAJADO,
                    progressoProximoNivel = 0.19f,
                    pontosParaProximoNivel = 81
                )
            ),
            onNavigateBack = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PerfilScreenVisionarioPreview() {
    AguiaBrancaTheme {
        PerfilScreenContent(
            uiState = PerfilUiState(
                isLoading = false,
                nome = "Maria Santos",
                area = "TI",
                perfil = "LIDER",
                inicialNome = 'M',
                pontuacao = Pontuacao(
                    total = 250,
                    nivel = NivelUsuario.VISIONARIO,
                    progressoProximoNivel = 1f,
                    pontosParaProximoNivel = 0
                )
            ),
            onNavigateBack = {},
            onLogout = {}
        )
    }
}
