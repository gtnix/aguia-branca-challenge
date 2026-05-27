package com.gtnix.aguiabranca.domain.model

data class Pontuacao(
    val total: Int,
    val nivel: NivelUsuario,
    val progressoProximoNivel: Float,
    val pontosParaProximoNivel: Int,
    val conquistas: List<Conquista> = emptyList(),
    val estatisticas: Estatisticas = Estatisticas()
)

data class Estatisticas(
    val totalIdeias: Int = 0,
    val ideiasAprovadas: Int = 0,
    val projetosParticipando: Int = 0
)

enum class ConquistaTier {
    BRONZE,
    PRATA,
    OURO,
    PLATINA
}

data class Conquista(
    val id: String,
    val tipo: TipoConquista,
    val desbloqueada: Boolean,
    val tier: ConquistaTier = ConquistaTier.BRONZE
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
    LIDER_INOVACAO("military_tech"),
    CURADOR("rate_review"),
    MENTOR("school"),
    EXECUTOR("build"),
    LIDER_RESULTADOS("trending_up"),
    ESTRATEGISTA("flag"),
    VISIONARIO_ESTRATEGICO("insights"),
    TRANSFORMADOR_ESTRATEGICO("workspace_premium")
}

fun TipoConquista.defaultTier(): ConquistaTier = when (this) {
    TipoConquista.BEM_VINDO,
    TipoConquista.PRIMEIRA_IDEIA,
    TipoConquista.CURADOR,
    TipoConquista.ESTRATEGISTA -> ConquistaTier.BRONZE
    TipoConquista.INOVADOR,
    TipoConquista.PRIMEIRA_APROVACAO,
    TipoConquista.MENTOR,
    TipoConquista.VISIONARIO_ESTRATEGICO -> ConquistaTier.PRATA
    TipoConquista.VISIONARIO_IDEIAS,
    TipoConquista.INFLUENCIADOR,
    TipoConquista.EM_PROJETO,
    TipoConquista.LIDER_INOVACAO,
    TipoConquista.LIDER_RESULTADOS -> ConquistaTier.OURO
    TipoConquista.TRANSFORMADOR,
    TipoConquista.EXECUTOR,
    TipoConquista.TRANSFORMADOR_ESTRATEGICO -> ConquistaTier.PLATINA
}

fun conquistasPorPerfil(perfil: PerfilUsuario): List<TipoConquista> = when (perfil) {
    PerfilUsuario.OPERADOR -> listOf(
        TipoConquista.BEM_VINDO,
        TipoConquista.PRIMEIRA_IDEIA,
        TipoConquista.INOVADOR,
        TipoConquista.VISIONARIO_IDEIAS,
        TipoConquista.PRIMEIRA_APROVACAO,
        TipoConquista.INFLUENCIADOR,
        TipoConquista.EM_PROJETO,
        TipoConquista.TRANSFORMADOR
    )
    PerfilUsuario.GESTOR -> listOf(
        TipoConquista.BEM_VINDO,
        TipoConquista.CURADOR,
        TipoConquista.MENTOR,
        TipoConquista.PRIMEIRA_APROVACAO,
        TipoConquista.LIDER_RESULTADOS,
        TipoConquista.EXECUTOR
    )
    PerfilUsuario.LIDER -> listOf(
        TipoConquista.BEM_VINDO,
        TipoConquista.ESTRATEGISTA,
        TipoConquista.VISIONARIO_ESTRATEGICO,
        TipoConquista.LIDER_INOVACAO,
        TipoConquista.TRANSFORMADOR_ESTRATEGICO
    )
}

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
        pontoMaximo = 40,
        icone = "star_outline",
        mensagemMotivacional = "Toda jornada começa com o primeiro passo"
    ),
    EM_ASCENSAO(
        label = "Em Ascensão",
        pontoMinimo = 41,
        pontoMaximo = 120,
        icone = "trending_up",
        mensagemMotivacional = "Você está ganhando ritmo!"
    ),
    ENGAJADO(
        label = "Engajado",
        pontoMinimo = 121,
        pontoMaximo = 280,
        icone = "auto_awesome",
        mensagemMotivacional = "Suas ideias fazem diferença"
    ),
    VISIONARIO(
        label = "Visionário",
        pontoMinimo = 281,
        pontoMaximo = 500,
        icone = "bolt",
        mensagemMotivacional = "Inovação corre nas suas veias"
    ),
    TRANSFORMADOR(
        label = "Transformador",
        pontoMinimo = 501,
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
    NivelUsuario.INICIANTE -> "Comece publicando a primeira orientação estratégica"
    NivelUsuario.EM_ASCENSAO -> "Direcione a equipe com orientações claras"
    NivelUsuario.ENGAJADO -> "Sua liderança orienta ideias com propósito"
    NivelUsuario.VISIONARIO -> "Você inspira a equipe a inovar com foco"
    NivelUsuario.TRANSFORMADOR -> "Suas orientações transformam o Grupo Águia Branca"
}

fun NivelUsuario.mensagemMotivacionalPara(perfil: PerfilUsuario): String = when (perfil) {
    PerfilUsuario.LIDER -> mensagemMotivacionalParaLider()
    else -> mensagemMotivacional
}
