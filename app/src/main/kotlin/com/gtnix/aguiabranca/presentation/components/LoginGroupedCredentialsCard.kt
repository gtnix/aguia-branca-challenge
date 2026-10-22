package com.gtnix.aguiabranca.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors

private val InsetFieldShape = RoundedCornerShape(12.dp)
private val InsetFieldSpacing = 10.dp

@Composable
fun LoginGroupedCredentialsCard(
    email: String,
    senha: String,
    onEmailChange: (String) -> Unit,
    onSenhaChange: (String) -> Unit,
    onSubmitFromPassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalDarkTheme.current
    val asa = LocalInovagabColors.current.asa
    val scheme = MaterialTheme.colorScheme

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

    val fieldFill = if (isDark) {
        Color.White.copy(alpha = 0.14f)
    } else {
        scheme.surfaceVariant
    }
    val placeholderColor = if (isDark) scheme.onSurface else scheme.onSurfaceVariant
    val iconTint = if (isDark) scheme.onSurface else scheme.onSurfaceVariant

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        cursorColor = asa,
        focusedTextColor = scheme.onSurface,
        unfocusedTextColor = scheme.onSurface
    )

    Column(modifier = modifier.fillMaxWidth()) {
        InsetFieldLabel(text = emailLabel)
        InsetFieldSurface(
            focused = emailFocused,
            fill = fieldFill,
            focusColor = asa
        ) {
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

        Spacer(modifier = Modifier.height(InsetFieldSpacing))

        InsetFieldLabel(text = passwordLabel)
        InsetFieldSurface(
            focused = passwordFocused,
            fill = fieldFill,
            focusColor = asa
        ) {
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
                            contentDescription = if (passwordVisible) {
                                hidePasswordDesc
                            } else {
                                showPasswordDesc
                            },
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
private fun InsetFieldLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun InsetFieldSurface(
    focused: Boolean,
    fill: Color,
    focusColor: Color,
    content: @Composable () -> Unit
) {
    val borderColor = if (focused) {
        focusColor
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = InsetFieldShape,
        color = fill,
        border = BorderStroke(if (focused) 1.5.dp else 1.dp, borderColor)
    ) {
        content()
    }
}
