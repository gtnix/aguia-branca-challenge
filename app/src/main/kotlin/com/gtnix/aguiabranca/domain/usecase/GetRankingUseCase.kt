package com.gtnix.aguiabranca.domain.usecase

import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.RankingEntrada
import com.gtnix.aguiabranca.domain.model.ResultadoRanking
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRankingUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val calcularPontuacao: CalcularPontuacaoUseCase
) {

    suspend fun executar(
        usuarioLogadoId: String,
        perfilFiltro: PerfilUsuario = PerfilUsuario.OPERADOR,
        divisaoFilter: DivisaoFilter = DivisaoFilter.MINHA_DIVISAO,
        divisaoUsuario: DivisaoNegocio = DivisaoNegocio.LOGISTICA
    ): ResultadoRanking {
        val usuarios = usuarioRepository.listarPorPerfil(perfilFiltro).first()
            .let { lista ->
                if (divisaoFilter == DivisaoFilter.MINHA_DIVISAO) {
                    lista.filter { it.divisao == divisaoUsuario }
                } else {
                    lista
                }
            }

        val entradas = usuarios.map { usuario ->
            val pontuacao = calcularPontuacao.calcular(usuario.id, perfilFiltro)
            RankingEntrada(
                usuarioId = usuario.id,
                nome = usuario.nome,
                pontos = pontuacao.total,
                nivel = pontuacao.nivel,
                inicialNome = usuario.nome.firstOrNull()?.uppercaseChar() ?: 'U',
                perfil = perfilFiltro,
                cargo = cargoLabel(perfilFiltro),
                divisao = usuario.divisao
            )
        }
            .sortedByDescending { it.pontos }

        val usuarioLogado = usuarios.find { it.id == usuarioLogadoId }
        val participaNoRanking = usuarioLogado != null && usuarioLogado.perfil == perfilFiltro

        val posicaoUsuario = if (participaNoRanking) {
            entradas.indexOfFirst { it.usuarioId == usuarioLogadoId }
                .let { if (it == -1) null else it + 1 }
        } else {
            null
        }

        return ResultadoRanking(
            topRanking = entradas.take(TOP_LIMIT),
            posicaoUsuarioLogado = posicaoUsuario,
            entradaUsuarioLogado = if (participaNoRanking) {
                entradas.find { it.usuarioId == usuarioLogadoId }
            } else {
                null
            },
            perfilFiltro = perfilFiltro,
            totalParticipantes = entradas.size
        )
    }

    private fun cargoLabel(perfil: PerfilUsuario): String = when (perfil) {
        PerfilUsuario.OPERADOR -> "Operador"
        PerfilUsuario.GESTOR -> "Gestor"
        PerfilUsuario.LIDER -> "Líder"
    }

    companion object {
        const val TOP_LIMIT = 10
    }
}
