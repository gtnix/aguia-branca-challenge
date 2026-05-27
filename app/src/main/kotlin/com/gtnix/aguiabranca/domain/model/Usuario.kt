package com.gtnix.aguiabranca.domain.model

data class Usuario(
    val id: String,
    val nome: String,
    val email: String,
    val perfil: PerfilUsuario,
    val area: AreaAtuacao,
    val divisao: DivisaoNegocio = DivisaoNegocio.LOGISTICA,
    val fotoPerfil: String? = null,
    val dataCadastro: Long = System.currentTimeMillis()
)

enum class DivisaoNegocio(val label: String) {
    PASSAGEIROS("Passageiros"),
    LOGISTICA("Logística"),
    COMERCIO("Comércio")
}

enum class PerfilUsuario {
    OPERADOR,
    GESTOR,
    LIDER
}

enum class AreaAtuacao {
    OPERACOES,
    LOGISTICA,
    COMERCIAL,
    FINANCEIRO,
    RH,
    TI,
    MARKETING,
    QUALIDADE
}
