package com.gtnix.aguiabranca.data.remote.dto

data class NivelDto(
    val codigo: String? = null,
    val label: String? = null,
    val xpMinimo: Int = 0,
    val proximoCodigo: String? = null,
    val xpProximo: Int? = null,
    val xpParaProximo: Int = 0,
    val progresso: Double = 0.0
)

data class TemporadaDto(
    val id: String = "",
    val label: String = "",
    val inicio: String? = null,
    val fim: String? = null,
    val diasRestantes: Long = 0
)

data class ConquistaDto(
    val tipo: String = "",
    val titulo: String? = null,
    val descricao: String? = null,
    val valor: Int = 0,
    val tier: String? = null,
    val desbloqueada: Boolean = false,
    val proximaMeta: Int? = null,
    val progresso: Double = 0.0,
    val desbloqueadaEm: String? = null
)

data class MissaoDto(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val progresso: Int = 0,
    val meta: Int = 1,
    val concluida: Boolean = false,
    val recompensaXp: Int = 0,
    val recompensaAsas: Int = 0
)

data class ImpactoDto(
    val ideiasEnviadas: Int = 0,
    val ideiasAprovadas: Int = 0,
    val ideiasAvaliadas: Int = 0,
    val ideiasAlinhadas: Int = 0,
    val projetos: Int = 0,
    val projetosConcluidos: Int = 0,
    val retornoMensal: Double? = null
)

data class EventoPontosDto(
    val id: String? = null,
    val tipo: String? = null,
    val descricao: String? = null,
    val xp: Int = 0,
    val asas: Int = 0,
    val criadoEm: String? = null,
    val estornado: Boolean = false
)

data class GamificacaoMeDto(
    val userId: String = "",
    val nome: String? = null,
    val perfil: String? = null,
    val divisao: String? = null,
    val equipe: String? = null,
    val xpTotal: Int = 0,
    val xpTemporada: Int = 0,
    val asas: Int = 0,
    val nivel: NivelDto? = null,
    val temporada: TemporadaDto? = null,
    val posicaoRanking: Int? = null,
    val totalRanking: Int = 0,
    val sequenciaSemanas: Int = 0,
    val conquistas: List<ConquistaDto> = emptyList(),
    val missoes: List<MissaoDto> = emptyList(),
    val impacto: ImpactoDto? = null,
    val novidades: List<EventoPontosDto> = emptyList()
)

data class ExtratoDto(
    val xpTotal: Int = 0,
    val asas: Int = 0,
    val eventos: List<EventoPontosDto> = emptyList()
)

data class RankingEntradaDto(
    val posicao: Int = 0,
    val usuarioId: String = "",
    val nome: String? = null,
    val perfil: String? = null,
    val divisao: String? = null,
    val equipe: String? = null,
    val xpTemporada: Int = 0,
    val nivel: String? = null,
    val nivelLabel: String? = null,
    val zonaPremio: Boolean = false,
    val voce: Boolean = false
)

data class RankingDto(
    val temporada: TemporadaDto? = null,
    val perfil: String? = null,
    val escopo: String? = null,
    val entradas: List<RankingEntradaDto> = emptyList(),
    val minhaPosicao: Int? = null,
    val minhaEntrada: RankingEntradaDto? = null,
    val totalParticipantes: Int = 0,
    val regraPremio: String? = null,
    val pagina: Int = 0,
    val tamanho: Int = 50,
    val temProxima: Boolean = false
)

data class IndicacaoInovaGabDto(
    val divisao: String? = null,
    val posicaoNaDivisao: Int = 0,
    val usuarioId: String = "",
    val nome: String? = null,
    val equipe: String? = null,
    val xpTemporada: Int = 0
)

data class IndicacoesInovaGabDto(
    val temporada: TemporadaDto? = null,
    val criterio: String? = null,
    val indicados: List<IndicacaoInovaGabDto> = emptyList()
)

data class EquipeRankingDto(
    val posicao: Int = 0,
    val equipe: String = "",
    val divisao: String? = null,
    val membros: Int = 0,
    val xpTotal: Int = 0,
    val xpMedio: Int = 0,
    val minhaEquipe: Boolean = false
)

data class RecompensaDto(
    val id: String = "",
    val titulo: String = "",
    val descricao: String? = null,
    val categoria: String? = null,
    val parceiro: String? = null,
    val custoAsas: Int = 0,
    val estoque: Int = 0,
    val nivelMinimo: String? = null,
    val nivelMinimoLabel: String? = null,
    val ativo: Boolean = true,
    val podeResgatar: Boolean = false,
    val motivoBloqueio: String? = null
)

data class ResgateDto(
    val id: String = "",
    val recompensaId: String = "",
    val recompensaTitulo: String? = null,
    val userId: String = "",
    val userNome: String? = null,
    val custoAsas: Int = 0,
    val status: String? = null,
    val observacao: String? = null,
    val aprovadorUserId: String? = null,
    val solicitadoEm: String? = null,
    val decididoEm: String? = null,
    val entregueEm: String? = null
)

data class FeedbackUtilRequestDto(
    val util: Boolean
)

data class VencedorCampanhaDto(
    val userId: String = "",
    val nome: String? = null,
    val xpCampanha: Int = 0,
    val bonusAsas: Int = 0
)

data class EncerramentoCampanhaDto(
    val orientacaoId: String = "",
    val campanha: String? = null,
    val encerradaEm: String? = null,
    val vencedores: List<VencedorCampanhaDto> = emptyList()
)
