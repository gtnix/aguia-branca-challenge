package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IdeiasViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository
) : ViewModel() {

    var uiState by mutableStateOf(IdeiasUiState())
        private set

    init {
        carregarIdeias()
    }

    private fun carregarIdeias() {
        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                ideiaRepository.listarTodas().collect { ideias ->
                    uiState = uiState.copy(
                        isLoading = false,
                        ideias = ideias
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar ideias"
                )
            }
        }
    }
}

data class IdeiasUiState(
    val isLoading: Boolean = false,
    val ideias: List<Ideia> = emptyList(),
    val errorMessage: String? = null
)
