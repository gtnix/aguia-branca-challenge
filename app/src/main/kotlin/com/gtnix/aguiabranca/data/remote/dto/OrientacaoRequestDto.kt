package com.gtnix.aguiabranca.data.remote.dto

data class OrientacaoRequestDto(
    val titulo: String,
    val descricao: String?,
    val categoria: String,
    val campanha: String,
    val campanhaFim: String? = null,
    val campanhaBonusAsas: Int? = null,
    val campanhaTopN: Int? = null,
    val divisao: String? = null
)
