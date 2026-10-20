package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.data.util.safeValueOf
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Projeto
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun ProjetoDto.toDomain(): Projeto {
    val investimento = investimento ?: 0.0
    val retornoMensal = retorno ?: 0.0
    val criado = parseIsoToEpoch(dataCriacao)
    val atualizado = parseIsoToEpoch(dataAtualizacao)
    val statusDominio = safeValueOf(status.orEmpty(), StatusProjeto.PLANEJADO)
    val concluido = statusDominio == StatusProjeto.CONCLUIDO
    val emAndamento = statusDominio == StatusProjeto.EM_ANDAMENTO
    return Projeto(
        id = id,
        nome = titulo,
        objetivo = objetivo.orEmpty().ifBlank { descricao.orEmpty() },
        descricao = descricao.orEmpty(),
        area = safeValueOf(area.orEmpty(), AreaAtuacao.OPERACOES),
        status = statusDominio,
        ideiaOrigemId = ideiaId,
        orientacaoId = orientacaoId,
        responsavelId = responsavelUserId.orEmpty(),
        responsavelNome = responsavelUserId.orEmpty(),
        dataCriacao = criado,
        dataInicio = if (emAndamento || concluido) atualizado else null,
        dataPrevistaConclusao = parseDateToEpoch(prazo),
        dataConclusao = if (concluido) atualizado else null,
        progresso = (progressoPercentual ?: 0).coerceIn(0, 100),
        resultados = resultadosObtidos,
        investimentoEstimado = investimento,
        investimentoRealizado = investimento,
        retornoEstimadoMensal = retornoMensal,
        retornoRealizadoMensal = retornoMensal,
        roiServidor = roi
    )
}

fun Projeto.toRequestDto(): ProjetoRequestDto = ProjetoRequestDto(
    titulo = nome,
    objetivo = objetivo,
    descricao = descricao,
    area = area.name,
    orientacaoId = requireNotNull(orientacaoId?.takeIf { it.isNotBlank() }) {
        "orientacaoId obrigatório"
    },
    ideiaId = ideiaOrigemId?.takeIf { it.isNotBlank() },
    etapa = status.name,
    status = status.name,
    investimento = investimentoParaApi(),
    retorno = retornoParaApi(),
    prazo = epochToDate(dataPrevistaConclusao),
    progressoPercentual = progresso.coerceIn(0, 100),
    resultadosObtidos = resultados
)

private fun Projeto.investimentoParaApi(): Double? {
    val valor = if (investimentoRealizado > 0) investimentoRealizado else investimentoEstimado
    return valor.takeIf { it > 0 }
}

private fun Projeto.retornoParaApi(): Double? {
    val valor = if (retornoRealizadoMensal > 0) retornoRealizadoMensal else retornoEstimadoMensal
    return valor.takeIf { it > 0 }
}

private fun parseIsoToEpoch(value: String?): Long {
    if (value.isNullOrBlank()) return System.currentTimeMillis()
    return runCatching { Instant.parse(value).toEpochMilli() }.getOrDefault(System.currentTimeMillis())
}

private fun parseDateToEpoch(value: String?): Long? {
    if (value.isNullOrBlank()) return null
    return runCatching {
        LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }.getOrNull()
}

private fun epochToDate(epochMillis: Long?): String? {
    if (epochMillis == null) return null
    return Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
        .format(DateTimeFormatter.ISO_LOCAL_DATE)
}
