package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.usecase.ideia.AprovarIdeiaUseCase
import com.gtnix.aguiabranca.domain.usecase.ideia.GetIdeiasUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.presentation.util.UiState
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

@HiltViewModel
class IdeiasViewModel @Inject constructor(
    private val getIdeiasUseCase: GetIdeiasUseCase,
    private val aprovarIdeiaUseCase: AprovarIdeiaUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Ideia>>>(UiState.Initial)
    val uiState: StateFlow<UiState<List<Ideia>>> = _uiState.asStateFlow()
    
    private val _filtroSelecionado = MutableStateFlow(FiltroIdeia.TODAS)
    val filtroSelecionado: StateFlow<FiltroIdeia> = _filtroSelecionado.asStateFlow()

    private val _actionMessageRes = MutableStateFlow<Int?>(null)
    val actionMessageRes: StateFlow<Int?> = _actionMessageRes.asStateFlow()
    
    val ideiasFiltradas: StateFlow<UiState<List<Ideia>>> = combine(
        _uiState,
        _filtroSelecionado
    ) { state, filtro ->
        when (state) {
            is UiState.Success -> {
                val filtered = when (filtro) {
                    FiltroIdeia.TODAS -> state.data
                    FiltroIdeia.PENDENTES -> state.data.filter { 
                        it.status == StatusIdeia.PENDENTE || it.status == StatusIdeia.EM_ANALISE 
                    }
                    FiltroIdeia.APROVADAS -> state.data.filter { 
                        it.status == StatusIdeia.APROVADA 
                    }
                    FiltroIdeia.EM_PROJETO -> state.data.filter { 
                        it.status == StatusIdeia.CONVERTIDA_PROJETO 
                    }
                }
                UiState.Success(filtered)
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
        get() {
            val perfil = currentUser?.perfil ?: return false
            return perfil == PerfilUsuario.GESTOR || perfil == PerfilUsuario.LIDER
        }

    init {
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
    
    fun selecionarFiltro(filtro: FiltroIdeia) {
        _filtroSelecionado.value = filtro
    }

    fun clearActionMessage() {
        _actionMessageRes.value = null
    }
    
    fun aprovarIdeia(ideia: Ideia) {
        viewModelScope.launch {
            when (val result = aprovarIdeiaUseCase(ideiaId = ideia.id, aprovada = true)) {
                is Result.Success -> _actionMessageRes.value = R.string.ideia_detalhe_acao_sucesso
                is Result.Error -> _actionMessageRes.value = R.string.error_approve_idea
                is Result.Loading -> Unit
            }
        }
    }
    
    fun reprovarIdeia(ideia: Ideia, feedback: String) {
        viewModelScope.launch {
            when (
                val result = aprovarIdeiaUseCase(
                    ideiaId = ideia.id,
                    aprovada = false,
                    feedback = feedback
                )
            ) {
                is Result.Success -> _actionMessageRes.value = R.string.ideia_detalhe_acao_sucesso
                is Result.Error -> _actionMessageRes.value = R.string.error_reject_idea
                is Result.Loading -> Unit
            }
        }
    }
}
