package com.gtnix.aguiabranca.presentation.util

import androidx.annotation.StringRes
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao

@StringRes
fun AreaAtuacao.labelRes(): Int = when (this) {
    AreaAtuacao.OPERACOES -> R.string.area_operacoes
    AreaAtuacao.LOGISTICA -> R.string.area_logistica
    AreaAtuacao.COMERCIAL -> R.string.area_comercial
    AreaAtuacao.FINANCEIRO -> R.string.area_financeiro
    AreaAtuacao.RH -> R.string.area_rh
    AreaAtuacao.TI -> R.string.area_ti
    AreaAtuacao.MARKETING -> R.string.area_marketing
    AreaAtuacao.QUALIDADE -> R.string.area_qualidade
}
