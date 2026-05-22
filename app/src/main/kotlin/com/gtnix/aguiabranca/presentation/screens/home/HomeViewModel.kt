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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun carregarDados(perfilString: String) {
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
                            errorMessage = null
                        )
                    }
                    is Result.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
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
}

data class HomeUiState(
    val isLoading: Boolean = false,
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
    val errorMessage: String? = null
) {
    val nomeUsuario: String
        get() = when (perfil) {
            PerfilUsuario.OPERADOR -> "Operador"
            PerfilUsuario.GESTOR -> "Gestor"
            PerfilUsuario.LIDER -> "Líder"
        }
}
