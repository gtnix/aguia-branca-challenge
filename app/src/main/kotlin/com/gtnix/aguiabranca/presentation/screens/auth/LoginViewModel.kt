package com.gtnix.aguiabranca.presentation.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.data.local.database.DatabaseSeeder
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.UserSession
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val userSession: UserSession
) : ViewModel() {

    /**
     * Estado da UI - Observado pelo Composable.
     *
     * `by mutableStateOf` usa delegação para criar getter/setter
     * que notifica o Compose quando o valor muda.
     */
    var uiState by mutableStateOf(LoginUiState())
        private set

    init {
        viewModelScope.launch { seeder.seedDatabaseIfEmpty() }
    }

    /**
     * Atualiza o campo de email.
     */
    fun onEmailChange(email: String) {
        uiState = uiState.copy(
            email = email,
            errorMessage = null // Limpa erro ao digitar
        )
    }

    /**
     * Atualiza o campo de senha.
     */
    fun onSenhaChange(senha: String) {
        uiState = uiState.copy(
            senha = senha,
            errorMessage = null
        )
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
        // Validação básica
        if (uiState.email.isBlank() || uiState.senha.isBlank()) {
            uiState = uiState.copy(errorMessage = "Preencha todos os campos")
            return
        }

        // Inicia loading
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        // viewModelScope.launch executa em coroutine
        // Isso não bloqueia a Main Thread!
        viewModelScope.launch {
            try {
                val usuario = usuarioRepository.autenticar(
                    email = uiState.email.trim(),
                    senha = uiState.senha
                )

                if (usuario != null) {
                    userSession.perfil = usuario.perfil
                    userSession.userId = usuario.id
                    userSession.userName = usuario.nome
                    userSession.area = usuario.area
                    uiState = uiState.copy(
                        isLoading = false,
                        loginSuccess = true,
                        usuarioLogado = usuario
                    )
                } else {
                    // Credenciais inválidas
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = "E-mail ou senha inválidos"
                    )
                }
            } catch (e: Exception) {
                // Erro de sistema
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Erro ao fazer login. Tente novamente."
                )
            }
        }
    }

    /**
     * Login de demonstração - Seleciona perfil diretamente.
     * Usado para testes sem banco populado.
     */
    fun onDemoLogin(perfil: PerfilUsuario) {
        userSession.perfil = perfil
        userSession.area = AreaAtuacao.OPERACOES
        uiState = uiState.copy(
            isLoading = false,
            loginSuccess = true,
            demoPerfil = perfil
        )
    }

    /**
     * Limpa o estado de sucesso após navegação.
     */
    fun onLoginHandled() {
        uiState = uiState.copy(loginSuccess = false)
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
