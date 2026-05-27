package com.gtnix.aguiabranca.domain.usecase.ranking

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.Estatisticas
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.Usuario
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.fake.FakeUsuarioRepository
import com.gtnix.aguiabranca.fake.TestDataFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetRankingUseCaseTest {

    private lateinit var useCase: GetRankingUseCase
    private lateinit var fakeUsuarioRepository: FakeUsuarioRepository
    private val calcularPontuacao = mockk<CalcularPontuacaoUseCase>()

    private val operador1 = TestDataFactory.createUsuario(
        id = "op-1",
        nome = "Operador Um",
        perfil = PerfilUsuario.OPERADOR,
        area = AreaAtuacao.OPERACOES
    )

    private val operador2 = TestDataFactory.createUsuario(
        id = "op-2",
        nome = "Operador Dois",
        perfil = PerfilUsuario.OPERADOR,
        area = AreaAtuacao.OPERACOES
    )

    private val operador3Passageiros = Usuario(
        id = "op-3",
        nome = "Operador Três",
        email = "op3@aguiabranca.com.br",
        perfil = PerfilUsuario.OPERADOR,
        area = AreaAtuacao.LOGISTICA,
        divisao = DivisaoNegocio.PASSAGEIROS
    )

    private val gestor = TestDataFactory.createUsuario(
        id = "gestor-1",
        nome = "Gestor Um",
        perfil = PerfilUsuario.GESTOR,
        area = AreaAtuacao.LOGISTICA
    )

    private fun pontuacao(total: Int) = Pontuacao(
        total = total,
        nivel = NivelUsuario.fromPontuacao(total),
        progressoProximoNivel = 0.5f,
        pontosParaProximoNivel = 10,
        conquistas = emptyList(),
        estatisticas = Estatisticas()
    )

    @Before
    fun setup() {
        fakeUsuarioRepository = FakeUsuarioRepository()
        useCase = GetRankingUseCase(fakeUsuarioRepository, calcularPontuacao)
    }

    private suspend fun seedUsuarios() {
        fakeUsuarioRepository.salvar(operador1)
        fakeUsuarioRepository.salvar(operador2)
        fakeUsuarioRepository.salvar(operador3Passageiros)
        fakeUsuarioRepository.salvar(gestor)
    }

    @Test
    fun `dado usuarios operadores quando executar com filtro OPERADOR então retorna ranking ordenado`() = runTest {
        seedUsuarios()
        coEvery { calcularPontuacao.calcular("op-1", PerfilUsuario.OPERADOR) } returns pontuacao(50)
        coEvery { calcularPontuacao.calcular("op-2", PerfilUsuario.OPERADOR) } returns pontuacao(100)
        coEvery { calcularPontuacao.calcular("op-3", PerfilUsuario.OPERADOR) } returns pontuacao(30)

        val resultado = useCase.executar(
            usuarioLogadoId = "op-1",
            perfilFiltro = PerfilUsuario.OPERADOR,
            divisaoFilter = DivisaoFilter.GRUPO_COMPLETO
        )

        assertEquals(3, resultado.topRanking.size)
        assertEquals("op-2", resultado.topRanking.first().usuarioId)
        assertEquals(100, resultado.topRanking.first().pontos)
        assertTrue(resultado.topRanking.zipWithNext().all { (a, b) -> a.pontos >= b.pontos })
    }

    @Test
    fun `dado divisao MINHA_DIVISAO quando executar então filtra por divisao`() = runTest {
        seedUsuarios()
        coEvery { calcularPontuacao.calcular("op-1", PerfilUsuario.OPERADOR) } returns pontuacao(50)
        coEvery { calcularPontuacao.calcular("op-2", PerfilUsuario.OPERADOR) } returns pontuacao(100)

        val resultado = useCase.executar(
            usuarioLogadoId = "op-1",
            perfilFiltro = PerfilUsuario.OPERADOR,
            divisaoFilter = DivisaoFilter.MINHA_DIVISAO,
            divisaoUsuario = DivisaoNegocio.LOGISTICA
        )

        assertTrue(resultado.topRanking.all { it.divisao == DivisaoNegocio.LOGISTICA })
    }

    @Test
    fun `dado divisao GRUPO_COMPLETO quando executar então nao filtra`() = runTest {
        seedUsuarios()
        coEvery { calcularPontuacao.calcular(any(), eq(PerfilUsuario.OPERADOR)) } returns pontuacao(50)

        val resultado = useCase.executar(
            usuarioLogadoId = "op-1",
            perfilFiltro = PerfilUsuario.OPERADOR,
            divisaoFilter = DivisaoFilter.GRUPO_COMPLETO
        )

        assertEquals(3, resultado.topRanking.size)
    }

    @Test
    fun `dado usuario logado no ranking quando executar então retorna posicao`() = runTest {
        seedUsuarios()
        coEvery { calcularPontuacao.calcular("op-1", PerfilUsuario.OPERADOR) } returns pontuacao(50)
        coEvery { calcularPontuacao.calcular("op-2", PerfilUsuario.OPERADOR) } returns pontuacao(100)
        coEvery { calcularPontuacao.calcular("op-3", PerfilUsuario.OPERADOR) } returns pontuacao(30)

        val resultado = useCase.executar(
            usuarioLogadoId = "op-1",
            perfilFiltro = PerfilUsuario.OPERADOR,
            divisaoFilter = DivisaoFilter.GRUPO_COMPLETO
        )

        assertNotNull(resultado.posicaoUsuarioLogado)
        assertEquals(2, resultado.posicaoUsuarioLogado)
        assertNotNull(resultado.entradaUsuarioLogado)
        assertEquals("op-1", resultado.entradaUsuarioLogado?.usuarioId)
    }

    @Test
    fun `dado usuario de outro perfil quando executar então nao retorna posicao`() = runTest {
        seedUsuarios()
        coEvery { calcularPontuacao.calcular(any(), eq(PerfilUsuario.OPERADOR)) } returns pontuacao(50)

        val resultado = useCase.executar(
            usuarioLogadoId = "gestor-1",
            perfilFiltro = PerfilUsuario.OPERADOR,
            divisaoFilter = DivisaoFilter.GRUPO_COMPLETO
        )

        assertNull(resultado.posicaoUsuarioLogado)
        assertNull(resultado.entradaUsuarioLogado)
    }

    @Test
    fun `dado nenhum usuario quando executar então retorna ranking vazio`() = runTest {
        val resultado = useCase.executar(
            usuarioLogadoId = "user-inexistente",
            perfilFiltro = PerfilUsuario.OPERADOR,
            divisaoFilter = DivisaoFilter.GRUPO_COMPLETO
        )

        assertTrue(resultado.topRanking.isEmpty())
        assertEquals(0, resultado.totalParticipantes)
        assertNull(resultado.posicaoUsuarioLogado)
    }

    @Test
    fun `dado ranking quando executar então respeita TOP_LIMIT`() = runTest {
        repeat(15) { i ->
            fakeUsuarioRepository.salvar(
                TestDataFactory.createUsuario(
                    id = "user-$i",
                    nome = "Usuário $i",
                    email = "user$i@aguiabranca.com.br",
                    perfil = PerfilUsuario.OPERADOR
                )
            )
            coEvery {
                calcularPontuacao.calcular("user-$i", PerfilUsuario.OPERADOR)
            } returns pontuacao(i * 10)
        }

        val resultado = useCase.executar(
            usuarioLogadoId = "user-0",
            perfilFiltro = PerfilUsuario.OPERADOR,
            divisaoFilter = DivisaoFilter.GRUPO_COMPLETO
        )

        assertEquals(GetRankingUseCase.TOP_LIMIT, resultado.topRanking.size)
        assertEquals(15, resultado.totalParticipantes)
    }
}
