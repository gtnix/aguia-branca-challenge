package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
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
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NovaOrientacaoViewModel @Inject constructor(
    private val orientacaoRepository: OrientacaoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NovaOrientacaoUiState(divisao = sessionManager.getCurrentUser()?.divisao)
    )
    val uiState: StateFlow<NovaOrientacaoUiState> = _uiState.asStateFlow()

    fun onDivisaoChange(divisao: DivisaoNegocio?) {
        _uiState.update { it.copy(divisao = divisao) }
    }

    fun onTituloChange(titulo: String) {
        _uiState.update { it.copy(titulo = titulo, errorMessageRes = null) }
    }

    fun onDescricaoChange(descricao: String) {
        _uiState.update { it.copy(descricao = descricao, errorMessageRes = null) }
    }

    fun onCategoriaChange(categoria: CategoriaOrientacao) {
        _uiState.update { it.copy(categoria = categoria) }
    }

    fun onCampanhaChange(campanha: String) {
        _uiState.update { it.copy(campanha = campanha, errorMessageRes = null) }
    }

    fun onPrioridadeChange(prioridade: Int) {
        _uiState.update { it.copy(prioridade = prioridade) }
    }

    fun onCampanhaPatrocinadaChange(campanha: CampanhaFormState) {
        _uiState.update { it.copy(campanhaPatrocinada = campanha, errorMessageRes = null) }
    }

    fun salvar(onSuccess: () -> Unit) {
        val current = _uiState.value
        val user = sessionManager.getCurrentUser()
        
        if (user == null) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_user_not_logged) }
            return
        }
        
        if (user.perfil != PerfilUsuario.LIDER) {
            _uiState.update { it.copy(errorMessageRes = R.string.error_only_leader_create_orientacao) }
            return
        }
        
        if (current.titulo.isBlank() || current.descricao.isBlank() || current.campanha.isBlank()) {
            _uiState.update { it.copy(errorMessageRes = R.string.novo_projeto_erro_campos) }
            return
        }

        val patrocinada = try {
            current.campanhaPatrocinada.toDomain()
        } catch (e: IllegalArgumentException) {
            _uiState.update { it.copy(errorMessageRes = R.string.campanha_erro_campos) }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val novaOrientacao = OrientacaoEstrategica(
                    id = "",
                    titulo = current.titulo.trim(),
                    descricao = current.descricao.trim(),
                    categoria = current.categoria,
                    prioridade = current.prioridade,
                    ativa = true,
                    criadoPor = user.id,
                    campanha = current.campanha.trim(),
                    campanhaPatrocinada = patrocinada,
                    divisao = current.divisao
                )

                orientacaoRepository.salvar(novaOrientacao)

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = R.string.error_network_required)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = R.string.error_save_orientacao
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
    val campanha: String = "Águia 2026 — Inovação em Campo",
    val prioridade: Int = 3,
    val divisao: DivisaoNegocio? = null,
    val campanhaPatrocinada: CampanhaFormState = CampanhaFormState(),
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null
)
