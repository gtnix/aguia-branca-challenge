package com.gtnix.aguiabranca.data.remote.dto

data class OrientacaoDto(
    val id: String,
    val titulo: String,
    val descricao: String? = null,
    val categoria: String,
    val campanha: String? = null,
    val dataCriacao: String? = null,
    val dataAtualizacao: String? = null,
    val ativo: Boolean = true,
    val criadoPorUserId: String? = null,
    val campanhaFim: String? = null,
    val campanhaBonusAsas: Int? = null,
    val campanhaTopN: Int? = null,
    val campanhaEncerradaEm: String? = null,
    val campanhaAtiva: Boolean = false,
    val divisao: String? = null
)
