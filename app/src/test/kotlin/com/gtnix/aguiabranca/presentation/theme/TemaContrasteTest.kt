package com.gtnix.aguiabranca.presentation.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TemaContrasteTest {

    @Test
    fun resolvePreferencia() {
        assertFalse(resolveDarkTheme(TemaPreferencia.CLARO, systemDark = true))
        assertTrue(resolveDarkTheme(TemaPreferencia.ESCURO, systemDark = false))
        assertTrue(resolveDarkTheme(TemaPreferencia.SISTEMA, systemDark = true))
        assertFalse(resolveDarkTheme(TemaPreferencia.SISTEMA, systemDark = false))
    }

    @Test
    fun contrasteTextoEBotao() {
        assertTrue(contrastRatio(LightOnPrimary, LightPrimary) >= 4.5)
        assertTrue(contrastRatio(LightOnSurface, LightBackground) >= 4.5)
        assertTrue(contrastRatio(LightOnSurface, LightSurface) >= 4.5)
        assertTrue(contrastRatio(LightOnSurfaceVariant, LightBackground) >= 4.5)
        assertTrue(contrastRatio(LightOnSecondary, LightSecondary) >= 4.5)

        assertTrue(contrastRatio(DarkOnPrimary, DarkPrimary) >= 4.5)
        assertTrue(contrastRatio(Color.White, DarkPrimary) >= 4.5)
        assertTrue(contrastRatio(Color.White, AzulAcao) < 4.5)
        assertTrue(contrastRatio(DarkOnSurface, DarkBackground) >= 4.5)
        assertTrue(contrastRatio(DarkOnSurface, DarkSurface) >= 4.5)
        assertTrue(contrastRatio(DarkOnSurfaceVariant, DarkSurface) >= 4.5)
        assertTrue(contrastRatio(DarkOnSecondary, DarkSecondary) >= 4.5)
        assertTrue(contrastRatio(DarkOnTertiary, DarkTertiary) >= 4.5)
        assertTrue(contrastRatio(Color.White, DarkTertiary) < 4.5)
    }

    @Test
    fun contrasteBordaEBotaoSecundarioEscuro() {
        val outlineSobreSurface = compositeOver(NoiteOutline, DarkSurface)
        assertTrue(contrastRatio(outlineSobreSurface, DarkSurface) >= 3.0)
        assertTrue(contrastRatio(DarkOnSurface, DarkSurface) >= 4.5)
    }
}
