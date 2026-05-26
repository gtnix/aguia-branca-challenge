package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.util.bounceClick

@Composable
fun PremiumFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(durationMillis = 200),
        label = "containerColor"
    )
    
    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(durationMillis = 200),
        label = "contentColor"
    )

    Surface(
        modifier = modifier.bounceClick(onClick = onClick),
        shape = RoundedCornerShape(100),
        color = containerColor
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PremiumFilterChipPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            var selectedIndex by remember { mutableStateOf(0) }
            val filters = listOf("Todas", "Pendentes", "Aprovadas", "Em Projeto")
            
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEachIndexed { index, label ->
                    PremiumFilterChip(
                        label = label,
                        selected = index == selectedIndex,
                        onClick = { selectedIndex = index }
                    )
                }
            }
        }
    }
}
