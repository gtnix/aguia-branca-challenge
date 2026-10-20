package com.gtnix.aguiabranca.presentation.screens.economia

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.repository.GamificacaoRepository
import com.gtnix.aguiabranca.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Catálogo da Loja visto pelo Líder: só consulta dos prêmios ativos. */
@HiltViewModel
class CatalogoRecompensasViewModel @Inject constructor(
    private val gamificacaoRepository: GamificacaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoRecompensasUiState())
    val uiState: StateFlow<CatalogoRecompensasUiState> = _uiState.asStateFlow()

    init {
        carregar()
    }

    fun carregar() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = gamificacaoRepository.recompensas(incluirInativas = false)) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, catalogo = result.data.filter(Recompensa::ativo))
                }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, mensagem = result.message) }
                Result.Loading -> Unit
            }
        }
    }

    fun consumirMensagem() {
        _uiState.update { it.copy(mensagem = null) }
    }
}

data class CatalogoRecompensasUiState(
    val isLoading: Boolean = true,
    val catalogo: List<Recompensa> = emptyList(),
    val mensagem: String? = null
)
