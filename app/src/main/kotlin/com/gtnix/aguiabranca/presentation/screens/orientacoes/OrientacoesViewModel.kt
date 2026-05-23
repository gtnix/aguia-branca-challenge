package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrientacoesViewModel @Inject constructor(
    private val orientacaoRepository: OrientacaoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrientacoesUiState())
    val uiState: StateFlow<OrientacoesUiState> = _uiState.asStateFlow()

    init {
        carregarOrientacoes()
    }

    private fun carregarOrientacoes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                orientacaoRepository.listarTodas().collect { orientacoes ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            orientacoes = orientacoes,
                            errorMessage = null
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar orientações"
                    )
                }
            }
        }
    }

    fun desativarOrientacao(id: String) {
        if (!isLider()) {
            _uiState.update { it.copy(errorMessage = "Apenas líderes podem gerenciar orientações") }
            return
        }
        
        viewModelScope.launch {
            try {
                orientacaoRepository.desativar(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao desativar orientação") }
            }
        }
    }

    fun excluirOrientacao(id: String) {
        if (!isLider()) {
            _uiState.update { it.copy(errorMessage = "Apenas líderes podem gerenciar orientações") }
            return
        }
        
        viewModelScope.launch {
            try {
                orientacaoRepository.excluir(id)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao excluir orientação") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun isLider(): Boolean {
        return sessionManager.getCurrentUser()?.perfil == PerfilUsuario.LIDER
    }
}

data class OrientacoesUiState(
    val isLoading: Boolean = false,
    val orientacoes: List<OrientacaoEstrategica> = emptyList(),
    val errorMessage: String? = null
)
