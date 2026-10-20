package com.gtnix.aguiabranca.presentation.components.badges

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.Conquista

enum class BadgeGridLayout {
    GRID,
    HORIZONTAL
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BadgeGrid(
    conquistas: List<Conquista>,
    modifier: Modifier = Modifier,
    layout: BadgeGridLayout = BadgeGridLayout.GRID,
    maxItemsInEachRow: Int = 3,
    badgeSize: Dp = 64.dp,
    showLabel: Boolean = true,
    newlyUnlockedIds: Set<String> = emptySet(),
    onBadgeClick: ((Conquista) -> Unit)? = null
) {
    when (layout) {
        BadgeGridLayout.GRID -> {
            FlowRow(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                maxItemsInEachRow = maxItemsInEachRow
            ) {
                conquistas.forEach { conquista ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        BadgeItem(
                            conquista = conquista,
                            size = badgeSize,
                            showLabel = showLabel,
                            isNewlyUnlocked = conquista.id in newlyUnlockedIds,
                            onClick = onBadgeClick?.let { { it(conquista) } }
                        )
                    }
                }
            }
        }

        BadgeGridLayout.HORIZONTAL -> {
            LazyRow(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 4.dp)
            ) {
                items(conquistas, key = { it.id }) { conquista ->
                    BadgeItem(
                        conquista = conquista,
                        size = badgeSize,
                        showLabel = showLabel,
                        isNewlyUnlocked = conquista.id in newlyUnlockedIds,
                        onClick = onBadgeClick?.let { { it(conquista) } }
                    )
                }
            }
        }
    }
}
