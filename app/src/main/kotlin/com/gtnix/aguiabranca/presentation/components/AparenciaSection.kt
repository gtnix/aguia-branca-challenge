package com.gtnix.aguiabranca.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LocalDarkTheme
import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import com.gtnix.aguiabranca.presentation.theme.TemaPreferencia
import com.gtnix.aguiabranca.presentation.theme.spacing

private data class AparenciaOpcao(
    val tema: TemaPreferencia,
    val labelRes: Int,
    val contentDescriptionRes: Int,
    val icon: ImageVector
)

private val opcoes = listOf(
    AparenciaOpcao(
        TemaPreferencia.CLARO,
        R.string.aparencia_claro,
        R.string.cd_aparencia_claro,
        Icons.Filled.LightMode
    ),
    AparenciaOpcao(
        TemaPreferencia.ESCURO,
        R.string.aparencia_escuro,
        R.string.cd_aparencia_escuro,
        Icons.Filled.DarkMode
    ),
    AparenciaOpcao(
        TemaPreferencia.SISTEMA,
        R.string.aparencia_sistema,
        R.string.cd_aparencia_sistema,
        Icons.Filled.PhoneAndroid
    )
)

@Composable
fun AparenciaSection(
    tema: TemaPreferencia,
    onTemaChange: (TemaPreferencia) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = MaterialTheme.spacing
    val isDark = LocalDarkTheme.current
    val borda = LocalInovagabColors.current.cardBorder
    val colors = SegmentedButtonDefaults.colors(
        activeContainerColor = if (isDark) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        },
        activeContentColor = if (isDark) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
        activeBorderColor = borda,
        inactiveContainerColor = if (isDark) {
            MaterialTheme.colorScheme.surfaceContainerHigh
        } else {
            MaterialTheme.colorScheme.surface
        },
        inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        inactiveBorderColor = borda
    )
    PerfilSubsectionHeader(title = stringResource(R.string.aparencia_titulo))
    Spacer(modifier = Modifier.height(spacing.medium))
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        opcoes.forEachIndexed { index, opcao ->
            SegmentedButton(
                selected = tema == opcao.tema,
                onClick = { onTemaChange(opcao.tema) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = opcoes.size),
                colors = colors,
                icon = {
                    Icon(
                        imageVector = opcao.icon,
                        contentDescription = stringResource(opcao.contentDescriptionRes)
                    )
                },
                label = {
                    Text(text = stringResource(opcao.labelRes))
                }
            )
        }
    }
}

@Preview
@Composable
private fun AparenciaSectionLightPreview() {
    InovagabTheme(darkTheme = false) {
        AparenciaSection(tema = TemaPreferencia.SISTEMA, onTemaChange = {})
    }
}

@Preview
@Composable
private fun AparenciaSectionDarkPreview() {
    InovagabTheme(darkTheme = true) {
        AparenciaSection(tema = TemaPreferencia.ESCURO, onTemaChange = {})
    }
}
