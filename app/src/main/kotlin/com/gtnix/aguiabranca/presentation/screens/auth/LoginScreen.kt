package com.gtnix.aguiabranca.presentation.screens.auth

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

private val LoginGradient = listOf(
    Color(0xFF0A2540),
    Color(0xFF0F3460),
    Color(0xFF16537E)
)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (PerfilUsuario) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage = uiState.errorMessageRes?.let { stringResource(it) }

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

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onSenhaChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onDemoLogin: (PerfilUsuario) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val featureUnderDevelopment = stringResource(R.string.feature_under_development)
    
    val infiniteTransition = rememberInfiniteTransition(label = "backgroundAnim")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = LoginGradient)
            )
            .imePadding()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-60).dp)
                .size(300.dp)
                .scale(glowScale)
                .blur(180.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFF5C00).copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 100.dp)
                .size(260.dp)
                .scale(glowScale * 0.9f)
                .blur(160.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            PremiumLoginHeader()

            Spacer(modifier = Modifier.height(40.dp))

            GlassLoginCard(
                email = uiState.email,
                senha = uiState.senha,
                isLoading = uiState.isLoading,
                errorMessageRes = uiState.errorMessageRes,
                onEmailChange = onEmailChange,
                onSenhaChange = onSenhaChange,
                onLoginClick = onLoginClick,
                onMoveFocus = { focusManager.moveFocus(FocusDirection.Down) },
                onClearFocus = { focusManager.clearFocus() },
                onForgotPasswordClick = {
                    Toast.makeText(context, featureUnderDevelopment, Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            PremiumDemoSection(onDemoLogin = onDemoLogin)
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PremiumLoginHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_inovagab_logo),
            contentDescription = stringResource(R.string.cd_logo),
            modifier = Modifier.size(80.dp),
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.auth_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = (-0.5).sp,
            modifier = Modifier.semantics { heading() }
        )
        
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.auth_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun GlassLoginCard(
    email: String,
    senha: String,
    isLoading: Boolean,
    errorMessageRes: Int?,
    onEmailChange: (String) -> Unit,
    onSenhaChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onMoveFocus: () -> Unit,
    onClearFocus: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val shape = RoundedCornerShape(24.dp)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Color.White.copy(alpha = 0.15f),
                shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GlassTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.auth_email_label),
                placeholder = stringResource(R.string.auth_email_placeholder),
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
                onImeAction = onMoveFocus
            )

            Spacer(modifier = Modifier.height(16.dp))

            GlassPasswordField(
                value = senha,
                onValueChange = onSenhaChange,
                label = stringResource(R.string.auth_password_label),
                onImeAction = {
                    onClearFocus()
                    onLoginClick()
                }
            )

            errorMessageRes?.let { messageRes ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(messageRes),
                    color = Color(0xFFFF6B6B),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.auth_login_button),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onForgotPasswordClick) {
                Text(
                    text = stringResource(R.string.auth_forgot_password),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {}
) {
    val emailIconDesc = stringResource(R.string.cd_email_field)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, color = Color.White.copy(alpha = 0.7f)) },
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.4f)) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = emailIconDesc,
                tint = Color.White.copy(alpha = 0.7f)
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
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.9f),
            focusedBorderColor = Color.White.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
            cursorColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.03f)
        )
    )
}

@Composable
private fun GlassPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onImeAction: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val passwordPlaceholder = stringResource(R.string.auth_password_placeholder)
    val showPasswordDesc = stringResource(R.string.cd_show_password)
    val hidePasswordDesc = stringResource(R.string.cd_hide_password)

    val passwordIconDesc = stringResource(R.string.cd_password_field)
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, color = Color.White.copy(alpha = 0.7f)) },
        placeholder = { Text(passwordPlaceholder, color = Color.White.copy(alpha = 0.4f)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = passwordIconDesc,
                tint = Color.White.copy(alpha = 0.7f)
            )
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) hidePasswordDesc else showPasswordDesc,
                    tint = Color.White.copy(alpha = 0.7f)
                )
            }
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onImeAction() }),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White.copy(alpha = 0.9f),
            focusedBorderColor = Color.White.copy(alpha = 0.5f),
            unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
            cursorColor = Color.White,
            focusedContainerColor = Color.White.copy(alpha = 0.05f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.03f)
        )
    )
}

@Composable
private fun PremiumDemoSection(
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
                color = Color.White.copy(alpha = 0.15f)
            )
            Text(
                text = "  ${stringResource(R.string.auth_demo_section)}  ",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.5f)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = Color.White.copy(alpha = 0.15f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.auth_login_as),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DemoProfileChip(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.perfil_operador),
                icon = Icons.Outlined.Engineering,
                onClick = { onDemoLogin(PerfilUsuario.OPERADOR) }
            )
            DemoProfileChip(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.perfil_gestor),
                icon = Icons.Outlined.SupervisorAccount,
                onClick = { onDemoLogin(PerfilUsuario.GESTOR) }
            )
            DemoProfileChip(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.perfil_lider),
                icon = Icons.Outlined.AdminPanelSettings,
                onClick = { onDemoLogin(PerfilUsuario.LIDER) }
            )
        }
    }
}

@Composable
private fun DemoProfileChip(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    
    Surface(
        modifier = modifier
            .border(
                1.dp,
                Color.White.copy(alpha = 0.15f),
                shape
            ),
        color = Color.White.copy(alpha = 0.08f),
        shape = shape,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = stringResource(R.string.cd_demo_profile, label),
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

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

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenDarkPreview() {
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
                errorMessageRes = R.string.login_error_invalid_credentials
            ),
            onEmailChange = {},
            onSenhaChange = {},
            onLoginClick = {},
            onDemoLogin = {}
        )
    }
}
