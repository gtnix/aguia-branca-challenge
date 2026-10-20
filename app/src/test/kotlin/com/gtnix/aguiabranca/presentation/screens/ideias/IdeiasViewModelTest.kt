package com.gtnix.aguiabranca.presentation.screens.ideias

import app.cash.turbine.test
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AnaliseIa
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.FaixaIa
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusAnaliseIa
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.usecase.ideia.GetIdeiasUseCase
import com.gtnix.aguiabranca.domain.usecase.ideia.ScoreIdeiasUseCase
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.presentation.util.UiState
import com.gtnix.aguiabranca.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class IdeiasViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: IdeiasViewModel
    private lateinit var fakeIdeiaRepository: FakeIdeiaRepository
    private lateinit var fakeSessionManager: FakeSessionManager

    private val gestorUser = TestDataFactory.createUsuario(
        id = "user-gestor",
        perfil = PerfilUsuario.GESTOR,
        area = AreaAtuacao.OPERACOES
    )

    private val ideias = listOf(
        TestDataFactory.createIdeia(
            id = "ideia-1",
            titulo = "Pendente",
            area = AreaAtuacao.OPERACOES,
            status = StatusIdeia.PENDENTE
        ),
        TestDataFactory.createIdeia(
            id = "ideia-2",
            titulo = "Aprovada",
            area = AreaAtuacao.OPERACOES,
            status = StatusIdeia.APROVADA
        ),
        TestDataFactory.createIdeia(
            id = "ideia-3",
            titulo = "Logistica",
            area = AreaAtuacao.LOGISTICA,
            status = StatusIdeia.PENDENTE
        ),
        TestDataFactory.createIdeia(
            id = "ideia-4",
            titulo = "Convertida",
            area = AreaAtuacao.OPERACOES,
            status = StatusIdeia.CONVERTIDA_PROJETO
        )
    )

    @Before
    fun setup() {
        fakeIdeiaRepository = FakeIdeiaRepository(ideias)
        fakeSessionManager = FakeSessionManager()
        fakeSessionManager.setUser(gestorUser)
        viewModel = criarViewModel()
    }

    private fun criarViewModel(): IdeiasViewModel = IdeiasViewModel(
        GetIdeiasUseCase(fakeIdeiaRepository, fakeSessionManager),
        ScoreIdeiasUseCase(fakeIdeiaRepository, fakeSessionManager),
        fakeSessionManager
    )

    @Test
    fun `carrega ideias filtradas por perfil do gestor`() = runTest {
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(4, (state as UiState.Success).data.size)
        assertTrue(viewModel.canEvaluateIdeias)
        assertTrue(!viewModel.canCreateIdeias)
    }

    @Test
    fun `operador cria e nao avalia`() = runTest {
        fakeSessionManager.setUser(
            TestDataFactory.createUsuario(
                id = "user-operador",
                perfil = PerfilUsuario.OPERADOR,
                area = AreaAtuacao.OPERACOES
            )
        )
        viewModel = criarViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.canCreateIdeias)
        assertTrue(!viewModel.canEvaluateIdeias)
    }

    @Test
    fun `lider nao avalia nem pontua na fila`() = runTest {
        fakeSessionManager.setUser(
            TestDataFactory.createUsuario(id = "user-lider", perfil = PerfilUsuario.LIDER)
        )
        viewModel = criarViewModel()
        advanceUntilIdle()

        assertTrue(!viewModel.canEvaluateIdeias)
        assertEquals(R.string.ideias_readonly_title, viewModel.listTitleRes)
    }

    @Test
    fun `filtro pendentes retorna apenas ideias pendentes`() = runTest {
        advanceUntilIdle()

        viewModel.selecionarFiltro(FiltroIdeia.PENDENTES)
        advanceUntilIdle()

        viewModel.ideiasFiltradas.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            val filtered = (state as UiState.Success).data
            assertEquals(2, filtered.size)
            assertTrue(filtered.all { it.status == StatusIdeia.PENDENTE })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filtro aprovadas inclui ideias convertidas em projeto`() = runTest {
        advanceUntilIdle()

        viewModel.selecionarFiltro(FiltroIdeia.APROVADAS)
        advanceUntilIdle()

        viewModel.ideiasFiltradas.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            val filtered = (state as UiState.Success).data
            assertEquals(2, filtered.size)
            assertTrue(filtered.all { it.isAprovadaOuConvertida() })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `filtro em projeto retorna apenas convertidas`() = runTest {
        advanceUntilIdle()

        viewModel.selecionarFiltro(FiltroIdeia.EM_PROJETO)
        advanceUntilIdle()

        viewModel.ideiasFiltradas.test {
            val state = awaitItem()
            assertTrue(state is UiState.Success)
            val filtered = (state as UiState.Success).data
            assertEquals(1, filtered.size)
            assertEquals(StatusIdeia.CONVERTIDA_PROJETO, filtered.first().status)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `gestor inicia na fila pendente e pontua com IA sem aprovar`() = runTest {
        advanceUntilIdle()

        assertEquals(FiltroIdeia.PENDENTES, viewModel.filtroSelecionado.value)

        viewModel.pontuarComIa()
        advanceUntilIdle()

        assertNull(fakeIdeiaRepository.getById("ideia-1")?.scoreIa)
        assertEquals(StatusIdeia.PENDENTE, fakeIdeiaRepository.getById("ideia-1")?.status)
        assertEquals(R.string.ideias_score_dados_insuficientes, viewModel.actionMessageRes.value)
    }

    @Test
    fun `fallback com impacto e esforco conhecidos mostra selo de estimativa`() = runTest {
        fakeIdeiaRepository = FakeIdeiaRepository(
            listOf(TestDataFactory.createIdeia(id = "ideia-1", impactoEstimado = 4, esforcoEstimado = 2))
        )
        viewModel = criarViewModel()
        advanceUntilIdle()

        viewModel.pontuarComIa()
        advanceUntilIdle()

        val ideia = fakeIdeiaRepository.getById("ideia-1")!!
        val selo = viewModel.seloDe(ideia)
        assertEquals(StatusAnaliseIa.ESTIMATIVA, selo?.status)
        assertTrue(selo!!.isEstimativa)
        assertEquals(R.string.ideias_score_fallback, viewModel.actionMessageRes.value)
    }

    @Test
    fun `operador nunca recebe selo da IA`() = runTest {
        fakeSessionManager.setUser(TestDataFactory.createUsuario(id = "user-1", perfil = PerfilUsuario.OPERADOR))
        viewModel = criarViewModel()
        advanceUntilIdle()

        val pontuada = ideias.first().copy(scoreIa = 90, analiseIa = AnaliseIa(StatusAnaliseIa.IA, FaixaIa.ALTA))

        assertNull(viewModel.seloDe(pontuada))
    }

    @Test
    fun `fila do gestor fica por ordem de chegada e nao pelo score`() = runTest {
        val agora = 1_000_000L
        fakeIdeiaRepository = FakeIdeiaRepository(
            listOf(
                TestDataFactory.createIdeia(id = "nova-score-alto").copy(dataCriacao = agora, scoreIa = 95),
                TestDataFactory.createIdeia(id = "antiga-score-baixo").copy(dataCriacao = agora - 10_000, scoreIa = 20),
                TestDataFactory.createIdeia(id = "meio-sem-score").copy(dataCriacao = agora - 5_000)
            )
        )
        viewModel = criarViewModel()
        advanceUntilIdle()

        viewModel.ideiasFiltradas.test {
            val ids = (awaitItem() as UiState.Success).data.map { it.id }
            assertEquals(listOf("antiga-score-baixo", "meio-sem-score", "nova-score-alto"), ids)
            assertEquals(OrdenacaoIdeias.FILA, viewModel.ordenacao.value)

            viewModel.alternarOrdenacao()
            val porIa = (awaitItem() as UiState.Success).data.map { it.id }
            assertEquals(listOf("nova-score-alto", "antiga-score-baixo", "meio-sem-score"), porIa)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `lider nao alterna ordenacao`() = runTest {
        fakeSessionManager.setUser(TestDataFactory.createUsuario(id = "user-lider", perfil = PerfilUsuario.LIDER))
        viewModel = criarViewModel()
        advanceUntilIdle()

        viewModel.alternarOrdenacao()

        assertEquals(OrdenacaoIdeias.FILA, viewModel.ordenacao.value)
    }
}
