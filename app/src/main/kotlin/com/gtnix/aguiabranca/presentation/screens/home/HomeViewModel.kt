package com.gtnix.aguiabranca.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.FunnelStepMetric
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.LeaderDashboardSnapshot
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.repository.GamificacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.domain.usecase.dashboard.DashboardData
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetDashboardUseCase
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetLeaderDashboardUseCase
import com.gtnix.aguiabranca.domain.usecase.dashboard.IdeiaConvertidaRecente
import com.gtnix.aguiabranca.domain.usecase.dashboard.WeeklyRecap
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.isOffline
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
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
    private val getLeaderDashboardUseCase: GetLeaderDashboardUseCase,
    private val sessionManager: SessionManager,
    private val calcularPontuacaoUseCase: CalcularPontuacaoUseCase,
    private val getRankingUseCase: GetRankingUseCase,
    private val gamificacaoRepository: GamificacaoRepository
) : ViewModel() {

    private val _perfilOverride = MutableStateFlow<PerfilUsuario?>(null)
    private val _refreshTrigger = MutableStateFlow(0)
    private val _isRefreshing = MutableStateFlow(false)
    private val _loadLeaderMetrics = MutableStateFlow(false)
    private val _loadGestorMetrics = MutableStateFlow(false)
    private val _gamificacao = MutableStateFlow<GamificacaoSnapshot?>(null)
    private val _catalogoLoja = MutableStateFlow(CatalogoHomeUi())
    private val _leaderSummary = MutableStateFlow<Result<LeaderDashboardSnapshot>?>(null)

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
        combine(_gamificacao, _catalogoLoja) { gamificacao, catalogo ->
            EconomiaHomeSnapshot(gamificacao, catalogo)
        },
        _leaderSummary
    ) { snapshot, user, result, economia, leaderSummary ->
        buildUiState(
            perfil = snapshot.perfil,
            isRefreshing = snapshot.isRefreshing,
            loadLeaderMetrics = snapshot.loadLeaderMetrics,
            loadGestorMetrics = snapshot.loadGestorMetrics,
            userName = user?.nome,
            result = result,
            gamificacao = economia.gamificacao,
            catalogoLoja = economia.catalogo,
            leaderSummary = leaderSummary
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
                    carregarCatalogo(perfil)
                }
        }

        viewModelScope.launch {
            combine(effectivePerfil, _refreshTrigger) { perfil, _ -> perfil }
                .collect { perfil ->
                    if (perfil != PerfilUsuario.LIDER) {
                        _leaderSummary.value = null
                    } else {
                        getLeaderDashboardUseCase().collect { _leaderSummary.value = it }
                    }
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

    /** Recarrega a economia sempre que a Home volta ao primeiro plano (Loja, Carteira, Gestão). */
    fun atualizarGamificacao() {
        viewModelScope.launch {
            val perfil = effectivePerfil.first()
            carregarGamificacao(perfil)
            carregarCatalogo(perfil)
        }
    }

    private suspend fun carregarGamificacao(perfil: PerfilUsuario) {
        val userId = sessionManager.getCurrentUser()?.id ?: return
        try {
            val pontuacao = calcularPontuacaoUseCase.calcular(userId, perfil)
            val ranking = runCatching { getRankingUseCase.executar(userId, perfil) }.getOrNull()
            _gamificacao.value = GamificacaoSnapshot(
                pontuacao = pontuacao,
                posicaoRanking = ranking?.posicaoUsuarioLogado,
                divisaoRanking = ranking?.entradaUsuarioLogado?.divisao?.label
            )
        } catch (_: Exception) {
            _gamificacao.value = null
        }
    }

    private suspend fun carregarCatalogo(perfil: PerfilUsuario) {
        if (perfil != PerfilUsuario.LIDER) {
            _catalogoLoja.value = CatalogoHomeUi()
            return
        }
        when (val result = gamificacaoRepository.recompensas(incluirInativas = true)) {
            is Result.Success -> _catalogoLoja.value = CatalogoHomeUi.from(result.data)
            else -> _catalogoLoja.value = CatalogoHomeUi(carregado = true)
        }
    }

    private fun buildUiState(
        perfil: PerfilUsuario,
        isRefreshing: Boolean,
        loadLeaderMetrics: Boolean,
        loadGestorMetrics: Boolean,
        userName: String?,
        result: Result<DashboardData>,
        gamificacao: GamificacaoSnapshot?,
        catalogoLoja: CatalogoHomeUi,
        leaderSummary: Result<LeaderDashboardSnapshot>?
    ): HomeUiState {
        val nomeUsuario = extractFirstName(userName)

        return when (result) {
            is Result.Success -> {
                val data = result.data
                val includeHeavyMetrics = perfil != PerfilUsuario.LIDER || loadLeaderMetrics
                val includeGestorMetrics = perfil != PerfilUsuario.GESTOR || loadGestorMetrics
                val kpis = leaderKpisOrLocal(perfil, data, leaderSummary)
                val stats = gamificacao?.pontuacao?.estatisticas
                val totalIdeias = if (perfil == PerfilUsuario.OPERADOR && stats != null) {
                    stats.totalIdeias
                } else {
                    kpis.totalIdeias
                }
                val ideiasAprovadas = if (perfil == PerfilUsuario.OPERADOR && stats != null) {
                    stats.ideiasAprovadas
                } else {
                    kpis.ideiasAprovadas
                }

                HomeUiState(
                    isLoading = false,
                    isRefreshing = isRefreshing,
                    perfil = perfil,
                    nomeUsuario = nomeUsuario,
                    orientacoes = data.orientacoes,
                    ideiasPorOrientacao = data.ideiasPorOrientacao,
                    atrasadosPorOrientacao = data.atrasadosPorOrientacao,
                    minhasIdeias = data.ideias,
                    ideiasConvertidasRecentes = data.ideiasConvertidasRecentes,
                    projetosEmAndamento = data.projetosEmAndamento,
                    totalIdeias = totalIdeias,
                    totalProjetos = kpis.totalProjetos,
                    ideiasAprovadas = ideiasAprovadas,
                    ideiasEmProjeto = kpis.ideiasEmProjeto,
                    ideiasPendentesAvaliacao = if (includeGestorMetrics) {
                        data.ideiasPendentesAvaliacao
                    } else {
                        0
                    },
                    filaPendente = if (includeGestorMetrics) data.filaPendente else emptyList(),
                    diasMaisAntigaPendente = if (includeGestorMetrics) data.diasMaisAntigaPendente else null,
                    aprovadasSemProjeto = if (includeGestorMetrics) data.aprovadasSemProjeto else emptyList(),
                    investimentoTotal = if (includeHeavyMetrics) kpis.investimentoTotal else 0.0,
                    retornoTotal = if (includeHeavyMetrics) kpis.retornoTotal else 0.0,
                    roiConsolidado = if (includeHeavyMetrics) kpis.roiConsolidado else 0.0,
                    lucroAnualizado = if (includeHeavyMetrics) kpis.lucroTotal else 0.0,
                    projetosAtrasados = if (includeHeavyMetrics) kpis.projetosAtrasados else 0,
                    tempoMedioAprovacaoDias = if (includeHeavyMetrics) {
                        kpis.tempoMedioAprovacaoDias
                    } else {
                        0
                    },
                    funil = if (includeHeavyMetrics) kpis.funil else emptyList(),
                    weeklyRecap = data.weeklyRecap,
                    atividadesRecentes = if (includeGestorMetrics) {
                        mapIdeiasToAtividades(data.ideiasRecentesParaAtividade)
                    } else {
                        emptyList()
                    },
                    pontuacao = gamificacao?.pontuacao,
                    posicaoRanking = gamificacao?.posicaoRanking,
                    divisaoRanking = gamificacao?.divisaoRanking,
                    catalogoLoja = catalogoLoja,
                    errorMessageRes = kpis.errorMessageRes
                )
            }
            is Result.Error -> {
                val stats = gamificacao?.pontuacao?.estatisticas
                HomeUiState(
                    isLoading = false,
                    isRefreshing = isRefreshing,
                    perfil = perfil,
                    nomeUsuario = nomeUsuario,
                    totalIdeias = if (perfil == PerfilUsuario.OPERADOR) stats?.totalIdeias ?: 0 else 0,
                    ideiasAprovadas = if (perfil == PerfilUsuario.OPERADOR) {
                        stats?.ideiasAprovadas ?: 0
                    } else {
                        0
                    },
                    pontuacao = gamificacao?.pontuacao,
                    posicaoRanking = gamificacao?.posicaoRanking,
                    divisaoRanking = gamificacao?.divisaoRanking,
                    catalogoLoja = catalogoLoja,
                    errorMessageRes = R.string.error_load_dashboard
                )
            }
            is Result.Loading -> HomeUiState(
                isLoading = true,
                isRefreshing = isRefreshing,
                perfil = perfil,
                nomeUsuario = nomeUsuario,
                pontuacao = gamificacao?.pontuacao,
                posicaoRanking = gamificacao?.posicaoRanking,
                divisaoRanking = gamificacao?.divisaoRanking,
                catalogoLoja = catalogoLoja
            )
        }
    }

    private fun leaderKpisOrLocal(
        perfil: PerfilUsuario,
        data: DashboardData,
        leaderSummary: Result<LeaderDashboardSnapshot>?
    ): HomeLeaderKpis {
        if (perfil != PerfilUsuario.LIDER) {
            return HomeLeaderKpis(
                totalIdeias = data.totalIdeias,
                totalProjetos = data.totalProjetos,
                ideiasAprovadas = data.ideiasAprovadas,
                ideiasEmProjeto = data.ideiasEmProjeto,
                investimentoTotal = data.investimentoTotal,
                retornoTotal = data.retornoTotal,
                roiConsolidado = data.roiConsolidado,
                projetosAtrasados = data.projetosAtrasados,
                tempoMedioAprovacaoDias = data.tempoMedioAprovacaoDias
            )
        }
        return when (leaderSummary) {
            is Result.Success -> HomeLeaderKpis(
                totalIdeias = leaderSummary.data.totalIdeias,
                totalProjetos = leaderSummary.data.projetosAtivos,
                ideiasAprovadas = leaderSummary.data.ideiasAprovadas,
                ideiasEmProjeto = leaderSummary.data.ideiasEmProjeto,
                investimentoTotal = leaderSummary.data.investimentoTotal,
                retornoTotal = leaderSummary.data.retornoTotal,
                roiConsolidado = leaderSummary.data.roiMedio,
                lucroTotal = leaderSummary.data.lucroTotal,
                projetosAtrasados = leaderSummary.data.projetosAtrasados,
                tempoMedioAprovacaoDias = leaderSummary.data.tempoMedioAprovacaoDias,
                funil = leaderSummary.data.funil
            )
            is Result.Error -> HomeLeaderKpis(errorMessageRes = errorRes(leaderSummary))
            is Result.Loading, null -> HomeLeaderKpis()
        }
    }

    private fun errorRes(error: Result.Error): Int {
        val text = error.message.orEmpty()
        return when {
            error.isOffline() -> R.string.error_network_required
            text.contains("Líder", ignoreCase = true) -> R.string.error_dashboard_forbidden
            else -> R.string.error_load_dashboard
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
    val posicaoRanking: Int?,
    val divisaoRanking: String?
)

private data class EconomiaHomeSnapshot(
    val gamificacao: GamificacaoSnapshot?,
    val catalogo: CatalogoHomeUi
)

data class CatalogoHomeUi(
    val carregado: Boolean = false,
    val premiosAtivos: Int = 0,
    val semEstoque: Int = 0
) {
    companion object {
        fun from(recompensas: List<Recompensa>): CatalogoHomeUi {
            val ativos = recompensas.filter { it.ativo }
            return CatalogoHomeUi(
                carregado = true,
                premiosAtivos = ativos.size,
                semEstoque = ativos.count { it.estoque <= 0 }
            )
        }
    }
}

private data class HomeLeaderKpis(
    val totalIdeias: Int = 0,
    val totalProjetos: Int = 0,
    val ideiasAprovadas: Int = 0,
    val ideiasEmProjeto: Int = 0,
    val investimentoTotal: Double = 0.0,
    val retornoTotal: Double = 0.0,
    val roiConsolidado: Double = 0.0,
    val lucroTotal: Double = 0.0,
    val projetosAtrasados: Int = 0,
    val tempoMedioAprovacaoDias: Int = 0,
    val funil: List<FunnelStepMetric> = emptyList(),
    val errorMessageRes: Int? = null
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
    val ideiasPorOrientacao: Map<String, Int> = emptyMap(),
    val atrasadosPorOrientacao: Map<String, Int> = emptyMap(),
    val minhasIdeias: List<Ideia> = emptyList(),
    val ideiasConvertidasRecentes: List<IdeiaConvertidaRecente> = emptyList(),
    val projetosEmAndamento: List<Projeto> = emptyList(),
    val totalIdeias: Int = 0,
    val totalProjetos: Int = 0,
    val ideiasAprovadas: Int = 0,
    val ideiasEmProjeto: Int = 0,
    val ideiasPendentesAvaliacao: Int = 0,
    val filaPendente: List<Ideia> = emptyList(),
    val diasMaisAntigaPendente: Int? = null,
    val aprovadasSemProjeto: List<Ideia> = emptyList(),
    val investimentoTotal: Double = 0.0,
    val retornoTotal: Double = 0.0,
    val roiConsolidado: Double = 0.0,
    val lucroAnualizado: Double = 0.0,
    val projetosAtrasados: Int = 0,
    val tempoMedioAprovacaoDias: Int = 0,
    val funil: List<FunnelStepMetric> = emptyList(),
    val weeklyRecap: WeeklyRecap = WeeklyRecap(),
    val atividadesRecentes: List<AtividadeRecente> = emptyList(),
    val pontuacao: Pontuacao? = null,
    val posicaoRanking: Int? = null,
    val divisaoRanking: String? = null,
    val catalogoLoja: CatalogoHomeUi = CatalogoHomeUi(),
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
