package com.gtnix.aguiabranca.presentation.screens.home

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.Estatisticas
import com.gtnix.aguiabranca.domain.model.RankingEntrada
import com.gtnix.aguiabranca.domain.model.LeaderDashboardSnapshot
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.GamificacaoRepository
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.domain.usecase.dashboard.DashboardData
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetDashboardUseCase
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetLeaderDashboardUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var sessionManager: FakeSessionManager
    private val getDashboardUseCase = mockk<GetDashboardUseCase>()
    private val getLeaderDashboardUseCase = mockk<GetLeaderDashboardUseCase>()
    private val calcularPontuacaoUseCase = mockk<CalcularPontuacaoUseCase>()
    private val getRankingUseCase = mockk<GetRankingUseCase>()
    private val gamificacaoRepository = mockk<GamificacaoRepository>()

    private val dashboardData = DashboardData(
        orientacoes = emptyList(),
        ideias = emptyList(),
        projetosEmAndamento = emptyList(),
        ideiasConvertidasRecentes = emptyList(),
        totalIdeias = 5,
        totalProjetos = 2,
        ideiasAprovadas = 3,
        ideiasEmProjeto = 1,
        ideiasPendentesAvaliacao = 2,
        investimentoTotal = 50_000.0,
        retornoTotal = 10_000.0,
        roiConsolidado = 140.0
    )

    private val pontuacaoOperador = Pontuacao(
        total = 120,
        nivel = NivelUsuario.EM_ASCENSAO,
        progressoProximoNivel = 0.99f,
        pontosParaProximoNivel = 1,
        estatisticas = Estatisticas(
            totalIdeias = 7,
            ideiasAprovadas = 2,
            projetosParticipando = 2
        )
    )

    private val leaderSnapshot = LeaderDashboardSnapshot(
        roiMedio = 26.67,
        lucroTotal = 28_000.0,
        investimentoTotal = 80_000.0,
        retornoTotal = 9_000.0,
        projetosNoPrazo = 1,
        projetosAtrasados = 0,
        ganhoProdutividadePercentual = 20.0,
        totalIdeias = 8,
        ideiasAprovadas = 3,
        ideiasEmProjeto = 2,
        projetosAtivos = 1,
        tempoMedioAprovacaoDias = 7,
        funil = emptyList(),
        desempenhoPorArea = emptyList()
    )

    @Before
    fun setup() {
        sessionManager = FakeSessionManager()
        every { getDashboardUseCase() } returns flowOf(Result.Success(dashboardData))
        every { getLeaderDashboardUseCase() } returns flowOf(Result.Success(leaderSnapshot))
        coEvery { calcularPontuacaoUseCase.calcular(any(), any<PerfilUsuario>()) } returns pontuacaoOperador
        coEvery { getRankingUseCase.executar(any(), any(), any(), any()) } returns mockk(relaxed = true)
        coEvery { gamificacaoRepository.recompensas(any()) } returns Result.Success(emptyList())
    }

    private fun createViewModel() {
        viewModel = HomeViewModel(
            getDashboardUseCase,
            getLeaderDashboardUseCase,
            sessionManager,
            calcularPontuacaoUseCase,
            getRankingUseCase,
            gamificacaoRepository
        )
    }

    @Test
    fun `dado usuario logado quando carrega dashboard então exibe dados com sucesso`() = runTest {
        val usuario = TestDataFactory.createUsuario(nome = "Maria Silva", perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(usuario)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(7, state.totalIdeias)
        assertEquals(2, state.ideiasAprovadas)
        assertEquals(2, state.totalProjetos)
        assertEquals(7, state.pontuacao?.estatisticas?.totalIdeias)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado operador no ranking quando carrega então expõe posição e divisão`() = runTest {
        val entrada = mockk<RankingEntrada> { every { divisao } returns DivisaoNegocio.LOGISTICA }
        coEvery { getRankingUseCase.executar(any(), any(), any(), any()) } returns mockk(relaxed = true) {
            every { posicaoUsuarioLogado } returns 2
            every { entradaUsuarioLogado } returns entrada
        }
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR))

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(2, state.posicaoRanking)
        assertEquals(DivisaoNegocio.LOGISTICA.label, state.divisaoRanking)
    }

    @Test
    fun `dado gestor quando carrega então expõe fila, aprovadas sem projeto e atrasados`() = runTest {
        val pendente = TestDataFactory.createIdeia("pendente")
        val aprovada = TestDataFactory.createIdeia("aprovada", status = StatusIdeia.APROVADA)
        every { getDashboardUseCase() } returns flowOf(
            Result.Success(
                dashboardData.copy(
                    filaPendente = listOf(pendente),
                    diasMaisAntigaPendente = 9,
                    aprovadasSemProjeto = listOf(aprovada),
                    projetosAtrasados = 2
                )
            )
        )
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading && it.filaPendente.isNotEmpty() }
        assertEquals(listOf("pendente"), state.filaPendente.map { it.id })
        assertEquals(9, state.diasMaisAntigaPendente)
        assertEquals(listOf("aprovada"), state.aprovadasSemProjeto.map { it.id })
        assertEquals(2, state.projetosAtrasados)
    }

    @Test
    fun `dado erro no dashboard quando carrega então exibe mensagem de erro`() = runTest {
        val usuario = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(usuario)
        every { getDashboardUseCase() } returns flowOf(Result.Error(Exception("Falha")))

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals(R.string.error_load_dashboard, state.errorMessageRes)
        assertEquals(7, state.totalIdeias)
        assertEquals(2, state.ideiasAprovadas)
    }

    @Test
    fun `dado perfil override quando carregarDados então atualiza perfil`() = runTest {
        val usuario = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(usuario)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        viewModel.carregarDados("GESTOR")
        advanceUntilIdle()

        val state = viewModel.uiState.first { it.perfil == PerfilUsuario.GESTOR && !it.isLoading }
        assertEquals(PerfilUsuario.GESTOR, state.perfil)
        assertEquals(140.0, state.roiConsolidado, 0.01)
        assertEquals(5, state.totalIdeias)
    }

    @Test
    fun `dado perfil lider quando carrega então usa KPIs do summary da API`() = runTest {
        val usuario = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(usuario)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first {
            !it.isLoading && it.perfil == PerfilUsuario.LIDER && it.roiConsolidado > 0.0
        }
        assertEquals(26.67, state.roiConsolidado, 0.01)
        assertEquals(80_000.0, state.investimentoTotal, 0.01)
        assertEquals(9_000.0, state.retornoTotal, 0.01)
        assertEquals(28_000.0, state.lucroAnualizado, 0.01)
        assertEquals(8, state.totalIdeias)
        assertEquals(3, state.ideiasAprovadas)
        assertEquals(2, state.ideiasEmProjeto)
        assertEquals(1, state.totalProjetos)
        assertEquals(0, state.projetosAtrasados)
        assertEquals(7, state.tempoMedioAprovacaoDias)
        assertEquals(0, state.catalogoLoja.premiosAtivos)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado perfil lider com catálogo quando carrega então conta ativos e sem estoque`() = runTest {
        val usuario = TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER)
        sessionManager.setUser(usuario)
        coEvery { gamificacaoRepository.recompensas(true) } returns Result.Success(
            listOf(
                recompensaHome(id = "a", ativo = true, estoque = 3),
                recompensaHome(id = "b", ativo = true, estoque = 0),
                recompensaHome(id = "c", ativo = false, estoque = 8)
            )
        )

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first {
            !it.isLoading && it.perfil == PerfilUsuario.LIDER && it.catalogoLoja.carregado
        }
        assertEquals(2, state.catalogoLoja.premiosAtivos)
        assertEquals(1, state.catalogoLoja.semEstoque)
    }

    @Test
    fun `dado estado inicial quando criado então isLoading é true`() = runTest {
        val initialState = HomeUiState(isLoading = true)
        assertTrue(initialState.isLoading)
    }

    @Test
    fun `dado nome completo quando exibe então mostra apenas primeiro nome`() = runTest {
        val usuario = TestDataFactory.createUsuario(nome = "Carlos Eduardo Santos")
        sessionManager.setUser(usuario)

        createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
        advanceUntilIdle()

        val state = viewModel.uiState.first { !it.isLoading }
        assertEquals("Carlos", state.nomeUsuario)
    }

    private fun recompensaHome(
        id: String,
        ativo: Boolean,
        estoque: Int
    ) = Recompensa(
        id = id,
        titulo = id,
        descricao = "",
        categoria = "VALE",
        parceiro = "",
        custoAsas = 60,
        estoque = estoque,
        nivelMinimo = NivelUsuario.INICIANTE,
        ativo = ativo,
        podeResgatar = false,
        motivoBloqueio = null
    )
}
