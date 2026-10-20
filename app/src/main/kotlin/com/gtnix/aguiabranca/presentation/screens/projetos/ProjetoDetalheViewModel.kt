package com.gtnix.aguiabranca.presentation.screens.projetos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
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
                        membrosEquipe = membrosEquipe,
                        errorMessage = if (projeto == null) "Projeto não encontrado" else null
                    )
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Sem conexão. Esta ação exige rede na Sprint 2.") }
            } catch (e: HttpException) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = if (e.code() == 403) {
                            "Você não tem permissão para esta ação."
                        } else {
                            "Erro ao carregar projeto"
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar projeto") }
            }
        }
    }

    fun onProgressoChange(value: Float) {
        _uiState.update { it.copy(progressoSlider = value, progressSavedHint = false) }
    }

    fun onProgressoChangeFinished() {
        val projeto = _uiState.value.projeto ?: return
        if (_uiState.value.perfil != PerfilUsuario.GESTOR) return
        val novoProgresso = _uiState.value.progressoSlider.toInt().coerceIn(0, 100)
        if (novoProgresso == projeto.progresso) return
        salvarProgresso(projeto, novoProgresso)
    }

    fun consumirSucesso() {
        _uiState.update { it.copy(actionSuccess = false, successMessageRes = null) }
    }

    private fun salvarProgresso(projeto: Projeto, novoProgresso: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSavingProgress = true,
                    actionSuccess = false,
                    successMessageRes = null,
                    errorMessage = null
                )
            }
            try {
                projetoRepository.atualizarProgresso(projeto.id, novoProgresso)
                val atualizado = projeto.copy(progresso = novoProgresso)
                _uiState.update {
                    it.copy(
                        projeto = atualizado,
                        progressoSlider = novoProgresso.toFloat(),
                        isSavingProgress = false,
                        progressSavedHint = true,
                        actionSuccess = true,
                        successMessageRes = R.string.projeto_detalhe_andamento_salvo,
                        errorMessage = null
                    )
                }
            } catch (e: IOException) {
                _uiState.update {
                    it.copy(
                        isSavingProgress = false,
                        errorMessage = "Sem conexão. Esta ação exige rede na Sprint 2."
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSavingProgress = false, errorMessage = "Erro ao salvar progresso")
                }
            }
        }
    }

    fun concluirProjeto() {
        val projeto = _uiState.value.projeto ?: return
        if (_uiState.value.perfil != PerfilUsuario.GESTOR) return
        viewModelScope.launch {
            _uiState.update { it.copy(actionSuccess = false, successMessageRes = null, errorMessage = null) }
            try {
                val concluido = projeto.copy(
                    status = StatusProjeto.CONCLUIDO,
                    progresso = 100,
                    dataConclusao = System.currentTimeMillis()
                )
                projetoRepository.salvar(concluido)
                _uiState.update {
                    it.copy(
                        projeto = concluido,
                        progressoSlider = 100f,
                        progressSavedHint = false,
                        actionSuccess = true,
                        successMessageRes = R.string.projeto_detalhe_concluido,
                        errorMessage = null
                    )
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = "Sem conexão. Esta ação exige rede na Sprint 2.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao concluir projeto") }
            }
        }
    }

    fun atualizarStatus() {
        val projeto = _uiState.value.projeto ?: return
        if (_uiState.value.perfil != PerfilUsuario.GESTOR) return
        val proximoStatus = proximoStatus(projeto.status) ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(actionSuccess = false, successMessageRes = null, errorMessage = null) }
            try {
                val atualizado = projeto.copy(
                    status = proximoStatus,
                    dataInicio = if (proximoStatus == StatusProjeto.EM_ANDAMENTO) {
                        projeto.dataInicio ?: System.currentTimeMillis()
                    } else {
                        projeto.dataInicio
                    }
                )
                projetoRepository.atualizarStatus(projeto.id, proximoStatus)
                _uiState.update {
                    it.copy(
                        projeto = atualizado,
                        actionSuccess = true,
                        successMessageRes = R.string.projeto_detalhe_sucesso,
                        errorMessage = null
                    )
                }
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = "Sem conexão. Esta ação exige rede na Sprint 2.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao atualizar status") }
            }
        }
    }

    private fun proximoStatus(atual: StatusProjeto): StatusProjeto? = when (atual) {
        StatusProjeto.PLANEJADO -> StatusProjeto.EM_ANDAMENTO
        else -> null
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
    val isSavingProgress: Boolean = false,
    val progressSavedHint: Boolean = false,
    val actionSuccess: Boolean = false,
    val successMessageRes: Int? = null,
    val marcos: List<MarcoTimeline> = emptyList(),
    val ideiasVinculadas: List<IdeiaVinculadaSimple> = emptyList(),
    val membrosEquipe: List<AvatarData> = emptyList()
)
