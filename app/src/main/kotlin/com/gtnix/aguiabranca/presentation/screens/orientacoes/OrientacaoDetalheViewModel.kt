package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrientacaoDetalheViewModel @Inject constructor(
    private val orientacaoRepository: OrientacaoRepository,
    private val ideiaRepository: IdeiaRepository,
    private val projetoRepository: ProjetoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrientacaoDetalheUiState())
    val uiState: StateFlow<OrientacaoDetalheUiState> = _uiState.asStateFlow()

    private val orientacaoId: String =
        savedStateHandle[Destination.OrientacaoDetalhe.ARG_ORIENTACAO_ID] ?: ""

    init {
        carregarOrientacao()
    }

    private fun carregarOrientacao() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val orientacao = orientacaoRepository.buscarPorId(orientacaoId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        orientacao = orientacao,
                        errorMessageRes = if (orientacao == null) R.string.orientacao_detalhe_not_found else null
                    )
                }
                if (orientacao != null) {
                    observarIdeiasAlinhadas()
                    observarProjetosVinculados()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = R.string.error_load_orientacao)
                }
            }
        }
    }

    private fun observarIdeiasAlinhadas() {
        viewModelScope.launch {
            ideiaRepository.listarPorOrientacao(orientacaoId).collect { ideias ->
                _uiState.update { it.copy(ideiasAlinhadas = ideias) }
            }
        }
    }

    private fun observarProjetosVinculados() {
        viewModelScope.launch {
            projetoRepository.listarPorOrientacao(orientacaoId).collect { projetos ->
                _uiState.update { it.copy(projetosVinculados = projetos) }
            }
        }
    }
}

data class OrientacaoDetalheUiState(
    val isLoading: Boolean = false,
    val orientacao: OrientacaoEstrategica? = null,
    val ideiasAlinhadas: List<Ideia> = emptyList(),
    val projetosVinculados: List<Projeto> = emptyList(),
    val errorMessageRes: Int? = null
) {
    val projetosEmAndamento: Int
        get() = projetosVinculados.count {
            it.status == StatusProjeto.EM_ANDAMENTO || it.status == StatusProjeto.PLANEJADO
        }
}
