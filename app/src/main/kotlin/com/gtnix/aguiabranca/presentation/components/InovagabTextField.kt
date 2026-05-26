package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

@Composable
fun InovagabTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    trailingIconClick: (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> Color.Transparent
        },
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "borderColor"
    )
    
    val borderWidth by animateFloatAsState(
        targetValue = if (isFocused || isError) 2f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "borderWidth"
    )
    
    val shape = MaterialTheme.shapes.small
    
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = borderWidth.dp,
                    color = borderColor,
                    shape = shape
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = if (leadingIcon != null) 48.dp else 16.dp,
                        end = if (trailingIcon != null) 48.dp else 16.dp,
                        top = 12.dp,
                        bottom = 12.dp
                    )
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            isError -> MaterialTheme.colorScheme.error
                            isFocused -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        },
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                val scrollState = rememberScrollState()
                
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .then(
                            if (!singleLine) Modifier.verticalScroll(scrollState) else Modifier
                        ),
                    enabled = enabled,
                    singleLine = singleLine,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = keyboardOptions,
                    keyboardActions = keyboardActions,
                    visualTransformation = visualTransformation,
                    interactionSource = interactionSource,
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (value.isEmpty() && placeholder != null) {
                                Text(
                                    text = placeholder,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 12.dp)
                        .size(24.dp),
                    tint = if (isFocused) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            if (trailingIcon != null) {
                if (trailingIconClick != null) {
                    IconButton(
                        onClick = trailingIconClick,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                            .size(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        if (isError) {
            Text(
                text = errorMessage ?: stringResource(R.string.validation_required_field),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InovagabTextFieldPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                var text1 by remember { mutableStateOf("") }
                InovagabTextField(
                    value = text1,
                    onValueChange = { text1 = it },
                    label = "Email",
                    placeholder = "Digite seu email",
                    leadingIcon = Icons.Default.Email
                )
                
                var text2 by remember { mutableStateOf("john@example.com") }
                InovagabTextField(
                    value = text2,
                    onValueChange = { text2 = it },
                    label = "Email preenchido",
                    leadingIcon = Icons.Default.Email
                )
                
                var text3 by remember { mutableStateOf("") }
                InovagabTextField(
                    value = text3,
                    onValueChange = { text3 = it },
                    placeholder = "Buscar ideias...",
                    leadingIcon = Icons.Default.Search
                )
                
                var text4 by remember { mutableStateOf("") }
                var showPassword by remember { mutableStateOf(false) }
                InovagabTextField(
                    value = text4,
                    onValueChange = { text4 = it },
                    label = "Senha",
                    trailingIcon = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    trailingIconClick = { showPassword = !showPassword }
                )
                
                var text5 by remember { mutableStateOf("") }
                InovagabTextField(
                    value = text5,
                    onValueChange = { text5 = it },
                    label = "Campo com erro",
                    isError = true
                )
            }
        }
    }
}
