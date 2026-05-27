package com.gtnix.aguiabranca.presentation.screens.projetos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.presentation.navigation.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NovoProjetoViewModel @Inject constructor(
    private val projetoRepository: ProjetoRepository,
    private val ideiaRepository: IdeiaRepository,
    private val orientacaoRepository: OrientacaoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val sessionManager: SessionManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _formState = MutableStateFlow(NovoProjetoFormState())

    val uiState: StateFlow<NovoProjetoUiState> = combine(
        _formState,
        orientacaoRepository.listarAtivas().catch { emit(emptyList()) },
        usuarioRepository.listarTodos().catch { emit(emptyList()) }
    ) { form, orientacoes, usuarios ->
        val currentUserId = sessionManager.getCurrentUser()?.id
        NovoProjetoUiState(
            nome = form.nome,
            objetivo = form.objetivo,
            descricao = form.descricao,
            area = form.area,
            investimentoEstimado = form.investimentoEstimado,
            retornoEstimadoMensal = form.retornoEstimadoMensal,
            ideiaOrigemId = form.ideiaOrigemId,
            orientacaoId = form.orientacaoId,
            membrosIds = form.membrosIds,
            dataPrevistaConclusao = form.dataPrevistaConclusao,
            isSaving = form.isSaving,
            errorMessageRes = form.errorMessageRes,
            orientacoesDisponiveis = orientacoes,
            usuariosDisponiveis = usuarios.filter { it.id != currentUserId }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NovoProjetoUiState()
    )

    private val ideiaId: String? = savedStateHandle[Destination.NovoProjeto.ARG_IDEIA_ID]

    init {
        if (!ideiaId.isNullOrBlank()) {
            carregarIdeia(ideiaId)
        }
    }

    private fun carregarIdeia(id: String) {
        viewModelScope.launch {
            try {
                val ideia = ideiaRepository.buscarPorId(id)
                if (ideia != null) {
                    _formState.update {
                        it.copy(
                            nome = ideia.titulo,
                            objetivo = deriveObjetivoFromIdeia(ideia.titulo, ideia.descricao),
                            descricao = ideia.descricao,
                            area = ideia.area,
                            orientacaoId = ideia.orientacaoId,
                            ideiaOrigemId = id
                        )
                    }
                }
            } catch (_: Exception) { }
        }
    }

    fun onNomeChange(value: String) {
        _formState.update { it.copy(nome = value) }
    }

    fun onObjetivoChange(value: String) {
        _formState.update { it.copy(objetivo = value) }
    }

    fun onDescricaoChange(value: String) {
        _formState.update { it.copy(descricao = value) }
    }

    fun onAreaChange(value: AreaAtuacao) {
        _formState.update { it.copy(area = value) }
    }

    fun onInvestimentoChange(value: String) {
        _formState.update { it.copy(investimentoEstimado = value) }
    }

    fun onRetornoChange(value: String) {
        _formState.update { it.copy(retornoEstimadoMensal = value) }
    }

    fun onOrientacaoChange(value: String?) {
        _formState.update { it.copy(orientacaoId = value) }
    }

    fun toggleMembro(usuarioId: String) {
        _formState.update { current ->
            val membros = current.membrosIds.toMutableList()
            if (usuarioId in membros) {
                membros.remove(usuarioId)
            } else {
                membros.add(usuarioId)
            }
            current.copy(membrosIds = membros)
        }
    }

    fun onDataPrevistaChange(value: Long?) {
        _formState.update { it.copy(dataPrevistaConclusao = value) }
    }

    fun salvar(onSuccess: () -> Unit) {
        val current = _formState.value
        if (current.nome.isBlank() || current.objetivo.isBlank()) {
            _formState.update { it.copy(errorMessageRes = R.string.novo_projeto_erro_campos) }
            return
        }

        val user = sessionManager.getCurrentUser()
        if (user == null) {
            _formState.update { it.copy(errorMessageRes = R.string.error_user_not_logged) }
            return
        }

        val investimento = current.investimentoEstimado.toDoubleOrNull() ?: 0.0
        val retorno = current.retornoEstimadoMensal.toDoubleOrNull() ?: 0.0

        _formState.update { it.copy(isSaving = true) }
        val projetoId = UUID.randomUUID().toString()

        viewModelScope.launch {
            try {
                val projeto = Projeto(
                    id = projetoId,
                    nome = current.nome,
                    objetivo = current.objetivo,
                    descricao = current.descricao,
                    area = current.area,
                    status = StatusProjeto.PLANEJADO,
                    ideiaOrigemId = current.ideiaOrigemId,
                    orientacaoId = current.orientacaoId,
                    responsavelId = user.id,
                    responsavelNome = user.nome,
                    membrosIds = current.membrosIds,
                    dataPrevistaConclusao = current.dataPrevistaConclusao,
                    investimentoEstimado = investimento,
                    retornoEstimadoMensal = retorno
                )
                projetoRepository.salvar(projeto)

                val origem = current.ideiaOrigemId
                if (!origem.isNullOrBlank()) {
                    ideiaRepository.atualizarStatus(origem, StatusIdeia.CONVERTIDA_PROJETO)
                    ideiaRepository.vincularProjeto(origem, projetoId)
                }

                _formState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _formState.update { it.copy(isSaving = false, errorMessageRes = R.string.novo_projeto_erro_salvar) }
            }
        }
    }
}

private data class NovoProjetoFormState(
    val nome: String = "",
    val objetivo: String = "",
    val descricao: String = "",
    val area: AreaAtuacao = AreaAtuacao.OPERACOES,
    val investimentoEstimado: String = "",
    val retornoEstimadoMensal: String = "",
    val ideiaOrigemId: String? = null,
    val orientacaoId: String? = null,
    val membrosIds: List<String> = emptyList(),
    val dataPrevistaConclusao: Long? = null,
    val isSaving: Boolean = false,
    val errorMessageRes: Int? = null
)

private fun deriveObjetivoFromIdeia(titulo: String, descricao: String): String {
    val firstSentence = descricao
        .substringBefore('.')
        .substringBefore('\n')
        .trim()
    return firstSentence.ifBlank { titulo.trim() }
}

data class NovoProjetoUiState(
    val nome: String = "",
    val objetivo: String = "",
    val descricao: String = "",
    val area: AreaAtuacao = AreaAtuacao.OPERACOES,
    val investimentoEstimado: String = "",
    val retornoEstimadoMensal: String = "",
    val ideiaOrigemId: String? = null,
    val orientacaoId: String? = null,
    val membrosIds: List<String> = emptyList(),
    val dataPrevistaConclusao: Long? = null,
    val orientacoesDisponiveis: List<OrientacaoEstrategica> = emptyList(),
    val usuariosDisponiveis: List<Usuario> = emptyList(),
    val isSaving: Boolean = false,
    val errorMessageRes: Int? = null
)
