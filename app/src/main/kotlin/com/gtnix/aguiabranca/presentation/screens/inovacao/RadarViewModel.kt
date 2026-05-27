package com.gtnix.aguiabranca.presentation.screens.inovacao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.StartupPartner
import com.gtnix.aguiabranca.domain.repository.InovacaoAbertaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadarViewModel @Inject constructor(
    private val repository: InovacaoAbertaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RadarUiState())
    val uiState: StateFlow<RadarUiState> = _uiState.asStateFlow()

    init {
        carregarStartups()
    }

    private fun carregarStartups() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val startups = repository.buscarStartupsRecomendadas()
                _uiState.update { it.copy(isLoading = false, startups = startups) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_radar_startups) }
            }
        }
    }
}

data class RadarUiState(
    val isLoading: Boolean = false,
    val startups: List<StartupPartner> = emptyList(),
    val errorMessageRes: Int? = null
)
