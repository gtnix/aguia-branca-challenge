package com.gtnix.aguiabranca.presentation.screens.projetos

import androidx.compose.animation.AnimatedVisibility
import kotlinx.coroutines.delay
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.presentation.components.AguiaTopBar
import com.gtnix.aguiabranca.presentation.components.AvatarData
import com.gtnix.aguiabranca.presentation.components.AvatarStack
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.GradientProgressBar
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.MarcoTimeline
import com.gtnix.aguiabranca.presentation.components.ProjectTimeline
import com.gtnix.aguiabranca.presentation.components.ProjetoStatusBadge
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.theme.WarningAmber
import com.gtnix.aguiabranca.presentation.theme.ErrorRed
import com.gtnix.aguiabranca.presentation.util.bounceClick
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjetoDetalheScreen(
    viewModel: ProjetoDetalheViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProjetoDetalheContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onAtualizarStatus = viewModel::atualizarStatus
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjetoDetalheContent(
    uiState: ProjetoDetalheUiState,
    onNavigateBack: () -> Unit,
    onAtualizarStatus: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AguiaTopBar(
                title = stringResource(R.string.projeto_detalhe_titulo),
                onBackClick = onNavigateBack
            )
        },
        bottomBar = {
            if (uiState.projeto != null && 
                (uiState.perfil == PerfilUsuario.GESTOR || uiState.perfil == PerfilUsuario.LIDER)) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    InovagabButton(
                        text = stringResource(R.string.projeto_atualizar_status),
                        onClick = onAtualizarStatus,
                        leadingIcon = Icons.Default.Refresh,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                }
            }
        }
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

            uiState.projeto == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.projeto_detalhe_nao_encontrado),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            else -> {
                val projeto = uiState.projeto
                var contentVisible by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    contentVisible = true
                }
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    AnimatedSection(
                        visible = contentVisible,
                        delayMillis = 0
                    ) {
                        HeaderSection(
                            nome = projeto.nome,
                            status = projeto.status
                        )
                    }
                    
                    AnimatedSection(
                        visible = contentVisible,
                        delayMillis = 100
                    ) {
                        ProgressSection(
                            progress = projeto.progresso / 100f
                        )
                    }
                    
                    AnimatedSection(
                        visible = contentVisible,
                        delayMillis = 200
                    ) {
                        InfoCardSection(
                            responsavel = projeto.responsavelNome,
                            projeto = projeto,
                            membros = uiState.membrosEquipe
                        )
                    }
                    
                    if (uiState.marcos.isNotEmpty()) {
                        AnimatedSection(
                            visible = contentVisible,
                            delayMillis = 300
                        ) {
                            MarcosSection(marcos = uiState.marcos)
                        }
                    }
                    
                    if (uiState.ideiasVinculadas.isNotEmpty()) {
                        AnimatedSection(
                            visible = contentVisible,
                            delayMillis = 400
                        ) {
                            IdeiasVinculadasSection(ideias = uiState.ideiasVinculadas)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
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
private fun HeaderSection(
    nome: String,
    status: StatusProjeto
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = nome,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        ProjetoStatusBadge(status = status)
    }
}

@Composable
private fun ProgressSection(progress: Float) {
    GradientProgressBar(
        progress = progress,
        modifier = Modifier.fillMaxWidth(),
        height = 8.dp,
        showLabel = true,
        gradientColors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    )
}

@Composable
private fun InfoCardSection(
    responsavel: String,
    projeto: Projeto,
    membros: List<AvatarData>
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InfoRow(
                icon = Icons.Default.Person,
                label = stringResource(R.string.projeto_detalhe_responsavel),
                value = responsavel
            )

            PrazoComparisonSection(projeto = projeto)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = stringResource(R.string.projeto_equipe),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "${membros.size} membros",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                
                AvatarStack(
                    avatars = membros,
                    maxVisible = 3,
                    avatarSize = 36.dp
                )
            }
        }
    }
}

private enum class PrazoStatus {
    INDEFINIDO,
    NO_PRAZO,
    PROXIMO,
    ATRASADO,
    CONCLUIDO_NO_PRAZO,
    CONCLUIDO_ATRASO
}

private fun calcularPrazoStatus(projeto: Projeto): PrazoStatus {
    val prazo = projeto.dataPrevistaConclusao ?: return PrazoStatus.INDEFINIDO
    val agora = System.currentTimeMillis()
    val seteDiasMs = 7L * 24 * 60 * 60 * 1000

    if (projeto.status == StatusProjeto.CONCLUIDO) {
        val dataReal = projeto.dataConclusao ?: agora
        return if (dataReal <= prazo) PrazoStatus.CONCLUIDO_NO_PRAZO else PrazoStatus.CONCLUIDO_ATRASO
    }

    return when {
        agora > prazo -> PrazoStatus.ATRASADO
        prazo - agora <= seteDiasMs -> PrazoStatus.PROXIMO
        else -> PrazoStatus.NO_PRAZO
    }
}

@Composable
private fun PrazoComparisonSection(projeto: Projeto) {
    val prazoStatus = calcularPrazoStatus(projeto)
    val statusColor = when (prazoStatus) {
        PrazoStatus.INDEFINIDO -> MaterialTheme.colorScheme.outline
        PrazoStatus.NO_PRAZO, PrazoStatus.CONCLUIDO_NO_PRAZO -> SuccessGreen
        PrazoStatus.PROXIMO -> WarningAmber
        PrazoStatus.ATRASADO, PrazoStatus.CONCLUIDO_ATRASO -> ErrorRed
    }
    val statusLabelRes = when (prazoStatus) {
        PrazoStatus.INDEFINIDO -> R.string.projeto_prazo_indefinido
        PrazoStatus.NO_PRAZO -> R.string.projeto_prazo_no_prazo
        PrazoStatus.PROXIMO -> R.string.projeto_prazo_proximo
        PrazoStatus.ATRASADO -> R.string.projeto_prazo_atrasado
        PrazoStatus.CONCLUIDO_NO_PRAZO -> R.string.projeto_prazo_concluido_prazo
        PrazoStatus.CONCLUIDO_ATRASO -> R.string.projeto_prazo_concluido_atraso
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.projeto_prazo),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(statusColor.copy(alpha = 0.1f))
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.projeto_prazo_previsto),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = formatDate(projeto.dataPrevistaConclusao),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(R.string.projeto_prazo_realizado),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = projeto.dataConclusao?.let { formatDate(it) }
                        ?: stringResource(R.string.projeto_prazo_indefinido),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(statusLabelRes),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MarcosSection(marcos: List<MarcoTimeline>) {
    Column {
        SectionHeader(
            title = stringResource(R.string.projeto_marcos),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        ProjectTimeline(
            marcos = marcos,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun IdeiasVinculadasSection(ideias: List<IdeiaVinculadaSimple>) {
    Column {
        SectionHeader(
            title = stringResource(R.string.projeto_ideias_vinculadas),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(end = 20.dp)
        ) {
            items(ideias, key = { it.id }) { ideia ->
                LinkedIdeiaCard(ideia = ideia)
            }
        }
    }
}

@Composable
private fun LinkedIdeiaCard(
    ideia: IdeiaVinculadaSimple,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    Card(
        modifier = modifier
            .width(200.dp)
            .bounceClick {},
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isDarkTheme) 0.dp else 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = ideia.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = ideia.descricao,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${ideia.upvotes}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long?): String {
    if (timestamp == null) return "A definir"
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    return sdf.format(Date(timestamp))
}
