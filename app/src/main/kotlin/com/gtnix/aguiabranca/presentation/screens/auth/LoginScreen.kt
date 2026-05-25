package com.gtnix.aguiabranca.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

/**
 * LoginScreen - Tela de Login
 *
 * ## Conceito FIAP - Material 04A (Telas Profissionais)
 *
 * ### Componentização
 *
 * A tela é dividida em componentes menores reutilizáveis:
 * - `LoginHeader`: Logo e título
 * - `LoginForm`: Campos de email e senha
 * - `DemoLoginSection`: Botões de demo
 *
 * ### Estrutura de Layout (Material 02A)
 *
 * ```
 * Box (container principal, fundo gradiente)
 *   └── Column (conteúdo vertical, centralizado)
 *         ├── LoginHeader
 *         ├── Card (formulário)
 *         │     └── Column
 *         │           ├── OutlinedTextField (email)
 *         │           ├── OutlinedTextField (senha)
 *         │           ├── Button (login)
 *         │           └── TextButton (esqueci senha)
 *         └── DemoLoginSection
 * ```
 *
 * ### State Hoisting
 *
 * O estado vive no ViewModel, não no Composable.
 * A Screen recebe estado e emite eventos.
 *
 * @param viewModel ViewModel injetado pelo Hilt
 * @param onLoginSuccess Callback quando login tem sucesso
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (PerfilUsuario) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.loginSuccess, uiState.perfil) {
        if (uiState.loginSuccess && uiState.perfil != null) {
            onLoginSuccess(uiState.perfil!!)
            viewModel.onLoginHandled()
        }
    }

    LoginScreenContent(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onSenhaChange = viewModel::onSenhaChange,
        onLoginClick = viewModel::onLoginClick,
        onDemoLogin = viewModel::onDemoLogin
    )
}

/**
 * Conteúdo da tela de Login - Stateless.
 *
 * Separar content permite preview sem ViewModel.
 */
@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onSenhaChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onDemoLogin: (PerfilUsuario) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Header com logo e título
            LoginHeader()

            Spacer(modifier = Modifier.height(32.dp))

            // Card do formulário
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Campo de Email
                    LoginTextField(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        label = "E-mail",
                        placeholder = "seu.email@aguiabranca.com.br",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                        onImeAction = { focusManager.moveFocus(FocusDirection.Down) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de Senha
                    PasswordTextField(
                        value = uiState.senha,
                        onValueChange = onSenhaChange,
                        label = "Senha",
                        onImeAction = { 
                            focusManager.clearFocus()
                            onLoginClick()
                        }
                    )

                    // Mensagem de erro
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botão de Login
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = "Entrar",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Link "Esqueci minha senha"
                    TextButton(onClick = { /* TODO */ }) {
                        Text(
                            text = "Esqueci minha senha",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Seção de login demo (para testes)
            DemoLoginSection(onDemoLogin = onDemoLogin)
        }
    }
}

/**
 * Header da tela de login com logo e título.
 */
@Composable
private fun LoginHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ícone representando o logo
        Icon(
            imageVector = Icons.Default.Business,
            contentDescription = "Logo Águia Branca",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Águia Branca",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = "Plataforma de Inovação",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
        )
    }
}

/**
 * Campo de texto customizado para login.
 *
 * ## Conceito FIAP - Material 04A
 *
 * OutlinedTextField é um componente Material que:
 * - Mostra borda quando focado
 * - Suporta label, placeholder, ícones
 * - Configura teclado específico (email, número, etc.)
 */
@Composable
private fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = { onImeAction() },
            onDone = { onImeAction() }
        ),
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}

/**
 * Campo de senha com toggle de visibilidade.
 */
@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onImeAction: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        placeholder = { Text("Digite sua senha") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) 
                        Icons.Default.VisibilityOff 
                    else 
                        Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) 
                        "Ocultar senha" 
                    else 
                        "Mostrar senha"
                )
            }
        },
        visualTransformation = if (passwordVisible) 
            VisualTransformation.None 
        else 
            PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onImeAction() }
        ),
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
}

/**
 * Seção de login de demonstração.
 *
 * Permite testar o app sem ter dados no banco.
 */
@Composable
private fun DemoLoginSection(
    onDemoLogin: (PerfilUsuario) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
            )
            Text(
                text = "  Demo  ",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Entrar como:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DemoButton(
                modifier = Modifier.weight(1f),
                label = "Operador",
                onClick = { onDemoLogin(PerfilUsuario.OPERADOR) }
            )
            DemoButton(
                modifier = Modifier.weight(1f),
                label = "Gestor",
                onClick = { onDemoLogin(PerfilUsuario.GESTOR) }
            )
            DemoButton(
                modifier = Modifier.weight(1f),
                label = "Líder",
                onClick = { onDemoLogin(PerfilUsuario.LIDER) }
            )
        }
    }
}

@Composable
private fun DemoButton(
    modifier: Modifier = Modifier,
    label: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

/**
 * Preview da tela de Login.
 *
 * ## Conceito FIAP - Preview
 *
 * O @Preview permite visualizar a UI no Android Studio
 * sem precisar rodar no emulador. Útil para desenvolvimento!
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    InovagabTheme {
        LoginScreenContent(
            uiState = LoginUiState(),
            onEmailChange = {},
            onSenhaChange = {},
            onLoginClick = {},
            onDemoLogin = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenLoadingPreview() {
    InovagabTheme {
        LoginScreenContent(
            uiState = LoginUiState(
                email = "operador@aguiabranca.com.br",
                senha = "123456",
                isLoading = true
            ),
            onEmailChange = {},
            onSenhaChange = {},
            onLoginClick = {},
            onDemoLogin = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenErrorPreview() {
    InovagabTheme {
        LoginScreenContent(
            uiState = LoginUiState(
                email = "teste@email.com",
                errorMessage = "E-mail ou senha inválidos"
            ),
            onEmailChange = {},
            onSenhaChange = {},
            onLoginClick = {},
            onDemoLogin = {}
        )
    }
}
