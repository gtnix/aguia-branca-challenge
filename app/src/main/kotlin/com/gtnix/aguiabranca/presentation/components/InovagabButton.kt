package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.SuccessGreen

enum class InovagabButtonVariant {
    Primary,
    Secondary,
    Success,
    Ghost,
    Neutral
}

@Composable
fun InovagabButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: InovagabButtonVariant = InovagabButtonVariant.Primary,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 400f
        ),
        label = "buttonScale"
    )
    
    val buttonShape = RoundedCornerShape(16.dp)
    
    val buttonModifier = modifier
        .defaultMinSize(minHeight = 48.dp)
        .scale(scale)
    
    when (variant) {
        InovagabButtonVariant.Primary -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                interactionSource = interactionSource
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }
        
        InovagabButtonVariant.Secondary -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = buttonShape,
                border = BorderStroke(
                    width = 1.5.dp,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.38f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                    disabledContentColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                interactionSource = interactionSource
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }

        InovagabButtonVariant.Success -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = buttonShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessGreen,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = SuccessGreen.copy(alpha = 0.38f),
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                interactionSource = interactionSource
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }
        
        InovagabButtonVariant.Ghost -> {
            TextButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = buttonShape,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.secondary,
                    disabledContentColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                interactionSource = interactionSource
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }
        
        InovagabButtonVariant.Neutral -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = buttonShape,
                border = BorderStroke(
                    width = 1.5.dp,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.outline
                    } else {
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
                    }
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                interactionSource = interactionSource
            ) {
                ButtonContent(text = text, leadingIcon = leadingIcon)
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    leadingIcon: ImageVector?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InovagabButtonPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InovagabButton(
                    text = "Primary Button",
                    onClick = {},
                    variant = InovagabButtonVariant.Primary,
                    leadingIcon = Icons.Default.Add
                )
                
                InovagabButton(
                    text = "Secondary Button",
                    onClick = {},
                    variant = InovagabButtonVariant.Secondary,
                    leadingIcon = Icons.Default.Send
                )
                
                InovagabButton(
                    text = "Ghost Button",
                    onClick = {},
                    variant = InovagabButtonVariant.Ghost
                )
                
                InovagabButton(
                    text = "Neutral Button",
                    onClick = {},
                    variant = InovagabButtonVariant.Neutral
                )
                
                InovagabButton(
                    text = "Disabled Primary",
                    onClick = {},
                    variant = InovagabButtonVariant.Primary,
                    enabled = false
                )
                
                InovagabButton(
                    text = "Disabled Neutral",
                    onClick = {},
                    variant = InovagabButtonVariant.Neutral,
                    enabled = false
                )
            }
        }
    }
}
