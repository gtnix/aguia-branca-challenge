package com.gtnix.aguiabranca.data.remote.dto

data class UsuarioDto(
    val id: String,
    val nome: String,
    val email: String,
    val perfil: String,
    val area: String? = null,
    val divisao: String? = null
)
