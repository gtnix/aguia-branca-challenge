package com.gtnix.aguiabranca.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * HomeViewModel - Estado e Lógica da Tela Principal
 *
 * ## Conceito FIAP - Material 10A (MVVM com Flow)
 *
 * Este ViewModel combina múltiplos Flows de dados para
 * construir o estado consolidado do dashboard.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val projetoRepository: ProjetoRepository,
    private val orientacaoRepository: OrientacaoRepository
) : ViewModel() {

    var uiState by mutableStateOf(HomeUiState())
        private set

    /**
     * Carrega dados do dashboard baseado no perfil do usuário.
     */
    fun carregarDados(perfilString: String) {
        val perfil = try {
            PerfilUsuario.valueOf(perfilString)
        } catch (e: Exception) {
            PerfilUsuario.OPERADOR
        }

        uiState = uiState.copy(
            isLoading = true,
            perfil = perfil
        )

        viewModelScope.launch {
            try {
                // Combina múltiplos Flows
                combine(
                    orientacaoRepository.listarAtivas(),
                    ideiaRepository.listarTodas(),
                    projetoRepository.listarTodos()
                ) { orientacoes, ideias, projetos ->
                    Triple(orientacoes, ideias, projetos)
                }.collect { (orientacoes, ideias, projetos) ->
                    uiState = uiState.copy(
                        isLoading = false,
                        orientacoes = orientacoes.take(3),
                        minhasIdeias = ideias.take(5),
                        projetosEmAndamento = projetos.filter { 
                            it.status.name == "EM_ANDAMENTO" 
                        }.take(5),
                        totalIdeias = ideias.size,
                        totalProjetos = projetos.size,
                        ideiasAprovadas = ideias.count { 
                            it.status.name == "APROVADA" || it.status.name == "CONVERTIDA_PROJETO"
                        }
                    )
                }
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar dados"
                )
            }
        }
    }
}

/**
 * Estado da tela Home/Dashboard.
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
    val orientacoes: List<OrientacaoEstrategica> = emptyList(),
    val minhasIdeias: List<Ideia> = emptyList(),
    val projetosEmAndamento: List<Projeto> = emptyList(),
    val totalIdeias: Int = 0,
    val totalProjetos: Int = 0,
    val ideiasAprovadas: Int = 0,
    val errorMessage: String? = null
) {
    val nomeUsuario: String
        get() = when (perfil) {
            PerfilUsuario.OPERADOR -> "Operador"
            PerfilUsuario.GESTOR -> "Gestor"
            PerfilUsuario.LIDER -> "Líder"
        }
}
