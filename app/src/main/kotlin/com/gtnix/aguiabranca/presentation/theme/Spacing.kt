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
    val FabSpacing = 16.dp
    val BottomPadding = 12.dp
    
    val ContentBottomPaddingWithFab = PillHeight + FabSize + FabSpacing + BottomPadding + 16.dp
    val ContentBottomPaddingNoFab = PillHeight + BottomPadding + 16.dp
}

object ScreenPadding {
    val Horizontal = 20.dp
    val SectionSpacing = 24.dp
    val CardCornerRadius = 16.dp
}
