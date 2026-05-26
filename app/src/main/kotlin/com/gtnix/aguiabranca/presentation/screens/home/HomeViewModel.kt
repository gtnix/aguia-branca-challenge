package com.gtnix.aguiabranca.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetDashboardUseCase
import com.gtnix.aguiabranca.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TipoAtividade {
    NOVA_IDEIA,
    IDEIA_APROVADA,
    IDEIA_EM_ANALISE,
    PROJETO_CRIADO,
    PROJETO_CONCLUIDO
}

data class AtividadeRecente(
    val id: String,
    val tipo: TipoAtividade,
    val titulo: String,
    val descricao: String,
    val autorNome: String,
    val autorAvatar: String? = null,
    val timestamp: Long,
    val isRead: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private var currentPerfil: String = ""

    fun carregarDados(perfilString: String) {
        currentPerfil = perfilString
        val perfil = try {
            PerfilUsuario.valueOf(perfilString)
        } catch (e: Exception) {
            PerfilUsuario.OPERADOR
        }

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            perfil = perfil
        )

        viewModelScope.launch {
            getDashboardUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val data = result.data
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            orientacoes = data.orientacoes,
                            minhasIdeias = data.ideias,
                            projetosEmAndamento = data.projetosEmAndamento,
                            totalIdeias = data.totalIdeias,
                            totalProjetos = data.totalProjetos,
                            ideiasAprovadas = data.ideiasAprovadas,
                            ideiasEmProjeto = data.ideiasEmProjeto,
                            investimentoTotal = data.investimentoTotal,
                            retornoTotal = data.retornoTotal,
                            roiConsolidado = data.roiConsolidado,
                            atividadesRecentes = generateMockAtividades(data.ideias),
                            errorMessage = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.message ?: "Erro ao carregar dados"
                        )
                    }
                    is Result.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            }
        }
    }
    
    fun refresh() {
        if (_uiState.value.isRefreshing) return
        
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        
        viewModelScope.launch {
            delay(500)
            carregarDados(currentPerfil)
        }
    }
    
    private fun generateMockAtividades(ideias: List<Ideia>): List<AtividadeRecente> {
        val now = System.currentTimeMillis()
        val atividades = mutableListOf<AtividadeRecente>()
        
        ideias.take(3).forEachIndexed { index, ideia ->
            val hoursAgo = when (index) {
                0 -> 2
                1 -> 5
                else -> 24
            }
            atividades.add(
                AtividadeRecente(
                    id = "ativ_${ideia.id}",
                    tipo = TipoAtividade.NOVA_IDEIA,
                    titulo = "Nova ideia: ${ideia.titulo}",
                    descricao = ideia.descricao.take(80) + if (ideia.descricao.length > 80) "..." else "",
                    autorNome = ideia.autorNome,
                    autorAvatar = null,
                    timestamp = now - (hoursAgo * 60 * 60 * 1000L),
                    isRead = index > 0
                )
            )
        }
        
        if (atividades.isEmpty()) {
            atividades.addAll(listOf(
                AtividadeRecente(
                    id = "mock_1",
                    tipo = TipoAtividade.NOVA_IDEIA,
                    titulo = "Nova ideia: Plataforma de Mentoria Interna",
                    descricao = "Uma plataforma para conectar mentores e mentorados dentro da empresa.",
                    autorNome = "Carlos Silva",
                    timestamp = now - (2 * 60 * 60 * 1000L)
                ),
                AtividadeRecente(
                    id = "mock_2",
                    tipo = TipoAtividade.NOVA_IDEIA,
                    titulo = "Nova ideia: IA para Suporte ao Cliente",
                    descricao = "Utilizar IA para automatizar e melhorar o atendimento ao cliente.",
                    autorNome = "Ana Costa",
                    timestamp = now - (5 * 60 * 60 * 1000L),
                    isRead = true
                ),
                AtividadeRecente(
                    id = "mock_3",
                    tipo = TipoAtividade.NOVA_IDEIA,
                    titulo = "Nova ideia: App de Mobilidade Corporativa",
                    descricao = "Aplicativo para otimizar o deslocamento dos colaboradores.",
                    autorNome = "Pedro Santos",
                    timestamp = now - (24 * 60 * 60 * 1000L),
                    isRead = true
                )
            ))
        }
        
        return atividades
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
    val orientacoes: List<OrientacaoEstrategica> = emptyList(),
    val minhasIdeias: List<Ideia> = emptyList(),
    val projetosEmAndamento: List<Projeto> = emptyList(),
    val totalIdeias: Int = 0,
    val totalProjetos: Int = 0,
    val ideiasAprovadas: Int = 0,
    val ideiasEmProjeto: Int = 0,
    val investimentoTotal: Double = 0.0,
    val retornoTotal: Double = 0.0,
    val roiConsolidado: Double = 0.0,
    val atividadesRecentes: List<AtividadeRecente> = emptyList(),
    val errorMessage: String? = null
) {
    val nomeUsuario: String
        get() = when (perfil) {
            PerfilUsuario.OPERADOR -> "Operador"
            PerfilUsuario.GESTOR -> "Gestor"
            PerfilUsuario.LIDER -> "Líder"
        }
    
    val engajamentoPercentual: Int
        get() {
            if (totalIdeias == 0) return 0
            return ((ideiasAprovadas.toFloat() / totalIdeias) * 100).toInt().coerceIn(0, 100)
        }

    val ideiasPendentes: Int
        get() = (totalIdeias - ideiasAprovadas).coerceAtLeast(0)

    val taxaConversao: Int
        get() {
            if (totalIdeias == 0) return 0
            return ((ideiasEmProjeto.toFloat() / totalIdeias) * 100).toInt().coerceIn(0, 100)
        }
}
