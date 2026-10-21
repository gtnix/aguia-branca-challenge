package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.CardDimensions
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.util.bounceClick

/**
 * Card de conteúdo padrão (visual do Detalhe da ideia): raio 16.dp, fill [surface],
 * sem sombra. No escuro a borda nasce do contraste com o fundo; no claro, hairline
 * [outlineVariant] a 40%.
 */
@Composable
fun InovagabFlatCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(CardDimensions.ContentPadding),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(CardDimensions.CornerRadiusSmall)
    Box(
        modifier = modifier
            .then(if (onClick != null) Modifier.bounceClick(scaleDown = 0.98f, onClick = onClick) else Modifier)
            .clip(shape)
            .background(containerColor)
            .inovagabContentCardBorder(shape)
            .padding(contentPadding)
    ) {
        content()
    }
}

/**
 * Bloco interno de 12.dp (score, destaque). Sem stroke; fill primary 15% no escuro
 * e primaryContainer no claro.
 */
@Composable
fun InovagabInsetSurface(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(CardDimensions.ContentPadding),
    containerColor: Color = inovagabInsetContainerColor(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(CardDimensions.CornerRadiusInset)
    Column(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .padding(contentPadding),
        verticalArrangement = verticalArrangement,
        content = content
    )
}

@Composable
fun inovagabInsetContainerColor(): Color {
    return if (LocalDarkTheme.current) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }
}

@Composable
fun inovagabContentCardBorderColor(): Color? {
    return if (LocalDarkTheme.current) {
        null
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(
            alpha = CardDimensions.ContentBorderLightAlpha
        )
    }
}

@Composable
fun Modifier.inovagabContentCardBorder(shape: Shape): Modifier {
    val borderColor = inovagabContentCardBorderColor()
    return if (borderColor != null) {
        border(CardDimensions.ContentBorderWidth, borderColor, shape)
    } else {
        this
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InovagabFlatCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            InovagabFlatCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column {
                    Text("Conteúdo", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    InovagabInsetSurface(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Text("Inset", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
