package com.gtnix.aguiabranca.presentation.util

import androidx.annotation.StringRes
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CriterioRubrica
import com.gtnix.aguiabranca.domain.model.FaixaIa
import com.gtnix.aguiabranca.domain.model.FrequenciaProblema
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusAnaliseIa

/**
 * Resumo da recomendação exibido nas listas. Mostra só a faixa, não o número,
 * para não ancorar o gestor antes de ele ler a ideia.
 */
data class SeloIa(
    val faixa: FaixaIa?,
    val status: StatusAnaliseIa
) {
    val isEstimativa: Boolean
        get() = status != StatusAnaliseIa.IA
}

/** O autor nunca vê a recomendação: evita desmotivar quem ainda espera a avaliação humana. */
fun seloIa(ideia: Ideia, perfil: PerfilUsuario?): SeloIa? {
    if (perfil != PerfilUsuario.GESTOR && perfil != PerfilUsuario.LIDER) return null
    val analise = ideia.analiseIa
    val status = analise?.status
    if (status != null) {
        return SeloIa(faixa = analise.faixa ?: ideia.scoreIa?.let(::faixaDoScore), status = status)
    }
    val score = ideia.scoreIa ?: return null
    return SeloIa(
        faixa = faixaDoScore(score),
        status = if (ideia.scoreIaFallback) StatusAnaliseIa.ESTIMATIVA else StatusAnaliseIa.IA
    )
}

/** Mesmos cortes de RubricaIdeia.faixa no backend. */
fun faixaDoScore(score: Int): FaixaIa = when {
    score >= 70 -> FaixaIa.ALTA
    score >= 40 -> FaixaIa.MEDIA
    else -> FaixaIa.BAIXA
}

@StringRes
fun SeloIa.labelRes(): Int {
    val faixaAtual = faixa
    return when {
        status == StatusAnaliseIa.DADOS_INSUFICIENTES || faixaAtual == null -> R.string.ia_selo_dados_insuficientes
        isEstimativa -> when (faixaAtual) {
            FaixaIa.ALTA -> R.string.ia_selo_estimativa_alta
            FaixaIa.MEDIA -> R.string.ia_selo_estimativa_media
            FaixaIa.BAIXA -> R.string.ia_selo_estimativa_baixa
        }
        else -> when (faixaAtual) {
            FaixaIa.ALTA -> R.string.ia_selo_ia_alta
            FaixaIa.MEDIA -> R.string.ia_selo_ia_media
            FaixaIa.BAIXA -> R.string.ia_selo_ia_baixa
        }
    }
}

@StringRes
fun FaixaIa.labelRes(): Int = when (this) {
    FaixaIa.ALTA -> R.string.ia_faixa_alta
    FaixaIa.MEDIA -> R.string.ia_faixa_media
    FaixaIa.BAIXA -> R.string.ia_faixa_baixa
}

@StringRes
fun CriterioRubrica.labelRes(): Int = when (this) {
    CriterioRubrica.ALINHAMENTO -> R.string.ia_criterio_alinhamento
    CriterioRubrica.IMPACTO -> R.string.ia_criterio_impacto
    CriterioRubrica.VIABILIDADE -> R.string.ia_criterio_viabilidade
    CriterioRubrica.CLAREZA -> R.string.ia_criterio_clareza
    CriterioRubrica.ORIGINALIDADE -> R.string.ia_criterio_originalidade
}

@StringRes
fun FrequenciaProblema.labelRes(): Int = when (this) {
    FrequenciaProblema.DIARIA -> R.string.frequencia_diaria
    FrequenciaProblema.SEMANAL -> R.string.frequencia_semanal
    FrequenciaProblema.MENSAL -> R.string.frequencia_mensal
    FrequenciaProblema.EVENTUAL -> R.string.frequencia_eventual
}
