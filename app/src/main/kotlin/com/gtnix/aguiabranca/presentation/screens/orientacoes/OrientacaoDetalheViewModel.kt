package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.DashboardByStrategy
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.VencedorCampanha
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.DashboardRepository
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.presentation.navigation.Destination
import java.io.IOException
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
    private val dashboardRepository: DashboardRepository,
    sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        OrientacaoDetalheUiState(perfil = sessionManager.getCurrentUser()?.perfil ?: PerfilUsuario.OPERADOR)
    )
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
                    carregarImpactoFinanceiro()
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = R.string.error_load_orientacao)
                }
            }
        }
    }

    /** Fecha a campanha no servidor, que paga o bônus aos top N, e recarrega o estado. */
    fun encerrarCampanha() {
        if (!_uiState.value.podeEncerrarCampanha) return
        _uiState.update { it.copy(encerrandoCampanha = true) }
        viewModelScope.launch {
            try {
                val vencedores = orientacaoRepository.encerrarCampanha(orientacaoId)
                val atualizada = orientacaoRepository.buscarPorId(orientacaoId)
                _uiState.update {
                    it.copy(
                        encerrandoCampanha = false,
                        orientacao = atualizada ?: it.orientacao,
                        vencedoresCampanha = vencedores
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(encerrandoCampanha = false, errorEncerramentoRes = R.string.campanha_erro_encerrar)
                }
            }
        }
    }

    fun consumirErroEncerramento() {
        _uiState.update { it.copy(errorEncerramentoRes = null) }
    }

    /** Recarrega sem spinner ao voltar da edição; as listas já são observadas. */
    fun recarregar() {
        if (_uiState.value.orientacao == null) return
        viewModelScope.launch {
            val atualizada = try {
                orientacaoRepository.buscarPorId(orientacaoId)
            } catch (e: Exception) {
                null
            }
            if (atualizada != null) {
                _uiState.update { it.copy(orientacao = atualizada) }
            }
        }
        carregarImpactoFinanceiro()
    }

    fun alternarAtivacao() {
        val atual = _uiState.value.orientacao ?: return
        if (!_uiState.value.isLider || _uiState.value.alterandoAtivacao) return
        _uiState.update { it.copy(alterandoAtivacao = true, errorAcaoRes = null) }
        viewModelScope.launch {
            try {
                if (atual.ativa) {
                    orientacaoRepository.desativar(orientacaoId)
                } else {
                    orientacaoRepository.ativar(orientacaoId)
                }
                val atualizada = orientacaoRepository.buscarPorId(orientacaoId)
                _uiState.update {
                    it.copy(
                        alterandoAtivacao = false,
                        orientacao = atualizada ?: atual.copy(ativa = !atual.ativa)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        alterandoAtivacao = false,
                        errorAcaoRes = when {
                            e is IOException -> R.string.error_network_required
                            atual.ativa -> R.string.error_deactivate_orientacao
                            else -> R.string.error_activate_orientacao
                        }
                    )
                }
            }
        }
    }

    fun consumirErroAcao() {
        _uiState.update { it.copy(errorAcaoRes = null) }
    }

    private fun carregarImpactoFinanceiro() {
        viewModelScope.launch {
            val resultado = try {
                dashboardRepository.obterPorEstrategia(orientacaoId)
            } catch (e: Exception) {
                null
            }
            if (resultado is Result.Success) {
                _uiState.update { it.copy(impacto = resultado.data) }
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
    val perfil: PerfilUsuario = PerfilUsuario.OPERADOR,
    val encerrandoCampanha: Boolean = false,
    val vencedoresCampanha: List<VencedorCampanha>? = null,
    val errorEncerramentoRes: Int? = null,
    val errorMessageRes: Int? = null,
    val impacto: DashboardByStrategy? = null,
    val alterandoAtivacao: Boolean = false,
    val errorAcaoRes: Int? = null
) {
    val isLider: Boolean
        get() = perfil == PerfilUsuario.LIDER

    val podeEncerrarCampanha: Boolean
        get() = isLider && orientacao?.campanhaPatrocinada?.let { !it.encerrada } == true

    val projetosEmAndamento: Int
        get() = projetosVinculados.count {
            it.status == StatusProjeto.EM_ANDAMENTO || it.status == StatusProjeto.PLANEJADO
        }

    val projetosAtrasados: Int
        get() = impacto?.projetosAtrasados ?: projetosVinculados.count { it.estaAtrasado() }

    val ideiasEmAvaliacao: Int
        get() = ideiasAlinhadas.count {
            it.status == StatusIdeia.PENDENTE || it.status == StatusIdeia.EM_ANALISE
        }

    val ideiasAprovadas: Int
        get() = ideiasAlinhadas.count { it.status == StatusIdeia.APROVADA }

    val ideiasEmProjeto: Int
        get() = ideiasAlinhadas.count { it.status == StatusIdeia.CONVERTIDA_PROJETO }

    /** Reprovadas ficam de fora: o líder quer ver o que ainda pode gerar valor. */
    val topIdeias: List<Ideia>
        get() = ideiasAlinhadas
            .filter { it.status != StatusIdeia.REPROVADA }
            .sortedWith(
                compareByDescending<Ideia> { it.scoreIa ?: -1 }
                    .thenByDescending { it.upvotes }
                    .thenByDescending { it.dataCriacao }
            )

    /** Projetos abertos primeiro, atrasados no topo, depois por progresso. */
    val projetosOrdenados: List<Projeto>
        get() = projetosVinculados
            .filter { it.status != StatusProjeto.CANCELADO }
            .sortedWith(
                compareBy<Projeto> { it.status == StatusProjeto.CONCLUIDO }
                    .thenByDescending { it.estaAtrasado() }
                    .thenByDescending { it.progresso }
            )
}
