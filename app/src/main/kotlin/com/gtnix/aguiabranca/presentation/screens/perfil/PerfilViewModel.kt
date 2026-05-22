package com.gtnix.aguiabranca.presentation.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.session.UserSession
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.Pontuacao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val userSession: UserSession,
    private val usuarioRepository: UsuarioRepository,
    private val calcularPontuacao: CalcularPontuacaoUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        carregarPerfil()
    }

    private fun carregarPerfil() {
        _uiState.update {
            it.copy(
                isLoading = true,
                nome = userSession.userName,
                area = userSession.area.name,
                perfil = userSession.perfil.name,
                inicialNome = userSession.userName.firstOrNull()?.uppercaseChar() ?: 'U'
            )
        }

        viewModelScope.launch {
            try {
                val usuario = usuarioRepository.buscarPorId(userSession.userId)
                if (usuario != null) {
                    _uiState.update {
                        it.copy(
                            nome = usuario.nome,
                            area = usuario.area.name,
                            perfil = usuario.perfil.name,
                            fotoPerfil = usuario.fotoPerfil,
                            inicialNome = usuario.nome.firstOrNull()?.uppercaseChar() ?: 'U'
                        )
                    }
                }

                val pontuacao = calcularPontuacao.calcular(userSession.userId)
                _uiState.update { it.copy(isLoading = false, pontuacao = pontuacao) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro ao carregar perfil") }
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            sessionManager.logout()
        }
        _uiState.update { it.copy(logoutSuccess = true) }
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
