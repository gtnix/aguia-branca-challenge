package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.usecase.ideia.GetIdeiasUseCase
import com.gtnix.aguiabranca.domain.usecase.ideia.ScoreIdeiasUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.isForbidden
import com.gtnix.aguiabranca.presentation.util.SeloIa
import com.gtnix.aguiabranca.presentation.util.UiState
import com.gtnix.aguiabranca.presentation.util.seloIa
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FiltroIdeia {
    TODAS,
    PENDENTES,
    APROVADAS,
    EM_PROJETO
}

enum class OrdenacaoIdeias {
    /** Ordem de chegada (mais antigas primeiro): a fila não depende da IA. */
    FILA,
    /** Opt-in do gestor: maior score da IA primeiro. */
    RECOMENDACAO_IA
}

@HiltViewModel
class IdeiasViewModel @Inject constructor(
    private val getIdeiasUseCase: GetIdeiasUseCase,
    private val scoreIdeiasUseCase: ScoreIdeiasUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Ideia>>>(UiState.Initial)
    val uiState: StateFlow<UiState<List<Ideia>>> = _uiState.asStateFlow()
    
    private val _filtroSelecionado = MutableStateFlow(FiltroIdeia.TODAS)
    val filtroSelecionado: StateFlow<FiltroIdeia> = _filtroSelecionado.asStateFlow()

    private val _ordenacao = MutableStateFlow(OrdenacaoIdeias.FILA)
    val ordenacao: StateFlow<OrdenacaoIdeias> = _ordenacao.asStateFlow()

    private val _actionMessageRes = MutableStateFlow<Int?>(null)
    val actionMessageRes: StateFlow<Int?> = _actionMessageRes.asStateFlow()

    private val _scoringInProgress = MutableStateFlow(false)
    val scoringInProgress: StateFlow<Boolean> = _scoringInProgress.asStateFlow()
    
    val ideiasFiltradas: StateFlow<UiState<List<Ideia>>> = combine(
        _uiState,
        _filtroSelecionado,
        _ordenacao
    ) { state, filtro, ordenacao ->
        when (state) {
            is UiState.Success -> {
                val filtered = when (filtro) {
                    FiltroIdeia.TODAS -> state.data
                    FiltroIdeia.PENDENTES -> state.data.filter { 
                        it.status == StatusIdeia.PENDENTE || it.status == StatusIdeia.EM_ANALISE 
                    }
                    FiltroIdeia.APROVADAS -> state.data.filter { it.isAprovadaOuConvertida() }
                    FiltroIdeia.EM_PROJETO -> state.data.filter { 
                        it.status == StatusIdeia.CONVERTIDA_PROJETO 
                    }
                }
                UiState.Success(ordenar(filtered, ordenacao))
            }
            else -> state
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Initial
    )
    
    val currentUser: Usuario?
        get() = sessionManager.getCurrentUser()

    val canEvaluateIdeias: Boolean
        get() = currentUser?.perfil == PerfilUsuario.GESTOR

    val canCreateIdeias: Boolean
        get() = currentUser?.perfil == PerfilUsuario.OPERADOR

    val listTitleRes: Int
        get() = when (currentUser?.perfil) {
            PerfilUsuario.GESTOR -> R.string.gestor_queue_title
            PerfilUsuario.LIDER -> R.string.ideias_readonly_title
            else -> R.string.my_ideas_title
        }

    init {
        if (currentUser?.perfil == PerfilUsuario.GESTOR) {
            _filtroSelecionado.value = FiltroIdeia.PENDENTES
        }
        loadIdeias()
    }

    private fun loadIdeias() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getIdeiasUseCase().collect { result ->
                _uiState.value = when (result) {
                    is Result.Success -> UiState.Success(result.data)
                    is Result.Error -> UiState.Error(result.message.orEmpty())
                    is Result.Loading -> UiState.Loading
                }
            }
        }
    }

    fun seloDe(ideia: Ideia): SeloIa? = seloIa(ideia, currentUser?.perfil)
    
    fun selecionarFiltro(filtro: FiltroIdeia) {
        _filtroSelecionado.value = filtro
    }

    fun selecionarOrdenacao(nova: OrdenacaoIdeias) {
        if (!canEvaluateIdeias || nova == _ordenacao.value) return
        _ordenacao.value = nova
    }

    fun alternarOrdenacao() {
        selecionarOrdenacao(
            if (_ordenacao.value == OrdenacaoIdeias.FILA) {
                OrdenacaoIdeias.RECOMENDACAO_IA
            } else {
                OrdenacaoIdeias.FILA
            }
        )
    }

    fun clearActionMessage() {
        _actionMessageRes.value = null
    }

    fun pontuarComIa() {
        if (!canEvaluateIdeias || _scoringInProgress.value) return
        viewModelScope.launch {
            val pendentes = (_uiState.value as? UiState.Success)?.data.orEmpty()
                .filter { it.status == StatusIdeia.PENDENTE || it.status == StatusIdeia.EM_ANALISE }
                .filter { !it.orientacaoId.isNullOrBlank() }
            if (pendentes.isEmpty()) {
                _actionMessageRes.value = R.string.ideias_score_vazio
                return@launch
            }
            _scoringInProgress.value = true
            var fallback = false
            var semDados = false
            var error = false
            var forbidden = false
            pendentes.groupBy { it.orientacaoId.orEmpty() }.forEach { (orientacaoId, grupo) ->
                when (
                    val result = scoreIdeiasUseCase(
                        orientacaoId = orientacaoId,
                        ideiaIds = grupo.map { it.id }
                    )
                ) {
                    is Result.Success -> {
                        if (result.data.fallback) fallback = true
                        if (result.data.semDados) semDados = true
                    }
                    is Result.Error -> if (result.isForbidden()) forbidden = true else error = true
                    is Result.Loading -> Unit
                }
            }
            _scoringInProgress.value = false
            _actionMessageRes.value = when {
                forbidden -> R.string.error_ideia_access_denied
                error -> R.string.error_score_ideas
                semDados -> R.string.ideias_score_dados_insuficientes
                fallback -> R.string.ideias_score_fallback
                else -> R.string.ideias_score_sucesso
            }
        }
    }

    private fun ordenar(ideias: List<Ideia>, ordenacao: OrdenacaoIdeias): List<Ideia> = when {
        !canEvaluateIdeias -> ideias
        ordenacao == OrdenacaoIdeias.RECOMENDACAO_IA -> ideias.sortedWith(
            compareByDescending<Ideia> { it.scoreIa ?: Int.MIN_VALUE }
                .thenBy { it.dataCriacao }
        )
        else -> ideias.sortedBy { it.dataCriacao }
    }
}
