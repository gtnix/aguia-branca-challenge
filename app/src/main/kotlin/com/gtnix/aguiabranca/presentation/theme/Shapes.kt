package com.gtnix.aguiabranca.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shapes - Águia Branca Design System
 *
 * ## Conceito FIAP - Material Design 3 Shapes
 *
 * O sistema de shapes define os arredondamentos padrão dos componentes.
 * Usar tokens de shapes garante consistência visual em todo o app.
 *
 * ### Escala de Shapes M3
 *
 * | Token       | Valor  | Uso típico                          |
 * |-------------|--------|-------------------------------------|
 * | extraSmall  | 4dp    | Chips, pequenos badges              |
 * | small       | 8dp    | TextFields, botões pequenos         |
 * | medium      | 12dp   | Cards, FABs                         |
 * | large       | 16dp   | Dialogs, bottom sheets              |
 * | extraLarge  | 24dp   | Modais grandes, containers hero     |
 *
 * ### Uso nas Telas
 *
 * ```kotlin
 * @Composable
 * fun MeuCard() {
 *     Card(
 *         shape = MaterialTheme.shapes.medium // 12dp
 *     ) {
 *         // conteúdo
 *     }
 * }
 *
 * // Para TextFields
 * OutlinedTextField(
 *     shape = MaterialTheme.shapes.small // 8dp
 * )
 * ```
 */
val AguiaBrancaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
