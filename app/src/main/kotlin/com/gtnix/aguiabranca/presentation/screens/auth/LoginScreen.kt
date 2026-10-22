package com.gtnix.aguiabranca.presentation.screens.auth

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.components.InovagabBrandLogo
import com.gtnix.aguiabranca.presentation.components.InovagabBrandLogoSize
import com.gtnix.aguiabranca.presentation.components.InovagabBrandLogoVariant
import com.gtnix.aguiabranca.presentation.components.LoginGroupedCredentialsCard
import com.gtnix.aguiabranca.presentation.components.LoginTechGlassCard
import com.gtnix.aguiabranca.presentation.theme.AsaAzul
import com.gtnix.aguiabranca.presentation.theme.AzulAcao
import com.gtnix.aguiabranca.presentation.theme.FundoContainer
import com.gtnix.aguiabranca.presentation.theme.FundoPagina
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LightSecondaryContainer
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.theme.MarinhoSuperficie
import com.gtnix.aguiabranca.presentation.theme.NoiteCard
import com.gtnix.aguiabranca.presentation.theme.NoiteCardAlto
import com.gtnix.aguiabranca.presentation.theme.NoiteFundo
import com.gtnix.aguiabranca.presentation.theme.contentOn

private val LoginButtonShape = RoundedCornerShape(percent = 50)
private val DemoChipShape = RoundedCornerShape(12.dp)

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

@Composable
private fun LoginScreenContent(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onSenhaChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onDemoLogin: (PerfilUsuario) -> Unit
) {
    val isDark = LocalDarkTheme.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val featureUnderDevelopment = stringResource(R.string.feature_under_development)

    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    Box(modifier = Modifier.fillMaxSize()) {
        LoginAtmosphere(isDark = isDark)

        AnimatedVisibility(
            visible = contentVisible,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(tween(280)) + slideInVertically(
                animationSpec = tween(280),
                initialOffsetY = { it / 12 }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = maxHeight)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .blur(48.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                AsaAzul.copy(alpha = if (isDark) 0.28f else 0.12f),
                                                Color.Transparent
                                            )
                                        ),
                                        shape = CircleShape
                                    )
                            )
                            InovagabBrandLogo(
                                variant = InovagabBrandLogoVariant.Auto,
                                size = InovagabBrandLogoSize.Stage,
                                markAsHeading = true
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        LoginTechGlassCard(modifier = Modifier.fillMaxWidth()) {
                            LoginFormSection(
                                email = uiState.email,
                                senha = uiState.senha,
                                isLoading = uiState.isLoading,
                                errorMessageRes = uiState.errorMessageRes,
                                onEmailChange = onEmailChange,
                                onSenhaChange = onSenhaChange,
                                onLoginClick = onLoginClick,
                                onClearFocus = { focusManager.clearFocus() },
                                onForgotPasswordClick = {
                                    Toast.makeText(context, featureUnderDevelopment, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }

                QuietDemoSection(onDemoLogin = onDemoLogin)
            }
        }
    }
}

@Composable
private fun LoginAtmosphere(isDark: Boolean) {
    val infinite = rememberInfiniteTransition(label = "login-atmosphere")
    val driftX by infinite.animateFloat(
        initialValue = -10f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow-drift-x"
    )
    val driftY by infinite.animateFloat(
        initialValue = 8f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow-drift-y"
    )

    val mesh = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(NoiteFundo, NoiteCard, NoiteCardAlto)
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(FundoPagina, FundoContainer, LightSecondaryContainer)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mesh)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (36 + driftX).dp, y = (56 + driftY).dp)
                .size(220.dp)
                .blur(90.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AzulAcao.copy(alpha = if (isDark) 0.32f else 0.20f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-72 + driftY).dp, y = (110 - driftX).dp)
                .size(240.dp)
                .blur(88.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            AsaAzul.copy(alpha = if (isDark) 0.34f else 0.18f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 48.dp)
                .size(300.dp)
                .blur(110.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            (if (isDark) MarinhoSuperficie else AsaAzul)
                                .copy(alpha = if (isDark) 0.42f else 0.12f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun LoginFormSection(
    email: String,
    senha: String,
    isLoading: Boolean,
    errorMessageRes: Int?,
    onEmailChange: (String) -> Unit,
    onSenhaChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onClearFocus: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LoginGroupedCredentialsCard(
            email = email,
            senha = senha,
            onEmailChange = onEmailChange,
            onSenhaChange = onSenhaChange,
            onSubmitFromPassword = {
                onClearFocus()
                onLoginClick()
            }
        )

        errorMessageRes?.let { messageRes ->
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = stringResource(messageRes),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            enabled = !isLoading,
            shape = LoginButtonShape,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = AzulAcao,
                contentColor = contentOn(AzulAcao),
                disabledContainerColor = AzulAcao.copy(alpha = 0.5f),
                disabledContentColor = contentOn(AzulAcao).copy(alpha = 0.5f)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = contentOn(AzulAcao),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.auth_login_button),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        TextButton(
            onClick = onForgotPasswordClick,
            contentPadding = PaddingValues(
                horizontal = 8.dp,
                vertical = 8.dp
            )
        ) {
            Text(
                text = stringResource(R.string.auth_forgot_password),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuietDemoSection(onDemoLogin: (PerfilUsuario) -> Unit) {
    val gab = LocalInovagabColors.current
    val isDark = LocalDarkTheme.current
    val chipColor = if (isDark) gab.surfaceElevated else Color.White.copy(alpha = 0.72f)
    val chipBorder = if (isDark) gab.cardBorder else AsaAzul.copy(alpha = 0.14f)
    val chipElevation = 0.dp
    val chipBorderWidth = if (isDark) 1.dp else 0.5.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.auth_login_as),
            modifier = Modifier.padding(bottom = 10.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DemoProfileChip(
                label = stringResource(R.string.perfil_operador),
                onClick = { onDemoLogin(PerfilUsuario.OPERADOR) },
                containerColor = chipColor,
                borderColor = chipBorder,
                borderWidth = chipBorderWidth,
                shadowElevation = chipElevation,
                modifier = Modifier.weight(1f)
            )
            DemoProfileChip(
                label = stringResource(R.string.perfil_gestor),
                onClick = { onDemoLogin(PerfilUsuario.GESTOR) },
                containerColor = chipColor,
                borderColor = chipBorder,
                borderWidth = chipBorderWidth,
                shadowElevation = chipElevation,
                modifier = Modifier.weight(1f)
            )
            DemoProfileChip(
                label = stringResource(R.string.perfil_lider),
                onClick = { onDemoLogin(PerfilUsuario.LIDER) },
                containerColor = chipColor,
                borderColor = chipBorder,
                borderWidth = chipBorderWidth,
                shadowElevation = chipElevation,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DemoProfileChip(
    label: String,
    onClick: () -> Unit,
    containerColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 1.dp,
    shadowElevation: Dp = 0.dp
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = DemoChipShape,
        color = containerColor,
        shadowElevation = shadowElevation,
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    InovagabTheme(darkTheme = false) {
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
    InovagabTheme(darkTheme = true) {
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
    InovagabTheme(darkTheme = false) {
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
    InovagabTheme(darkTheme = false) {
        LoginScreenContent(
            uiState = LoginUiState(
                email = "teste@email.com",
                errorMessageRes = R.string.login_error_api_unreachable
            ),
            onEmailChange = {},
            onSenhaChange = {},
            onLoginClick = {},
            onDemoLogin = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenDarkLoadingPreview() {
    InovagabTheme(darkTheme = true) {
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

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenDarkErrorPreview() {
    InovagabTheme(darkTheme = true) {
        LoginScreenContent(
            uiState = LoginUiState(
                email = "teste@email.com",
                errorMessageRes = R.string.login_error_api_unreachable
            ),
            onEmailChange = {},
            onSenhaChange = {},
            onLoginClick = {},
            onDemoLogin = {}
        )
    }
}
