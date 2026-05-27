package com.gtnix.aguiabranca.presentation.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.data.local.database.DatabaseSeeder
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

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val seeder: DatabaseSeeder,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { seeder.seedDatabaseIfEmpty() }
    }

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

        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

        viewModelScope.launch {
            try {
                val usuario = usuarioRepository.autenticar(
                    email = current.email.trim(),
                    senha = current.senha
                )

                if (usuario != null) {
                    sessionManager.login(usuario)
                    _uiState.update {
                        it.copy(isLoading = false, loginSuccess = true, usuarioLogado = usuario)
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessageRes = R.string.login_error_invalid_credentials)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Login failed", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = R.string.error_login)
                }
            }
        }
    }

    fun onDemoLogin(perfil: PerfilUsuario) {
        val email = when (perfil) {
            PerfilUsuario.LIDER -> "marcos.silva@aguiabranca.com.br"
            PerfilUsuario.GESTOR -> "ana.oliveira@aguiabranca.com.br"
            PerfilUsuario.OPERADOR -> "pedro.santos@aguiabranca.com.br"
        }

        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

        viewModelScope.launch {
            try {
                val usuario = usuarioRepository.autenticar(email = email, senha = "123456")
                if (usuario != null) {
                    sessionManager.login(usuario)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            usuarioLogado = usuario,
                            demoPerfil = perfil
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
            } catch (e: Exception) {
                Log.e(TAG, "Demo login failed", e)
                _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = R.string.error_login)
                }
            }
        }
    }

    fun onLoginHandled() {
        _uiState.update { it.copy(loginSuccess = false) }
    }

    companion object {
        private const val TAG = "LoginViewModel"
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
