package com.gtnix.aguiabranca.domain.model

data class StartupPartner(
    val id: String,
    val nome: String,
    val setor: String,
    val descricao: String,
    val matchScore: Int
)
