package com.gtnix.aguiabranca.presentation.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Perfil mostra só identidade (dados do RH, somente leitura) e preferências do app. */
@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.currentUserFlow.collect { user ->
                if (user == null) {
                    _uiState.update { it.copy(isLoading = false) }
                    return@collect
                }

                _uiState.update { it.comUsuario(user).copy(isLoading = true, errorMessageRes = null) }

                try {
                    val usuario = usuarioRepository.buscarPorId(user.id) ?: user
                    _uiState.update { it.comUsuario(usuario).copy(isLoading = false) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_load_profile) }
                }
            }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            sessionManager.logout()
        }
        _uiState.update { it.copy(logoutSuccess = true) }
    }

    private fun PerfilUiState.comUsuario(usuario: Usuario) = copy(
        nome = usuario.nome,
        email = usuario.email,
        area = usuario.area,
        divisao = usuario.divisao,
        perfil = usuario.perfil,
        inicialNome = usuario.nome.firstOrNull()?.uppercaseChar() ?: 'U'
    )
}

data class PerfilUiState(
    val isLoading: Boolean = true,
    val nome: String = "",
    val email: String = "",
    val area: AreaAtuacao? = null,
    val divisao: DivisaoNegocio? = null,
    val perfil: PerfilUsuario? = null,
    val inicialNome: Char = 'U',
    val errorMessageRes: Int? = null,
    val logoutSuccess: Boolean = false
)
