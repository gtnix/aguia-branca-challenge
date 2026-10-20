package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.data.util.safeValueOf
import com.gtnix.aguiabranca.domain.model.AnaliseIa
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.CriterioIa
import com.gtnix.aguiabranca.domain.model.CriterioRubrica
import com.gtnix.aguiabranca.domain.model.FaixaIa
import com.gtnix.aguiabranca.domain.model.FrequenciaProblema
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrigemEstimativa
import com.gtnix.aguiabranca.domain.model.RankingIdeia
import com.gtnix.aguiabranca.domain.model.RascunhoFeedback
import com.gtnix.aguiabranca.domain.model.ScoreIdeiasResult
import com.gtnix.aguiabranca.domain.model.StatusAnaliseIa
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import java.time.Instant

fun IdeiaDto.toDomain(): Ideia {
    val criado = parseIsoToEpoch(dataCriacao)
    val statusDominio = safeValueOf(status.orEmpty(), StatusIdeia.PENDENTE)
    val avaliada = statusDominio == StatusIdeia.APROVADA || statusDominio == StatusIdeia.REPROVADA
    return Ideia(
        id = id,
        titulo = titulo,
        descricao = descricao.orEmpty(),
        tipo = safeValueOf(tipo.orEmpty(), TipoIdeia.IDEIA),
        area = safeValueOf(area.orEmpty(), AreaAtuacao.OPERACOES),
        status = statusDominio,
        autorId = autorUserId.orEmpty(),
        autorNome = autorNome?.takeIf { it.isNotBlank() } ?: autorUserId.orEmpty(),
        orientacaoId = orientacaoId,
        feedback = feedbackReprovacao,
        dataCriacao = criado,
        dataAvaliacao = if (avaliada) parseIsoToEpoch(dataAtualizacao) else null,
        impactoEstimado = impacto ?: 0,
        esforcoEstimado = esforco ?: 0,
        prioridade = prioridade,
        scoreIa = scoreIa,
        justificativaIa = justificativaIa,
        scoreIaFallback = scoreIaFallback == true,
        upvotes = apoios,
        temSimilaridade = duplicadaDe != null,
        avaliadorId = avaliadorUserId,
        feedbackUtil = feedbackUtil,
        problema = problema?.takeIf { it.isNotBlank() },
        publicoAfetado = publicoAfetado?.takeIf { it.isNotBlank() },
        frequencia = frequencia?.let { runCatching { FrequenciaProblema.valueOf(it) }.getOrNull() },
        beneficio = beneficio?.takeIf { it.isNotBlank() },
        analiseIa = analiseIa?.toDomain()
    )
}

fun AnaliseIaDto.toDomain(): AnaliseIa {
    return AnaliseIa(
        status = status?.let { runCatching { StatusAnaliseIa.valueOf(it) }.getOrNull() },
        faixa = faixa?.let { runCatching { FaixaIa.valueOf(it) }.getOrNull() },
        criterios = criterios.mapNotNull { dto ->
            val criterio = runCatching { CriterioRubrica.valueOf(dto.criterio) }.getOrNull()
                ?: return@mapNotNull null
            CriterioIa(criterio, dto.nota, dto.peso, dto.evidencia.orEmpty())
        },
        riscos = riscos,
        perguntas = perguntas,
        impactoSugerido = impactoSugerido,
        esforcoSugerido = esforcoSugerido,
        estimativaOrigem = estimativaOrigem?.let { runCatching { OrigemEstimativa.valueOf(it) }.getOrNull() },
        motivoAjusteEstimativa = motivoAjusteEstimativa,
        modelo = modelo,
        concordaIa = concordaIa,
        motivoDiscordanciaIa = motivoDiscordanciaIa
    )
}

fun Ideia.toRequestDto(): IdeiaRequestDto = IdeiaRequestDto(
    titulo = titulo,
    descricao = descricao,
    tipo = tipo.name,
    area = area.name,
    orientacaoId = requireNotNull(orientacaoId?.takeIf { it.isNotBlank() }) {
        "orientacaoId obrigatório"
    },
    problema = problema?.takeIf { it.isNotBlank() },
    publicoAfetado = publicoAfetado?.takeIf { it.isNotBlank() },
    frequencia = frequencia?.name,
    beneficio = beneficio?.takeIf { it.isNotBlank() }
)

fun ScoreIdeasResponseDto.toDomain(): ScoreIdeiasResult = ScoreIdeiasResult(
    ranking = ranking.map {
        RankingIdeia(
            ideiaId = it.ideiaId,
            score = it.score,
            justificativa = it.justificativa.orEmpty(),
            status = it.status?.let { s -> runCatching { StatusAnaliseIa.valueOf(s) }.getOrNull() }
                ?: StatusAnaliseIa.IA
        )
    },
    modelo = modelo.orEmpty(),
    fallback = fallback
)

fun RascunhoFeedbackResponseDto.toDomain(): RascunhoFeedback = RascunhoFeedback(
    texto = rascunho,
    fallback = fallback
)

private fun parseIsoToEpoch(value: String?): Long {
    if (value.isNullOrBlank()) return System.currentTimeMillis()
    return runCatching { Instant.parse(value).toEpochMilli() }.getOrDefault(System.currentTimeMillis())
}
