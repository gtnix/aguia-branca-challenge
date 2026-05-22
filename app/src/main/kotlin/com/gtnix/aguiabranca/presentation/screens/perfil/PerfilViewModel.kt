package com.gtnix.aguiabranca.presentation.screens.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.UserSession
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.Pontuacao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val userSession: UserSession,
    private val usuarioRepository: UsuarioRepository,
    private val calcularPontuacao: CalcularPontuacaoUseCase
) : ViewModel() {

    var uiState by mutableStateOf(PerfilUiState())
        private set

    init {
        carregarPerfil()
    }

    private fun carregarPerfil() {
        uiState = uiState.copy(
            isLoading = true,
            nome = userSession.userName,
            area = userSession.area.name,
            perfil = userSession.perfil.name,
            inicialNome = userSession.userName.firstOrNull()?.uppercaseChar() ?: 'U'
        )

        viewModelScope.launch {
            try {
                val usuario = usuarioRepository.buscarPorId(userSession.userId)
                if (usuario != null) {
                    uiState = uiState.copy(
                        nome = usuario.nome,
                        area = usuario.area.name,
                        perfil = usuario.perfil.name,
                        fotoPerfil = usuario.fotoPerfil,
                        inicialNome = usuario.nome.firstOrNull()?.uppercaseChar() ?: 'U'
                    )
                }

                val pontuacao = calcularPontuacao.calcular(userSession.userId)
                uiState = uiState.copy(
                    isLoading = false,
                    pontuacao = pontuacao
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erro ao carregar perfil"
                )
            }
        }
    }

    fun onLogout() {
        userSession.clear()
        uiState = uiState.copy(logoutSuccess = true)
    }
}

data class PerfilUiState(
    val isLoading: Boolean = true,
    val nome: String = "",
    val area: String = "",
    val perfil: String = "",
    val fotoPerfil: String? = null,
    val inicialNome: Char = 'U',
    val pontuacao: Pontuacao? = null,
    val errorMessage: String? = null,
    val logoutSuccess: Boolean = false
)
