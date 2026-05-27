package com.gtnix.aguiabranca.domain.usecase.dashboard

import app.cash.turbine.test
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.usecase.ideia.ContarIdeiasPendentesPorAreaUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeProjetoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.FakeUsuarioRepository
import com.gtnix.aguiabranca.fake.TestDataFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetDashboardUseCaseTest {

    private lateinit var useCase: GetDashboardUseCase
    private lateinit var fakeIdeiaRepository: FakeIdeiaRepository
    private lateinit var fakeProjetoRepository: FakeProjetoRepository
    private lateinit var fakeOrientacaoRepository: FakeOrientacaoRepository
    private lateinit var fakeUsuarioRepository: FakeUsuarioRepository
    private lateinit var fakeSessionManager: FakeSessionManager

    private val ideias = listOf(
        TestDataFactory.createIdeia("ideia-1", status = StatusIdeia.PENDENTE),
        TestDataFactory.createIdeia("ideia-2", status = StatusIdeia.APROVADA),
        TestDataFactory.createIdeia(
            id = "ideia-3",
            status = StatusIdeia.CONVERTIDA_PROJETO,
            projetoId = "projeto-1",
            dataAvaliacao = 1_000L
        ),
        TestDataFactory.createIdeia("ideia-4", status = StatusIdeia.REPROVADA)
    )

    private val projetos = listOf(
        TestDataFactory.createProjeto(
            id = "projeto-1",
            status = StatusProjeto.EM_ANDAMENTO,
            investimentoRealizado = 100_000.0,
            retornoRealizadoMensal = 10_000.0
        ),
        TestDataFactory.createProjeto(
            id = "projeto-2",
            status = StatusProjeto.PLANEJADO,
            investimentoRealizado = 50_000.0,
            retornoRealizadoMensal = 5_000.0
        )
    )

    private val orientacoes = listOf(
        TestDataFactory.createOrientacao("orientacao-1"),
        TestDataFactory.createOrientacao("orientacao-2"),
        TestDataFactory.createOrientacao("orientacao-3"),
        TestDataFactory.createOrientacao("orientacao-4")
    )

    @Before
    fun setup() {
        fakeIdeiaRepository = FakeIdeiaRepository(ideias)
        fakeProjetoRepository = FakeProjetoRepository(projetos)
        fakeOrientacaoRepository = FakeOrientacaoRepository(orientacoes)
        fakeUsuarioRepository = FakeUsuarioRepository()
        fakeSessionManager = FakeSessionManager()
        val liderUser = TestDataFactory.createUsuario(
            id = "lider-1",
            perfil = PerfilUsuario.LIDER
        )
        // Para o líder filtrar ideias da própria divisão, o autor "user-1" das
        // ideias precisa também estar registrado na mesma divisão (default LOGISTICA).
        val autorIdeias = TestDataFactory.createUsuario(
            id = "user-1",
            perfil = PerfilUsuario.OPERADOR
        )
        fakeSessionManager.setUser(liderUser)
        kotlinx.coroutines.runBlocking {
            fakeUsuarioRepository.salvar(liderUser)
            fakeUsuarioRepository.salvar(autorIdeias)
        }
        useCase = GetDashboardUseCase(
            fakeIdeiaRepository,
            fakeProjetoRepository,
            fakeOrientacaoRepository,
            fakeUsuarioRepository,
            fakeSessionManager,
            ContarIdeiasPendentesPorAreaUseCase(fakeIdeiaRepository)
        )
    }

    @Test
    fun `agrega contagens de ideias e projetos`() = runTest {
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data

            assertEquals(4, data.totalIdeias)
            assertEquals(4, data.ideias.size)
            assertEquals(2, data.totalProjetos)
            assertEquals(2, data.ideiasAprovadas)
            assertEquals(1, data.ideiasEmProjeto)
            assertEquals(1, data.projetosEmAndamento.size)
            assertEquals(3, data.orientacoes.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `calcula investimento total e retorno mensal`() = runTest {
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data

            assertEquals(150_000.0, data.investimentoTotal, 0.01)
            assertEquals(15_000.0, data.retornoTotal, 0.01)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `calcula ROI consolidado anualizado`() = runTest {
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data

            val expectedRoi = ((15_000.0 * 12) - 150_000.0) / 150_000.0 * 100
            assertEquals(expectedRoi, data.roiConsolidado, 0.01)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `limita preview de ideias e orientacoes`() = runTest {
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data

            assertEquals(4, data.ideias.size.coerceAtMost(5))
            assertEquals(3, data.orientacoes.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retorna ideias convertidas recentes com projeto associado`() = runTest {
        useCase().test {
            val result = awaitItem()
            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data

            assertEquals(1, data.ideiasConvertidasRecentes.size)
            assertEquals("ideia-3", data.ideiasConvertidasRecentes.first().ideia.id)
            assertEquals("projeto-1", data.ideiasConvertidasRecentes.first().projeto.id)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
