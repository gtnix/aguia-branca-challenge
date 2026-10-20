package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.CardDimensions
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.util.bounceClick

/**
 * Superfície plana padrão das telas do líder: raio 16dp, borda hairline e sem
 * sombra nem gradiente. Todo conteúdo respeita o mesmo inset ([contentPadding]).
 */
@Composable
fun InovagabFlatCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    content: @Composable () -> Unit
) {
    val gab = LocalInovagabColors.current
    val shape = RoundedCornerShape(CardDimensions.CornerRadiusSmall)
    val container = if (LocalDarkTheme.current) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surface
    }
    Box(
        modifier = modifier
            .then(if (onClick != null) Modifier.bounceClick(scaleDown = 0.98f, onClick = onClick) else Modifier)
            .clip(shape)
            .background(container)
            .border(1.dp, gab.cardBorder, shape)
            .padding(contentPadding)
    ) {
        content()
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InovagabFlatCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            InovagabFlatCard(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Conteúdo", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
