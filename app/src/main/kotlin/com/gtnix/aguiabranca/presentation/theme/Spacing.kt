package com.gtnix.aguiabranca.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp
)

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current

object NavBarDimensions {
    val PillHeight = 64.dp
    val FabSize = 56.dp
    val DockGap = 10.dp
    val PillPaddingBottom = 24.dp

    // Total space to reserve at the bottom of lists
    val ContentBottomPadding = PillHeight + PillPaddingBottom + 16.dp
}

object ScreenPadding {
    val Horizontal = 20.dp
    val SectionSpacing = 28.dp
}

object CardDimensions {
    val HorizontalCardWidth = 280.dp
    val HorizontalCardHeight = 180.dp
    val CompactCardWidth = 200.dp
    val CompactCardHeight = 140.dp
    /** Hero / pódio. Não usar em card de conteúdo. */
    val CornerRadiusLarge = 24.dp
    /** @deprecated Não usar em lista. Card de conteúdo = [CornerRadiusSmall]. */
    val CornerRadiusMedium = 20.dp
    /** Card de conteúdo padrão (Detalhe da ideia). */
    val CornerRadiusSmall = 16.dp
    /** Bloco interno (score, inset). Alinhado a shapes.small. */
    val CornerRadiusInset = 12.dp
    val ContentPadding = 16.dp
    val ContentBorderWidth = 1.dp
    const val ContentBorderLightAlpha = 0.4f
}
