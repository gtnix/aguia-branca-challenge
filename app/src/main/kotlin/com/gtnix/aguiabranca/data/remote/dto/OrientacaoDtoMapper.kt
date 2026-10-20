package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.data.util.safeValueOf
import com.gtnix.aguiabranca.domain.model.CampanhaPatrocinada
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import java.time.Instant
import java.time.LocalDate

fun OrientacaoDto.toDomain(): OrientacaoEstrategica {
    val campanhaValor = campanha.orEmpty()
    return OrientacaoEstrategica(
        id = id,
        titulo = titulo,
        descricao = descricao.orEmpty(),
        categoria = safeValueOf(categoria, CategoriaOrientacao.EFICIENCIA_OPERACIONAL),
        prioridade = parsePrioridade(campanhaValor),
        ativa = ativo,
        criadoPor = criadoPorUserId.orEmpty(),
        dataCriacao = parseIsoToEpoch(dataCriacao),
        campanha = campanhaValor,
        campanhaPatrocinada = campanhaFim
            ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            ?.let { fim ->
                CampanhaPatrocinada(
                    fim = fim,
                    bonusAsas = campanhaBonusAsas ?: 0,
                    topN = campanhaTopN ?: 0,
                    ativa = campanhaAtiva,
                    encerrada = campanhaEncerradaEm != null
                )
            },
        divisao = divisao?.let { valor -> DivisaoNegocio.entries.firstOrNull { it.name == valor } }
    )
}

fun OrientacaoEstrategica.toRequestDto(): OrientacaoRequestDto = OrientacaoRequestDto(
    titulo = titulo,
    descricao = descricao,
    categoria = categoria.name,
    campanha = campanha.ifBlank { "P$prioridade" },
    campanhaFim = campanhaPatrocinada?.fim?.toString(),
    campanhaBonusAsas = campanhaPatrocinada?.bonusAsas,
    campanhaTopN = campanhaPatrocinada?.topN,
    divisao = divisao?.name
)

private fun parseIsoToEpoch(value: String?): Long {
    if (value.isNullOrBlank()) return System.currentTimeMillis()
    return runCatching { Instant.parse(value).toEpochMilli() }.getOrDefault(System.currentTimeMillis())
}

private fun parsePrioridade(campanha: String): Int {
    val match = Regex("""P([1-5])""").find(campanha)
    return match?.groupValues?.get(1)?.toIntOrNull() ?: 3
}
