package com.gtnix.aguiabranca.presentation.screens.leader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.DashboardInsight
import com.gtnix.aguiabranca.domain.model.FunnelStepMetric
import com.gtnix.aguiabranca.domain.model.LeaderDashboardSnapshot
import com.gtnix.aguiabranca.domain.model.MetricasIa
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.repository.DashboardRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.usecase.dashboard.AreaDesempenhoMetric
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetLeaderDashboardUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.isOffline
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Chave usada para representar "sem filtro" na lista de estratégias. */
private const val FILTER_NONE = ""

@HiltViewModel
class LeaderDashboardViewModel @Inject constructor(
    private val getLeaderDashboardUseCase: GetLeaderDashboardUseCase,
    private val dashboardRepository: DashboardRepository,
    private val orientacaoRepository: OrientacaoRepository
) : ViewModel() {

    // ----- filter state -----
    private val _selectedOrientacaoId = MutableStateFlow(FILTER_NONE)
    val selectedOrientacaoId: StateFlow<String> = _selectedOrientacaoId

    // ----- orientacoes for filter selector -----
    private val _orientacoes = MutableStateFlow<List<OrientacaoEstrategica>>(emptyList())
    val orientacoes: StateFlow<List<OrientacaoEstrategica>> = _orientacoes

    // ----- narrative state -----
    private val _insight = MutableStateFlow(DashboardInsight.vazio)
    private val _isInsightLoading = MutableStateFlow(false)
    private var narrativeJob: Job? = null

    // ----- AI governance metrics -----
    private val _metricasIa = MutableStateFlow<MetricasIa?>(null)

    // ----- refresh -----
    private val _refreshTrigger = MutableStateFlow(0)
    private val _isRefreshing = MutableStateFlow(false)

    private val dashboardResult = combine(_refreshTrigger, _selectedOrientacaoId) { _, id -> id }
        .flatMapLatest { orientacaoId ->
            getLeaderDashboardUseCase(orientacaoId.takeIf { it.isNotBlank() })
                .onStart { emit(Result.Loading) }
        }
        .flowOn(Dispatchers.IO)

    val uiState: StateFlow<LeaderDashboardUiState> = combine(
        _isRefreshing,
        dashboardResult,
        _insight,
        _isInsightLoading,
        _metricasIa
    ) { isRefreshing, result, insight, isInsightLoading, metricasIa ->
        when (result) {
            is Result.Success -> toUiState(result.data, isRefreshing, insight, isInsightLoading)
                .copy(metricasIa = metricasIa)
            is Result.Error -> LeaderDashboardUiState(
                isLoading = false,
                isRefreshing = isRefreshing,
                insight = insight,
                isInsightLoading = isInsightLoading,
                metricasIa = metricasIa,
                errorMessageRes = errorRes(result)
            )
            is Result.Loading -> LeaderDashboardUiState(
                isLoading = true,
                isRefreshing = isRefreshing,
                isInsightLoading = isInsightLoading
            )
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LeaderDashboardUiState(isLoading = true, isInsightLoading = true)
        )

    init {
        // Load orientacoes for filter selector
        orientacaoRepository.listarAtivas()
            .onEach { _orientacoes.value = it }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)

        // Fetch narrative whenever filter or refresh trigger changes
        viewModelScope.launch(Dispatchers.IO) {
            combine(_refreshTrigger, _selectedOrientacaoId) { _, id -> id }
                .collect { orientacaoId ->
                    fetchMetricasIa(orientacaoId.takeIf { it.isNotBlank() })
                    fetchInsight(orientacaoId.takeIf { it.isNotBlank() })
                }
        }
    }

    fun selectOrientacao(orientacaoId: String?) {
        _selectedOrientacaoId.value = orientacaoId?.takeIf { it.isNotBlank() } ?: FILTER_NONE
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

    private suspend fun fetchMetricasIa(orientacaoId: String?) {
        _metricasIa.value = when (val result = dashboardRepository.obterMetricasIa(orientacaoId)) {
            is Result.Success -> result.data
            else -> null
        }
    }

    private suspend fun fetchInsight(orientacaoId: String?) {
        narrativeJob?.cancel()
        narrativeJob = viewModelScope.launch(Dispatchers.IO) {
            _isInsightLoading.value = true
            _insight.value = DashboardInsight.vazio
            when (val result = dashboardRepository.obterInsightIa(orientacaoId)) {
                is Result.Success -> _insight.value = result.data
                else -> _insight.value = DashboardInsight.vazio
            }
            _isInsightLoading.value = false
        }
        narrativeJob?.join()
    }

    private fun toUiState(
        data: LeaderDashboardSnapshot,
        isRefreshing: Boolean,
        insight: DashboardInsight,
        isInsightLoading: Boolean
    ): LeaderDashboardUiState {
        val totalIdeias = data.totalIdeias
        val ideiasAprovadas = data.ideiasAprovadas
        val ideiasEmProjeto = data.ideiasEmProjeto
        val aprovadasNoFluxo = ideiasAprovadas + ideiasEmProjeto
        val taxaAprovacao = if (totalIdeias > 0) aprovadasNoFluxo * 100 / totalIdeias else 0
        val taxaConversao = if (aprovadasNoFluxo > 0) ideiasEmProjeto * 100 / aprovadasNoFluxo else 0

        return LeaderDashboardUiState(
            isLoading = false,
            isRefreshing = isRefreshing,
            insight = insight,
            isInsightLoading = isInsightLoading,
            desempenhoAreas = data.desempenhoPorArea,
            funil = data.funil,
            taxaAprovacaoPercent = taxaAprovacao,
            taxaConversaoPercent = taxaConversao,
            roiMedio = data.roiMedio,
            tempoMedio = data.tempoMedioAprovacaoDias,
            totalIdeias = totalIdeias,
            ideiasAprovadas = ideiasAprovadas,
            ideiasEmProjeto = ideiasEmProjeto,
            projetosAtivos = data.projetosAtivos,
            investimentoTotal = data.investimentoTotal,
            retornoMensal = data.retornoTotal,
            lucroAnualizado = data.lucroTotal,
            projetosNoPrazo = data.projetosNoPrazo,
            projetosAtrasados = data.projetosAtrasados,
            progressoMedioProjetos = data.ganhoProdutividadePercentual,
            errorMessageRes = null
        )
    }

    private fun errorRes(error: Result.Error): Int {
        val text = error.message.orEmpty()
        return when {
            error.isOffline() -> R.string.error_network_required
            text.contains("Líder", ignoreCase = true) -> R.string.error_dashboard_forbidden
            else -> R.string.error_load_dashboard
        }
    }

    companion object {
        private const val REFRESH_DEBOUNCE_MS = 400L
    }
}

data class LeaderDashboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isInsightLoading: Boolean = false,
    val insight: DashboardInsight = DashboardInsight.vazio,
    val desempenhoAreas: List<AreaDesempenhoMetric> = emptyList(),
    val funil: List<FunnelStepMetric> = emptyList(),
    /** Taxa de aprovação: (estoque APROVADA + CONVERTIDA) / totalIdeias × 100 */
    val taxaAprovacaoPercent: Int = 0,
    /** Taxa de conversão: CONVERTIDA / (estoque APROVADA + CONVERTIDA) × 100 */
    val taxaConversaoPercent: Int = 0,
    val roiMedio: Double = 0.0,
    val tempoMedio: Int = 0,
    val totalIdeias: Int = 0,
    val ideiasAprovadas: Int = 0,
    val ideiasEmProjeto: Int = 0,
    val projetosAtivos: Int = 0,
    val investimentoTotal: Double = 0.0,
    val retornoMensal: Double = 0.0,
    val lucroAnualizado: Double = 0.0,
    val projetosNoPrazo: Int = 0,
    val projetosAtrasados: Int = 0,
    val progressoMedioProjetos: Double = 0.0,
    val metricasIa: MetricasIa? = null,
    val errorMessageRes: Int? = null
)
