package com.gtnix.aguiabranca.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Theme - Águia Branca Challenge
 *
 * ## Conceito FIAP - Material Design 3 Theme
 *
 * O tema Compose é configurado via `MaterialTheme`, que define:
 * - `colorScheme`: Paleta de cores (light/dark)
 * - `typography`: Estilos de texto
 * - `shapes`: Formas de componentes (arredondamentos)
 *
 * ### Estrutura Hierárquica
 *
 * ```
 * AguiaBrancaTheme
 *     │
 *     └── MaterialTheme
 *             │
 *             ├── colorScheme   → LightColors / DarkColors
 *             ├── typography    → AguiaBrancaTypography
 *             ├── shapes        → AguiaBrancaShapes
 *             └── content       → Telas do app
 * ```
 *
 * ### Uso nas Telas
 *
 * ```kotlin
 * @Composable
 * fun MinhaScreen() {
 *     // Acessa cores do tema
 *     val primaryColor = MaterialTheme.colorScheme.primary
 *
 *     // Acessa tipografia
 *     Text(
 *         text = "Título",
 *         style = MaterialTheme.typography.headlineMedium
 *     )
 * }
 * ```
 */

/**
 * Esquema de cores para modo Light.
 */
private val LightColorScheme = lightColorScheme(
    // Cores primárias
    primary = AguiaBrancaBlue,
    onPrimary = OnAguiaBrancaBlue,
    primaryContainer = AguiaBrancaBlueLight,
    onPrimaryContainer = AguiaBrancaBlueDark,

    // Cores secundárias
    secondary = AguiaBrancaOrange,
    onSecondary = OnAguiaBrancaOrange,
    secondaryContainer = AguiaBrancaOrangeLight,
    onSecondaryContainer = AguiaBrancaOrangeDark,

    // Cores terciárias
    tertiary = AguiaBrancaGreen,
    onTertiary = OnAguiaBrancaGreen,
    tertiaryContainer = AguiaBrancaGreenLight,
    onTertiaryContainer = AguiaBrancaGreen,

    // Background e Surface
    background = BackgroundLight,
    onBackground = AguiaBrancaBlueDark,
    surface = SurfaceLight,
    onSurface = AguiaBrancaBlueDark,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = AguiaBrancaBlue,

    // Erro
    error = ErrorColor,
    onError = OnAguiaBrancaBlue,
    errorContainer = ErrorColor.copy(alpha = 0.1f),
    onErrorContainer = ErrorColor,

    // Outline
    outline = AguiaBrancaBlue.copy(alpha = 0.5f),
    outlineVariant = AguiaBrancaBlue.copy(alpha = 0.2f)
)

/**
 * Esquema de cores para modo Dark.
 */
private val DarkColorScheme = darkColorScheme(
    // Cores primárias
    primary = AguiaBrancaBlueLight,
    onPrimary = AguiaBrancaBlueDark,
    primaryContainer = AguiaBrancaBlue,
    onPrimaryContainer = AguiaBrancaBlueLight,

    // Cores secundárias
    secondary = AguiaBrancaOrangeLight,
    onSecondary = AguiaBrancaOrangeDark,
    secondaryContainer = AguiaBrancaOrangeDark,
    onSecondaryContainer = AguiaBrancaOrangeLight,

    // Cores terciárias
    tertiary = AguiaBrancaGreenLight,
    onTertiary = AguiaBrancaGreen,
    tertiaryContainer = AguiaBrancaGreen,
    onTertiaryContainer = AguiaBrancaGreenLight,

    // Background e Surface
    background = BackgroundDark,
    onBackground = SurfaceLight,
    surface = SurfaceDark,
    onSurface = SurfaceLight,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = SurfaceVariantLight,

    // Erro
    error = ErrorColorDark,
    onError = BackgroundDark,
    errorContainer = ErrorColor,
    onErrorContainer = ErrorColorDark,

    // Outline
    outline = AguiaBrancaBlueLight.copy(alpha = 0.5f),
    outlineVariant = AguiaBrancaBlueLight.copy(alpha = 0.2f)
)

/**
 * Tema principal do Águia Branca Challenge.
 *
 * @param darkTheme Se true, usa tema escuro
 * @param dynamicColor Se true (Android 12+), usa cores dinâmicas do wallpaper
 * @param content Conteúdo do app
 */
@Composable
fun AguiaBrancaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color está disponível no Android 12+
    // Desabilitado por padrão para manter identidade visual
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Configura a cor da status bar
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AguiaBrancaTypography,
            shapes = AguiaBrancaShapes,
            content = content
        )
    }
}
