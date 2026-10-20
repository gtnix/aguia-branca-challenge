package com.gtnix.aguiabranca.presentation.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.data.remote.NetworkMonitor
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
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val sessionManager: SessionManager,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessageRes = null) }
    }

    fun onSenhaChange(senha: String) {
        _uiState.update { it.copy(senha = senha, errorMessageRes = null) }
    }

    fun onLoginClick() {
        val current = _uiState.value
        if (current.email.isBlank() || current.senha.isBlank()) {
            _uiState.update { it.copy(errorMessageRes = R.string.login_error_empty_fields) }
            return
        }
        authenticate(current.email.trim(), current.senha)
    }

    fun onDemoLogin(perfil: PerfilUsuario) {
        val email = when (perfil) {
            PerfilUsuario.LIDER -> DEMO_LIDER_EMAIL
            PerfilUsuario.GESTOR -> DEMO_GESTOR_EMAIL
            PerfilUsuario.OPERADOR -> DEMO_OPERADOR_EMAIL
        }
        authenticate(email, DEMO_PASSWORD, perfil)
    }

    fun onLoginHandled() {
        _uiState.update { it.copy(loginSuccess = false) }
    }

    private fun authenticate(
        email: String,
        senha: String,
        demoPerfil: PerfilUsuario? = null
    ) {
        if (!networkMonitor.isOnline()) {
            _uiState.update {
                it.copy(isLoading = false, errorMessageRes = R.string.login_error_network)
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

        viewModelScope.launch {
            try {
                val usuario = usuarioRepository.autenticar(email = email, senha = senha)
                if (usuario != null) {
                    sessionManager.login(usuario)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            usuarioLogado = usuario,
                            demoPerfil = demoPerfil
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = R.string.login_error_invalid_credentials
                        )
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Login network failure", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = R.string.login_error_api_unreachable)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login failed", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = R.string.error_login)
                }
            }
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
        private const val DEMO_PASSWORD = "123456"
        private const val DEMO_LIDER_EMAIL = "marcos.silva@aguiabranca.com.br"
        private const val DEMO_GESTOR_EMAIL = "ana.oliveira@aguiabranca.com.br"
        private const val DEMO_OPERADOR_EMAIL = "pedro.santos@aguiabranca.com.br"
    }
}

data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val loginSuccess: Boolean = false,
    val usuarioLogado: Usuario? = null,
    val demoPerfil: PerfilUsuario? = null
) {
    val perfil: PerfilUsuario?
        get() = usuarioLogado?.perfil ?: demoPerfil
}
