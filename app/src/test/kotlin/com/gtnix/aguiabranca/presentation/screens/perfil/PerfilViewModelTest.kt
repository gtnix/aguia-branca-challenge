package com.gtnix.aguiabranca.presentation.screens.perfil

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.Estatisticas
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.ResultadoRanking
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase
import com.gtnix.aguiabranca.domain.usecase.GetRankingUseCase
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.FakeUsuarioRepository
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class PerfilViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PerfilViewModel
    private lateinit var usuarioRepository: FakeUsuarioRepository
    private lateinit var sessionManager: FakeSessionManager
    private val calcularPontuacao = mockk<CalcularPontuacaoUseCase>()
    private val getRankingUseCase = mockk<GetRankingUseCase>()

    private val usuario = TestDataFactory.createUsuario(
        id = "user-perfil",
        nome = "João Silva",
        perfil = PerfilUsuario.OPERADOR,
        area = AreaAtuacao.OPERACOES
    )

    private val pontuacaoMock = Pontuacao(
        total = 50,
        nivel = NivelUsuario.EM_ASCENSAO,
        progressoProximoNivel = 0.5f,
        pontosParaProximoNivel = 40,
        conquistas = emptyList(),
        estatisticas = Estatisticas(totalIdeias = 5, ideiasAprovadas = 2, projetosParticipando = 1)
    )

    private val rankingMock = ResultadoRanking(
        topRanking = emptyList(),
        posicaoUsuarioLogado = 3,
        entradaUsuarioLogado = null,
        perfilFiltro = PerfilUsuario.OPERADOR,
        totalParticipantes = 10
    )

    @Before
    fun setup() {
        usuarioRepository = FakeUsuarioRepository()
        sessionManager = FakeSessionManager()

        coEvery { calcularPontuacao.calcular(any(), any<PerfilUsuario>()) } returns pontuacaoMock
        coEvery {
            getRankingUseCase.executar(
                usuarioLogadoId = any(),
                perfilFiltro = any(),
                divisaoFilter = any<DivisaoFilter>(),
                divisaoUsuario = any()
            )
        } returns rankingMock
    }

    private fun createViewModel() {
        viewModel = PerfilViewModel(usuarioRepository, calcularPontuacao, getRankingUseCase, sessionManager)
    }

    @Test
    fun `dado usuario logado quando iniciar então carrega dados do perfil`() = runTest {
        sessionManager.setUser(usuario)
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("João Silva", state.nome)
        assertEquals(AreaAtuacao.OPERACOES.name, state.area)
        assertEquals(PerfilUsuario.OPERADOR.name, state.perfil)
        assertEquals('J', state.inicialNome)
        assertEquals(pontuacaoMock, state.pontuacao)
        assertEquals(3, state.posicaoRanking)
        assertEquals(10, state.totalParticipantesRanking)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado usuario nao logado quando iniciar então nao carrega pontuacao`() = runTest {
        sessionManager.setUser(null)
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.pontuacao)
    }

    @Test
    fun `dado usuario logado quando fazer logout então marca sucesso`() = runTest {
        sessionManager.setUser(usuario)
        createViewModel()
        advanceUntilIdle()

        viewModel.onLogout()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.logoutSuccess)
        assertNull(sessionManager.getCurrentUser())
    }

    @Test
    fun `dado erro ao calcular pontuacao quando carregar então retorna erro`() = runTest {
        coEvery { calcularPontuacao.calcular(any(), any<PerfilUsuario>()) } throws RuntimeException("Erro")
        sessionManager.setUser(usuario)
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(R.string.error_load_profile, state.errorMessageRes)
    }

    @Test
    fun `dado conquistas desbloqueadas quando limpar then reseta estado`() = runTest {
        sessionManager.setUser(usuario)
        createViewModel()
        advanceUntilIdle()

        viewModel.clearNewlyUnlocked()

        assertTrue(viewModel.uiState.value.newlyUnlockedIds.isEmpty())
        assertFalse(viewModel.uiState.value.showCelebration)
    }
}
