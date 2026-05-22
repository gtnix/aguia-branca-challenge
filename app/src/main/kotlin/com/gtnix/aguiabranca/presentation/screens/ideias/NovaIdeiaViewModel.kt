package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NovaIdeiaViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository
) : ViewModel() {

    var uiState by mutableStateOf(NovaIdeiaUiState())
        private set

    fun onTituloChange(titulo: String) {
        uiState = uiState.copy(titulo = titulo, errorMessage = null)
    }

    fun onDescricaoChange(descricao: String) {
        uiState = uiState.copy(descricao = descricao, errorMessage = null)
    }

    fun onAreaChange(area: AreaAtuacao) {
        uiState = uiState.copy(area = area)
    }

    fun onTipoChange(tipo: TipoIdeia) {
        uiState = uiState.copy(tipo = tipo)
    }

    fun salvar(onSuccess: () -> Unit) {
        if (uiState.titulo.isBlank() || uiState.descricao.isBlank()) {
            uiState = uiState.copy(errorMessage = "Preencha todos os campos")
            return
        }

        uiState = uiState.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val novaIdeia = Ideia(
                    id = UUID.randomUUID().toString(),
                    titulo = uiState.titulo.trim(),
                    descricao = uiState.descricao.trim(),
                    tipo = uiState.tipo,
                    area = uiState.area,
                    autorId = "demo-user", // Em produção, viria do usuário logado
                    autorNome = "Usuário Demo"
                )

                ideiaRepository.salvar(novaIdeia)

                uiState = uiState.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erro ao salvar ideia"
                )
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
    val errorMessage: String? = null
)
