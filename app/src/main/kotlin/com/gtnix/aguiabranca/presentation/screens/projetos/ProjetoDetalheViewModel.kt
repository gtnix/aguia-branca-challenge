package com.gtnix.aguiabranca.presentation.screens.projetos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.presentation.components.AvatarData
import com.gtnix.aguiabranca.presentation.components.MarcoTimeline
import com.gtnix.aguiabranca.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjetoDetalheViewModel @Inject constructor(
    private val projetoRepository: ProjetoRepository,
    private val ideiaRepository: IdeiaRepository,
    private val usuarioRepository: UsuarioRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjetoDetalheUiState())
    val uiState: StateFlow<ProjetoDetalheUiState> = _uiState.asStateFlow()

    private val projetoId: String = savedStateHandle[Destination.ProjetoDetalhe.ARG_PROJETO_ID] ?: ""

    init {
        carregarProjeto()
    }

    private fun carregarProjeto() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val projeto = projetoRepository.buscarPorId(projetoId)
                val perfil = sessionManager.getCurrentUser()?.perfil ?: PerfilUsuario.OPERADOR
                val ideiasVinculadas = projeto?.let { carregarIdeiasVinculadas(it) } ?: emptyList()
                val membrosEquipe = projeto?.let { carregarMembrosEquipe(it) } ?: emptyList()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        projeto = projeto,
                        perfil = perfil,
                        progressoSlider = projeto?.progresso?.toFloat() ?: 0f,
                        ideiasVinculadas = ideiasVinculadas,
                        membrosEquipe = membrosEquipe
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar projeto") }
            }
        }
    }

    fun onProgressoChange(value: Float) {
        _uiState.update { it.copy(progressoSlider = value) }
    }

    fun salvarProgresso() {
        val projeto = _uiState.value.projeto ?: return
        viewModelScope.launch {
            try {
                val novoProgresso = _uiState.value.progressoSlider.toInt()
                projetoRepository.atualizarProgresso(projeto.id, novoProgresso)
                val atualizado = projeto.copy(progresso = novoProgresso)
                _uiState.update { it.copy(projeto = atualizado, actionSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao salvar progresso") }
            }
        }
    }

    fun concluirProjeto() {
        val projeto = _uiState.value.projeto ?: return
        viewModelScope.launch {
            try {
                val concluido = projeto.copy(
                    status = StatusProjeto.CONCLUIDO,
                    progresso = 100,
                    dataConclusao = System.currentTimeMillis()
                )
                projetoRepository.salvar(concluido)
                _uiState.update { it.copy(projeto = concluido, progressoSlider = 100f, actionSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao concluir projeto") }
            }
        }
    }

    fun atualizarStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(actionSuccess = true) }
            delay(2000)
            _uiState.update { it.copy(actionSuccess = false) }
        }
    }

    private suspend fun carregarIdeiasVinculadas(projeto: Projeto): List<IdeiaVinculadaSimple> {
        val ideiaId = projeto.ideiaOrigemId ?: return emptyList()
        val ideia = ideiaRepository.buscarPorId(ideiaId) ?: return emptyList()
        return listOf(
            IdeiaVinculadaSimple(
                id = ideia.id,
                titulo = ideia.titulo,
                descricao = ideia.descricao,
                upvotes = ideia.upvotes
            )
        )
    }

    private suspend fun carregarMembrosEquipe(projeto: Projeto): List<AvatarData> {
        val membros = mutableListOf(AvatarData(extrairIniciais(projeto.responsavelNome)))
        projeto.membrosIds
            .filter { it != projeto.responsavelId }
            .forEach { membroId ->
                usuarioRepository.buscarPorId(membroId)?.let { usuario ->
                    membros.add(AvatarData(extrairIniciais(usuario.nome)))
                }
            }
        return membros
    }

    private fun extrairIniciais(nome: String): String =
        nome.split(" ")
            .take(2)
            .mapNotNull { it.firstOrNull()?.uppercase() }
            .joinToString("")
}

data class IdeiaVinculadaSimple(
    val id: String,
    val titulo: String,
    val descricao: String,
    val upvotes: Int
)

data class ProjetoDetalheUiState(
    val isLoading: Boolean = false,
    val projeto: Projeto? = null,
    val perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
    val progressoSlider: Float = 0f,
    val errorMessage: String? = null,
    val actionSuccess: Boolean = false,
    val marcos: List<MarcoTimeline> = emptyList(),
    val ideiasVinculadas: List<IdeiaVinculadaSimple> = emptyList(),
    val membrosEquipe: List<AvatarData> = emptyList()
)
