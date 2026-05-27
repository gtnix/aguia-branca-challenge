package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrientacoesViewModel @Inject constructor(
    private val orientacaoRepository: OrientacaoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _actionErrorRes = MutableStateFlow<Int?>(null)

    private val _isLider = sessionManager.getCurrentUser()?.perfil == PerfilUsuario.LIDER

    val uiState: StateFlow<OrientacoesUiState> = combine(
        orientacaoRepository.listarTodas()
            .map { orientacoes ->
                OrientacoesUiState(
                    isLoading = false,
                    orientacoes = orientacoes,
                    isLider = _isLider,
                    errorMessageRes = null
                )
            }
            .catch {
                emit(
                    OrientacoesUiState(
                        isLoading = false,
                        isLider = _isLider,
                        errorMessageRes = R.string.error_load_orientacoes
                    )
                )
            },
        _actionErrorRes
    ) { loadedState, actionErrorRes ->
        loadedState.copy(errorMessageRes = actionErrorRes ?: loadedState.errorMessageRes)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OrientacoesUiState(isLoading = true, isLider = _isLider)
    )

    fun toggleAtivacao(id: String, estaAtiva: Boolean) {
        if (!isLider()) {
            _actionErrorRes.value = R.string.error_only_leader_orientacoes
            return
        }

        viewModelScope.launch {
            try {
                if (estaAtiva) {
                    orientacaoRepository.desativar(id)
                } else {
                    orientacaoRepository.ativar(id)
                }
            } catch (e: Exception) {
                _actionErrorRes.value = if (estaAtiva) {
                    R.string.error_deactivate_orientacao
                } else {
                    R.string.error_activate_orientacao
                }
            }
        }
    }

    fun desativarOrientacao(id: String) {
        toggleAtivacao(id, estaAtiva = true)
    }

    fun excluirOrientacao(id: String) {
        if (!isLider()) {
            _actionErrorRes.value = R.string.error_only_leader_orientacoes
            return
        }

        viewModelScope.launch {
            try {
                orientacaoRepository.excluir(id)
            } catch (e: Exception) {
                _actionErrorRes.value = R.string.error_delete_orientacao
            }
        }
    }

    fun clearError() {
        _actionErrorRes.value = null
    }

    private fun isLider(): Boolean {
        return sessionManager.getCurrentUser()?.perfil == PerfilUsuario.LIDER
    }
}

data class OrientacoesUiState(
    val isLoading: Boolean = false,
    val orientacoes: List<OrientacaoEstrategica> = emptyList(),
    val isLider: Boolean = false,
    val errorMessageRes: Int? = null
)
