package com.gtnix.aguiabranca.domain.usecase

import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.ResultadoRanking
import com.gtnix.aguiabranca.domain.repository.GamificacaoRepository
import com.gtnix.aguiabranca.domain.util.Result
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ranking da temporada vindo do servidor, com todos os colegas do perfil (não só o usuário em cache).
 * A divisão do usuário é resolvida pelo token no backend.
 */
@Singleton
class GetRankingUseCase @Inject constructor(
    private val gamificacaoRepository: GamificacaoRepository
) {

    @Suppress("UNUSED_PARAMETER")
    suspend fun executar(
        usuarioLogadoId: String,
        perfilFiltro: PerfilUsuario = PerfilUsuario.OPERADOR,
        divisaoFilter: DivisaoFilter = DivisaoFilter.MINHA_DIVISAO,
        divisaoUsuario: DivisaoNegocio = DivisaoNegocio.LOGISTICA
    ): ResultadoRanking = when (val result = gamificacaoRepository.ranking(perfilFiltro, divisaoFilter)) {
        is Result.Success -> result.data.copy(topRanking = result.data.topRanking.take(TOP_LIMIT))
        is Result.Error -> throw result.exception
        Result.Loading -> error("estado inesperado")
    }

    companion object {
        const val TOP_LIMIT = 10
    }
}
