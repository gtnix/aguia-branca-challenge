package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NovaIdeiaViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val orientacaoRepository: OrientacaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NovaIdeiaUiState())
    val uiState: StateFlow<NovaIdeiaUiState> = _uiState.asStateFlow()

    init {
        carregarOrientacoes()
    }

    private fun carregarOrientacoes() {
        viewModelScope.launch {
            try {
                orientacaoRepository.listarAtivas().collect { orientacoes ->
                    _uiState.update { it.copy(orientacoes = orientacoes) }
                }
            } catch (_: Exception) { }
        }
    }

    fun onTituloChange(titulo: String) {
        _uiState.update { it.copy(titulo = titulo, errorMessage = null) }
    }

    fun onDescricaoChange(descricao: String) {
        _uiState.update { it.copy(descricao = descricao, errorMessage = null) }
    }

    fun onAreaChange(area: AreaAtuacao) {
        _uiState.update { it.copy(area = area) }
    }

    fun onTipoChange(tipo: TipoIdeia) {
        _uiState.update { it.copy(tipo = tipo) }
    }

    fun salvar(onSuccess: () -> Unit) {
        val current = _uiState.value
        if (current.titulo.isBlank() || current.descricao.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val novaIdeia = Ideia(
                    id = UUID.randomUUID().toString(),
                    titulo = current.titulo.trim(),
                    descricao = current.descricao.trim(),
                    tipo = current.tipo,
                    area = current.area,
                    autorId = "demo-user",
                    autorNome = "Usuário Demo"
                )

                ideiaRepository.salvar(novaIdeia)

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao salvar ideia") }
            }
        }
    }
}

data class NovaIdeiaUiState(
    val titulo: String = "",
    val descricao: String = "",
    val tipo: TipoIdeia = TipoIdeia.IDEIA,
    val area: AreaAtuacao = AreaAtuacao.OPERACOES,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val orientacoes: List<OrientacaoEstrategica> = emptyList()
)
