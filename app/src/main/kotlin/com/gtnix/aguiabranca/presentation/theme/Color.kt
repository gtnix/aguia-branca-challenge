package com.gtnix.aguiabranca.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta de Cores - Águia Branca Challenge
 *
 * ## Conceito FIAP - Material Design 3
 *
 * O Material 3 usa um sistema de cores baseado em:
 * - **Primary**: Cor principal da marca
 * - **Secondary**: Cor complementar
 * - **Tertiary**: Cor de destaque
 * - **Surface**: Cor de fundo de cards e containers
 * - **Background**: Cor de fundo geral
 * - **Error**: Cor para erros
 *
 * Cada cor tem variantes:
 * - `Container`: Versão mais clara para backgrounds
 * - `On[Cor]`: Cor do texto/ícone sobre essa cor
 *
 * ## Cores Corporativas Águia Branca
 *
 * Inspiradas na identidade visual da empresa:
 * - Azul: Confiança, profissionalismo
 * - Laranja: Energia, inovação
 * - Verde: Sustentabilidade (parte do challenge!)
 */

// =============================================================================
// CORES PRIMÁRIAS - Azul Corporativo
// =============================================================================

/** Azul principal - Botões, links, ações primárias */
val AguiaBrancaBlue = Color(0xFF1565C0)

/** Azul escuro - Headers, topbar */
val AguiaBrancaBlueDark = Color(0xFF0D47A1)

/** Azul claro - Container de destaque */
val AguiaBrancaBlueLight = Color(0xFFBBDEFB)

/** Cor sobre azul - Texto branco */
val OnAguiaBrancaBlue = Color(0xFFFFFFFF)

// =============================================================================
// CORES SECUNDÁRIAS - Laranja Energia
// =============================================================================

/** Laranja - FABs, ações secundárias */
val AguiaBrancaOrange = Color(0xFFFF8F00)

/** Laranja escuro */
val AguiaBrancaOrangeDark = Color(0xFFE65100)

/** Laranja claro - Container */
val AguiaBrancaOrangeLight = Color(0xFFFFE0B2)

/** Cor sobre laranja */
val OnAguiaBrancaOrange = Color(0xFF000000)

// =============================================================================
// CORES TERCIÁRIAS - Verde Sustentabilidade
// =============================================================================

/** Verde - Ações sustentáveis, sucesso */
val AguiaBrancaGreen = Color(0xFF2E7D32)

/** Verde claro - Container */
val AguiaBrancaGreenLight = Color(0xFFC8E6C9)

/** Cor sobre verde */
val OnAguiaBrancaGreen = Color(0xFFFFFFFF)

// =============================================================================
// CORES DE SUPERFÍCIE E BACKGROUND
// =============================================================================

/** Background Light */
val BackgroundLight = Color(0xFFF8F9FA)

/** Surface Light - Cards */
val SurfaceLight = Color(0xFFFFFFFF)

/** Surface Variant Light */
val SurfaceVariantLight = Color(0xFFE7E8EC)

/** Background Dark */
val BackgroundDark = Color(0xFF121212)

/** Surface Dark */
val SurfaceDark = Color(0xFF1E1E1E)

/** Surface Variant Dark */
val SurfaceVariantDark = Color(0xFF2C2C2C)

// =============================================================================
// CORES SEMÂNTICAS
// =============================================================================

/** Erro */
val ErrorColor = Color(0xFFB00020)
val ErrorColorDark = Color(0xFFCF6679)

/** Sucesso */
val SuccessColor = Color(0xFF4CAF50)

/** Warning */
val WarningColor = Color(0xFFFFC107)

/** Info */
val InfoColor = Color(0xFF2196F3)

// =============================================================================
// CORES DE STATUS (Ideias e Projetos)
// =============================================================================

/** Pendente - Cinza */
val StatusPendente = Color(0xFF9E9E9E)

/** Em Análise - Azul */
val StatusEmAnalise = Color(0xFF2196F3)

/** Aprovado - Verde */
val StatusAprovado = Color(0xFF4CAF50)

/** Reprovado - Vermelho */
val StatusReprovado = Color(0xFFF44336)

/** Em Andamento - Laranja */
val StatusEmAndamento = Color(0xFFFF9800)

/** Concluído - Verde Escuro */
val StatusConcluido = Color(0xFF2E7D32)
