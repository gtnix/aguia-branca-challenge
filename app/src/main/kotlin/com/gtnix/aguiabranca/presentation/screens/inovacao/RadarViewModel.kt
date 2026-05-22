package com.gtnix.aguiabranca.presentation.screens.inovacao

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.StartupPartner
import com.gtnix.aguiabranca.domain.repository.InovacaoAbertaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadarViewModel @Inject constructor(
    private val repository: InovacaoAbertaRepository
) : ViewModel() {

    var uiState by mutableStateOf(RadarUiState())
        private set

    init {
        carregarStartups()
    }

    private fun carregarStartups() {
        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val startups = repository.buscarStartupsRecomendadas()
                uiState = uiState.copy(
                    isLoading = false,
                    startups = startups
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erro ao buscar startups parceiras"
                )
            }
        }
    }
}

data class RadarUiState(
    val isLoading: Boolean = false,
    val startups: List<StartupPartner> = emptyList(),
    val errorMessage: String? = null
)
