package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdeiaDetalheViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val userSession: UserSession,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(IdeiaDetalheUiState())
        private set

    private val ideiaId: String = savedStateHandle[Destination.IdeiaDetalhe.ARG_IDEIA_ID] ?: ""

    init {
        carregarIdeia()
    }

    private fun carregarIdeia() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val ideia = ideiaRepository.buscarPorId(ideiaId)
                uiState = uiState.copy(
                    isLoading = false,
                    ideia = ideia,
                    perfil = userSession.perfil,
                    impacto = ideia?.impactoEstimado?.toFloat() ?: 1f,
                    esforco = ideia?.esforcoEstimado?.toFloat() ?: 1f
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar ideia"
                )
            }
        }
    }

    fun onImpactoChange(value: Float) {
        uiState = uiState.copy(impacto = value)
    }

    fun onEsforcoChange(value: Float) {
        uiState = uiState.copy(esforco = value)
    }

    fun onFeedbackChange(text: String) {
        uiState = uiState.copy(feedback = text)
    }

    fun iniciarAnalise() {
        val ideia = uiState.ideia ?: return
        viewModelScope.launch {
            try {
                val atualizada = ideia.copy(
                    status = StatusIdeia.EM_ANALISE,
                    dataAvaliacao = System.currentTimeMillis()
                )
                ideiaRepository.salvar(atualizada)
                uiState = uiState.copy(ideia = atualizada)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Erro ao iniciar análise")
            }
        }
    }

    fun aprovar() {
        val ideia = uiState.ideia ?: return
        viewModelScope.launch {
            try {
                val atualizada = ideia.copy(
                    status = StatusIdeia.APROVADA,
                    feedback = uiState.feedback.ifBlank { null },
                    impactoEstimado = uiState.impacto.toInt(),
                    esforcoEstimado = uiState.esforco.toInt(),
                    dataAvaliacao = System.currentTimeMillis()
                )
                ideiaRepository.salvar(atualizada)
                uiState = uiState.copy(ideia = atualizada, actionSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Erro ao aprovar ideia")
            }
        }
    }

    fun reprovar() {
        val ideia = uiState.ideia ?: return
        if (uiState.feedback.isBlank()) {
            uiState = uiState.copy(errorMessage = "Feedback é obrigatório para reprovar")
            return
        }
        viewModelScope.launch {
            try {
                val atualizada = ideia.copy(
                    status = StatusIdeia.REPROVADA,
                    feedback = uiState.feedback,
                    impactoEstimado = uiState.impacto.toInt(),
                    esforcoEstimado = uiState.esforco.toInt(),
                    dataAvaliacao = System.currentTimeMillis()
                )
                ideiaRepository.salvar(atualizada)
                uiState = uiState.copy(ideia = atualizada, actionSuccess = true)
            } catch (e: Exception) {
                uiState = uiState.copy(errorMessage = "Erro ao reprovar ideia")
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
