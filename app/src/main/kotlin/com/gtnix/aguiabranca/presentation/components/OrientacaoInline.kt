package com.gtnix.aguiabranca.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * OrientacaoInline - Componente para dicas e orientações inline em formulários.
 *
 * Exibe uma dica visual não-colapsável para auxiliar o usuário no preenchimento.
 * Ideal para formulários onde o contexto é importante.
 *
 * @param texto Texto da orientação/dica
 * @param modifier Modificador opcional
 * @param icon Ícone da dica (padrão: Lightbulb)
 * @param variant Variante visual: TIP para dicas, INFO para informações
 */
@Composable
fun OrientacaoInline(
    texto: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Lightbulb,
    variant: OrientacaoVariant = OrientacaoVariant.TIP
) {
    val containerColor = when (variant) {
        OrientacaoVariant.TIP -> MaterialTheme.colorScheme.secondaryContainer
        OrientacaoVariant.INFO -> MaterialTheme.colorScheme.primaryContainer
    }
    val contentColor = when (variant) {
        OrientacaoVariant.TIP -> MaterialTheme.colorScheme.onSecondaryContainer
        OrientacaoVariant.INFO -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = texto,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor
            )
        }
    }
}

/**
 * Variantes visuais para OrientacaoInline.
 */
enum class OrientacaoVariant {
    TIP,
    INFO
}
