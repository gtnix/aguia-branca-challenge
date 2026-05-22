package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.session.UserSession
import com.gtnix.aguiabranca.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdeiaDetalheViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val userSession: UserSession,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(IdeiaDetalheUiState())
    val uiState: StateFlow<IdeiaDetalheUiState> = _uiState.asStateFlow()

    private val ideiaId: String = savedStateHandle[Destination.IdeiaDetalhe.ARG_IDEIA_ID] ?: ""

    init {
        carregarIdeia()
    }

    private fun carregarIdeia() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val ideia = ideiaRepository.buscarPorId(ideiaId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ideia = ideia,
                        perfil = userSession.perfil,
                        impacto = ideia?.impactoEstimado?.toFloat() ?: 1f,
                        esforco = ideia?.esforcoEstimado?.toFloat() ?: 1f
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar ideia") }
            }
        }
    }

    fun onImpactoChange(value: Float) {
        _uiState.update { it.copy(impacto = value) }
    }

    fun onEsforcoChange(value: Float) {
        _uiState.update { it.copy(esforco = value) }
    }

    fun onFeedbackChange(text: String) {
        _uiState.update { it.copy(feedback = text) }
    }

    fun iniciarAnalise() {
        val ideia = _uiState.value.ideia ?: return
        viewModelScope.launch {
            try {
                val atualizada = ideia.copy(
                    status = StatusIdeia.EM_ANALISE,
                    dataAvaliacao = System.currentTimeMillis()
                )
                ideiaRepository.salvar(atualizada)
                _uiState.update { it.copy(ideia = atualizada) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao iniciar análise") }
            }
        }
    }

    fun aprovar() {
        val current = _uiState.value
        val ideia = current.ideia ?: return
        viewModelScope.launch {
            try {
                val atualizada = ideia.copy(
                    status = StatusIdeia.APROVADA,
                    feedback = current.feedback.ifBlank { null },
                    impactoEstimado = current.impacto.toInt(),
                    esforcoEstimado = current.esforco.toInt(),
                    dataAvaliacao = System.currentTimeMillis()
                )
                ideiaRepository.salvar(atualizada)
                _uiState.update { it.copy(ideia = atualizada, actionSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao aprovar ideia") }
            }
        }
    }

    fun reprovar() {
        val current = _uiState.value
        val ideia = current.ideia ?: return
        if (current.feedback.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Feedback é obrigatório para reprovar") }
            return
        }
        viewModelScope.launch {
            try {
                val atualizada = ideia.copy(
                    status = StatusIdeia.REPROVADA,
                    feedback = current.feedback,
                    impactoEstimado = current.impacto.toInt(),
                    esforcoEstimado = current.esforco.toInt(),
                    dataAvaliacao = System.currentTimeMillis()
                )
                ideiaRepository.salvar(atualizada)
                _uiState.update { it.copy(ideia = atualizada, actionSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao reprovar ideia") }
            }
        }
    }
}

data class IdeiaDetalheUiState(
    val isLoading: Boolean = false,
    val ideia: Ideia? = null,
    val perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
    val impacto: Float = 1f,
    val esforco: Float = 1f,
    val feedback: String = "",
    val errorMessage: String? = null,
    val actionSuccess: Boolean = false
) {
    val scorePriorizacao: Int
        get() = impacto.toInt() - esforco.toInt()
}
