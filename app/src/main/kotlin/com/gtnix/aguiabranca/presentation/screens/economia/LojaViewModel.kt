package com.gtnix.aguiabranca.presentation.screens.economia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.model.Resgate
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
class LojaViewModel @Inject constructor(
    private val gamificacaoRepository: GamificacaoRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val perfil = sessionManager.getCurrentUser()?.perfil ?: PerfilUsuario.OPERADOR

    private val _uiState = MutableStateFlow(LojaUiState())
    val uiState: StateFlow<LojaUiState> = _uiState.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        _uiState.update { it.copy(isLoading = true, erro = null) }
        viewModelScope.launch {
            val painel = async { gamificacaoRepository.painel(perfil) }
            val catalogo = async { gamificacaoRepository.recompensas() }
            val meus = async { gamificacaoRepository.meusResgates() }

            val painelResult = painel.await()
            val catalogoResult = catalogo.await()
            val meusResult = meus.await()

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    asas = (painelResult as? Result.Success)?.data?.asas ?: state.asas,
                    nivel = (painelResult as? Result.Success)?.data?.nivel ?: state.nivel,
                    xp = (painelResult as? Result.Success)?.data?.total ?: state.xp,
                    recompensas = (catalogoResult as? Result.Success)?.data ?: state.recompensas,
                    meusResgates = (meusResult as? Result.Success)?.data ?: state.meusResgates,
                    erro = (catalogoResult as? Result.Error)?.message
                )
            }
        }
    }

    fun pedirConfirmacao(recompensa: Recompensa) {
        if (!recompensa.podeResgatar) return
        _uiState.update { it.copy(confirmando = recompensa) }
    }

    fun cancelarConfirmacao() {
        _uiState.update { it.copy(confirmando = null) }
    }

    fun confirmarResgate() {
        val alvo = _uiState.value.confirmando ?: return
        _uiState.update { it.copy(confirmando = null, isResgatando = true, mensagem = null) }
        viewModelScope.launch {
            when (val result = gamificacaoRepository.resgatar(alvo.id)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isResgatando = false, resgateConcluido = true) }
                    carregar()
                }
                is Result.Error -> _uiState.update {
                    it.copy(isResgatando = false, mensagem = result.message)
                }
                Result.Loading -> Unit
            }
        }
    }

    fun consumirMensagem() {
        _uiState.update { it.copy(mensagem = null, resgateConcluido = false) }
    }
}

data class LojaUiState(
    val isLoading: Boolean = true,
    val isResgatando: Boolean = false,
    val asas: Int = 0,
    val xp: Int = 0,
    val nivel: NivelUsuario = NivelUsuario.INICIANTE,
    val recompensas: List<Recompensa> = emptyList(),
    val meusResgates: List<Resgate> = emptyList(),
    val confirmando: Recompensa? = null,
    val resgateConcluido: Boolean = false,
    val mensagem: String? = null,
    val erro: String? = null
) {
    /** Disponíveis primeiro, depois as mais baratas: o colaborador vê logo o que já pode pegar. */
    val recompensasOrdenadas: List<Recompensa>
        get() = recompensas.sortedWith(compareByDescending<Recompensa> { it.podeResgatar }.thenBy { it.custoAsas })
}
