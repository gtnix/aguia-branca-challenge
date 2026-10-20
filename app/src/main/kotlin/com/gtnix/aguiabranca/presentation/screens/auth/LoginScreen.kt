package com.gtnix.aguiabranca.presentation.screens.auth

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.components.LoginCredentialsFieldStyle
import com.gtnix.aguiabranca.presentation.components.LoginGroupedCredentialsCard
import com.gtnix.aguiabranca.presentation.theme.AsaAzul
import com.gtnix.aguiabranca.presentation.theme.AzulAcao
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.MarinhoInstitucional
import com.gtnix.aguiabranca.presentation.theme.MarinhoSuperficie
import com.gtnix.aguiabranca.presentation.theme.contentOn

private val LoginButtonShape = RoundedCornerShape(percent = 50)
private val LoginSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
private val IosSegmentShape = RoundedCornerShape(12.dp)
private val StageHeight = 260.dp
private val SymbolHeight = 124.dp
private val SheetOverlap = 24.dp

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
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val featureUnderDevelopment = stringResource(R.string.feature_under_development)

    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        LoginAmbientGlow()

        Column(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(280)) + slideInVertically(
                    animationSpec = tween(280),
                    initialOffsetY = { it / 10 }
                )
            ) {
                LoginStageHero()
            }

            AnimatedVisibility(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                visible = contentVisible,
                enter = fadeIn(tween(320, delayMillis = 80)) + slideInVertically(
                    animationSpec = tween(320, delayMillis = 80),
                    initialOffsetY = { it / 12 }
                )
            ) {
                LoginFrostedDrawer(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = -SheetOverlap)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(top = 28.dp, bottom = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.auth_welcome),
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

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
                            },
                            onDarkSheet = true
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        QuietDemoSection(
                            onDemoLogin = onDemoLogin,
                            onDarkSheet = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.LoginAmbientGlow() {
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .offset(x = 40.dp, y = 80.dp)
            .size(200.dp)
            .blur(80.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AzulAcao.copy(alpha = 0.22f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
    Box(
        modifier = Modifier
            .align(Alignment.CenterStart)
            .offset(x = (-60).dp, y = 200.dp)
            .size(180.dp)
            .blur(72.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AsaAzul.copy(alpha = 0.22f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}

@Composable
private fun LoginFrostedDrawer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val sheet = if (LocalDarkTheme.current) MarinhoSuperficie else MarinhoInstitucional
    Box(
        modifier = modifier
            .clip(LoginSheetShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(48.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AsaAzul.copy(alpha = 0.45f),
                            sheet.copy(alpha = 0.72f),
                            sheet.copy(alpha = 0.92f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(sheet.copy(alpha = 0.88f))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.22f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.14f),
                    shape = LoginSheetShape
                )
        )
        content()
    }
}

@Composable
private fun LoginStageHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .height(StageHeight),
        contentAlignment = Alignment.Center
    ) {
        val logoDescription = stringResource(R.string.cd_logo)
        Image(
            painter = painterResource(id = R.drawable.ic_inovagab_logo),
            contentDescription = logoDescription,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .height(SymbolHeight)
                .semantics(mergeDescendants = true) { heading() },
            contentScale = ContentScale.Fit
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
    onForgotPasswordClick: () -> Unit,
    onDarkSheet: Boolean = false
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
            },
            onDarkSheet = onDarkSheet,
            fieldStyle = if (onDarkSheet) {
                LoginCredentialsFieldStyle.IosInset
            } else {
                LoginCredentialsFieldStyle.Grouped
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
                defaultElevation = if (onDarkSheet) 0.dp else 2.dp,
                pressedElevation = if (onDarkSheet) 0.dp else 4.dp
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
                color = if (onDarkSheet) {
                    Color.White.copy(alpha = 0.72f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun QuietDemoSection(
    onDemoLogin: (PerfilUsuario) -> Unit,
    onDarkSheet: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.auth_login_as),
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (onDarkSheet) {
                Color.White.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        if (onDarkSheet) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = IosSegmentShape,
                color = Color.White.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IosSegmentItem(
                        label = stringResource(R.string.perfil_operador),
                        onClick = { onDemoLogin(PerfilUsuario.OPERADOR) },
                        showDivider = true
                    )
                    IosSegmentItem(
                        label = stringResource(R.string.perfil_gestor),
                        onClick = { onDemoLogin(PerfilUsuario.GESTOR) },
                        showDivider = true
                    )
                    IosSegmentItem(
                        label = stringResource(R.string.perfil_lider),
                        onClick = { onDemoLogin(PerfilUsuario.LIDER) },
                        showDivider = false
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DemoProfileLink(
                    label = stringResource(R.string.perfil_operador),
                    onClick = { onDemoLogin(PerfilUsuario.OPERADOR) },
                    onDarkSheet = onDarkSheet
                )
                DemoSeparator(onDarkSheet = onDarkSheet)
                DemoProfileLink(
                    label = stringResource(R.string.perfil_gestor),
                    onClick = { onDemoLogin(PerfilUsuario.GESTOR) },
                    onDarkSheet = onDarkSheet
                )
                DemoSeparator(onDarkSheet = onDarkSheet)
                DemoProfileLink(
                    label = stringResource(R.string.perfil_lider),
                    onClick = { onDemoLogin(PerfilUsuario.LIDER) },
                    onDarkSheet = onDarkSheet
                )
            }
        }
    }
}

@Composable
private fun RowScope.IosSegmentItem(
    label: String,
    onClick: () -> Unit,
    showDivider: Boolean
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            maxLines = 1
        )
    }
    if (showDivider) {
        Box(
            modifier = Modifier
                .width(1.dp)
                .height(24.dp)
                .background(Color.White.copy(alpha = 0.2f))
        )
    }
}

@Composable
private fun DemoSeparator(onDarkSheet: Boolean = false) {
    Text(
        text = "·",
        modifier = Modifier.padding(horizontal = 6.dp),
        style = MaterialTheme.typography.labelSmall,
        color = if (onDarkSheet) {
            Color.White.copy(alpha = 0.35f)
        } else {
            MaterialTheme.colorScheme.outline
        }
    )
}

@Composable
private fun DemoProfileLink(
    label: String,
    onClick: () -> Unit,
    onDarkSheet: Boolean = false
) {
    TextButton(
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = if (onDarkSheet) {
                AzulAcao
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
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
