package com.gtnix.aguiabranca.presentation.screens.projetos

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.session.UserSession
import com.gtnix.aguiabranca.presentation.components.AvatarData
import com.gtnix.aguiabranca.presentation.components.AvatarStack
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.GradientProgressBar
import com.gtnix.aguiabranca.presentation.components.InovagabButton
import com.gtnix.aguiabranca.presentation.components.MarcoTimeline
import com.gtnix.aguiabranca.presentation.components.ProjectTimeline
import com.gtnix.aguiabranca.presentation.components.ProjetoStatusBadge
import com.gtnix.aguiabranca.presentation.components.SectionHeader
import com.gtnix.aguiabranca.presentation.components.TimelineStatus
import com.gtnix.aguiabranca.presentation.navigation.Destination
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen
import com.gtnix.aguiabranca.presentation.util.bounceClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProjetoDetalheViewModel @Inject constructor(
    private val projetoRepository: ProjetoRepository,
    private val userSession: UserSession,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjetoDetalheUiState())
    val uiState: StateFlow<ProjetoDetalheUiState> = _uiState.asStateFlow()

    private val projetoId: String = savedStateHandle[Destination.ProjetoDetalhe.ARG_PROJETO_ID] ?: ""

    init {
        carregarProjeto()
    }

    private fun carregarProjeto() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val projeto = projetoRepository.buscarPorId(projetoId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        projeto = projeto,
                        perfil = userSession.perfil,
                        progressoSlider = projeto?.progresso?.toFloat() ?: 0f,
                        marcos = generateMockMarcos(projeto),
                        ideiasVinculadas = generateMockIdeiasVinculadas(),
                        membrosEquipe = generateMockMembros(projeto)
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar projeto") }
            }
        }
    }

    fun onProgressoChange(value: Float) {
        _uiState.update { it.copy(progressoSlider = value) }
    }

    fun salvarProgresso() {
        val projeto = _uiState.value.projeto ?: return
        viewModelScope.launch {
            try {
                val novoProgresso = _uiState.value.progressoSlider.toInt()
                projetoRepository.atualizarProgresso(projeto.id, novoProgresso)
                val atualizado = projeto.copy(progresso = novoProgresso)
                _uiState.update { it.copy(projeto = atualizado, actionSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao salvar progresso") }
            }
        }
    }

    fun concluirProjeto() {
        val projeto = _uiState.value.projeto ?: return
        viewModelScope.launch {
            try {
                val concluido = projeto.copy(
                    status = StatusProjeto.CONCLUIDO,
                    progresso = 100,
                    dataConclusao = System.currentTimeMillis()
                )
                projetoRepository.salvar(concluido)
                _uiState.update { it.copy(projeto = concluido, progressoSlider = 100f, actionSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao concluir projeto") }
            }
        }
    }

    fun atualizarStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(actionSuccess = true) }
            delay(2000)
            _uiState.update { it.copy(actionSuccess = false) }
        }
    }

    private fun generateMockMarcos(projeto: Projeto?): List<MarcoTimeline> {
        if (projeto == null) return emptyList()
        
        val progresso = projeto.progresso
        return listOf(
            MarcoTimeline(
                titulo = "Levantamento de Requisitos",
                data = "10/05/2024",
                status = if (progresso >= 20) TimelineStatus.COMPLETED else TimelineStatus.FUTURE
            ),
            MarcoTimeline(
                titulo = "Desenvolvimento do MVP",
                data = "05/06/2024",
                status = if (progresso >= 40) TimelineStatus.COMPLETED 
                        else if (progresso >= 20) TimelineStatus.CURRENT 
                        else TimelineStatus.FUTURE
            ),
            MarcoTimeline(
                titulo = "Testes e Validação",
                data = "Em andamento",
                status = if (progresso >= 60) TimelineStatus.COMPLETED 
                        else if (progresso >= 40) TimelineStatus.CURRENT 
                        else TimelineStatus.FUTURE
            ),
            MarcoTimeline(
                titulo = "Implantação Piloto",
                data = "20/06/2024",
                status = if (progresso >= 80) TimelineStatus.COMPLETED 
                        else if (progresso >= 60) TimelineStatus.CURRENT 
                        else TimelineStatus.FUTURE
            ),
            MarcoTimeline(
                titulo = "Lançamento Oficial",
                data = "30/06/2024",
                status = if (progresso >= 100) TimelineStatus.COMPLETED 
                        else if (progresso >= 80) TimelineStatus.CURRENT 
                        else TimelineStatus.FUTURE
            )
        )
    }

    private fun generateMockIdeiasVinculadas(): List<IdeiaVinculadaSimple> {
        return listOf(
            IdeiaVinculadaSimple(
                id = "1",
                titulo = "Alertas Inteligentes de Manutenção",
                descricao = "Reduzir falhas e tempo de inatividade da frota.",
                upvotes = 128
            ),
            IdeiaVinculadaSimple(
                id = "2",
                titulo = "Otimização de Rotas com IA",
                descricao = "Reduzir custo de combustível e melhorar entregas.",
                upvotes = 96
            ),
            IdeiaVinculadaSimple(
                id = "3",
                titulo = "Dashboard de Consumo",
                descricao = "Visualização em tempo real do consumo.",
                upvotes = 74
            )
        )
    }

    private fun generateMockMembros(projeto: Projeto?): List<AvatarData> {
        if (projeto == null) return emptyList()
        
        val responsavelInitials = projeto.responsavelNome
            .split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
        
        return listOf(
            AvatarData(responsavelInitials),
            AvatarData("JC"),
            AvatarData("MR"),
            AvatarData("PL"),
            AvatarData("TC")
        )
    }
}

data class IdeiaVinculadaSimple(
    val id: String,
    val titulo: String,
    val descricao: String,
    val upvotes: Int
)

data class ProjetoDetalheUiState(
    val isLoading: Boolean = false,
    val projeto: Projeto? = null,
    val perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
    val progressoSlider: Float = 0f,
    val errorMessage: String? = null,
    val actionSuccess: Boolean = false,
    val marcos: List<MarcoTimeline> = emptyList(),
    val ideiasVinculadas: List<IdeiaVinculadaSimple> = emptyList(),
    val membrosEquipe: List<AvatarData> = emptyList()
)

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
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.btn_voltar),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
                            prazo = formatDate(projeto.dataPrevistaConclusao),
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
    prazo: String,
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
            
            InfoRow(
                icon = Icons.Default.CalendarToday,
                label = stringResource(R.string.projeto_prazo),
                value = prazo
            )
            
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
