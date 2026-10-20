package com.gtnix.aguiabranca.domain.model

/** Recomendação da IA para o gestor. Nunca chega ao autor: o backend omite estes campos para OPERADOR. */
data class AnaliseIa(
    /** Nulo quando o gestor salvou a estimativa antes de qualquer análise. */
    val status: StatusAnaliseIa? = null,
    val faixa: FaixaIa? = null,
    val criterios: List<CriterioIa> = emptyList(),
    val riscos: List<String> = emptyList(),
    val perguntas: List<String> = emptyList(),
    val impactoSugerido: Int? = null,
    val esforcoSugerido: Int? = null,
    val estimativaOrigem: OrigemEstimativa? = null,
    val motivoAjusteEstimativa: String? = null,
    val modelo: String? = null,
    val concordaIa: Boolean? = null,
    val motivoDiscordanciaIa: String? = null
)

enum class StatusAnaliseIa {
    /** Rubrica preenchida pelo Gemini. */
    IA,
    /** IA indisponível: score só com impacto e esforço. */
    ESTIMATIVA,
    /** IA indisponível e sem impacto/esforço: sem score. */
    DADOS_INSUFICIENTES
}

enum class FaixaIa {
    ALTA,
    MEDIA,
    BAIXA
}

enum class OrigemEstimativa {
    IA,
    GESTOR
}

enum class CriterioRubrica {
    ALINHAMENTO,
    IMPACTO,
    VIABILIDADE,
    CLAREZA,
    ORIGINALIDADE
}

data class CriterioIa(
    val criterio: CriterioRubrica,
    val nota: Int,
    val peso: Int,
    val evidencia: String
)

enum class FrequenciaProblema {
    DIARIA,
    SEMANAL,
    MENSAL,
    EVENTUAL
}

data class RascunhoFeedback(
    val texto: String,
    val fallback: Boolean
)
