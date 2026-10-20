package com.gtnix.inovagab.domain;

/**
 * Origem do score exibido ao gestor.
 * <ul>
 *   <li>{@code IA} – rubrica preenchida pelo Gemini.</li>
 *   <li>{@code ESTIMATIVA} – IA indisponível; score calculado só com impacto e esforço conhecidos.</li>
 *   <li>{@code DADOS_INSUFICIENTES} – IA indisponível e sem impacto/esforço; nenhum score é gerado.</li>
 * </ul>
 */
public enum StatusAnaliseIa {
    IA,
    ESTIMATIVA,
    DADOS_INSUFICIENTES
}
