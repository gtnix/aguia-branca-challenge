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

        val ideiasComBonus = ideias.count { ideia ->
            ideia.status == StatusIdeia.APROVADA || ideia.status == StatusIdeia.CONVERTIDA_PROJETO
        }
        total += ideiasComBonus * PONTOS_BONUS_APROVADA

        val nivel = NivelUsuario.fromPontuacao(total)
        val progresso = calcularProgresso(total, nivel)

        return Pontuacao(
            total = total,
            nivel = nivel,
            progressoProximoNivel = progresso,
            pontosParaProximoNivel = calcularPontosRestantes(total, nivel)
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
    val pontosParaProximoNivel: Int
)

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
