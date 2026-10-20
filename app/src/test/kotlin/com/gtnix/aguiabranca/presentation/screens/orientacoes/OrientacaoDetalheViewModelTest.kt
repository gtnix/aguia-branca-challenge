package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.lifecycle.SavedStateHandle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.DashboardByStrategy
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.repository.DashboardRepository
import com.gtnix.aguiabranca.domain.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeProjetoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.domain.model.CampanhaPatrocinada
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import java.time.LocalDate
import org.junit.Assert.assertTrue
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.presentation.navigation.Destination
import com.gtnix.aguiabranca.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrientacaoDetalheViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: OrientacaoDetalheViewModel
    private lateinit var orientacaoRepository: FakeOrientacaoRepository
    private lateinit var ideiaRepository: FakeIdeiaRepository
    private lateinit var projetoRepository: FakeProjetoRepository
    private lateinit var sessionManager: FakeSessionManager
    private val dashboardRepository = mockk<DashboardRepository>()

    private val orientacao = TestDataFactory.createOrientacao(
        id = "orientacao-123",
        titulo = "Redução de Custos"
    )

    @Before
    fun setup() {
        orientacaoRepository = FakeOrientacaoRepository(listOf(orientacao))
        ideiaRepository = FakeIdeiaRepository()
        projetoRepository = FakeProjetoRepository()
        sessionManager = FakeSessionManager()
        coEvery { dashboardRepository.obterPorEstrategia(any()) } returns
            Result.Error(Exception("offline"))
    }

    private fun createViewModel(orientacaoId: String = "orientacao-123") {
        val savedStateHandle = SavedStateHandle(
            mapOf(Destination.OrientacaoDetalhe.ARG_ORIENTACAO_ID to orientacaoId)
        )
        viewModel = OrientacaoDetalheViewModel(
            orientacaoRepository,
            ideiaRepository,
            projetoRepository,
            dashboardRepository,
            sessionManager,
            savedStateHandle
        )
    }

    @Test
    fun `dado orientacao existente quando carrega então exibe dados`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.orientacao)
        assertEquals("Redução de Custos", state.orientacao?.titulo)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado orientacao inexistente quando carrega então exibe erro not found`() = runTest {
        createViewModel(orientacaoId = "inexistente")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.orientacao)
        assertEquals(R.string.orientacao_detalhe_not_found, state.errorMessageRes)
    }

    @Test
    fun `dado ideias alinhadas quando carrega então exibe lista de ideias`() = runTest {
        val ideia1 = TestDataFactory.createIdeia(id = "i1", titulo = "Ideia 1").copy(orientacaoId = "orientacao-123")
        val ideia2 = TestDataFactory.createIdeia(id = "i2", titulo = "Ideia 2").copy(orientacaoId = "orientacao-123")
        val ideiaOutra = TestDataFactory.createIdeia(id = "i3", titulo = "Outra").copy(orientacaoId = "outra-orientacao")
        ideiaRepository = FakeIdeiaRepository(listOf(ideia1, ideia2, ideiaOutra))

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.ideiasAlinhadas.size)
    }

    @Test
    fun `dado projetos vinculados quando carrega então exibe lista de projetos`() = runTest {
        val projeto1 = TestDataFactory.createProjeto(id = "p1", status = StatusProjeto.EM_ANDAMENTO)
            .copy(orientacaoId = "orientacao-123")
        val projeto2 = TestDataFactory.createProjeto(id = "p2", status = StatusProjeto.CONCLUIDO)
            .copy(orientacaoId = "orientacao-123")
        projetoRepository = FakeProjetoRepository(listOf(projeto1, projeto2))

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.projetosVinculados.size)
    }

    @Test
    fun `dado projetos vinculados quando calcula projetosEmAndamento então conta corretamente`() = runTest {
        val projetoAndamento = TestDataFactory.createProjeto(id = "p1", status = StatusProjeto.EM_ANDAMENTO)
            .copy(orientacaoId = "orientacao-123")
        val projetoPlanejado = TestDataFactory.createProjeto(id = "p2", status = StatusProjeto.PLANEJADO)
            .copy(orientacaoId = "orientacao-123")
        val projetoConcluido = TestDataFactory.createProjeto(id = "p3", status = StatusProjeto.CONCLUIDO)
            .copy(orientacaoId = "orientacao-123")
        projetoRepository = FakeProjetoRepository(listOf(projetoAndamento, projetoPlanejado, projetoConcluido))

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.projetosEmAndamento)
    }

    @Test
    fun `dado ideias com status variados quando carrega então deriva funil e top por score`() = runTest {
        val base = TestDataFactory.createIdeia(id = "x", titulo = "x").copy(orientacaoId = "orientacao-123")
        ideiaRepository = FakeIdeiaRepository(
            listOf(
                base.copy(id = "a", status = StatusIdeia.PENDENTE, scoreIa = 40),
                base.copy(id = "b", status = StatusIdeia.APROVADA, scoreIa = 90),
                base.copy(id = "c", status = StatusIdeia.CONVERTIDA_PROJETO, scoreIa = 70),
                base.copy(id = "d", status = StatusIdeia.REPROVADA, scoreIa = 99)
            )
        )

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(1, state.ideiasEmAvaliacao)
        assertEquals(1, state.ideiasAprovadas)
        assertEquals(1, state.ideiasEmProjeto)
        assertEquals(listOf("b", "c", "a"), state.topIdeias.map { it.id })
    }

    @Test
    fun `dado impacto do servidor quando carrega então usa atrasados do servidor`() = runTest {
        coEvery { dashboardRepository.obterPorEstrategia("orientacao-123") } returns Result.Success(
            DashboardByStrategy(
                orientacaoId = "orientacao-123",
                titulo = "Redução de Custos",
                ativo = true,
                investimentoTotal = 100_000.0,
                retornoTotal = 10_000.0,
                lucroTotal = 20_000.0,
                roiMedio = 20.0,
                projetosNoPrazo = 1,
                projetosAtrasados = 2,
                projetos = emptyList()
            )
        )

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.projetosAtrasados)
        assertEquals(20.0, state.impacto?.roiMedio ?: 0.0, 0.001)
    }

    @Test
    fun `dado projeto com prazo vencido e servidor offline então conta atraso localmente`() = runTest {
        val vencido = TestDataFactory.createProjeto(id = "p1", status = StatusProjeto.EM_ANDAMENTO)
            .copy(orientacaoId = "orientacao-123", dataPrevistaConclusao = System.currentTimeMillis() - 86_400_000)
        projetoRepository = FakeProjetoRepository(listOf(vencido))

        createViewModel()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.projetosAtrasados)
    }

    @Test
    fun `dado lider quando alterna ativacao então desativa a orientacao`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(id = "lider-1", perfil = PerfilUsuario.LIDER))
        createViewModel()
        advanceUntilIdle()

        viewModel.alternarAtivacao()
        advanceUntilIdle()

        assertEquals(false, viewModel.uiState.value.orientacao?.ativa)
        assertFalse(viewModel.uiState.value.alterandoAtivacao)
    }

    @Test
    fun `dado sem ideias alinhadas quando carrega então lista vazia`() = runTest {
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(0, state.ideiasAlinhadas.size)
    }

    @Test
    fun `dado lider e campanha aberta quando encerrar então chama servidor e expõe vencedores`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(id = "lider-1", perfil = PerfilUsuario.LIDER))
        orientacaoRepository = FakeOrientacaoRepository(
            listOf(orientacao.copy(campanhaPatrocinada = CampanhaPatrocinada(LocalDate.of(2026, 12, 1), 30, 3)))
        )
        createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.podeEncerrarCampanha)

        viewModel.encerrarCampanha()
        advanceUntilIdle()

        assertEquals(listOf("orientacao-123"), orientacaoRepository.campanhasEncerradas)
        assertEquals(emptyList<Any>(), viewModel.uiState.value.vencedoresCampanha)
        assertFalse(viewModel.uiState.value.encerrandoCampanha)
    }

    @Test
    fun `dado operador quando campanha aberta então não pode encerrar`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(id = "op-1", perfil = PerfilUsuario.OPERADOR))
        orientacaoRepository = FakeOrientacaoRepository(
            listOf(orientacao.copy(campanhaPatrocinada = CampanhaPatrocinada(LocalDate.of(2026, 12, 1), 30, 3)))
        )
        createViewModel()
        advanceUntilIdle()

        viewModel.encerrarCampanha()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.podeEncerrarCampanha)
        assertTrue(orientacaoRepository.campanhasEncerradas.isEmpty())
    }
}
