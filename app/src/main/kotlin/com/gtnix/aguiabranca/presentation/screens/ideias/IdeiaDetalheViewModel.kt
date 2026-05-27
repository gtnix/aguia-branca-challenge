package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.usecase.ideia.AprovarIdeiaUseCase
import com.gtnix.aguiabranca.domain.util.Result
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
    private val orientacaoRepository: OrientacaoRepository,
    private val aprovarIdeiaUseCase: AprovarIdeiaUseCase,
    private val sessionManager: SessionManager,
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
                val perfil = sessionManager.getCurrentUser()?.perfil ?: PerfilUsuario.OPERADOR
                val orientacao = ideia?.orientacaoId?.let { orientacaoRepository.buscarPorId(it) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ideia = ideia,
                        perfil = perfil,
                        orientacao = orientacao,
                        impacto = (ideia?.impactoEstimado?.takeIf { value -> value > 0 } ?: 3).toFloat(),
                        esforco = (ideia?.esforcoEstimado?.takeIf { value -> value > 0 } ?: 2).toFloat()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_load_idea) }
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
        _uiState.update { it.copy(feedback = text, errorMessageRes = null) }
    }

    fun iniciarAnalise() {
        val ideia = _uiState.value.ideia ?: return
        if (ideia.status != StatusIdeia.PENDENTE) return
        viewModelScope.launch {
            try {
                ideiaRepository.atualizarStatus(ideia.id, StatusIdeia.EM_ANALISE)
                refreshIdeia()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessageRes = R.string.error_approve_idea) }
            }
        }
    }

    fun upvote() {
        if (_uiState.value.jaVotou) return
        viewModelScope.launch {
            try {
                ideiaRepository.incrementUpvote(ideiaId)
                val atualizada = ideiaRepository.buscarPorId(ideiaId)
                _uiState.update { it.copy(ideia = atualizada, jaVotou = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessageRes = R.string.error_upvote_idea) }
            }
        }
    }

    fun aprovar() {
        val current = _uiState.value
        val ideia = current.ideia ?: return
        if (!current.canEvaluate || !current.isPendingEvaluation) return
        viewModelScope.launch {
            when (
                val result = aprovarIdeiaUseCase(
                    ideiaId = ideia.id,
                    aprovada = true,
                    feedback = current.feedback.ifBlank { null },
                    impactoEstimado = current.impacto.toInt(),
                    esforcoEstimado = current.esforco.toInt()
                )
            ) {
                is Result.Success -> refreshIdeia(showSuccess = true)
                is Result.Error -> {
                    _uiState.update { it.copy(errorMessageRes = R.string.error_approve_idea) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun reprovar() {
        val current = _uiState.value
        val ideia = current.ideia ?: return
        if (!current.canEvaluate || !current.isPendingEvaluation) return
        if (current.feedback.isBlank()) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_feedback_required_reject) }
            return
        }
        viewModelScope.launch {
            when (
                val result = aprovarIdeiaUseCase(
                    ideiaId = ideia.id,
                    aprovada = false,
                    feedback = current.feedback,
                    impactoEstimado = current.impacto.toInt(),
                    esforcoEstimado = current.esforco.toInt()
                )
            ) {
                is Result.Success -> refreshIdeia(showSuccess = true)
                is Result.Error -> {
                    _uiState.update { it.copy(errorMessageRes = R.string.error_reject_idea) }
                }
                is Result.Loading -> Unit
            }
        }
    }

    private suspend fun refreshIdeia(showSuccess: Boolean = false) {
        val atualizada = ideiaRepository.buscarPorId(ideiaId)
        val orientacao = atualizada?.orientacaoId?.let { orientacaoRepository.buscarPorId(it) }
        _uiState.update {
            it.copy(
                ideia = atualizada,
                orientacao = orientacao,
                actionSuccess = showSuccess,
                errorMessageRes = null
            )
        }
    }
}

data class IdeiaDetalheUiState(
    val isLoading: Boolean = false,
    val ideia: Ideia? = null,
    val perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
    val orientacao: OrientacaoEstrategica? = null,
    val impacto: Float = 3f,
    val esforco: Float = 2f,
    val feedback: String = "",
    val errorMessageRes: Int? = null,
    val actionSuccess: Boolean = false,
    val jaVotou: Boolean = false
) {
    val scorePriorizacao: Int
        get() = impacto.toInt() - esforco.toInt()

    val canEvaluate: Boolean
        get() = perfil == PerfilUsuario.GESTOR

    val isPendingEvaluation: Boolean
        get() = ideia?.status == StatusIdeia.PENDENTE || ideia?.status == StatusIdeia.EM_ANALISE
}
