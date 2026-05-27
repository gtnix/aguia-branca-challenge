package com.gtnix.aguiabranca.presentation.screens.leader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.usecase.dashboard.AreaDesempenhoMetric
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetDashboardUseCase
import com.gtnix.aguiabranca.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderDashboardViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase
) : ViewModel() {

    private val _refreshTrigger = MutableStateFlow(0)
    private val _isRefreshing = MutableStateFlow(false)

    private val dashboardResult = _refreshTrigger
        .flatMapLatest {
            getDashboardUseCase()
                .onStart { emit(Result.Loading) }
        }
        .flowOn(Dispatchers.IO)

    val uiState: StateFlow<LeaderDashboardUiState> = combine(
        _isRefreshing,
        dashboardResult
    ) { isRefreshing, result ->
        when (result) {
            is Result.Success -> {
                val data = result.data
                LeaderDashboardUiState(
                    isLoading = false,
                    isRefreshing = isRefreshing,
                    aiNarrative = generateAINarrative(data.totalIdeias, data.roiConsolidado),
                    desempenhoAreas = data.desempenhoPorArea,
                    roiMedio = data.roiConsolidado,
                    tempoMedio = data.tempoMedioAprovacaoDias,
                    totalIdeias = data.totalIdeias,
                    ideiasAprovadas = data.ideiasAprovadas,
                    ideiasEmProjeto = data.ideiasEmProjeto,
                    projetosAtivos = data.totalProjetos,
                    investimentoTotal = data.investimentoTotal,
                    retornoTotal = data.retornoTotal,
                    errorMessageRes = null
                )
            }
            is Result.Error -> LeaderDashboardUiState(
                isLoading = false,
                isRefreshing = isRefreshing,
                errorMessageRes = R.string.error_load_dashboard
            )
            is Result.Loading -> LeaderDashboardUiState(
                isLoading = true,
                isRefreshing = isRefreshing
            )
        }
    }
        .flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LeaderDashboardUiState(isLoading = true)
        )

    fun refresh() {
        if (_isRefreshing.value) return

        viewModelScope.launch {
            _isRefreshing.value = true
            _refreshTrigger.value += 1
            delay(REFRESH_DEBOUNCE_MS)
            _isRefreshing.value = false
        }
    }

    private fun generateAINarrative(totalIdeias: Int, roi: Double): String {
        return when {
            totalIdeias == 0 ->
                "Ainda não há ideias registradas. Incentive a equipe a submeter propostas alinhadas às orientações estratégicas."
            roi > 0 ->
                "O portfólio de inovação conta com $totalIdeias ideias ativas, com ROI consolidado de ${String.format("%.0f", roi)}% a.a."
            else ->
                "O portfólio de inovação conta com $totalIdeias ideias ativas. Acompanhe a conversão em projetos para elevar o retorno."
        }
    }

    companion object {
        private const val REFRESH_DEBOUNCE_MS = 400L
    }
}

data class LeaderDashboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val aiNarrative: String = "",
    val desempenhoAreas: List<AreaDesempenhoMetric> = emptyList(),
    val roiMedio: Double = 0.0,
    val tempoMedio: Int = 0,
    val totalIdeias: Int = 0,
    val ideiasAprovadas: Int = 0,
    val ideiasEmProjeto: Int = 0,
    val projetosAtivos: Int = 0,
    val investimentoTotal: Double = 0.0,
    val retornoTotal: Double = 0.0,
    val errorMessageRes: Int? = null
)
