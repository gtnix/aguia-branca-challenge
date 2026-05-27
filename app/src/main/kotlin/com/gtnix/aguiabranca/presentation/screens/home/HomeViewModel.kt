package com.gtnix.aguiabranca.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.domain.usecase.dashboard.DashboardData
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetDashboardUseCase
import com.gtnix.aguiabranca.domain.usecase.dashboard.IdeiaConvertidaRecente
import com.gtnix.aguiabranca.domain.usecase.dashboard.WeeklyRecap
import com.gtnix.aguiabranca.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
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
    private val getDashboardUseCase: GetDashboardUseCase,
    private val sessionManager: SessionManager,
    private val calcularPontuacaoUseCase: CalcularPontuacaoUseCase,
    private val getRankingUseCase: GetRankingUseCase
) : ViewModel() {

    private val _perfilOverride = MutableStateFlow<PerfilUsuario?>(null)
    private val _refreshTrigger = MutableStateFlow(0)
    private val _isRefreshing = MutableStateFlow(false)
    private val _loadLeaderMetrics = MutableStateFlow(false)
    private val _loadGestorMetrics = MutableStateFlow(false)
    private val _gamificacao = MutableStateFlow<GamificacaoSnapshot?>(null)

    private val effectivePerfil = combine(
        _perfilOverride,
        sessionManager.currentUserFlow
    ) { override, user ->
        override ?: user?.perfil ?: PerfilUsuario.OPERADOR
    }

    private val dashboardResult = combine(
        effectivePerfil,
        _refreshTrigger
    ) { perfil, _ -> perfil }
        .flatMapLatest { perfil ->
            getDashboardUseCase()
                .onStart { emit(Result.Loading) }
                .stripLeaderOnlyMetrics(perfil)
        }
        .flowOn(Dispatchers.IO)

    val uiState: StateFlow<HomeUiState> = combine(
        combine(
            effectivePerfil,
            _isRefreshing,
            _loadLeaderMetrics,
            _loadGestorMetrics
        ) { perfil, isRefreshing, loadLeaderMetrics, loadGestorMetrics ->
            DeferredMetricsSnapshot(
                perfil = perfil,
                isRefreshing = isRefreshing,
                loadLeaderMetrics = loadLeaderMetrics,
                loadGestorMetrics = loadGestorMetrics
            )
        },
        sessionManager.currentUserFlow,
        dashboardResult,
        _gamificacao
    ) { snapshot, user, result, gamificacao ->
        buildUiState(
            perfil = snapshot.perfil,
            isRefreshing = snapshot.isRefreshing,
            loadLeaderMetrics = snapshot.loadLeaderMetrics,
            loadGestorMetrics = snapshot.loadGestorMetrics,
            userName = user?.nome,
            result = result,
            gamificacao = gamificacao
        )
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isLoading = true)
        )

    init {
        viewModelScope.launch {
            effectivePerfil.collect { perfil ->
                when (perfil) {
                    PerfilUsuario.LIDER -> {
                        _loadLeaderMetrics.value = false
                        _loadGestorMetrics.value = true
                        delay(LEADER_METRICS_DEFER_MS)
                        _loadLeaderMetrics.value = true
                    }
                    PerfilUsuario.GESTOR -> {
                        _loadGestorMetrics.value = false
                        _loadLeaderMetrics.value = true
                        delay(GESTOR_METRICS_DEFER_MS)
                        _loadGestorMetrics.value = true
                    }
                    else -> {
                        _loadLeaderMetrics.value = true
                        _loadGestorMetrics.value = true
                    }
                }
            }
        }

        viewModelScope.launch {
            combine(effectivePerfil, _refreshTrigger) { perfil, _ -> perfil }
                .collect { perfil ->
                    carregarGamificacao(perfil)
                }
        }
    }

    fun carregarDados(perfilString: String) {
        _perfilOverride.value = try {
            PerfilUsuario.valueOf(perfilString)
        } catch (_: Exception) {
            PerfilUsuario.OPERADOR
        }
    }

    fun refresh() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            _refreshTrigger.value += 1
            delay(REFRESH_DEBOUNCE_MS)
            _isRefreshing.value = false
        }
    }

    private suspend fun carregarGamificacao(perfil: PerfilUsuario) {
        val userId = sessionManager.getCurrentUser()?.id ?: return
        try {
            val pontuacao = calcularPontuacaoUseCase.calcular(userId, perfil)
            val ranking = runCatching { getRankingUseCase.executar(userId, perfil) }.getOrNull()
            _gamificacao.value = GamificacaoSnapshot(
                pontuacao = pontuacao,
                posicaoRanking = ranking?.posicaoUsuarioLogado
            )
        } catch (_: Exception) {
            _gamificacao.value = null
        }
    }

    private fun buildUiState(
        perfil: PerfilUsuario,
        isRefreshing: Boolean,
        loadLeaderMetrics: Boolean,
        loadGestorMetrics: Boolean,
        userName: String?,
        result: Result<DashboardData>,
        gamificacao: GamificacaoSnapshot?
    ): HomeUiState {
        val nomeUsuario = extractFirstName(userName)

        return when (result) {
            is Result.Success -> {
                val data = result.data
                val includeHeavyMetrics = perfil != PerfilUsuario.LIDER || loadLeaderMetrics
                val includeGestorMetrics = perfil != PerfilUsuario.GESTOR || loadGestorMetrics

                HomeUiState(
                    isLoading = false,
                    isRefreshing = isRefreshing,
                    perfil = perfil,
                    nomeUsuario = nomeUsuario,
                    orientacoes = data.orientacoes,
                    minhasIdeias = data.ideias,
                    ideiasConvertidasRecentes = data.ideiasConvertidasRecentes,
                    projetosEmAndamento = data.projetosEmAndamento,
                    totalIdeias = data.totalIdeias,
                    totalProjetos = data.totalProjetos,
                    ideiasAprovadas = data.ideiasAprovadas,
                    ideiasEmProjeto = data.ideiasEmProjeto,
                    ideiasPendentesAvaliacao = if (includeGestorMetrics) {
                        data.ideiasPendentesAvaliacao
                    } else {
                        0
                    },
                    investimentoTotal = if (includeHeavyMetrics) data.investimentoTotal else 0.0,
                    retornoTotal = if (includeHeavyMetrics) data.retornoTotal else 0.0,
                    roiConsolidado = if (includeHeavyMetrics) data.roiConsolidado else 0.0,
                    weeklyRecap = data.weeklyRecap,
                    atividadesRecentes = if (includeGestorMetrics) {
                        mapIdeiasToAtividades(data.ideiasRecentesParaAtividade)
                    } else {
                        emptyList()
                    },
                    pontuacao = gamificacao?.pontuacao,
                    posicaoRanking = gamificacao?.posicaoRanking,
                    errorMessageRes = null
                )
            }
            is Result.Error -> HomeUiState(
                isLoading = false,
                isRefreshing = isRefreshing,
                perfil = perfil,
                nomeUsuario = nomeUsuario,
                pontuacao = gamificacao?.pontuacao,
                posicaoRanking = gamificacao?.posicaoRanking,
                errorMessageRes = R.string.error_load_dashboard
            )
            is Result.Loading -> HomeUiState(
                isLoading = true,
                isRefreshing = isRefreshing,
                perfil = perfil,
                nomeUsuario = nomeUsuario,
                pontuacao = gamificacao?.pontuacao,
                posicaoRanking = gamificacao?.posicaoRanking
            )
        }
    }

    private fun extractFirstName(fullName: String?): String {
        if (fullName.isNullOrBlank()) return "Usuário"
        val baseName = fullName.substringBefore("(").trim()
        return baseName.substringBefore(" ").ifBlank { baseName }
    }

    private fun mapIdeiasToAtividades(ideias: List<Ideia>): List<AtividadeRecente> {
        return ideias.mapIndexed { index, ideia ->
            AtividadeRecente(
                id = ideia.id,
                tipo = TipoAtividade.NOVA_IDEIA,
                titulo = "Nova ideia: ${ideia.titulo}",
                descricao = ideia.descricao.take(80) + if (ideia.descricao.length > 80) "..." else "",
                autorNome = ideia.autorNome,
                timestamp = ideia.dataCriacao,
                isRead = index > 0
            )
        }
    }

    companion object {
        private const val REFRESH_DEBOUNCE_MS = 400L
        private const val LEADER_METRICS_DEFER_MS = 150L
        private const val GESTOR_METRICS_DEFER_MS = 150L
    }
}

private data class DeferredMetricsSnapshot(
    val perfil: PerfilUsuario,
    val isRefreshing: Boolean,
    val loadLeaderMetrics: Boolean,
    val loadGestorMetrics: Boolean
)

private data class GamificacaoSnapshot(
    val pontuacao: Pontuacao,
    val posicaoRanking: Int?
)

private fun Flow<Result<DashboardData>>.stripLeaderOnlyMetrics(
    perfil: PerfilUsuario
): Flow<Result<DashboardData>> = map { result ->
    when (result) {
        is Result.Success -> if (perfil == PerfilUsuario.LIDER) {
            result
        } else {
            Result.Success(
                result.data.copy(
                    desempenhoPorArea = emptyList(),
                    tempoMedioAprovacaoDias = 0
                )
            )
        }
        else -> result
    }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
    val nomeUsuario: String = "Usuário",
    val orientacoes: List<OrientacaoEstrategica> = emptyList(),
    val minhasIdeias: List<Ideia> = emptyList(),
    val ideiasConvertidasRecentes: List<IdeiaConvertidaRecente> = emptyList(),
    val projetosEmAndamento: List<Projeto> = emptyList(),
    val totalIdeias: Int = 0,
    val totalProjetos: Int = 0,
    val ideiasAprovadas: Int = 0,
    val ideiasEmProjeto: Int = 0,
    val ideiasPendentesAvaliacao: Int = 0,
    val investimentoTotal: Double = 0.0,
    val retornoTotal: Double = 0.0,
    val roiConsolidado: Double = 0.0,
    val weeklyRecap: WeeklyRecap = WeeklyRecap(),
    val atividadesRecentes: List<AtividadeRecente> = emptyList(),
    val pontuacao: Pontuacao? = null,
    val posicaoRanking: Int? = null,
    val errorMessageRes: Int? = null
) {
    val engajamentoPercentual: Int
        get() {
            if (totalIdeias == 0) return 0
            return ((ideiasAprovadas.toFloat() / totalIdeias) * 100).toInt().coerceIn(0, 100)
        }

    val ideiasPendentes: Int
        get() = ideiasPendentesAvaliacao

    val taxaConversao: Int
        get() {
            if (totalIdeias == 0) return 0
            return ((ideiasEmProjeto.toFloat() / totalIdeias) * 100).toInt().coerceIn(0, 100)
        }
}
