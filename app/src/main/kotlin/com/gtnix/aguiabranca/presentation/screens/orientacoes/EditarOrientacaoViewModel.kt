package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditarOrientacaoViewModel @Inject constructor(
    private val orientacaoRepository: OrientacaoRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orientacaoId: String = checkNotNull(
        savedStateHandle[Destination.EditarOrientacao.ARG_ORIENTACAO_ID]
    )

    private val _uiState = MutableStateFlow(EditarOrientacaoUiState())
    val uiState: StateFlow<EditarOrientacaoUiState> = _uiState.asStateFlow()

    init {
        carregarOrientacao()
    }

    private fun carregarOrientacao() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val orientacao = orientacaoRepository.buscarPorId(orientacaoId)
                if (orientacao != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            orientacaoOriginal = orientacao,
                            titulo = orientacao.titulo,
                            descricao = orientacao.descricao,
                            categoria = orientacao.categoria,
                            prioridade = orientacao.prioridade
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = R.string.error_orientacao_not_found
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = R.string.error_load_orientacao
                    )
                }
            }
        }
    }

    fun onTituloChange(titulo: String) {
        _uiState.update { it.copy(titulo = titulo, errorMessageRes = null) }
    }

    fun onDescricaoChange(descricao: String) {
        _uiState.update { it.copy(descricao = descricao, errorMessageRes = null) }
    }

    fun onCategoriaChange(categoria: CategoriaOrientacao) {
        _uiState.update { it.copy(categoria = categoria) }
    }

    fun onPrioridadeChange(prioridade: Int) {
        _uiState.update { it.copy(prioridade = prioridade) }
    }

    fun salvar(onSuccess: () -> Unit) {
        val current = _uiState.value
        val user = sessionManager.getCurrentUser()
        val orientacaoOriginal = current.orientacaoOriginal

        if (user == null) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_user_not_logged) }
            return
        }

        if (user.perfil != PerfilUsuario.LIDER) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_only_leader_create_orientacao) }
            return
        }

        if (orientacaoOriginal == null) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_orientacao_not_found) }
            return
        }

        if (current.titulo.isBlank() || current.descricao.isBlank()) {
            _uiState.update { it.copy(errorMessageRes = R.string.novo_projeto_erro_campos) }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val orientacaoAtualizada = orientacaoOriginal.copy(
                    titulo = current.titulo.trim(),
                    descricao = current.descricao.trim(),
                    categoria = current.categoria,
                    prioridade = current.prioridade
                )

                orientacaoRepository.salvar(orientacaoAtualizada)

                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessageRes = R.string.error_save_orientacao
                    )
                }
            }
        }
    }
}

data class EditarOrientacaoUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val orientacaoOriginal: OrientacaoEstrategica? = null,
    val titulo: String = "",
    val descricao: String = "",
    val categoria: CategoriaOrientacao = CategoriaOrientacao.EFICIENCIA_OPERACIONAL,
    val prioridade: Int = 3,
    val errorMessageRes: Int? = null
)
