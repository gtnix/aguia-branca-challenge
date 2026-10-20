package com.gtnix.aguiabranca.presentation.screens.economia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.EventoPontos
import com.gtnix.aguiabranca.domain.model.Impacto
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.repository.GamificacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CarteiraViewModel @Inject constructor(
    private val gamificacaoRepository: GamificacaoRepository,
    sessionManager: SessionManager
) : ViewModel() {

    val perfil: PerfilUsuario = sessionManager.getCurrentUser()?.perfil ?: PerfilUsuario.OPERADOR

    private val _uiState = MutableStateFlow(CarteiraUiState())
    val uiState: StateFlow<CarteiraUiState> = _uiState.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        _uiState.update { it.copy(isLoading = true, erro = null) }
        viewModelScope.launch {
            val extrato = async { gamificacaoRepository.extrato() }
            val painel = async { gamificacaoRepository.painel(perfil) }
            val extratoResult = extrato.await()
            val painelResult = painel.await()

            _uiState.update { state ->
                when (extratoResult) {
                    is Result.Success -> state.copy(
                        isLoading = false,
                        xpTotal = extratoResult.data.xpTotal,
                        asas = extratoResult.data.asas,
                        eventos = extratoResult.data.eventos,
                        impacto = (painelResult as? Result.Success)?.data?.impacto ?: state.impacto
                    )
                    is Result.Error -> state.copy(isLoading = false, erro = extratoResult.message)
                    Result.Loading -> state
                }
            }
            if (extratoResult is Result.Success) {
                gamificacaoRepository.marcarNovidadesVistas()
            }
        }
    }
}

data class CarteiraUiState(
    val isLoading: Boolean = true,
    val xpTotal: Int = 0,
    val asas: Int = 0,
    val eventos: List<EventoPontos> = emptyList(),
    val impacto: Impacto = Impacto(),
    val erro: String? = null
)
