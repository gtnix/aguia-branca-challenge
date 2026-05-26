package com.gtnix.aguiabranca.presentation.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.gtnix.aguiabranca.domain.usecase.Conquista
import com.gtnix.aguiabranca.domain.usecase.Estatisticas
import com.gtnix.aguiabranca.domain.usecase.NivelUsuario
import com.gtnix.aguiabranca.domain.usecase.Pontuacao
import com.gtnix.aguiabranca.domain.usecase.TipoConquista
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LevelEngajado
import com.gtnix.aguiabranca.presentation.theme.LevelIniciante
import com.gtnix.aguiabranca.presentation.theme.LevelVisionario
import com.gtnix.aguiabranca.presentation.theme.NavBarDimensions
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onNavigateBack: (() -> Unit)? = null,
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
    onNavigateBack: (() -> Unit)?,
    onLogout: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenPadding.Horizontal)
                    .padding(top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.perfil_titulo),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                ProfileHeader(
                    nome = uiState.nome,
                    area = formatAreaLabel(uiState.area),
                    perfil = formatPerfilLabel(uiState.perfil),
                    inicialNome = uiState.inicialNome,
                    isDarkTheme = isDarkTheme
                )

                Spacer(modifier = Modifier.height(20.dp))

                uiState.pontuacao?.let { pontuacao ->
                    NivelCard(
                        pontuacao = pontuacao,
                        isDarkTheme = isDarkTheme
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    ConquistasSection(
                        conquistas = pontuacao.conquistas,
                        isDarkTheme = isDarkTheme
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    EstatisticasRow(
                        estatisticas = pontuacao.estatisticas,
                        isDarkTheme = isDarkTheme
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onLogout)
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.perfil_logout),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(NavBarDimensions.ContentBottomPaddingWithFab))
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    nome: String,
    area: String,
    perfil: String,
    inicialNome: Char,
    isDarkTheme: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .then(
                    if (!isDarkTheme) {
                        Modifier.shadow(
                            elevation = 8.dp,
                            shape = CircleShape,
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )
                    } else Modifier
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = inicialNome.toString(),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = nome,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "$area  •  $perfil",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NivelCard(
    pontuacao: Pontuacao,
    isDarkTheme: Boolean
) {
    val (icon, levelColor) = nivelIconAndColor(pontuacao.nivel)
    
    GlassCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.perfil_nivel_atual),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(levelColor.copy(alpha = if (isDarkTheme) 0.15f else 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = levelColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Text(
                            text = pontuacao.nivel.label,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = levelColor
                        )
                    }
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${pontuacao.total}",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.perfil_pontos),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            LinearProgressIndicator(
                progress = { pontuacao.progressoProximoNivel },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = levelColor,
                trackColor = levelColor.copy(alpha = if (isDarkTheme) 0.15f else 0.1f),
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val proximoNivel = NivelUsuario.proximoNivel(pontuacao.nivel)
            Text(
                text = if (proximoNivel != null) {
                    stringResource(R.string.perfil_pontos_para_nivel, pontuacao.pontosParaProximoNivel, proximoNivel.label)
                } else {
                    stringResource(R.string.perfil_nivel_maximo)
                },
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = if (proximoNivel == null) levelColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ConquistasSection(
    conquistas: List<Conquista>,
    isDarkTheme: Boolean
) {
    val desbloqueadas = conquistas.count { it.desbloqueada }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.perfil_conquistas_label),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$desbloqueadas/${conquistas.size}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.height(180.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(conquistas) { conquista ->
                BadgeItem(
                    conquista = conquista,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

@Composable
private fun BadgeItem(
    conquista: Conquista,
    isDarkTheme: Boolean
) {
    val icon = getConquistaIcon(conquista.tipo)
    val color = if (conquista.desbloqueada) {
        getConquistaColor(conquista.tipo)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
    }
    
    val backgroundColor = if (conquista.desbloqueada) {
        color.copy(alpha = if (isDarkTheme) 0.15f else 0.1f)
    } else {
        if (isDarkTheme) {
            Color(0xFF1C1C1E)
        } else {
            Color(0xFFF3F4F6)
        }
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .then(
                    if (conquista.desbloqueada && !isDarkTheme) {
                        Modifier.shadow(
                            elevation = 2.dp,
                            shape = CircleShape,
                            ambientColor = color.copy(alpha = 0.15f),
                            spotColor = color.copy(alpha = 0.15f)
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = conquista.tipo.titulo,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = conquista.tipo.titulo,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (conquista.desbloqueada) FontWeight.Medium else FontWeight.Normal,
            color = if (conquista.desbloqueada) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            },
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun EstatisticasRow(
    estatisticas: Estatisticas,
    isDarkTheme: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "Ideias",
            value = estatisticas.totalIdeias,
            icon = Icons.Default.Lightbulb,
            color = MaterialTheme.colorScheme.primary,
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Aprovadas",
            value = estatisticas.ideiasAprovadas,
            icon = Icons.Default.CheckCircle,
            color = SuccessGreen,
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            label = "Projetos",
            value = estatisticas.projetosParticipando,
            icon = Icons.Default.Folder,
            color = MaterialTheme.colorScheme.tertiary,
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    label: String,
    value: Int,
    icon: ImageVector,
    color: Color,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.06f)
    } else {
        Color(0xFFE5E7EB)
    }
    
    val backgroundColor = if (isDarkTheme) {
        Color(0xFF1C1C1E)
    } else {
        Color.White
    }
    
    val cardModifier = if (isDarkTheme) {
        modifier
            .clip(cardShape)
            .border(1.dp, borderColor, cardShape)
            .background(backgroundColor)
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
            .background(backgroundColor)
    }
    
    Box(modifier = cardModifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = color.copy(alpha = if (isDarkTheme) 0.15f else 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun nivelIconAndColor(nivel: NivelUsuario): Pair<ImageVector, Color> = when (nivel) {
    NivelUsuario.INICIANTE -> Icons.Filled.Star to LevelIniciante
    NivelUsuario.ENGAJADO -> Icons.AutoMirrored.Filled.TrendingUp to LevelEngajado
    NivelUsuario.VISIONARIO -> Icons.Filled.EmojiEvents to LevelVisionario
}

private fun getConquistaIcon(tipo: TipoConquista): ImageVector = when (tipo) {
    TipoConquista.PRIMEIRA_IDEIA -> Icons.Default.Lightbulb
    TipoConquista.INOVADOR -> Icons.Default.Stars
    TipoConquista.VISIONARIO_IDEIAS -> Icons.Default.Visibility
    TipoConquista.PRIMEIRA_APROVACAO -> Icons.Default.CheckCircle
    TipoConquista.INFLUENCIADOR -> Icons.Default.EmojiEvents
    TipoConquista.EM_PROJETO -> Icons.Default.RocketLaunch
    TipoConquista.TRANSFORMADOR -> Icons.Default.AutoAwesome
    TipoConquista.LIDER_INOVACAO -> Icons.Default.MilitaryTech
}

private fun getConquistaColor(tipo: TipoConquista): Color = when (tipo) {
    TipoConquista.PRIMEIRA_IDEIA -> Color(0xFFFFC107)
    TipoConquista.INOVADOR -> Color(0xFFFF9800)
    TipoConquista.VISIONARIO_IDEIAS -> Color(0xFF9C27B0)
    TipoConquista.PRIMEIRA_APROVACAO -> Color(0xFF4CAF50)
    TipoConquista.INFLUENCIADOR -> Color(0xFFFFD700)
    TipoConquista.EM_PROJETO -> Color(0xFF2196F3)
    TipoConquista.TRANSFORMADOR -> Color(0xFFE91E63)
    TipoConquista.LIDER_INOVACAO -> Color(0xFFFF5722)
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
    InovagabTheme {
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
                    pontosParaProximoNivel = 81,
                    conquistas = listOf(
                        Conquista("1", TipoConquista.PRIMEIRA_IDEIA, true),
                        Conquista("2", TipoConquista.INOVADOR, true),
                        Conquista("3", TipoConquista.VISIONARIO_IDEIAS, false),
                        Conquista("4", TipoConquista.PRIMEIRA_APROVACAO, true),
                        Conquista("5", TipoConquista.INFLUENCIADOR, false),
                        Conquista("6", TipoConquista.EM_PROJETO, true),
                        Conquista("7", TipoConquista.TRANSFORMADOR, false),
                        Conquista("8", TipoConquista.LIDER_INOVACAO, false)
                    ),
                    estatisticas = Estatisticas(
                        totalIdeias = 7,
                        ideiasAprovadas = 4,
                        projetosParticipando = 1
                    )
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
    InovagabTheme {
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
                    pontosParaProximoNivel = 0,
                    conquistas = listOf(
                        Conquista("1", TipoConquista.PRIMEIRA_IDEIA, true),
                        Conquista("2", TipoConquista.INOVADOR, true),
                        Conquista("3", TipoConquista.VISIONARIO_IDEIAS, true),
                        Conquista("4", TipoConquista.PRIMEIRA_APROVACAO, true),
                        Conquista("5", TipoConquista.INFLUENCIADOR, true),
                        Conquista("6", TipoConquista.EM_PROJETO, true),
                        Conquista("7", TipoConquista.TRANSFORMADOR, true),
                        Conquista("8", TipoConquista.LIDER_INOVACAO, true)
                    ),
                    estatisticas = Estatisticas(
                        totalIdeias = 18,
                        ideiasAprovadas = 12,
                        projetosParticipando = 5
                    )
                )
            ),
            onNavigateBack = {},
            onLogout = {}
        )
    }
}
