package com.gtnix.aguiabranca.presentation.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Glifo autoral da moeda Asas: asa clássica em três planos preenchidos,
 * ponta aguda e ombro recortado. Não é o recorte da holding.
 */
object AsasIcons {
    val Wings: ImageVector
        get() {
            val cached = _wings
            if (cached != null) return cached
            val vector = ImageVector.Builder(
                name = "AsasWings",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
                path(fill = SolidColor(Color.Black)) {
                    // Plano 1 — covert: ombro recortado e ponta
                    moveTo(4.8f, 21.8f)
                    curveTo(3.4f, 18.8f, 3.2f, 14.6f, 4.8f, 11.2f)
                    curveTo(6.4f, 7.6f, 10.4f, 4.2f, 15.2f, 2.4f)
                    curveTo(18.2f, 1.4f, 21.2f, 1.2f, 22.6f, 2.2f)
                    curveTo(23f, 3f, 22.2f, 3.8f, 20.8f, 4.4f)
                    curveTo(17.6f, 5.8f, 14.4f, 7.6f, 11.8f, 9.8f)
                    curveTo(10.6f, 10.8f, 9.8f, 12f, 9.2f, 13.2f)
                    curveTo(8.2f, 12.4f, 7f, 12.6f, 6.6f, 13.8f)
                    curveTo(5.8f, 13.6f, 5.2f, 14.6f, 5.4f, 15.8f)
                    curveTo(4.8f, 16f, 4.8f, 17.4f, 5.6f, 18.6f)
                    curveTo(5.6f, 19.8f, 5.4f, 21f, 4.8f, 21.8f)
                    close()
                    // Plano 2 — camada média
                    moveTo(9.8f, 12.8f)
                    curveTo(13f, 10.4f, 16.8f, 8.2f, 20.2f, 6.8f)
                    curveTo(21.2f, 6.4f, 21.6f, 7.6f, 20.6f, 8.4f)
                    curveTo(17.6f, 10.2f, 14.2f, 12.2f, 11f, 14f)
                    curveTo(10f, 14.6f, 9f, 13.8f, 9.8f, 12.8f)
                    close()
                    // Plano 3 — primárias
                    moveTo(8.6f, 16.2f)
                    curveTo(12.2f, 13.6f, 16.2f, 11.4f, 19.2f, 10f)
                    curveTo(20.2f, 9.6f, 20.6f, 10.8f, 19.6f, 11.6f)
                    curveTo(16.8f, 13.6f, 13.2f, 15.8f, 9.8f, 17.8f)
                    curveTo(8.6f, 18.6f, 7.8f, 17.2f, 8.6f, 16.2f)
                    close()
                }
            }.build()
            _wings = vector
            return vector
        }
}

private var _wings: ImageVector? = null
