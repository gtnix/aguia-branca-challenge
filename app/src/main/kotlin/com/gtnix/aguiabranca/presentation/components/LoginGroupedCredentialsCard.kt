package com.gtnix.aguiabranca.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R

enum class LoginCredentialsFieldStyle {
    /** Single card with divider (Material). */
    Grouped,
    /** Separate inset fields (iOS Settings / sign-in). */
    IosInset
}

private val GroupShape = RoundedCornerShape(16.dp)
private val IosFieldShape = RoundedCornerShape(12.dp)
private val IosFieldSpacing = 10.dp

@Composable
fun LoginGroupedCredentialsCard(
    email: String,
    senha: String,
    onEmailChange: (String) -> Unit,
    onSenhaChange: (String) -> Unit,
    onSubmitFromPassword: () -> Unit,
    modifier: Modifier = Modifier,
    onDarkSheet: Boolean = false,
    fieldStyle: LoginCredentialsFieldStyle = LoginCredentialsFieldStyle.Grouped
) {
    if (onDarkSheet && fieldStyle == LoginCredentialsFieldStyle.IosInset) {
        LoginIosInsetCredentials(
            email = email,
            senha = senha,
            onEmailChange = onEmailChange,
            onSenhaChange = onSenhaChange,
            onSubmitFromPassword = onSubmitFromPassword,
            modifier = modifier
        )
        return
    }

    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val passwordFocusRequester = remember { FocusRequester() }

    val groupFocused = emailFocused || passwordFocused
    val borderColor = when {
        onDarkSheet && groupFocused -> MaterialTheme.colorScheme.tertiary
        onDarkSheet -> Color.White.copy(alpha = 0.28f)
        groupFocused -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val borderWidth = if (groupFocused) 1.5.dp else 1.dp

    val emailPlaceholder = stringResource(R.string.auth_email_placeholder)
    val passwordPlaceholder = stringResource(R.string.auth_password_placeholder)
    val emailIconDesc = stringResource(R.string.cd_email_field)
    val passwordIconDesc = stringResource(R.string.cd_password_field)
    val showPasswordDesc = stringResource(R.string.cd_show_password)
    val hidePasswordDesc = stringResource(R.string.cd_hide_password)

    val fieldColors = if (onDarkSheet) {
        TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    } else {
        TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.secondary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        )
    }

    val placeholderColor = if (onDarkSheet) {
        Color.White.copy(alpha = 0.55f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val iconTint = if (onDarkSheet) {
        Color.White.copy(alpha = 0.65f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val dividerColor = if (onDarkSheet) {
        Color.White.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    val surfaceModifier = if (onDarkSheet) {
        modifier.fillMaxWidth()
    } else {
        modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = GroupShape,
                ambientColor = Color(0x14002361),
                spotColor = Color(0x1A002361)
            )
    }

    Surface(
        modifier = surfaceModifier,
        shape = GroupShape,
        color = if (onDarkSheet) {
            Color.White.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { emailFocused = it.isFocused },
                placeholder = {
                    Text(
                        text = emailPlaceholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = placeholderColor
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = emailIconDesc,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                ),
                colors = fieldColors
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = dividerColor
            )

            TextField(
                value = senha,
                onValueChange = onSenhaChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester)
                    .onFocusChanged { passwordFocused = it.isFocused },
                placeholder = {
                    Text(
                        text = passwordPlaceholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = placeholderColor
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = passwordIconDesc,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (passwordVisible) hidePasswordDesc else showPasswordDesc,
                            tint = iconTint
                        )
                    }
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onSubmitFromPassword() }),
                colors = fieldColors
            )
        }
    }
}

@Composable
private fun LoginIosInsetCredentials(
    email: String,
    senha: String,
    onEmailChange: (String) -> Unit,
    onSenhaChange: (String) -> Unit,
    onSubmitFromPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    var emailFocused by remember { mutableStateOf(false) }
    var passwordFocused by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    val passwordFocusRequester = remember { FocusRequester() }

    val emailLabel = stringResource(R.string.auth_email_label)
    val passwordLabel = stringResource(R.string.auth_password_label)
    val emailPlaceholder = stringResource(R.string.auth_email_placeholder)
    val passwordPlaceholder = stringResource(R.string.auth_password_placeholder)
    val showPasswordDesc = stringResource(R.string.cd_show_password)
    val hidePasswordDesc = stringResource(R.string.cd_hide_password)

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        cursorColor = MaterialTheme.colorScheme.tertiary,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
    )
    val placeholderColor = Color.White.copy(alpha = 0.5f)

    Column(modifier = modifier.fillMaxWidth()) {
        IosInsetFieldLabel(text = emailLabel)
        IosInsetFieldSurface(focused = emailFocused) {
            TextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .onFocusChanged { emailFocused = it.isFocused },
                placeholder = {
                    Text(
                        text = emailPlaceholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = placeholderColor
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                ),
                colors = fieldColors
            )
        }

        Spacer(modifier = Modifier.height(IosFieldSpacing))

        IosInsetFieldLabel(text = passwordLabel)
        IosInsetFieldSurface(focused = passwordFocused) {
            TextField(
                value = senha,
                onValueChange = onSenhaChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .focusRequester(passwordFocusRequester)
                    .onFocusChanged { passwordFocused = it.isFocused },
                placeholder = {
                    Text(
                        text = passwordPlaceholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = placeholderColor
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (passwordVisible) hidePasswordDesc else showPasswordDesc,
                            tint = Color.White.copy(alpha = 0.55f)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onSubmitFromPassword() }),
                colors = fieldColors
            )
        }
    }
}

@Composable
private fun IosInsetFieldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        style = MaterialTheme.typography.labelMedium,
        color = Color.White.copy(alpha = 0.72f)
    )
}

@Composable
private fun IosInsetFieldSurface(
    focused: Boolean,
    content: @Composable () -> Unit
) {
    val borderColor = if (focused) {
        MaterialTheme.colorScheme.tertiary
    } else {
        Color.White.copy(alpha = 0.22f)
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = IosFieldShape,
        color = Color.White.copy(alpha = 0.14f),
        border = BorderStroke(if (focused) 1.5.dp else 1.dp, borderColor)
    ) {
        content()
    }
}
