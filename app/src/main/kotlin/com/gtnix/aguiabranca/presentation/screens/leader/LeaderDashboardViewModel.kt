package com.gtnix.aguiabranca.presentation.screens.leader

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetDashboardUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.presentation.components.BarChartData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderDashboardViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderDashboardUiState())
    val uiState: StateFlow<LeaderDashboardUiState> = _uiState.asStateFlow()

    init {
        carregarDados()
    }

    fun carregarDados() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        viewModelScope.launch {
            getDashboardUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val data = result.data
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            aiNarrative = generateAINarrative(data.totalIdeias, data.roiConsolidado),
                            desempenhoAreas = generateDesempenhoAreas(),
                            roiMedio = data.roiConsolidado,
                            tempoMedio = 45,
                            npsInterno = 87,
                            totalIdeias = data.totalIdeias,
                            ideiasAprovadas = data.ideiasAprovadas,
                            projetosAtivos = data.totalProjetos,
                            investimentoTotal = data.investimentoTotal,
                            retornoTotal = data.retornoTotal,
                            errorMessage = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            aiNarrative = generateMockAINarrative(),
                            desempenhoAreas = generateDesempenhoAreas(),
                            roiMedio = 22.0,
                            tempoMedio = 45,
                            npsInterno = 87,
                            totalIdeias = 47,
                            ideiasAprovadas = 18,
                            projetosAtivos = 7,
                            investimentoTotal = 350000.0,
                            retornoTotal = 480000.0,
                            errorMessage = null
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
            carregarDados()
        }
    }

    private fun generateAINarrative(totalIdeias: Int, roi: Double): String {
        return "Esta semana, a divisão de Logística registrou um aumento de 40% em ideias " +
               "voltadas para redução de custos. Três novos projetos foram iniciados, " +
               "com previsão de ROI consolidado de ${String.format("%.0f", roi)}% a.a."
    }

    private fun generateMockAINarrative(): String {
        return "Esta semana, a divisão de Logística registrou um aumento de 40% em ideias " +
               "voltadas para redução de custos. Três novos projetos foram iniciados, " +
               "com previsão de ROI consolidado de 22% a.a."
    }

    private fun generateDesempenhoAreas(): List<BarChartData> {
        return listOf(
            BarChartData(
                label = "Logística",
                value = 40f,
                color = Color(0xFF00D4B2)
            ),
            BarChartData(
                label = "Qualidade",
                value = 32f,
                color = Color(0xFF00D4B2)
            ),
            BarChartData(
                label = "RH",
                value = 18f,
                color = Color(0xFFFF7A00)
            ),
            BarChartData(
                label = "TI",
                value = 12f,
                color = Color(0xFFFF7A00)
            )
        )
    }
}

data class LeaderDashboardUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val aiNarrative: String = "",
    val desempenhoAreas: List<BarChartData> = emptyList(),
    val roiMedio: Double = 0.0,
    val tempoMedio: Int = 0,
    val npsInterno: Int = 0,
    val totalIdeias: Int = 0,
    val ideiasAprovadas: Int = 0,
    val projetosAtivos: Int = 0,
    val investimentoTotal: Double = 0.0,
    val retornoTotal: Double = 0.0,
    val errorMessage: String? = null
)
