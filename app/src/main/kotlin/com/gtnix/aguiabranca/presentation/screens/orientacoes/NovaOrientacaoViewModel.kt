package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NovaOrientacaoViewModel @Inject constructor(
    private val orientacaoRepository: OrientacaoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NovaOrientacaoUiState())
    val uiState: StateFlow<NovaOrientacaoUiState> = _uiState.asStateFlow()

    fun onTituloChange(titulo: String) {
        _uiState.update { it.copy(titulo = titulo, errorMessage = null) }
    }

    fun onDescricaoChange(descricao: String) {
        _uiState.update { it.copy(descricao = descricao, errorMessage = null) }
    }

    fun onCategoriaChange(categoria: CategoriaOrientacao) {
        _uiState.update { it.copy(categoria = categoria) }
    }

    fun onPrioridadeChange(prioridade: Int) {
        _uiState.update { it.copy(prioridade = prioridade) }
    }

    fun salvar(onSuccess: () -> Unit) {
        val current = _uiState.value
        val user = sessionManager.getCurrentUser()
        
        if (user == null) {
            _uiState.update { it.copy(errorMessage = "Usuário não logado") }
            return
        }
        
        if (user.perfil != PerfilUsuario.LIDER) {
            _uiState.update { it.copy(errorMessage = "Apenas líderes podem criar orientações") }
            return
        }
        
        if (current.titulo.isBlank() || current.descricao.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos obrigatórios") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val novaOrientacao = OrientacaoEstrategica(
                    id = UUID.randomUUID().toString(),
                    titulo = current.titulo.trim(),
                    descricao = current.descricao.trim(),
                    categoria = current.categoria,
                    prioridade = current.prioridade,
                    ativa = true,
                    criadoPor = user.id
                )

                orientacaoRepository.salvar(novaOrientacao)

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        errorMessage = "Erro ao salvar orientação"
                    ) 
                }
            }
        }
    }
}

data class NovaOrientacaoUiState(
    val titulo: String = "",
    val descricao: String = "",
    val categoria: CategoriaOrientacao = CategoriaOrientacao.EFICIENCIA_OPERACIONAL,
    val prioridade: Int = 3,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
