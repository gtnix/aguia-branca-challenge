package com.gtnix.aguiabranca.presentation.screens.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import com.gtnix.aguiabranca.presentation.components.InovagabFlatCard
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.BuildConfig
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.AparenciaSection
import com.gtnix.aguiabranca.presentation.components.PerfilSubsectionHeader
import com.gtnix.aguiabranca.presentation.theme.AsaAzul
import com.gtnix.aguiabranca.presentation.theme.CardDimensions
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.MarinhoInstitucional
import com.gtnix.aguiabranca.presentation.theme.NavBarDimensions
import com.gtnix.aguiabranca.presentation.theme.ScreenPadding
import com.gtnix.aguiabranca.presentation.theme.TemaPreferencia
import com.gtnix.aguiabranca.presentation.theme.TemaViewModel
import com.gtnix.aguiabranca.presentation.theme.spacing

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    onNavigateBack: (() -> Unit)? = null,
    onLogout: () -> Unit,
    temaViewModel: TemaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tema by temaViewModel.tema.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) onLogout()
    }

    PerfilScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onLogout = viewModel::onLogout,
        tema = tema,
        onTemaChange = temaViewModel::selecionar
    )
}

@Composable
private fun PerfilScreenContent(
    uiState: PerfilUiState,
    onNavigateBack: (() -> Unit)?,
    onLogout: () -> Unit,
    tema: TemaPreferencia = TemaPreferencia.SISTEMA,
    onTemaChange: (TemaPreferencia) -> Unit = {}
) {
    val spacing = MaterialTheme.spacing

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AguiaTopBar(
                title = stringResource(R.string.perfil_titulo),
                onBackClick = onNavigateBack
            )
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                return@Column
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ScreenPadding.Horizontal)
                    .padding(top = spacing.small)
            ) {
                ProfileHeader(
                    nome = uiState.nome,
                    inicial = uiState.inicialNome,
                    subtitulo = listOfNotNull(
                        uiState.perfil?.let { perfilLabel(it) },
                        uiState.divisao?.label
                    ).joinToString("  ·  ")
                )

                uiState.errorMessageRes?.let { erro ->
                    Spacer(modifier = Modifier.height(spacing.medium))
                    Text(
                        text = stringResource(erro),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(spacing.extraLarge))

                SeusDadosSection(uiState = uiState)

                Spacer(modifier = Modifier.height(spacing.extraLarge))

                AparenciaSection(tema = tema, onTemaChange = onTemaChange)

                Spacer(modifier = Modifier.height(spacing.extraLarge))

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(CardDimensions.CornerRadiusSmall),
                    border = BorderStroke(1.dp, LocalInovagabColors.current.cardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (LocalDarkTheme.current) {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        text = stringResource(R.string.perfil_logout),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(spacing.medium))

                Text(
                    text = stringResource(R.string.perfil_versao, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(
                        NavBarDimensions.ContentBottomPadding +
                            WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    )
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(nome: String, inicial: Char, subtitulo: String) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(MarinhoInstitucional, AsaAzul))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = inicial.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(spacing.medium))
        Text(
            text = nome,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (subtitulo.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitulo,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SeusDadosSection(uiState: PerfilUiState) {
    val spacing = MaterialTheme.spacing
    val campos = listOfNotNull(
        uiState.email.takeIf { it.isNotBlank() }?.let {
            Triple(Icons.Outlined.Email, stringResource(R.string.perfil_dado_email), it)
        },
        uiState.divisao?.let {
            Triple(Icons.Outlined.Business, stringResource(R.string.perfil_dado_divisao), it.label)
        },
        uiState.area?.let {
            Triple(Icons.Outlined.Work, stringResource(R.string.perfil_dado_area), areaLabel(it))
        },
        uiState.perfil?.let {
            Triple(Icons.Outlined.Badge, stringResource(R.string.perfil_dado_perfil), perfilLabel(it))
        }
    )

    PerfilSubsectionHeader(title = stringResource(R.string.perfil_seus_dados))
    Spacer(modifier = Modifier.height(spacing.small))

    InovagabFlatCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column {
            campos.forEachIndexed { index, (icon, rotulo, valor) ->
                DadoLinha(icon = icon, rotulo = rotulo, valor = valor)
                if (index < campos.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 52.dp),
                        color = LocalInovagabColors.current.cardBorder
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(spacing.small))

    Row(
        modifier = Modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(14.dp)
                .padding(top = 1.dp)
        )
        Text(
            text = stringResource(R.string.perfil_dados_rh),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DadoLinha(icon: ImageVector, rotulo: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rotulo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun perfilLabel(perfil: PerfilUsuario): String = when (perfil) {
    PerfilUsuario.OPERADOR -> stringResource(R.string.perfil_operador)
    PerfilUsuario.GESTOR -> stringResource(R.string.perfil_gestor)
    PerfilUsuario.LIDER -> stringResource(R.string.perfil_lider)
}

private fun areaLabel(area: AreaAtuacao): String = when (area) {
    AreaAtuacao.OPERACOES -> "Operações"
    AreaAtuacao.LOGISTICA -> "Logística"
    AreaAtuacao.COMERCIAL -> "Comercial"
    AreaAtuacao.FINANCEIRO -> "Financeiro"
    AreaAtuacao.RH -> "RH"
    AreaAtuacao.TI -> "TI"
    AreaAtuacao.MARKETING -> "Marketing"
    AreaAtuacao.QUALIDADE -> "Qualidade"
}

@Preview(showBackground = true, name = "Claro")
@Composable
private fun PerfilScreenPreview() {
    InovagabTheme(darkTheme = false) {
        PerfilScreenContent(
            uiState = PerfilUiState(
                isLoading = false,
                nome = "Pedro Santos",
                email = "pedro.santos@aguiabranca.com.br",
                area = AreaAtuacao.OPERACOES,
                divisao = DivisaoNegocio.LOGISTICA,
                perfil = PerfilUsuario.OPERADOR,
                inicialNome = 'P'
            ),
            onNavigateBack = {},
            onLogout = {}
        )
    }
}

@Preview(showBackground = true, name = "Escuro")
@Composable
private fun PerfilScreenDarkPreview() {
    InovagabTheme(darkTheme = true) {
        PerfilScreenContent(
            uiState = PerfilUiState(
                isLoading = false,
                nome = "Maria Santos",
                email = "maria.santos@aguiabranca.com.br",
                area = AreaAtuacao.TI,
                divisao = DivisaoNegocio.PASSAGEIROS,
                perfil = PerfilUsuario.LIDER,
                inicialNome = 'M'
            ),
            onNavigateBack = {},
            onLogout = {}
        )
    }
}
