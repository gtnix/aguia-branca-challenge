package com.gtnix.aguiabranca.presentation.screens.ideias

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.FrequenciaProblema
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NovaIdeiaViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    orientacaoRepository: OrientacaoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _formState = MutableStateFlow(
        NovaIdeiaFormState(area = sessionManager.getCurrentUser()?.area ?: AreaAtuacao.OPERACOES)
    )

    val uiState: StateFlow<NovaIdeiaUiState> = combine(
        _formState,
        orientacaoRepository.listarAtivas()
            .catch { emit(emptyList()) }
    ) { form, orientacoes ->
        NovaIdeiaUiState(
            titulo = form.titulo,
            descricao = form.descricao,
            tipo = form.tipo,
            area = form.area,
            orientacaoId = form.orientacaoId,
            publicoAfetado = form.publicoAfetado,
            frequencia = form.frequencia,
            isLoading = form.isLoading,
            errorMessageRes = form.errorMessageRes,
            orientacoes = orientacoes
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NovaIdeiaUiState()
    )

    fun onTituloChange(titulo: String) {
        _formState.update { it.copy(titulo = titulo, errorMessageRes = null) }
    }

    fun onDescricaoChange(descricao: String) {
        _formState.update { it.copy(descricao = descricao, errorMessageRes = null) }
    }

    fun onAreaChange(area: AreaAtuacao) {
        _formState.update { it.copy(area = area) }
    }

    fun onOrientacaoChange(orientacaoId: String?) {
        _formState.update { it.copy(orientacaoId = orientacaoId) }
    }

    fun onPublicoAfetadoChange(texto: String) {
        _formState.update { it.copy(publicoAfetado = texto.take(PUBLICO_MAX)) }
    }

    /** Tocar na frequência já selecionada limpa a resposta: o campo é opcional. */
    fun onFrequenciaChange(frequencia: FrequenciaProblema) {
        _formState.update { it.copy(frequencia = if (it.frequencia == frequencia) null else frequencia) }
    }

    fun onTipoChange(tipo: TipoIdeia) {
        _formState.update { it.copy(tipo = tipo) }
    }

    fun salvar(onSuccess: () -> Unit) {
        val current = _formState.value
        val user = sessionManager.getCurrentUser()

        if (user == null) {
            _formState.update { it.copy(errorMessageRes = R.string.error_user_not_logged) }
            return
        }

        if (user.perfil != PerfilUsuario.OPERADOR) {
            _formState.update { it.copy(errorMessageRes = R.string.error_only_operator_create_ideia) }
            return
        }

        if (current.titulo.isBlank() || current.descricao.isBlank()) {
            _formState.update { it.copy(errorMessageRes = R.string.error_nova_ideia_fields) }
            return
        }

        if (current.orientacaoId.isNullOrBlank()) {
            _formState.update { it.copy(errorMessageRes = R.string.error_ideia_orientacao_required) }
            return
        }

        _formState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val novaIdeia = Ideia(
                    id = "",
                    titulo = current.titulo.trim(),
                    descricao = current.descricao.trim(),
                    tipo = current.tipo,
                    area = current.area,
                    autorId = user.id,
                    autorNome = user.nome,
                    orientacaoId = current.orientacaoId,
                    publicoAfetado = current.publicoAfetado.trim().ifBlank { null },
                    frequencia = current.frequencia
                )

                ideiaRepository.salvar(novaIdeia)

                _formState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: IOException) {
                Log.e(TAG, "Failed to save idea offline", e)
                _formState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_network_required) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save idea", e)
                _formState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_save_idea) }
            }
        }
    }

    companion object {
        private const val TAG = "NovaIdeiaViewModel"
        const val PUBLICO_MAX = 300
    }
}

private data class NovaIdeiaFormState(
    val titulo: String = "",
    val descricao: String = "",
    val tipo: TipoIdeia = TipoIdeia.IDEIA,
    val area: AreaAtuacao = AreaAtuacao.OPERACOES,
    val orientacaoId: String? = null,
    val publicoAfetado: String = "",
    val frequencia: FrequenciaProblema? = null,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null
)

data class NovaIdeiaUiState(
    val titulo: String = "",
    val descricao: String = "",
    val tipo: TipoIdeia = TipoIdeia.IDEIA,
    val area: AreaAtuacao = AreaAtuacao.OPERACOES,
    val orientacaoId: String? = null,
    val publicoAfetado: String = "",
    val frequencia: FrequenciaProblema? = null,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val orientacoes: List<OrientacaoEstrategica> = emptyList()
)
