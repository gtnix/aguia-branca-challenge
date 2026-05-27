package com.gtnix.aguiabranca.presentation.screens.projetos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class FiltroProjeto {
    TODOS,
    PLANEJADO,
    EM_ANDAMENTO,
    PAUSADO,
    CONCLUIDO
}

@HiltViewModel
class ProjetosViewModel @Inject constructor(
    projetoRepository: ProjetoRepository,
    sessionManager: SessionManager
) : ViewModel() {

    private val _filtroSelecionado = MutableStateFlow(FiltroProjeto.TODOS)
    val filtroSelecionado: StateFlow<FiltroProjeto> = _filtroSelecionado.asStateFlow()

    val uiState: StateFlow<ProjetosUiState> = combine(
        projetoRepository.listarTodos(),
        sessionManager.currentUserFlow,
        _filtroSelecionado
    ) { projetos, user, filtro ->
        if (user == null) {
            ProjetosUiState(
                isLoading = false,
                errorMessageRes = R.string.error_user_not_logged
            )
        } else {
            val projetosPorPerfil = when (user.perfil) {
                PerfilUsuario.GESTOR -> projetos.filter {
                    it.area == user.area || it.responsavelId == user.id
                }
                else -> projetos
            }
            val projetosFiltrados = when (filtro) {
                FiltroProjeto.TODOS -> projetosPorPerfil
                FiltroProjeto.PLANEJADO -> projetosPorPerfil.filter { it.status == StatusProjeto.PLANEJADO }
                FiltroProjeto.EM_ANDAMENTO -> projetosPorPerfil.filter { it.status == StatusProjeto.EM_ANDAMENTO }
                FiltroProjeto.PAUSADO -> projetosPorPerfil.filter { it.status == StatusProjeto.PAUSADO }
                FiltroProjeto.CONCLUIDO -> projetosPorPerfil.filter { it.status == StatusProjeto.CONCLUIDO }
            }
            ProjetosUiState(isLoading = false, projetos = projetosFiltrados)
        }
    }
        .catch {
            emit(ProjetosUiState(isLoading = false, errorMessageRes = R.string.error_load_projetos))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProjetosUiState(isLoading = true)
        )

    fun selecionarFiltro(filtro: FiltroProjeto) {
        _filtroSelecionado.value = filtro
    }
}

data class ProjetosUiState(
    val isLoading: Boolean = false,
    val projetos: List<Projeto> = emptyList(),
    val errorMessageRes: Int? = null
)
