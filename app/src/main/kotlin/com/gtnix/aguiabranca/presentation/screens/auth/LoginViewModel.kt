package com.gtnix.aguiabranca.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.data.local.database.DatabaseSeeder
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.domain.session.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * LoginViewModel - Estado e Lógica da Tela de Login
 *
 * ## Conceito FIAP - Material 10A (MVVM)
 *
 * O ViewModel é o "cérebro" da tela. Ele:
 * - Mantém o estado da UI (`uiState`)
 * - Processa eventos da View (ex: `onLoginClick`)
 * - Chama o Repository para acessar dados
 * - NÃO conhece detalhes da UI (Compose, Android Views)
 *
 * ### Padrão MVVM
 *
 * ```
 * ┌──────────────┐         ┌────────────────┐         ┌────────────┐
 * │     VIEW     │ ──────► │   VIEWMODEL    │ ──────► │ REPOSITORY │
 * │  (Compose)   │ eventos │    (Estado)    │  dados  │   (Room)   │
 * │              │ ◄────── │                │ ◄────── │            │
 * └──────────────┘  state  └────────────────┘  flow   └────────────┘
 * ```
 *
 * ### @HiltViewModel
 *
 * Permite que o Hilt crie e injete dependências automaticamente.
 * O ViewModel sobrevive a rotações de tela.
 *
 * ### Estado com mutableStateOf
 *
 * `mutableStateOf` cria um estado observável pelo Compose.
 * Quando o valor muda, a UI recompõe automaticamente.
 *
 * ```kotlin
 * // No ViewModel
 * var uiState by mutableStateOf(LoginUiState())
 *     private set
 *
 * // Na View (Composable)
 * val state = viewModel.uiState
 * // Composable recompõe quando state muda!
 * ```
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val seeder: DatabaseSeeder,
    private val userSession: UserSession,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { seeder.seedDatabaseIfEmpty() }
    }

    /**
     * Atualiza o campo de email.
     */
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onSenhaChange(senha: String) {
        _uiState.update { it.copy(senha = senha, errorMessage = null) }
    }

    /**
     * Processa o clique no botão de Login.
     *
     * ## Fluxo
     *
     * 1. Valida campos (não vazios)
     * 2. Mostra loading
     * 3. Chama repository para autenticar
     * 4. Se sucesso: atualiza estado com usuário
     * 5. Se erro: mostra mensagem
     */
    fun onLoginClick() {
        val current = _uiState.value
        if (current.email.isBlank() || current.senha.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

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
                        it.copy(isLoading = false, errorMessage = "E-mail ou senha inválidos")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Erro ao fazer login. Tente novamente.")
                }
            }
        }
    }

    /**
     * Login de demonstração - Seleciona perfil diretamente.
     * Usado para testes sem banco populado.
     */
    fun onDemoLogin(perfil: PerfilUsuario) {
        val demoUser = Usuario(
            id = "demo-${perfil.name.lowercase()}",
            nome = "Demo ${perfil.name}",
            email = "demo@aguiabranca.com.br",
            perfil = perfil,
            area = AreaAtuacao.OPERACOES
        )
        viewModelScope.launch {
            sessionManager.login(demoUser)
        }
        _uiState.update { it.copy(isLoading = false, loginSuccess = true, demoPerfil = perfil) }
    }

    fun onLoginHandled() {
        _uiState.update { it.copy(loginSuccess = false) }
    }
}

/**
 * Estado da tela de Login.
 *
 * ## Conceito FIAP - UI State
 *
 * Encapsula TODO o estado necessário para renderizar a tela:
 * - Valores dos campos
 * - Estados de loading/erro
 * - Resultado do login
 *
 * Usar data class permite `copy()` para criar novas versões
 * sem mutação (imutabilidade).
 */
data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val usuarioLogado: Usuario? = null,
    val demoPerfil: PerfilUsuario? = null
) {
    /**
     * Retorna o perfil do usuário (logado ou demo).
     */
    val perfil: PerfilUsuario?
        get() = usuarioLogado?.perfil ?: demoPerfil
}
