package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.presentation.components.PremiumFilterChip
import com.gtnix.aguiabranca.presentation.util.orientacaoDivisaoLabel

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DivisaoSelector(
    selected: DivisaoNegocio?,
    onSelect: (DivisaoNegocio?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Divisão",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (listOf<DivisaoNegocio?>(null) + DivisaoNegocio.entries).forEach { divisao ->
                PremiumFilterChip(
                    label = orientacaoDivisaoLabel(divisao),
                    selected = selected == divisao,
                    onClick = { onSelect(divisao) }
                )
            }
        }
    }
}
