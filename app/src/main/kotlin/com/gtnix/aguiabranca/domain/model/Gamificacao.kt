package com.gtnix.aguiabranca.domain.model

/**
 * Painel da Economia de Inovação GAB, calculado pelo servidor (GET /gamificacao/me).
 * XP mede reputação (nível e ranking da temporada); Asas são a moeda resgatável na loja.
 */
data class Pontuacao(
    val total: Int,
    val nivel: NivelUsuario,
    val progressoProximoNivel: Float,
    val pontosParaProximoNivel: Int,
    val conquistas: List<Conquista> = emptyList(),
    val estatisticas: Estatisticas = Estatisticas(),
    val asas: Int = 0,
    val xpTemporada: Int = 0,
    val temporada: TemporadaInfo? = null,
    val posicaoRanking: Int? = null,
    val totalRanking: Int = 0,
    val sequenciaSemanas: Int = 0,
    val missoes: List<Missao> = emptyList(),
    val impacto: Impacto = Impacto(),
    val novidades: List<EventoPontos> = emptyList()
)

data class Estatisticas(
    val totalIdeias: Int = 0,
    val ideiasAprovadas: Int = 0,
    val projetosParticipando: Int = 0,
    val ideiasAvaliadas: Int = 0,
    val orientacoesAtivas: Int = 0,
    val ideiasAlinhadas: Int = 0,
    val projetosDirecionados: Int = 0,
    val ideiasDivisao: Int = 0
)

data class TemporadaInfo(
    val id: String,
    val label: String,
    val diasRestantes: Long
)

data class Missao(
    val id: String,
    val titulo: String,
    val descricao: String,
    val progresso: Int,
    val meta: Int,
    val concluida: Boolean,
    val recompensaXp: Int,
    val recompensaAsas: Int
) {
    val fracao: Float
        get() = if (meta <= 0) 0f else (progresso.toFloat() / meta).coerceIn(0f, 1f)
}

data class Impacto(
    val ideiasEnviadas: Int = 0,
    val ideiasAprovadas: Int = 0,
    val ideiasAvaliadas: Int = 0,
    val ideiasAlinhadas: Int = 0,
    val projetos: Int = 0,
    val projetosConcluidos: Int = 0,
    val retornoMensal: Double = 0.0
)

data class EventoPontos(
    val id: String,
    val tipo: String,
    val descricao: String,
    val xp: Int,
    val asas: Int,
    val criadoEm: Long,
    val estornado: Boolean
)

data class Extrato(
    val xpTotal: Int,
    val asas: Int,
    val eventos: List<EventoPontos>
)

enum class ConquistaTier {
    BRONZE,
    PRATA,
    OURO,
    PLATINA
}

/** Metas de cada tier, iguais às do servidor (docs/GAMIFICACAO.md). */
val METAS_TIER = listOf(1, 3, 10, 25)

data class Conquista(
    val id: String,
    val tipo: TipoConquista,
    val desbloqueada: Boolean,
    val tier: ConquistaTier = ConquistaTier.BRONZE,
    val titulo: String = "",
    val descricao: String = "",
    val valor: Int = 0,
    val proximaMeta: Int? = null,
    val progresso: Float = if (desbloqueada) 1f else 0f,
    val desbloqueadaEm: Long? = null
)

enum class TipoConquista(val icone: String) {
    BEM_VINDO("celebration"),
    PRIMEIRA_IDEIA("lightbulb"),
    INOVADOR("stars"),
    VISIONARIO_IDEIAS("visibility"),
    PRIMEIRA_APROVACAO("check_circle"),
    INFLUENCIADOR("emoji_events"),
    EM_PROJETO("rocket_launch"),
    TRANSFORMADOR("auto_awesome"),
    GUARDIAO_SEGURANCA("health_and_safety"),
    LIDER_INOVACAO("military_tech"),
    CURADOR("rate_review"),
    MENTOR("school"),
    EXECUTOR("build"),
    LIDER_RESULTADOS("trending_up"),
    ESTRATEGISTA("flag"),
    VISIONARIO_ESTRATEGICO("insights"),
    TRANSFORMADOR_ESTRATEGICO("workspace_premium")
}

/** Faixas de XP iguais a NivelGamificacao no backend. */
enum class NivelUsuario(
    val label: String,
    val pontoMinimo: Int,
    val pontoMaximo: Int,
    val icone: String,
    val mensagemMotivacional: String
) {
    INICIANTE(
        label = "Iniciante",
        pontoMinimo = 0,
        pontoMaximo = 49,
        icone = "star_outline",
        mensagemMotivacional = "Toda jornada começa com o primeiro passo"
    ),
    EM_ASCENSAO(
        label = "Em Ascensão",
        pontoMinimo = 50,
        pontoMaximo = 149,
        icone = "trending_up",
        mensagemMotivacional = "Você está ganhando ritmo!"
    ),
    ENGAJADO(
        label = "Engajado",
        pontoMinimo = 150,
        pontoMaximo = 399,
        icone = "auto_awesome",
        mensagemMotivacional = "Suas ideias fazem diferença"
    ),
    VISIONARIO(
        label = "Visionário",
        pontoMinimo = 400,
        pontoMaximo = 899,
        icone = "bolt",
        mensagemMotivacional = "Inovação corre nas suas veias"
    ),
    TRANSFORMADOR(
        label = "Transformador",
        pontoMinimo = 900,
        pontoMaximo = Int.MAX_VALUE,
        icone = "emoji_events",
        mensagemMotivacional = "Você transforma a Águia Branca"
    );

    companion object {
        fun fromPontuacao(pontos: Int): NivelUsuario = when {
            pontos <= INICIANTE.pontoMaximo -> INICIANTE
            pontos <= EM_ASCENSAO.pontoMaximo -> EM_ASCENSAO
            pontos <= ENGAJADO.pontoMaximo -> ENGAJADO
            pontos <= VISIONARIO.pontoMaximo -> VISIONARIO
            else -> TRANSFORMADOR
        }

        fun proximoNivel(atual: NivelUsuario): NivelUsuario? = when (atual) {
            INICIANTE -> EM_ASCENSAO
            EM_ASCENSAO -> ENGAJADO
            ENGAJADO -> VISIONARIO
            VISIONARIO -> TRANSFORMADOR
            TRANSFORMADOR -> null
        }
    }
}

fun NivelUsuario.mensagemMotivacionalParaLider(): String = when (this) {
    NivelUsuario.INICIANTE -> "Publique orientações que tragam ideias da operação"
    NivelUsuario.EM_ASCENSAO -> "Suas orientações já estão recebendo ideias"
    NivelUsuario.ENGAJADO -> "Sua liderança orienta ideias com propósito"
    NivelUsuario.VISIONARIO -> "Projetos das suas orientações estão entregando"
    NivelUsuario.TRANSFORMADOR -> "Suas orientações transformam o Grupo Águia Branca"
}

fun NivelUsuario.mensagemMotivacionalPara(perfil: PerfilUsuario): String = when (perfil) {
    PerfilUsuario.LIDER -> mensagemMotivacionalParaLider()
    else -> mensagemMotivacional
}
