package com.gtnix.aguiabranca.domain.model

data class Recompensa(
    val id: String,
    val titulo: String,
    val descricao: String,
    val categoria: String,
    val parceiro: String,
    val custoAsas: Int,
    val estoque: Int,
    val nivelMinimo: NivelUsuario,
    val ativo: Boolean,
    val podeResgatar: Boolean,
    val motivoBloqueio: String?
)

enum class StatusResgate(val label: String) {
    SOLICITADO("Confirmado"),
    APROVADO("Confirmado"),
    ENTREGUE("Entregue"),
    NEGADO("Negado")
}

data class Resgate(
    val id: String,
    val recompensaId: String,
    val recompensaTitulo: String,
    val userId: String,
    val userNome: String,
    val custoAsas: Int,
    val status: StatusResgate,
    val observacao: String?,
    val solicitadoEm: Long,
    val decididoEm: Long?
)

data class EquipeRanking(
    val posicao: Int,
    val equipe: String,
    val divisao: DivisaoNegocio?,
    val membros: Int,
    val xpTotal: Int,
    val xpMedio: Int,
    val minhaEquipe: Boolean
)

data class VencedorCampanha(
    val userId: String,
    val nome: String,
    val xpCampanha: Int,
    val bonusAsas: Int
)
