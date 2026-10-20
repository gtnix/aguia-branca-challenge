package com.gtnix.aguiabranca.data.remote.dto

data class ProjetoDto(
    val id: String,
    val titulo: String,
    val objetivo: String? = null,
    val descricao: String? = null,
    val area: String? = null,
    val orientacaoId: String? = null,
    val ideiaId: String? = null,
    val etapa: String? = null,
    val status: String? = null,
    val investimento: Double? = null,
    val retorno: Double? = null,
    val roi: Double? = null,
    val prazo: String? = null,
    val progressoPercentual: Int? = null,
    val resultadosObtidos: String? = null,
    val responsavelUserId: String? = null,
    val dataCriacao: String? = null,
    val dataAtualizacao: String? = null
)

data class ProjetoRequestDto(
    val titulo: String,
    val objetivo: String,
    val descricao: String,
    val area: String,
    val orientacaoId: String,
    val ideiaId: String? = null,
    val etapa: String? = null,
    val status: String? = null,
    val investimento: Double? = null,
    val retorno: Double? = null,
    val prazo: String? = null,
    val progressoPercentual: Int? = null,
    val resultadosObtidos: String? = null
)

data class ProgressoRequestDto(
    val etapa: String? = null,
    val status: String? = null,
    val progressoPercentual: Int? = null,
    val resultadosObtidos: String? = null
)
