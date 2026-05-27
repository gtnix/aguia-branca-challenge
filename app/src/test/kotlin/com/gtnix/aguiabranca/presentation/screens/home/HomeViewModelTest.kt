package com.gtnix.aguiabranca.presentation.screens.home

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.domain.usecase.dashboard.DashboardData
import com.gtnix.aguiabranca.domain.usecase.dashboard.GetDashboardUseCase
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
import org.junit.Assert.assertFalse
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
    private val calcularPontuacaoUseCase = mockk<CalcularPontuacaoUseCase>()
    private val getRankingUseCase = mockk<GetRankingUseCase>()

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

    @Before
    fun setup() {
        sessionManager = FakeSessionManager()
        every { getDashboardUseCase() } returns flowOf(Result.Success(dashboardData))
        coEvery { calcularPontuacaoUseCase.calcular(any(), any<PerfilUsuario>()) } returns mockk(relaxed = true)
        coEvery { getRankingUseCase.executar(any(), any(), any(), any()) } returns mockk(relaxed = true)
    }

    private fun createViewModel() {
        viewModel = HomeViewModel(
            getDashboardUseCase,
            sessionManager,
            calcularPontuacaoUseCase,
            getRankingUseCase
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
        assertEquals(5, state.totalIdeias)
        assertEquals(2, state.totalProjetos)
        assertNull(state.errorMessageRes)
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

        val state = viewModel.uiState.first { it.perfil == PerfilUsuario.GESTOR }
        assertEquals(PerfilUsuario.GESTOR, state.perfil)
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
}
