package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.domain.model.DashboardInsight

data class DashboardNarrativeDto(
    val titulo: String = "",
    val resumo: String = "",
    val evidencias: List<String> = emptyList(),
    val acaoRecomendada: String = "",
    val modelo: String = "",
    val fallback: Boolean = true,
    val geradoEm: String? = null
)

fun DashboardNarrativeDto.toDomain(): DashboardInsight = DashboardInsight(
    titulo = titulo,
    resumo = resumo,
    evidencias = evidencias,
    acaoRecomendada = acaoRecomendada,
    modelo = modelo,
    isFallback = fallback,
    geradoEm = geradoEm
)
