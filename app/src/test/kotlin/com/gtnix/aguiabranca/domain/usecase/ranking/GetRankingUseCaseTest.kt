package com.gtnix.aguiabranca.domain.usecase.ranking

import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.RankingEntrada
import com.gtnix.aguiabranca.domain.model.ResultadoRanking
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeGamificacaoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class GetRankingUseCaseTest {

    private lateinit var gamificacao: FakeGamificacaoRepository
    private lateinit var useCase: GetRankingUseCase

    private fun entrada(id: String, xp: Int, zonaPremio: Boolean = false) = RankingEntrada(
        usuarioId = id,
        nome = "Colaborador $id",
        pontos = xp,
        nivel = NivelUsuario.fromPontuacao(xp),
        inicialNome = 'C',
        perfil = PerfilUsuario.OPERADOR,
        cargo = "Operador",
        divisao = DivisaoNegocio.LOGISTICA,
        zonaPremio = zonaPremio
    )

    @Before
    fun setup() {
        gamificacao = FakeGamificacaoRepository()
        useCase = GetRankingUseCase(gamificacao)
    }

    @Test
    fun `dado ranking do servidor quando executar então repassa perfil e escopo`() = runTest {
        useCase.executar(
            usuarioLogadoId = "op-1",
            perfilFiltro = PerfilUsuario.GESTOR,
            divisaoFilter = DivisaoFilter.GRUPO_COMPLETO
        )

        assertEquals(listOf(PerfilUsuario.GESTOR to DivisaoFilter.GRUPO_COMPLETO), gamificacao.rankingChamadas)
    }

    @Test
    fun `dado mais de dez colegas quando executar então limita o top e preserva posição e total`() = runTest {
        val entradas = (1..15).map { entrada("op-$it", xp = 200 - it * 10, zonaPremio = it <= 3) }
        gamificacao.rankingResults[PerfilUsuario.OPERADOR] = Result.Success(
            ResultadoRanking(
                topRanking = entradas,
                posicaoUsuarioLogado = 12,
                entradaUsuarioLogado = entradas[11],
                perfilFiltro = PerfilUsuario.OPERADOR,
                totalParticipantes = 15,
                regraPremio = "Top 3 por divisão"
            )
        )

        val resultado = useCase.executar(usuarioLogadoId = "op-12")

        assertEquals(GetRankingUseCase.TOP_LIMIT, resultado.topRanking.size)
        assertEquals(12, resultado.posicaoUsuarioLogado)
        assertEquals("op-12", resultado.entradaUsuarioLogado?.usuarioId)
        assertEquals(15, resultado.totalParticipantes)
        assertEquals("Top 3 por divisão", resultado.regraPremio)
        assertTrue(resultado.topRanking.take(3).all { it.zonaPremio })
    }

    @Test
    fun `dado erro do servidor quando executar então propaga para a tela tratar`() = runTest {
        gamificacao.rankingResults[PerfilUsuario.OPERADOR] = Result.Error(IllegalStateException("403"))

        try {
            useCase.executar(usuarioLogadoId = "op-1")
            fail("deveria lançar")
        } catch (e: IllegalStateException) {
            assertEquals("403", e.message)
        }
    }
}
