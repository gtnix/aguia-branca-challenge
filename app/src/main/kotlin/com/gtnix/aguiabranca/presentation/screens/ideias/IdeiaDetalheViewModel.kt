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
import com.gtnix.aguiabranca.domain.usecase.ideia.GerarRascunhoFeedbackUseCase
import com.gtnix.aguiabranca.domain.usecase.ideia.SalvarEstimativaUseCase
import com.gtnix.aguiabranca.domain.usecase.ideia.ScoreIdeiasUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.isForbidden
import com.gtnix.aguiabranca.domain.util.isOffline
import com.gtnix.aguiabranca.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class IdeiaDetalheViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val orientacaoRepository: OrientacaoRepository,
    private val aprovarIdeiaUseCase: AprovarIdeiaUseCase,
    private val scoreIdeiasUseCase: ScoreIdeiasUseCase,
    private val salvarEstimativaUseCase: SalvarEstimativaUseCase,
    private val gerarRascunhoFeedbackUseCase: GerarRascunhoFeedbackUseCase,
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
                val user = sessionManager.getCurrentUser()
                val perfil = user?.perfil ?: PerfilUsuario.OPERADOR
                val orientacao = ideia?.orientacaoId?.let { orientacaoRepository.buscarPorId(it) }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ideia = ideia,
                        perfil = perfil,
                        currentUserId = user?.id,
                        orientacao = orientacao,
                        impacto = impactoInicial(ideia).toFloat(),
                        esforco = esforcoInicial(ideia).toFloat(),
                        errorMessageRes = if (ideia == null) R.string.error_load_idea else null
                    )
                }
            } catch (e: HttpException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        ideia = null,
                        errorMessageRes = if (e.code() == 403) {
                            R.string.error_ideia_access_denied
                        } else {
                            R.string.error_load_idea
                        }
                    )
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_network_required) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_load_idea) }
            }
        }
    }

    fun onImpactoChange(value: Float) {
        _uiState.update { it.copy(impacto = value, estimativaSalva = false) }
    }

    fun onEsforcoChange(value: Float) {
        _uiState.update { it.copy(esforco = value, estimativaSalva = false) }
    }

    fun salvarEstimativa() {
        val current = _uiState.value
        if (current.ideia == null || !current.canEvaluate || !current.isPendingEvaluation) return
        viewModelScope.launch {
            if (persistirEstimativa(current)) {
                refreshIdeia()
                _uiState.update { it.copy(estimativaSalva = true) }
            }
        }
    }

    /** Só chama a API quando o gestor mudou impacto/esforço em relação ao valor oficial. */
    private suspend fun persistirEstimativa(current: IdeiaDetalheUiState): Boolean {
        val ideia = current.ideia ?: return false
        val impacto = current.impacto.toInt()
        val esforco = current.esforco.toInt()
        if (impacto == ideia.impactoEstimado && esforco == ideia.esforcoEstimado) return true
        return when (
            val result = salvarEstimativaUseCase(
                ideiaId = ideia.id,
                impacto = impacto,
                esforco = esforco,
                motivoAjuste = null
            )
        ) {
            is Result.Success -> true
            is Result.Error -> {
                _uiState.update {
                    it.copy(errorMessageRes = mutationErrorRes(result, R.string.error_salvar_estimativa))
                }
                false
            }
            is Result.Loading -> false
        }
    }

    fun onFeedbackChange(text: String) {
        _uiState.update { it.copy(feedback = text, errorMessageRes = null) }
    }

    /** Toque no mesmo botão desmarca: a opinião sobre a IA é opcional. */
    fun onConcordaIaChange(concorda: Boolean) {
        _uiState.update {
            val novo = if (it.concordaIa == concorda) null else concorda
            it.copy(
                concordaIa = novo,
                motivoDiscordancia = if (novo == false) it.motivoDiscordancia else ""
            )
        }
    }

    fun onMotivoDiscordanciaChange(text: String) {
        _uiState.update { it.copy(motivoDiscordancia = text.take(MOTIVO_MAX)) }
    }

    fun gerarRascunhoFeedback() {
        val current = _uiState.value
        val ideia = current.ideia ?: return
        if (!current.canEvaluate || !current.isPendingEvaluation || current.rascunhoEmAndamento) return
        viewModelScope.launch {
            _uiState.update { it.copy(rascunhoEmAndamento = true, errorMessageRes = null) }
            when (
                val result = gerarRascunhoFeedbackUseCase(
                    ideiaId = ideia.id,
                    observacoes = current.feedback.takeIf { it.isNotBlank() }
                )
            ) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        feedback = result.data.texto,
                        rascunhoEmAndamento = false,
                        rascunhoFallback = result.data.fallback
                    )
                }
                is Result.Error -> _uiState.update {
                    it.copy(
                        rascunhoEmAndamento = false,
                        errorMessageRes = mutationErrorRes(result, R.string.error_rascunho_feedback)
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun iniciarAnalise() {
        pontuarComIa()
    }

    fun pontuarComIa() {
        val current = _uiState.value
        val ideia = current.ideia ?: return
        val orientacaoId = ideia.orientacaoId
        if (!current.canEvaluate || orientacaoId.isNullOrBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(scoringInProgress = true, errorMessageRes = null) }
            when (val result = scoreIdeiasUseCase(orientacaoId, listOf(ideia.id))) {
                is Result.Success -> {
                    refreshIdeia(scoreFallback = result.data.fallback)
                    _uiState.update {
                        it.copy(
                            impacto = impactoInicial(it.ideia).toFloat(),
                            esforco = esforcoInicial(it.ideia).toFloat()
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            scoringInProgress = false,
                            errorMessageRes = mutationErrorRes(result, R.string.error_score_ideas)
                        )
                    }
                }
                is Result.Loading -> Unit
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

    /** Só o autor responde, e uma única vez: o servidor credita o bônus de qualidade ao gestor. */
    fun marcarFeedbackUtil(util: Boolean) {
        val current = _uiState.value
        if (!current.canRateFeedback) return
        viewModelScope.launch {
            try {
                val atualizada = ideiaRepository.marcarFeedbackUtil(ideiaId, util)
                _uiState.update { it.copy(ideia = atualizada, feedbackUtilEnviado = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessageRes = R.string.error_feedback_util) }
            }
        }
    }

    fun aprovar() {
        avaliar(aprovada = true)
    }

    fun reprovar() {
        val current = _uiState.value
        if (!current.canEvaluate || !current.isPendingEvaluation) return
        if (current.feedback.isBlank()) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_feedback_required_reject) }
            return
        }
        avaliar(aprovada = false)
    }

    private fun avaliar(aprovada: Boolean) {
        val current = _uiState.value
        val ideia = current.ideia ?: return
        if (!current.canEvaluate || !current.isPendingEvaluation) return
        viewModelScope.launch {
            if (!persistirEstimativa(current)) return@launch
            when (
                val result = aprovarIdeiaUseCase(
                    ideiaId = ideia.id,
                    aprovada = aprovada,
                    feedback = current.feedback.ifBlank { null },
                    concordaIa = current.concordaIa.takeIf { current.temRecomendacaoIa },
                    motivoDiscordanciaIa = current.motivoDiscordancia.ifBlank { null }
                )
            ) {
                is Result.Success -> refreshIdeia(showSuccess = true)
                is Result.Error -> _uiState.update {
                    it.copy(
                        errorMessageRes = mutationErrorRes(
                            result,
                            if (aprovada) R.string.error_approve_idea else R.string.error_reject_idea
                        )
                    )
                }
                is Result.Loading -> Unit
            }
        }
    }

    fun excluir() {
        val current = _uiState.value
        if (!current.canDelete) return
        viewModelScope.launch {
            try {
                ideiaRepository.excluir(ideiaId)
                _uiState.update { it.copy(deleted = true, errorMessageRes = null) }
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessageRes = R.string.error_network_required) }
            } catch (e: HttpException) {
                _uiState.update {
                    it.copy(
                        errorMessageRes = when (e.code()) {
                            403 -> R.string.error_ideia_access_denied
                            400 -> R.string.ideia_excluir_somente_pendente
                            else -> R.string.error_delete_ideia
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessageRes = R.string.error_delete_ideia) }
            }
        }
    }

    private suspend fun refreshIdeia(showSuccess: Boolean = false, scoreFallback: Boolean = false) {
        val atualizada = ideiaRepository.buscarPorId(ideiaId)
        val orientacao = atualizada?.orientacaoId?.let { orientacaoRepository.buscarPorId(it) }
        _uiState.update {
            it.copy(
                ideia = atualizada,
                orientacao = orientacao,
                actionSuccess = showSuccess,
                scoringInProgress = false,
                scoreFallback = scoreFallback || (atualizada?.scoreIaFallback == true),
                errorMessageRes = null
            )
        }
    }

    private fun mutationErrorRes(result: Result.Error, fallback: Int): Int = when {
        result.isForbidden() -> R.string.error_ideia_access_denied
        result.isOffline() -> R.string.error_network_required
        else -> fallback
    }

    companion object {
        const val MOTIVO_MAX = 300

        /** Valor oficial do gestor tem precedência; senão parte da sugestão da IA. */
        internal fun impactoInicial(ideia: Ideia?): Int =
            ideia?.impactoEstimado?.takeIf { it in 1..5 }
                ?: ideia?.analiseIa?.impactoSugerido?.takeIf { it in 1..5 }
                ?: 3

        internal fun esforcoInicial(ideia: Ideia?): Int =
            ideia?.esforcoEstimado?.takeIf { it in 1..5 }
                ?: ideia?.analiseIa?.esforcoSugerido?.takeIf { it in 1..5 }
                ?: 3
    }
}

data class IdeiaDetalheUiState(
    val isLoading: Boolean = false,
    val ideia: Ideia? = null,
    val perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
    val currentUserId: String? = null,
    val orientacao: OrientacaoEstrategica? = null,
    val impacto: Float = 3f,
    val esforco: Float = 3f,
    val estimativaSalva: Boolean = false,
    val feedback: String = "",
    val concordaIa: Boolean? = null,
    val motivoDiscordancia: String = "",
    val rascunhoEmAndamento: Boolean = false,
    val rascunhoFallback: Boolean = false,
    val errorMessageRes: Int? = null,
    val actionSuccess: Boolean = false,
    val scoringInProgress: Boolean = false,
    val scoreFallback: Boolean = false,
    val jaVotou: Boolean = false,
    val feedbackUtilEnviado: Boolean = false,
    val deleted: Boolean = false
) {
    val canSupport: Boolean
        get() = (perfil == PerfilUsuario.GESTOR || perfil == PerfilUsuario.LIDER)
            && ideia != null
            && ideia.autorId != currentUserId

    val isAuthor: Boolean
        get() = perfil == PerfilUsuario.OPERADOR && ideia != null && ideia.autorId == currentUserId

    val canRateFeedback: Boolean
        get() = isAuthor && !ideia?.feedback.isNullOrBlank() && ideia?.feedbackUtil == null

    val canEvaluate: Boolean
        get() = perfil == PerfilUsuario.GESTOR

    val canViewIa: Boolean
        get() = perfil == PerfilUsuario.GESTOR || perfil == PerfilUsuario.LIDER

    val canDelete: Boolean
        get() = perfil == PerfilUsuario.OPERADOR
            && ideia != null
            && ideia.autorId == currentUserId
            && ideia.status == StatusIdeia.PENDENTE

    val isPendingEvaluation: Boolean
        get() = ideia?.status == StatusIdeia.PENDENTE || ideia?.status == StatusIdeia.EM_ANALISE

    /** Só faz sentido perguntar se concorda quando existe um score para concordar. */
    val temRecomendacaoIa: Boolean
        get() = ideia?.scoreIa != null

    val impactoSugeridoIa: Int?
        get() = ideia?.analiseIa?.impactoSugerido

    val esforcoSugeridoIa: Int?
        get() = ideia?.analiseIa?.esforcoSugerido
}
