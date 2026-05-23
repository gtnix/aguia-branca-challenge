package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.usecase.ideia.GetIdeiasUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.presentation.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdeiasViewModel @Inject constructor(
    private val getIdeiasUseCase: GetIdeiasUseCase,
    private val ideiaRepository: IdeiaRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Ideia>>>(UiState.Initial)
    val uiState: StateFlow<UiState<List<Ideia>>> = _uiState.asStateFlow()
    
    val currentUser: Usuario?
        get() = sessionManager.getCurrentUser()

    init {
        loadIdeias()
    }

    private fun loadIdeias() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            getIdeiasUseCase().collect { result ->
                _uiState.value = when (result) {
                    is Result.Success -> UiState.Success(result.data)
                    is Result.Error -> UiState.Error(result.message ?: "Erro desconhecido")
                    is Result.Loading -> UiState.Loading
                }
            }
        }
    }
    
    fun aprovarIdeia(ideia: Ideia) {
        viewModelScope.launch {
            ideiaRepository.atualizarStatus(
                id = ideia.id,
                novoStatus = StatusIdeia.APROVADA,
                feedback = null
            )
        }
    }
    
    fun reprovarIdeia(ideia: Ideia) {
        viewModelScope.launch {
            ideiaRepository.atualizarStatus(
                id = ideia.id,
                novoStatus = StatusIdeia.REPROVADA,
                feedback = "Reprovada via ação rápida"
            )
        }
    }
}
