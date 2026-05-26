package com.gtnix.aguiabranca.domain.usecase

import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalcularPontuacaoUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository
) {

    suspend fun calcular(userId: String): Pontuacao {
        val ideias = ideiaRepository.listarPorAutor(userId).first()

        var total = ideias.size * PONTOS_POR_IDEIA

        val ideiasAprovadas = ideias.count { it.status == StatusIdeia.APROVADA }
        val ideiasConvertidas = ideias.count { it.status == StatusIdeia.CONVERTIDA_PROJETO }
        val ideiasComBonus = ideiasAprovadas + ideiasConvertidas
        total += ideiasComBonus * PONTOS_BONUS_APROVADA

        val nivel = NivelUsuario.fromPontuacao(total)
        val progresso = calcularProgresso(total, nivel)
        
        val conquistas = calcularConquistas(
            totalIdeias = ideias.size,
            ideiasAprovadas = ideiasAprovadas,
            ideiasEmProjeto = ideiasConvertidas
        )
        
        val estatisticas = Estatisticas(
            totalIdeias = ideias.size,
            ideiasAprovadas = ideiasAprovadas + ideiasConvertidas,
            projetosParticipando = ideiasConvertidas
        )

        return Pontuacao(
            total = total,
            nivel = nivel,
            progressoProximoNivel = progresso,
            pontosParaProximoNivel = calcularPontosRestantes(total, nivel),
            conquistas = conquistas,
            estatisticas = estatisticas
        )
    }
    
    private fun calcularConquistas(
        totalIdeias: Int,
        ideiasAprovadas: Int,
        ideiasEmProjeto: Int
    ): List<Conquista> {
        return listOf(
            Conquista(
                id = "primeira_ideia",
                tipo = TipoConquista.PRIMEIRA_IDEIA,
                desbloqueada = totalIdeias >= 1
            ),
            Conquista(
                id = "inovador",
                tipo = TipoConquista.INOVADOR,
                desbloqueada = totalIdeias >= 5
            ),
            Conquista(
                id = "visionario_ideias",
                tipo = TipoConquista.VISIONARIO_IDEIAS,
                desbloqueada = totalIdeias >= 10
            ),
            Conquista(
                id = "primeira_aprovacao",
                tipo = TipoConquista.PRIMEIRA_APROVACAO,
                desbloqueada = ideiasAprovadas >= 1
            ),
            Conquista(
                id = "influenciador",
                tipo = TipoConquista.INFLUENCIADOR,
                desbloqueada = ideiasAprovadas >= 5
            ),
            Conquista(
                id = "em_projeto",
                tipo = TipoConquista.EM_PROJETO,
                desbloqueada = ideiasEmProjeto >= 1
            ),
            Conquista(
                id = "transformador",
                tipo = TipoConquista.TRANSFORMADOR,
                desbloqueada = ideiasEmProjeto >= 3
            ),
            Conquista(
                id = "lider_inovacao",
                tipo = TipoConquista.LIDER_INOVACAO,
                desbloqueada = totalIdeias >= 15 && ideiasAprovadas >= 10
            )
        )
    }

    private fun calcularProgresso(total: Int, nivel: NivelUsuario): Float {
        if (nivel == NivelUsuario.VISIONARIO) return 1f
        val range = nivel.pontoMaximo - nivel.pontoMinimo + 1
        val within = total - nivel.pontoMinimo
        return (within.toFloat() / range).coerceIn(0f, 1f)
    }

    private fun calcularPontosRestantes(total: Int, nivel: NivelUsuario): Int {
        if (nivel == NivelUsuario.VISIONARIO) return 0
        return nivel.pontoMaximo - total + 1
    }

    companion object {
        const val PONTOS_POR_IDEIA = 10
        const val PONTOS_BONUS_APROVADA = 40
    }
}

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

data class Conquista(
    val id: String,
    val tipo: TipoConquista,
    val desbloqueada: Boolean
)

enum class TipoConquista(
    val titulo: String,
    val descricao: String,
    val icone: String
) {
    PRIMEIRA_IDEIA(
        titulo = "Primeira Ideia",
        descricao = "Submeteu sua primeira ideia",
        icone = "lightbulb"
    ),
    INOVADOR(
        titulo = "Inovador",
        descricao = "Submeteu 5 ideias",
        icone = "stars"
    ),
    VISIONARIO_IDEIAS(
        titulo = "Visionário",
        descricao = "Submeteu 10+ ideias",
        icone = "visibility"
    ),
    PRIMEIRA_APROVACAO(
        titulo = "Aprovado",
        descricao = "Teve uma ideia aprovada",
        icone = "check_circle"
    ),
    INFLUENCIADOR(
        titulo = "Influenciador",
        descricao = "5 ideias aprovadas",
        icone = "emoji_events"
    ),
    EM_PROJETO(
        titulo = "Em Ação",
        descricao = "Ideia virou projeto",
        icone = "rocket_launch"
    ),
    TRANSFORMADOR(
        titulo = "Transformador",
        descricao = "3 ideias em projeto",
        icone = "auto_awesome"
    ),
    LIDER_INOVACAO(
        titulo = "Líder de Inovação",
        descricao = "15+ ideias, 10+ aprovadas",
        icone = "military_tech"
    )
}

enum class NivelUsuario(val label: String, val pontoMinimo: Int, val pontoMaximo: Int) {
    INICIANTE("Iniciante", 0, 50),
    ENGAJADO("Engajado", 51, 150),
    VISIONARIO("Visionário", 151, Int.MAX_VALUE);

    companion object {
        fun fromPontuacao(pontos: Int): NivelUsuario = when {
            pontos <= INICIANTE.pontoMaximo -> INICIANTE
            pontos <= ENGAJADO.pontoMaximo -> ENGAJADO
            else -> VISIONARIO
        }

        fun proximoNivel(atual: NivelUsuario): NivelUsuario? = when (atual) {
            INICIANTE -> ENGAJADO
            ENGAJADO -> VISIONARIO
            VISIONARIO -> null
        }
    }
}
