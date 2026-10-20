package com.gtnix.aguiabranca.data.remote.dto

data class LoginResponseDto(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val usuario: UsuarioDto
)
